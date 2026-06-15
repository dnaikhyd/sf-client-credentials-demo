import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Account Reader for Salesforce
 * Reads and displays 10 Account records from Salesforce
 *
 * Usage:
 *   javac AccountReader.java
 *   java AccountReader              # Normal mode
 *   java AccountReader --debug      # Debug mode with detailed REST API info
 */
public class AccountReader {
    
    private static final String ENV_FILE = "../.env";
    private static String accessToken;
    private static String instanceUrl;
    private static boolean debugMode = false;
    
    public static void main(String[] args) {
        // Check for debug flag
        if (args.length > 0 && "--debug".equals(args[0])) {
            debugMode = true;
            System.out.println("=== DEBUG MODE ENABLED ===\n");
        }
        
        System.out.println("=== Salesforce Account Reader ===\n");
        
        try {
            // Load configuration
            Map<String, String> config = loadEnvFile();
            String clientId = config.get("SALESFORCE_CLIENT_ID");
            String clientSecret = config.get("SALESFORCE_CLIENT_SECRET");
            String tokenUrl = config.get("SALESFORCE_TOKEN_URL");
            
            if (clientId == null || clientSecret == null || tokenUrl == null) {
                System.err.println("[ERROR] Missing required configuration in .env file");
                System.exit(1);
            }
            
            // Authenticate
            System.out.println("[Step 1] Authenticating with Salesforce...");
            authenticate(clientId, clientSecret, tokenUrl);
            System.out.println("[SUCCESS] Authentication successful!\n");
            
            // Query and display accounts
            System.out.println("[Step 2] Querying 10 Account records...\n");
            queryAndDisplayAccounts();
            
            System.out.println("\n=== Account read completed! ===");
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Authenticate and get access token
     */
    private static void authenticate(String clientId, String clientSecret, String tokenUrl) throws IOException {
        if (debugMode) {
            System.out.println("[DEBUG] ========== OAuth 2.0 Authentication REST API ==========");
            System.out.println("[DEBUG] REST API Endpoint: " + tokenUrl);
            System.out.println("[DEBUG] HTTP Method: POST");
            System.out.println("[DEBUG] Content-Type: application/x-www-form-urlencoded");
            System.out.println("[DEBUG] Request Body: grant_type=client_credentials&client_id=" + maskValue(clientId) + "&client_secret=" + maskValue(clientSecret));
            System.out.println();
        }
        
        URL url = new URL(tokenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            String requestBody = String.format(
                "grant_type=client_credentials&client_id=%s&client_secret=%s",
                clientId, clientSecret
            );
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            String responseBody = readStream(conn.getInputStream());
            
            if (debugMode) {
                System.out.println("[DEBUG] Response Code: " + responseCode);
                System.out.println("[DEBUG] Response Body: " + maskSensitiveData(responseBody));
                System.out.println("[DEBUG] ============================================================\n");
            }
            
            if (responseCode == 200) {
                accessToken = extractJsonValue(responseBody, "access_token");
                instanceUrl = extractJsonValue(responseBody, "instance_url");
            } else {
                throw new IOException("Authentication failed: " + responseBody);
            }
            
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Query and display Account records
     */
    private static void queryAndDisplayAccounts() throws IOException {
        // SOQL query to get 10 accounts with key fields
        String soql = "SELECT Id, Name, Industry, Type, Phone, BillingCity, BillingState, CreatedDate FROM Account ORDER BY CreatedDate DESC LIMIT 10";
        String encodedQuery = URLEncoder.encode(soql, StandardCharsets.UTF_8.toString());
        
        String queryUrl = instanceUrl + "/services/data/v59.0/query?q=" + encodedQuery;
        
        if (debugMode) {
            System.out.println("[DEBUG] ========== SOQL Query REST API ==========");
            System.out.println("[DEBUG] REST API Endpoint: " + instanceUrl + "/services/data/v59.0/query");
            System.out.println("[DEBUG] HTTP Method: GET");
            System.out.println("[DEBUG] Headers:");
            System.out.println("[DEBUG]   Authorization: Bearer " + maskValue(accessToken));
            System.out.println("[DEBUG]   Accept: application/json");
            System.out.println("[DEBUG] SOQL Query: " + soql);
            System.out.println("[DEBUG] Full URL: " + queryUrl);
            System.out.println();
        }
        
        URL url = new URL(queryUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");
            
            int responseCode = conn.getResponseCode();
            
            if (debugMode) {
                System.out.println("[DEBUG] Response Code: " + responseCode);
            }
            
            if (responseCode == 200) {
                String responseBody = readStream(conn.getInputStream());
                if (debugMode) {
                    System.out.println("[DEBUG] Response Body (first 500 chars): " +
                        (responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody));
                    System.out.println("[DEBUG] ================================================\n");
                }
                parseAndDisplayAccounts(responseBody);
            } else {
                String errorResponse = readStream(conn.getErrorStream());
                if (debugMode) {
                    System.out.println("[DEBUG] Error Response: " + errorResponse);
                    System.out.println("[DEBUG] ================================================\n");
                }
                System.err.println("[ERROR] Query failed: " + errorResponse);
            }
            
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Parse JSON response and display accounts in a formatted table
     */
    private static void parseAndDisplayAccounts(String json) {
        // Extract total size
        String totalSize = extractJsonValue(json, "totalSize");
        System.out.println("Total Accounts found: " + totalSize);
        System.out.println();
        
        // Print table header
        System.out.println("========================================================================================================");
        System.out.println(" #  | Account Name                    | Industry        | Type        | Phone          | City/State");
        System.out.println("========================================================================================================");
        
        // Parse records array
        int recordsStart = json.indexOf("\"records\"");
        if (recordsStart == -1) {
            System.out.println(" No records found");
            System.out.println("========================================================================================================");
            return;
        }
        
        // Split by record objects
        String[] parts = json.substring(recordsStart).split("\\{\"attributes\"");
        int recordNum = 0;
        
        for (int i = 1; i < parts.length && recordNum < 10; i++) {
            String record = parts[i];
            recordNum++;
            
            // Extract fields
            String id = extractFieldValue(record, "Id");
            String name = extractFieldValue(record, "Name");
            String industry = extractFieldValue(record, "Industry");
            String type = extractFieldValue(record, "Type");
            String phone = extractFieldValue(record, "Phone");
            String city = extractFieldValue(record, "BillingCity");
            String state = extractFieldValue(record, "BillingState");
            
            // Format values
            name = truncate(name != null ? name : "N/A", 30);
            industry = truncate(industry != null ? industry : "N/A", 15);
            type = truncate(type != null ? type : "N/A", 11);
            phone = truncate(phone != null ? phone : "N/A", 14);
            String location = truncate(formatLocation(city, state), 23);
            
            // Print row
            System.out.printf(" %-2d | %-30s | %-15s | %-11s | %-14s | %-23s%n",
                recordNum, name, industry, type, phone, location);
        }
        
        System.out.println("========================================================================================================");
        System.out.println("\nDisplayed " + recordNum + " records");
    }
    
    /**
     * Extract field value from JSON record
     */
    private static String extractFieldValue(String record, String fieldName) {
        String searchKey = "\"" + fieldName + "\"";
        int keyIndex = record.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int colonIndex = record.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        
        // Skip whitespace after colon
        int valueStart = colonIndex + 1;
        while (valueStart < record.length() && Character.isWhitespace(record.charAt(valueStart))) {
            valueStart++;
        }
        
        // Check if value is null
        if (record.startsWith("null", valueStart)) {
            return null;
        }
        
        // Check if value is a string (starts with quote)
        if (record.charAt(valueStart) == '"') {
            int startQuote = valueStart;
            int endQuote = record.indexOf("\"", startQuote + 1);
            if (endQuote == -1) return null;
            return record.substring(startQuote + 1, endQuote);
        }
        
        return null;
    }
    
    /**
     * Format location from city and state
     */
    private static String formatLocation(String city, String state) {
        if (city != null && state != null) {
            return city + ", " + state;
        } else if (city != null) {
            return city;
        } else if (state != null) {
            return state;
        }
        return "N/A";
    }
    
    /**
     * Truncate string to specified length
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Load environment variables from .env file
     */
    private static Map<String, String> loadEnvFile() throws IOException {
        Map<String, String> config = new HashMap<>();
        
        File envFile = new File(ENV_FILE);
        if (!envFile.exists()) {
            throw new IOException(".env file not found!");
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    config.put(key, value);
                }
            }
        }
        
        return config;
    }
    
    /**
     * Read input stream to string
     */
    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
    
    /**
     * Simple JSON value extraction
     */
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        
        // Check if it's a number (for totalSize)
        int valueStart = colonIndex + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        
        if (Character.isDigit(json.charAt(valueStart))) {
            int valueEnd = valueStart;
            while (valueEnd < json.length() && Character.isDigit(json.charAt(valueEnd))) {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd);
        }
        
        int startQuote = json.indexOf("\"", colonIndex);
        if (startQuote == -1) return null;
        
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;
        
        return json.substring(startQuote + 1, endQuote);
    }
    
    /**
     * Mask sensitive values for display
     */
    private static String maskValue(String value) {
        if (value == null || value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
    
    /**
     * Mask sensitive data in JSON responses
     */
    private static String maskSensitiveData(String json) {
        if (json == null) return "";
        // Mask access_token values
        return json.replaceAll("\"access_token\":\"[^\"]+\"", "\"access_token\":\"****MASKED****\"")
                   .replaceAll("\"refresh_token\":\"[^\"]+\"", "\"refresh_token\":\"****MASKED****\"");
    }
}

// Made with Bob
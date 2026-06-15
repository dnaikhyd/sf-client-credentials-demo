import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Bulk Data Loader for Salesforce
 * Creates 10 records each for Account, Contact, and Product objects
 *
 * Usage:
 *   javac BulkDataLoader.java
 *   java BulkDataLoader              # Normal mode
 *   java BulkDataLoader --debug      # Debug mode with detailed REST API info
 */
public class BulkDataLoader {
    
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
        
        System.out.println("=== Salesforce Bulk Data Loader ===\n");
        
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
            
            // Create records
            int accountsCreated = createAccounts();
            int contactsCreated = createContacts();
            int productsCreated = createProducts();
            
            // Summary
            System.out.println("\n=== Summary ===");
            System.out.println("Accounts created: " + accountsCreated);
            System.out.println("Contacts created: " + contactsCreated);
            System.out.println("Products created: " + productsCreated);
            System.out.println("\n=== Bulk data load completed! ===");
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] " + e.getMessage());
            if (debugMode) {
                e.printStackTrace();
            }
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
     * Create 10 Account records
     */
    private static int createAccounts() throws IOException {
        System.out.println("[Step 2] Creating 10 Account records...");
        int created = 0;
        
        for (int i = 1; i <= 10; i++) {
            String accountJson = String.format(
                "{\"Name\":\"Test Account %d\",\"Industry\":\"Technology\",\"Type\":\"Customer\",\"Phone\":\"555-000-%04d\"}",
                i, i
            );
            
            if (createRecord("Account", accountJson)) {
                created++;
                System.out.println("  ✓ Created Account " + i);
            } else {
                System.err.println("  ✗ Failed to create Account " + i);
            }
        }
        
        System.out.println("[SUCCESS] Created " + created + " Accounts\n");
        return created;
    }
    
    /**
     * Create 10 Contact records
     */
    private static int createContacts() throws IOException {
        System.out.println("[Step 3] Creating 10 Contact records...");
        int created = 0;
        
        String[] firstNames = {"John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa", "James", "Maria"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};
        
        for (int i = 0; i < 10; i++) {
            String contactJson = String.format(
                "{\"FirstName\":\"%s\",\"LastName\":\"%s\",\"Email\":\"%s.%s@example.com\",\"Phone\":\"555-100-%04d\"}",
                firstNames[i], lastNames[i],
                firstNames[i].toLowerCase(), lastNames[i].toLowerCase(),
                i + 1
            );
            
            if (createRecord("Contact", contactJson)) {
                created++;
                System.out.println("  ✓ Created Contact: " + firstNames[i] + " " + lastNames[i]);
            } else {
                System.err.println("  ✗ Failed to create Contact: " + firstNames[i] + " " + lastNames[i]);
            }
        }
        
        System.out.println("[SUCCESS] Created " + created + " Contacts\n");
        return created;
    }
    
    /**
     * Create 10 Product records
     */
    private static int createProducts() throws IOException {
        System.out.println("[Step 4] Creating 10 Product records...");
        int created = 0;
        
        String[] productNames = {
            "Enterprise Software License", "Cloud Storage Plan", "API Integration Package",
            "Security Suite", "Analytics Dashboard", "Mobile App License",
            "Training Program", "Support Package", "Consulting Services", "Custom Development"
        };
        
        for (int i = 0; i < 10; i++) {
            String productJson = String.format(
                "{\"Name\":\"%s\",\"ProductCode\":\"PROD-%03d\",\"IsActive\":true,\"Description\":\"Test product %d\"}",
                productNames[i], i + 1, i + 1
            );
            
            if (createRecord("Product2", productJson)) {
                created++;
                System.out.println("  ✓ Created Product: " + productNames[i]);
            } else {
                System.err.println("  ✗ Failed to create Product: " + productNames[i]);
            }
        }
        
        System.out.println("[SUCCESS] Created " + created + " Products\n");
        return created;
    }
    
    /**
     * Create a single record in Salesforce
     */
    private static boolean createRecord(String objectType, String jsonBody) throws IOException {
        String apiUrl = instanceUrl + "/services/data/v59.0/sobjects/" + objectType;
        
        if (debugMode) {
            System.out.println("    [DEBUG] ========== Create " + objectType + " REST API ==========");
            System.out.println("    [DEBUG] REST API Endpoint: " + apiUrl);
            System.out.println("    [DEBUG] HTTP Method: POST");
            System.out.println("    [DEBUG] Content-Type: application/json");
            System.out.println("    [DEBUG] Request Body: " + jsonBody);
        }
        
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = conn.getResponseCode();
            
            if (debugMode) {
                System.out.println("    [DEBUG] Response Code: " + responseCode);
                System.out.println("    [DEBUG] ================================================");
            }
            
            if (responseCode == 201) {
                return true;
            } else {
                String errorResponse = readStream(conn.getErrorStream());
                if (debugMode) {
                    System.err.println("    [DEBUG] Error Response: " + errorResponse);
                }
                return false;
            }
            
        } finally {
            conn.disconnect();
        }
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
        
        int startQuote = json.indexOf("\"", colonIndex);
        if (startQuote == -1) return null;
        
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;
        
        return json.substring(startQuote + 1, endQuote);
    }
}

// Made with Bob
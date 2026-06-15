import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple standalone connection test for Salesforce OAuth 2.0 Client Credentials Flow.
 * This class can be compiled and run without Maven.
 *
 * Usage:
 *   javac SimpleConnectionTest.java
 *   java SimpleConnectionTest              # Normal mode
 *   java SimpleConnectionTest --debug      # Debug mode with detailed REST API info
 */
public class SimpleConnectionTest {
    
    private static final String ENV_FILE = ".env";
    private static boolean debugMode = false;
    
    public static void main(String[] args) {
        // Check for debug flag
        if (args.length > 0 && "--debug".equals(args[0])) {
            debugMode = true;
            System.out.println("=== DEBUG MODE ENABLED ===\n");
        }
        
        System.out.println("=== Salesforce OAuth 2.0 Client Credentials Connection Test ===\n");
        
        try {
            // Load configuration from .env file
            Map<String, String> config = loadEnvFile();
            
            String clientId = config.get("SALESFORCE_CLIENT_ID");
            String clientSecret = config.get("SALESFORCE_CLIENT_SECRET");
            String tokenUrl = config.get("SALESFORCE_TOKEN_URL");
            
            if (clientId == null || clientSecret == null || tokenUrl == null) {
                System.err.println("[ERROR] Missing required configuration in .env file");
                System.err.println("Please ensure SALESFORCE_CLIENT_ID, SALESFORCE_CLIENT_SECRET, and SALESFORCE_TOKEN_URL are set.");
                System.exit(1);
            }
            
            System.out.println("Configuration loaded:");
            System.out.println("  Client ID: " + maskValue(clientId));
            System.out.println("  Client Secret: " + maskValue(clientSecret));
            System.out.println("  Token URL: " + tokenUrl);
            System.out.println();
            
            // Step 1: Authenticate
            System.out.println("[Step 1] Authenticating with Salesforce...");
            AuthResponse authResponse = authenticate(clientId, clientSecret, tokenUrl);
            
            if (authResponse.success) {
                System.out.println("[SUCCESS] Authentication successful!");
                System.out.println("  Access Token: " + maskValue(authResponse.accessToken));
                System.out.println("  Instance URL: " + authResponse.instanceUrl);
                System.out.println("  Token Type: " + authResponse.tokenType);
                System.out.println();
                
                // Step 2: Test API call
                System.out.println("[Step 2] Testing API connection...");
                testApiConnection(authResponse.instanceUrl, authResponse.accessToken);
                
                System.out.println("\n=== Connection test completed successfully! ===");
            } else {
                System.err.println("[ERROR] Authentication failed!");
                System.err.println("Error: " + authResponse.error);
                System.err.println("Description: " + authResponse.errorDescription);
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Authenticate using OAuth 2.0 Client Credentials Flow
     */
    private static AuthResponse authenticate(String clientId, String clientSecret, String tokenUrl) throws IOException {
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
            // Prepare request
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            
            // Build request body
            String requestBody = String.format(
                "grant_type=client_credentials&client_id=%s&client_secret=%s",
                URLEncoder.encode(clientId, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(clientSecret, StandardCharsets.UTF_8.toString())
            );
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = conn.getResponseCode();
            String responseBody;
            
            if (debugMode) {
                System.out.println("[DEBUG] Response Code: " + responseCode);
            }
            
            if (responseCode == 200) {
                responseBody = readStream(conn.getInputStream());
                if (debugMode) {
                    System.out.println("[DEBUG] Response Body: " + maskSensitiveData(responseBody));
                    System.out.println("[DEBUG] ============================================================\n");
                }
                return parseSuccessResponse(responseBody);
            } else {
                responseBody = readStream(conn.getErrorStream());
                if (debugMode) {
                    System.out.println("[DEBUG] Error Response: " + responseBody);
                    System.out.println("[DEBUG] ============================================================\n");
                }
                return parseErrorResponse(responseBody);
            }
            
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Test API connection by getting API versions
     */
    private static void testApiConnection(String instanceUrl, String accessToken) throws IOException {
        String apiUrl = instanceUrl + "/services/data/";
        
        if (debugMode) {
            System.out.println("[DEBUG] ========== API Versions REST API ==========");
            System.out.println("[DEBUG] REST API Endpoint: " + apiUrl);
            System.out.println("[DEBUG] HTTP Method: GET");
            System.out.println("[DEBUG] Headers:");
            System.out.println("[DEBUG]   Authorization: Bearer " + maskValue(accessToken));
            System.out.println("[DEBUG]   Accept: application/json");
            System.out.println();
        }
        
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Accept", "application/json");
            
            int responseCode = conn.getResponseCode();
            String responseBody = readStream(conn.getInputStream());
            
            if (debugMode) {
                System.out.println("[DEBUG] Response Code: " + responseCode);
                System.out.println("[DEBUG] Response Body (first 300 chars): " +
                    (responseBody.length() > 300 ? responseBody.substring(0, 300) + "..." : responseBody));
                System.out.println("[DEBUG] ============================================================\n");
            }
            
            if (responseCode == 200) {
                System.out.println("[SUCCESS] API connection successful!");
                System.out.println("  Response Code: " + responseCode);
                System.out.println("  Available API versions:");
                
                // Simple parsing to show versions
                String[] lines = responseBody.split("\"version\"");
                for (int i = 1; i < Math.min(lines.length, 4); i++) {
                    String version = lines[i].split("\"")[1];
                    System.out.println("    - v" + version);
                }
            } else {
                System.err.println("[ERROR] API call failed with status code: " + responseCode);
                if (debugMode) {
                    System.err.println("Response: " + responseBody);
                }
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
            System.err.println("[ERROR] .env file not found!");
            System.err.println("Please copy .env.template to .env and fill in your credentials.");
            System.exit(1);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // Parse key=value
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
     * Parse successful authentication response
     */
    private static AuthResponse parseSuccessResponse(String json) {
        AuthResponse response = new AuthResponse();
        response.success = true;
        
        // Simple JSON parsing (without external libraries)
        response.accessToken = extractJsonValue(json, "access_token");
        response.instanceUrl = extractJsonValue(json, "instance_url");
        response.tokenType = extractJsonValue(json, "token_type");
        
        return response;
    }
    
    /**
     * Parse error authentication response
     */
    private static AuthResponse parseErrorResponse(String json) {
        AuthResponse response = new AuthResponse();
        response.success = false;
        response.error = extractJsonValue(json, "error");
        response.errorDescription = extractJsonValue(json, "error_description");
        return response;
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
    
    /**
     * Authentication response holder
     */
    static class AuthResponse {
        boolean success;
        String accessToken;
        String instanceUrl;
        String tokenType;
        String error;
        String errorDescription;
    }
}

// Made with Bob

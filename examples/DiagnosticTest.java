import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Diagnostic tool to help troubleshoot Salesforce OAuth 2.0 Client Credentials Flow issues.
 *
 * Usage:
 *   javac DiagnosticTest.java
 *   java DiagnosticTest              # Normal mode
 *   java DiagnosticTest --debug      # Debug mode with detailed REST API info
 */
public class DiagnosticTest {
    
    private static final String ENV_FILE = "../.env";
    private static boolean debugMode = false;
    
    public static void main(String[] args) {
        // Check for debug flag
        if (args.length > 0 && "--debug".equals(args[0])) {
            debugMode = true;
            System.out.println("=== DEBUG MODE ENABLED ===\n");
        }
        
        System.out.println("=== Salesforce OAuth 2.0 Client Credentials Diagnostic Tool ===\n");
        
        try {
            // Load configuration
            Map<String, String> config = loadEnvFile();
            
            String clientId = config.get("SALESFORCE_CLIENT_ID");
            String clientSecret = config.get("SALESFORCE_CLIENT_SECRET");
            String tokenUrl = config.get("SALESFORCE_TOKEN_URL");
            
            System.out.println("Configuration Check:");
            System.out.println("  Client ID: " + (clientId != null ? maskValue(clientId) : "[NOT SET]"));
            System.out.println("  Client Secret: " + (clientSecret != null ? maskValue(clientSecret) : "[NOT SET]"));
            System.out.println("  Token URL: " + (tokenUrl != null ? tokenUrl : "[NOT SET]"));
            System.out.println();
            
            if (clientId == null || clientSecret == null || tokenUrl == null) {
                System.err.println("[ERROR] Configuration incomplete. Please check your .env file.");
                System.exit(1);
            }
            
            // Check token URL format
            System.out.println("Token URL Analysis:");
            if (tokenUrl.contains("login.salesforce.com")) {
                System.out.println("  Type: Production");
            } else if (tokenUrl.contains("test.salesforce.com")) {
                System.out.println("  Type: Sandbox");
            } else if (tokenUrl.contains(".my.salesforce.com")) {
                System.out.println("  Type: My Domain");
                System.out.println("  [INFO] You're using a My Domain URL. This is correct for External Client Apps.");
            } else {
                System.out.println("  Type: Custom/Unknown");
            }
            System.out.println();
            
            // Common issues and solutions
            System.out.println("Common Issues and Solutions:");
            System.out.println();
            
            System.out.println("1. 'invalid_grant: request not supported on this domain'");
            System.out.println("   Possible causes:");
            System.out.println("   - External Client App not properly configured");
            System.out.println("   - Client Credentials grant type not enabled");
            System.out.println("   - Using wrong token URL (should use My Domain URL for External Client Apps)");
            System.out.println();
            
            System.out.println("2. Token URL for External Client Apps:");
            System.out.println("   - Production: https://YOUR_DOMAIN.my.salesforce.com/services/oauth2/token");
            System.out.println("   - Sandbox: https://YOUR_DOMAIN--SANDBOX.sandbox.my.salesforce.com/services/oauth2/token");
            System.out.println("   - Replace YOUR_DOMAIN with your actual Salesforce domain");
            System.out.println();
            
            System.out.println("3. Verify External Client App Setup:");
            System.out.println("   a. Go to Setup → Apps → App Manager");
            System.out.println("   b. Find your External Client App");
            System.out.println("   c. Verify 'Client Credentials' grant type is selected");
            System.out.println("   d. Verify Permission Set is assigned");
            System.out.println("   e. Check that the app is Active");
            System.out.println();
            
            System.out.println("4. Get Your My Domain URL:");
            System.out.println("   a. Log in to Salesforce");
            System.out.println("   b. Look at the URL in your browser");
            System.out.println("   c. It should look like: https://yourcompany.my.salesforce.com");
            System.out.println("   d. Use this domain in your token URL");
            System.out.println();
            
            // Test connectivity to token endpoint
            System.out.println("Testing connectivity to token endpoint...");
            testConnectivity(tokenUrl);
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testConnectivity(String tokenUrl) {
        try {
            if (debugMode) {
                System.out.println("[DEBUG] ========== Connectivity Test REST API ==========");
                System.out.println("[DEBUG] REST API Endpoint: " + tokenUrl);
                System.out.println("[DEBUG] HTTP Method: GET");
                System.out.println("[DEBUG] Timeout: 5000ms");
                System.out.println();
            }
            
            URL url = new URL(tokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            
            if (debugMode) {
                System.out.println("[DEBUG] Response Code: " + responseCode);
                System.out.println("[DEBUG] Response Message: " + conn.getResponseMessage());
                System.out.println("[DEBUG] ============================================================");
                System.out.println();
            }
            
            System.out.println("  Endpoint reachable: YES");
            System.out.println("  Response code: " + responseCode);
            
            if (responseCode == 405) {
                System.out.println("  [INFO] 405 Method Not Allowed is expected for GET request");
                System.out.println("  [INFO] This means the endpoint exists and is reachable");
            }
            
            conn.disconnect();
        } catch (Exception e) {
            if (debugMode) {
                System.out.println("[DEBUG] Exception Type: " + e.getClass().getName());
                System.out.println("[DEBUG] Exception Message: " + e.getMessage());
                System.out.println("[DEBUG] ============================================================");
                System.out.println();
            }
            
            System.err.println("  Endpoint reachable: NO");
            System.err.println("  Error: " + e.getMessage());
            
            if (debugMode) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }
    
    private static Map<String, String> loadEnvFile() throws IOException {
        Map<String, String> config = new HashMap<>();
        
        File envFile = new File(ENV_FILE);
        if (!envFile.exists()) {
            System.err.println("[ERROR] .env file not found!");
            System.exit(1);
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
    
    private static String maskValue(String value) {
        if (value == null || value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
}

// Made with Bob

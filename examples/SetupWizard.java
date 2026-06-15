import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Interactive Setup Wizard for Salesforce External Client App
 * Guides users through the manual setup process step-by-step
 *
 * Usage:
 *   javac SetupWizard.java
 *   java SetupWizard              # Normal mode
 *   java SetupWizard --debug      # Debug mode with detailed information
 */
public class SetupWizard {
    
    private static final String ENV_FILE = ".env";
    private static final Scanner scanner = new Scanner(System.in);
    private static Map<String, String> config = new HashMap<>();
    private static boolean debugMode = false;
    
    public static void main(String[] args) {
        // Check for debug flag
        if (args.length > 0 && "--debug".equals(args[0])) {
            debugMode = true;
            System.out.println("=== DEBUG MODE ENABLED ===\n");
        }
        
        printHeader();
        
        try {
            // Load existing config if available
            loadExistingConfig();
            
            // Step 1: Get Salesforce Domain
            String domain = getDomain();
            
            // Step 2: Guide through Permission Set creation
            guidePermissionSetCreation();
            
            // Step 3: Guide through External Client App creation
            guideExternalClientAppCreation(domain);
            
            // Step 4: Get credentials
            String clientId = getClientId();
            String clientSecret = getClientSecret();
            
            // Step 5: Guide through Permission Set assignment
            guidePermissionSetAssignment();
            
            // Step 6: Save configuration
            saveConfiguration(domain, clientId, clientSecret);
            
            // Step 7: Test connection
            testConnection();
            
            printSuccess();
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Setup failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static void printHeader() {
        System.out.println("================================================================================");
        System.out.println("    SALESFORCE EXTERNAL CLIENT APP - INTERACTIVE SETUP WIZARD");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("This wizard will guide you through setting up OAuth 2.0 Client Credentials Flow");
        System.out.println("for Salesforce External Client Apps.");
        System.out.println();
        System.out.println("You will need:");
        System.out.println("  - Salesforce System Administrator access");
        System.out.println("  - Your Salesforce org URL");
        System.out.println("  - About 10-15 minutes");
        System.out.println();
        System.out.println("================================================================================");
        System.out.println();
    }
    
    private static void loadExistingConfig() {
        try {
            File envFile = new File(ENV_FILE);
            if (envFile.exists()) {
                if (debugMode) {
                    System.out.println("[DEBUG] Loading existing .env file...");
                }
                
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int equalsIndex = line.indexOf('=');
                        if (equalsIndex > 0) {
                            String key = line.substring(0, equalsIndex).trim();
                            String value = line.substring(equalsIndex + 1).trim();
                            config.put(key, value);
                            lineCount++;
                            if (debugMode) {
                                System.out.println("[DEBUG] Loaded: " + key + " = " + maskValue(value));
                            }
                        }
                    }
                    if (debugMode) {
                        System.out.println("[DEBUG] Loaded " + lineCount + " configuration entries");
                        System.out.println();
                    }
                }
                System.out.println("[INFO] Found existing .env file. Current configuration will be updated.");
                System.out.println();
            } else if (debugMode) {
                System.out.println("[DEBUG] No existing .env file found. Will create new one.");
                System.out.println();
            }
        } catch (IOException e) {
            if (debugMode) {
                System.out.println("[DEBUG] Error loading .env file: " + e.getMessage());
                System.out.println();
            }
            // Ignore, will create new file
        }
    }
    
    private static String getDomain() {
        System.out.println("================================================================================");
        System.out.println("STEP 1: SALESFORCE DOMAIN");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("First, we need your Salesforce domain (My Domain URL).");
        System.out.println();
        System.out.println("To find your domain:");
        System.out.println("  1. Log in to your Salesforce org");
        System.out.println("  2. Look at the URL in your browser");
        System.out.println("  3. It should look like: https://yourcompany.my.salesforce.com");
        System.out.println();
        
        String existingDomain = extractDomainFromConfig();
        if (existingDomain != null) {
            System.out.println("Current domain: " + existingDomain);
            System.out.print("Keep this domain? (Y/n): ");
            String response = scanner.nextLine().trim();
            if (response.isEmpty() || response.equalsIgnoreCase("y")) {
                System.out.println("[OK] Using existing domain: " + existingDomain);
                System.out.println();
                return existingDomain;
            }
        }
        
        while (true) {
            System.out.print("Enter your Salesforce domain URL: ");
            String domain = scanner.nextLine().trim();
            
            if (domain.isEmpty()) {
                System.out.println("[ERROR] Domain cannot be empty. Please try again.");
                continue;
            }
            
            // Clean up the domain
            domain = domain.replace("https://", "").replace("http://", "");
            if (domain.endsWith("/")) {
                domain = domain.substring(0, domain.length() - 1);
            }
            
            // Validate domain format
            if (!domain.contains(".my.salesforce.com") && !domain.contains(".develop.my.salesforce.com")) {
                System.out.println("[WARNING] Domain doesn't look like a My Domain URL.");
                System.out.print("Continue anyway? (y/N): ");
                String response = scanner.nextLine().trim();
                if (!response.equalsIgnoreCase("y")) {
                    continue;
                }
            }
            
            System.out.println("[OK] Domain set to: " + domain);
            System.out.println();
            return domain;
        }
    }
    
    private static String extractDomainFromConfig() {
        String tokenUrl = config.get("SALESFORCE_TOKEN_URL");
        if (tokenUrl != null) {
            tokenUrl = tokenUrl.replace("https://", "").replace("http://", "");
            int slashIndex = tokenUrl.indexOf("/");
            if (slashIndex > 0) {
                return tokenUrl.substring(0, slashIndex);
            }
        }
        return null;
    }
    
    private static void guidePermissionSetCreation() {
        System.out.println("================================================================================");
        System.out.println("STEP 2: CREATE PERMISSION SET");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Now, let's create a Permission Set for API access.");
        System.out.println();
        System.out.println("Follow these steps in Salesforce:");
        System.out.println();
        System.out.println("  1. Click the Setup gear icon (top right)");
        System.out.println("  2. In Quick Find, search for: Permission Sets");
        System.out.println("  3. Click 'New'");
        System.out.println("  4. Enter:");
        System.out.println("     - Label: API Access for OAuth");
        System.out.println("     - API Name: API_Access_for_OAuth");
        System.out.println("  5. Click 'Save'");
        System.out.println();
        System.out.println("  6. Click 'System Permissions'");
        System.out.println("  7. Click 'Edit'");
        System.out.println("  8. Check: 'API Enabled' (CRITICAL!)");
        System.out.println("  9. Check: 'View All Data' (for testing)");
        System.out.println("  10. Click 'Save'");
        System.out.println();
        System.out.println("  11. Click 'Object Settings'");
        System.out.println("  12. Click 'Account'");
        System.out.println("  13. Click 'Edit'");
        System.out.println("  14. Check: Read, Create, Edit permissions");
        System.out.println("  15. Click 'Save'");
        System.out.println();
        
        waitForUserConfirmation("Have you completed creating the Permission Set?");
    }
    
    private static void guideExternalClientAppCreation(String domain) {
        System.out.println("================================================================================");
        System.out.println("STEP 3: CREATE EXTERNAL CLIENT APP");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Now, let's create the External Client App.");
        System.out.println();
        System.out.println("Follow these steps in Salesforce:");
        System.out.println();
        System.out.println("  1. In Quick Find, search for: App Manager");
        System.out.println("  2. Click 'New External Client App' (NOT 'New Connected App')");
        System.out.println("  3. Enter:");
        System.out.println("     - External Client App Name: OAuth Playground");
        System.out.println("     - Description: OAuth 2.0 Client Credentials Flow");
        System.out.println();
        System.out.println("  4. In OAuth Settings:");
        System.out.println("     - Grant Types: Check 'Client Credentials'");
        System.out.println("     - Scopes: Select these:");
        System.out.println("       * Manage user data via APIs (api)");
        System.out.println("       * Perform requests at any time (refresh_token, offline_access)");
        System.out.println("     - Token Endpoint Auth Method: Client Secret (Post)");
        System.out.println();
        System.out.println("  5. Click 'Save'");
        System.out.println();
        System.out.println("  6. WAIT 2-10 minutes for the app to be provisioned");
        System.out.println();
        
        waitForUserConfirmation("Have you completed creating the External Client App and waited for provisioning?");
    }
    
    private static String getClientId() {
        System.out.println("================================================================================");
        System.out.println("STEP 4: GET CONSUMER KEY (CLIENT ID)");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Now, let's get your Consumer Key (Client ID).");
        System.out.println();
        System.out.println("In Salesforce:");
        System.out.println("  1. You should be on the External Client App detail page");
        System.out.println("  2. Find the 'OAuth Settings' section");
        System.out.println("  3. Copy the 'Consumer Key'");
        System.out.println();
        
        String existingClientId = config.get("SALESFORCE_CLIENT_ID");
        if (existingClientId != null && !existingClientId.equals("your_consumer_key_here")) {
            System.out.println("Current Client ID: " + maskValue(existingClientId));
            System.out.print("Keep this Client ID? (Y/n): ");
            String response = scanner.nextLine().trim();
            if (response.isEmpty() || response.equalsIgnoreCase("y")) {
                System.out.println("[OK] Using existing Client ID");
                System.out.println();
                return existingClientId;
            }
        }
        
        while (true) {
            System.out.print("Paste your Consumer Key here: ");
            String clientId = scanner.nextLine().trim();
            
            if (clientId.isEmpty()) {
                System.out.println("[ERROR] Client ID cannot be empty. Please try again.");
                continue;
            }
            
            if (clientId.length() < 50) {
                System.out.println("[WARNING] Client ID seems too short. Typical length is 80+ characters.");
                System.out.print("Continue anyway? (y/N): ");
                String response = scanner.nextLine().trim();
                if (!response.equalsIgnoreCase("y")) {
                    continue;
                }
            }
            
            System.out.println("[OK] Client ID saved: " + maskValue(clientId));
            System.out.println();
            return clientId;
        }
    }
    
    private static String getClientSecret() {
        System.out.println("================================================================================");
        System.out.println("STEP 5: GET CONSUMER SECRET (CLIENT SECRET)");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Now, let's get your Consumer Secret (Client Secret).");
        System.out.println();
        System.out.println("In Salesforce:");
        System.out.println("  1. Click 'Manage Consumer Details' button");
        System.out.println("  2. You may need to verify your identity (check your email)");
        System.out.println("  3. Copy the 'Consumer Secret'");
        System.out.println();
        System.out.println("[SECURITY] Keep this secret secure! Don't share it or commit it to version control.");
        System.out.println();
        
        String existingClientSecret = config.get("SALESFORCE_CLIENT_SECRET");
        if (existingClientSecret != null && !existingClientSecret.equals("your_consumer_secret_here")) {
            System.out.println("Current Client Secret: " + maskValue(existingClientSecret));
            System.out.print("Keep this Client Secret? (Y/n): ");
            String response = scanner.nextLine().trim();
            if (response.isEmpty() || response.equalsIgnoreCase("y")) {
                System.out.println("[OK] Using existing Client Secret");
                System.out.println();
                return existingClientSecret;
            }
        }
        
        while (true) {
            System.out.print("Paste your Consumer Secret here: ");
            String clientSecret = scanner.nextLine().trim();
            
            if (clientSecret.isEmpty()) {
                System.out.println("[ERROR] Client Secret cannot be empty. Please try again.");
                continue;
            }
            
            if (clientSecret.length() < 30) {
                System.out.println("[WARNING] Client Secret seems too short. Typical length is 60+ characters.");
                System.out.print("Continue anyway? (y/N): ");
                String response = scanner.nextLine().trim();
                if (!response.equalsIgnoreCase("y")) {
                    continue;
                }
            }
            
            System.out.println("[OK] Client Secret saved: " + maskValue(clientSecret));
            System.out.println();
            return clientSecret;
        }
    }
    
    private static void guidePermissionSetAssignment() {
        System.out.println("================================================================================");
        System.out.println("STEP 6: ASSIGN PERMISSION SET");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Finally, let's assign the Permission Set to the External Client App.");
        System.out.println();
        System.out.println("In Salesforce:");
        System.out.println("  1. You should still be on the External Client App page");
        System.out.println("  2. Click the 'Manage' button (top right)");
        System.out.println("  3. Click 'Manage Permission Sets'");
        System.out.println("  4. Click 'Assign Permission Sets'");
        System.out.println("  5. Select 'API Access for OAuth'");
        System.out.println("  6. Click 'Assign'");
        System.out.println("  7. Verify it appears in the 'Assigned Permission Sets' list");
        System.out.println();
        
        waitForUserConfirmation("Have you completed assigning the Permission Set?");
    }
    
    private static void saveConfiguration(String domain, String clientId, String clientSecret) throws IOException {
        System.out.println("================================================================================");
        System.out.println("STEP 7: SAVE CONFIGURATION");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Saving configuration to .env file...");
        
        if (debugMode) {
            System.out.println("[DEBUG] Preparing configuration entries...");
        }
        
        config.put("SALESFORCE_CLIENT_ID", clientId);
        config.put("SALESFORCE_CLIENT_SECRET", clientSecret);
        config.put("SALESFORCE_TOKEN_URL", "https://" + domain + "/services/oauth2/token");
        config.put("SALESFORCE_API_VERSION", config.getOrDefault("SALESFORCE_API_VERSION", "v59.0"));
        config.put("LOG_LEVEL", config.getOrDefault("LOG_LEVEL", "INFO"));
        
        if (debugMode) {
            System.out.println("[DEBUG] Writing to file: " + ENV_FILE);
            System.out.println("[DEBUG] Configuration entries: " + config.size());
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ENV_FILE))) {
            writer.println("# Salesforce OAuth 2.0 Client Credentials Configuration");
            writer.println("# Generated by Setup Wizard on " + new Date());
            writer.println();
            writer.println("# Salesforce External Client App Credentials");
            writer.println("SALESFORCE_CLIENT_ID=" + clientId);
            writer.println("SALESFORCE_CLIENT_SECRET=" + clientSecret);
            writer.println();
            writer.println("# Salesforce Instance URL");
            writer.println("SALESFORCE_TOKEN_URL=" + config.get("SALESFORCE_TOKEN_URL"));
            writer.println();
            writer.println("# API Version");
            writer.println("SALESFORCE_API_VERSION=" + config.get("SALESFORCE_API_VERSION"));
            writer.println();
            writer.println("# Logging Level");
            writer.println("LOG_LEVEL=" + config.get("LOG_LEVEL"));
        }
        
        if (debugMode) {
            System.out.println("[DEBUG] File written successfully");
            System.out.println();
        }
        
        System.out.println("[OK] Configuration saved to .env file");
        System.out.println();
        System.out.println("Configuration summary:");
        System.out.println("  Client ID: " + maskValue(clientId));
        System.out.println("  Client Secret: " + maskValue(clientSecret));
        System.out.println("  Token URL: " + config.get("SALESFORCE_TOKEN_URL"));
        System.out.println("  API Version: " + config.get("SALESFORCE_API_VERSION"));
        System.out.println();
    }
    
    private static void testConnection() {
        System.out.println("================================================================================");
        System.out.println("STEP 8: TEST CONNECTION");
        System.out.println("================================================================================");
        System.out.println();
        System.out.print("Would you like to test the connection now? (Y/n): ");
        String response = scanner.nextLine().trim();
        
        if (!response.isEmpty() && !response.equalsIgnoreCase("y")) {
            System.out.println("[SKIPPED] You can test later by running: java SimpleConnectionTest");
            System.out.println();
            return;
        }
        
        System.out.println();
        System.out.println("Testing connection...");
        System.out.println();
        
        try {
            if (debugMode) {
                System.out.println("[DEBUG] Executing: java SimpleConnectionTest");
                System.out.println("[DEBUG] Working directory: " + System.getProperty("user.dir"));
                System.out.println();
            }
            
            // Run SimpleConnectionTest
            ProcessBuilder pb = new ProcessBuilder("java", "SimpleConnectionTest");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            
            int exitCode = process.waitFor();
            System.out.println();
            
            if (debugMode) {
                System.out.println("[DEBUG] Process exit code: " + exitCode);
                System.out.println();
            }
            
            if (exitCode == 0) {
                System.out.println("[SUCCESS] Connection test passed!");
            } else {
                System.out.println("[WARNING] Connection test failed. Please review the output above.");
                System.out.println("You can run the test again with: java SimpleConnectionTest");
            }
            
        } catch (Exception e) {
            if (debugMode) {
                System.out.println("[DEBUG] Exception: " + e.getClass().getName());
                System.out.println("[DEBUG] Message: " + e.getMessage());
                e.printStackTrace();
                System.out.println();
            }
            
            System.out.println("[ERROR] Could not run connection test: " + e.getMessage());
            System.out.println("You can run it manually with: java SimpleConnectionTest");
        }
        
        System.out.println();
    }
    
    private static void printSuccess() {
        System.out.println("================================================================================");
        System.out.println("    SETUP COMPLETE!");
        System.out.println("================================================================================");
        System.out.println();
        System.out.println("Your Salesforce OAuth 2.0 Client Credentials Flow is now configured!");
        System.out.println();
        System.out.println("Next steps:");
        System.out.println("  1. Test connection: java SimpleConnectionTest");
        System.out.println("  2. Run diagnostics: java DiagnosticTest");
        System.out.println("  3. Explore examples: See README.md");
        System.out.println();
        System.out.println("Configuration file: .env");
        System.out.println("Documentation: SETUP-EXTERNAL-CLIENT-APP.md");
        System.out.println();
        System.out.println("================================================================================");
    }
    
    private static void waitForUserConfirmation(String message) {
        System.out.print(message + " (Y/n): ");
        String response = scanner.nextLine().trim();
        
        if (!response.isEmpty() && !response.equalsIgnoreCase("y")) {
            System.out.println();
            System.out.println("[INFO] Please complete the step above before continuing.");
            System.out.print("Ready to continue? (Y/n): ");
            response = scanner.nextLine().trim();
            
            if (!response.isEmpty() && !response.equalsIgnoreCase("y")) {
                System.out.println("[CANCELLED] Setup wizard cancelled. You can restart anytime.");
                System.exit(0);
            }
        }
        
        System.out.println("[OK] Continuing...");
        System.out.println();
    }
    
    private static String maskValue(String value) {
        if (value == null || value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
}

// Made with Bob

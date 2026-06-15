package com.salesforce.playground;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.salesforce.playground.auth.SalesforceAuth;
import com.salesforce.playground.auth.SalesforceAuthException;
import com.salesforce.playground.client.SalesforceClient;
import com.salesforce.playground.client.SalesforceClientException;
import com.salesforce.playground.util.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main playground class for testing Salesforce OAuth 2.0 Client Credentials Flow
 * and API operations.
 */
public class SalesforcePlayground {
    private static final Logger logger = LoggerFactory.getLogger(SalesforcePlayground.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static void main(String[] args) {
        logger.info("=== Salesforce OAuth 2.0 Client Credentials Playground ===");
        
        try {
            // Step 1: Load configuration
            logger.info("\n[Step 1] Loading configuration...");
            if (!ConfigLoader.isConfigured()) {
                logger.error("Configuration is incomplete. Please set up your .env file.");
                logger.error("Copy .env.template to .env and fill in your Salesforce credentials.");
                System.exit(1);
            }
            ConfigLoader.printConfiguration();
            
            // Step 2: Initialize authentication
            logger.info("\n[Step 2] Initializing Salesforce authentication...");
            SalesforceAuth auth = new SalesforceAuth(
                ConfigLoader.getSalesforceClientId(),
                ConfigLoader.getSalesforceClientSecret(),
                ConfigLoader.getSalesforceTokenUrl()
            );
            
            // Step 3: Authenticate
            logger.info("\n[Step 3] Authenticating with Salesforce...");
            SalesforceAuth.AuthenticationResponse authResponse = auth.authenticate();
            logger.info("✓ Authentication successful!");
            logger.info("  Instance URL: {}", authResponse.getInstanceUrl());
            
            // Step 4: Initialize Salesforce client
            logger.info("\n[Step 4] Initializing Salesforce client...");
            SalesforceClient client = new SalesforceClient(auth, ConfigLoader.getSalesforceApiVersion());
            logger.info("✓ Client initialized");
            
            // Step 5: Run example operations
            logger.info("\n[Step 5] Running example operations...");
            runExampleOperations(client);
            
            logger.info("\n=== Playground execution completed successfully! ===");
            
        } catch (SalesforceAuthException e) {
            logger.error("\n❌ Authentication failed: {}", e.getMessage());
            logger.error("Please check your credentials and try again.");
            System.exit(1);
        } catch (SalesforceClientException e) {
            logger.error("\n❌ API operation failed: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("\n❌ Unexpected error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    /**
     * Runs example Salesforce API operations.
     */
    private static void runExampleOperations(SalesforceClient client) 
            throws SalesforceClientException {
        
        // Example 1: Get API versions
        logger.info("\n--- Example 1: Get API Versions ---");
        try {
            String versions = client.getApiVersions();
            logger.info("Available API versions:\n{}", versions);
        } catch (Exception e) {
            logger.error("Failed to get API versions: {}", e.getMessage());
        }
        
        // Example 2: Get organization limits
        logger.info("\n--- Example 2: Get Organization Limits ---");
        try {
            JsonObject limits = client.getLimits();
            logger.info("Organization limits:");
            logger.info("  Daily API Requests: {}/{}", 
                       limits.getAsJsonObject("DailyApiRequests").get("Remaining"),
                       limits.getAsJsonObject("DailyApiRequests").get("Max"));
            logger.info("  Concurrent API Requests: {}/{}", 
                       limits.getAsJsonObject("ConcurrentAsyncGetReportInstances").get("Remaining"),
                       limits.getAsJsonObject("ConcurrentAsyncGetReportInstances").get("Max"));
        } catch (Exception e) {
            logger.error("Failed to get limits: {}", e.getMessage());
        }
        
        // Example 3: Query Account records
        logger.info("\n--- Example 3: Query Account Records ---");
        try {
            JsonObject queryResult = client.query("SELECT Id, Name, Industry FROM Account LIMIT 5");
            int totalSize = queryResult.get("totalSize").getAsInt();
            logger.info("Found {} Account records", totalSize);
            
            if (totalSize > 0) {
                logger.info("Sample accounts:");
                queryResult.getAsJsonArray("records").forEach(record -> {
                    JsonObject acc = record.getAsJsonObject();
                    String id = acc.get("Id").getAsString();
                    String name = acc.get("Name").getAsString();
                    String industry = acc.has("Industry") && !acc.get("Industry").isJsonNull() 
                                    ? acc.get("Industry").getAsString() 
                                    : "N/A";
                    logger.info("  - {} | {} | {}", id, name, industry);
                });
            }
        } catch (Exception e) {
            logger.error("Failed to query accounts: {}", e.getMessage());
        }
        
        // Example 4: Describe Account object
        logger.info("\n--- Example 4: Describe Account Object ---");
        try {
            JsonObject describe = client.describeSObject("Account");
            logger.info("Account object metadata:");
            logger.info("  Label: {}", describe.get("label").getAsString());
            logger.info("  API Name: {}", describe.get("name").getAsString());
            logger.info("  Createable: {}", describe.get("createable").getAsBoolean());
            logger.info("  Updateable: {}", describe.get("updateable").getAsBoolean());
            logger.info("  Deletable: {}", describe.get("deletable").getAsBoolean());
            logger.info("  Number of fields: {}", describe.getAsJsonArray("fields").size());
        } catch (Exception e) {
            logger.error("Failed to describe Account: {}", e.getMessage());
        }
        
        // Example 5: Create, Update, and Delete a test record
        logger.info("\n--- Example 5: Create, Update, and Delete Test Record ---");
        String testAccountId = null;
        try {
            // Create
            logger.info("Creating test Account...");
            JsonObject newAccount = new JsonObject();
            newAccount.addProperty("Name", "Test Account - OAuth Playground");
            newAccount.addProperty("Industry", "Technology");
            newAccount.addProperty("Description", "Created by Salesforce OAuth 2.0 Playground");
            
            JsonObject createResponse = client.createRecord("Account", newAccount);
            testAccountId = createResponse.get("id").getAsString();
            logger.info("✓ Created Account with ID: {}", testAccountId);
            
            // Read
            logger.info("Reading created Account...");
            JsonObject account = client.getRecord("Account", testAccountId);
            logger.info("✓ Retrieved Account: {}", account.get("Name").getAsString());
            
            // Update
            logger.info("Updating Account...");
            JsonObject updateData = new JsonObject();
            updateData.addProperty("Industry", "Software");
            updateData.addProperty("Description", "Updated by Salesforce OAuth 2.0 Playground");
            client.updateRecord("Account", testAccountId, updateData);
            logger.info("✓ Updated Account");
            
            // Verify update
            JsonObject updatedAccount = client.getRecord("Account", testAccountId);
            logger.info("✓ Verified update - Industry: {}", 
                       updatedAccount.get("Industry").getAsString());
            
            // Delete
            logger.info("Deleting test Account...");
            client.deleteRecord("Account", testAccountId);
            logger.info("✓ Deleted Account with ID: {}", testAccountId);
            testAccountId = null; // Mark as deleted
            
        } catch (Exception e) {
            logger.error("Failed during CRUD operations: {}", e.getMessage());
            
            // Cleanup: Try to delete the test record if it was created
            if (testAccountId != null) {
                try {
                    logger.info("Attempting cleanup - deleting test Account...");
                    client.deleteRecord("Account", testAccountId);
                    logger.info("✓ Cleanup successful");
                } catch (Exception cleanupError) {
                    logger.error("Failed to cleanup test record: {}", cleanupError.getMessage());
                }
            }
        }
        
        // Example 6: Query with more complex SOQL
        logger.info("\n--- Example 6: Complex SOQL Query ---");
        try {
            String soql = "SELECT Id, Name, (SELECT Id, FirstName, LastName FROM Contacts LIMIT 3) " +
                         "FROM Account WHERE Industry != null LIMIT 3";
            JsonObject queryResult = client.query(soql);
            logger.info("Query result:\n{}", gson.toJson(queryResult));
        } catch (Exception e) {
            logger.error("Failed to execute complex query: {}", e.getMessage());
        }
    }
}

// Made with Bob

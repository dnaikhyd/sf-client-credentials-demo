package com.salesforce.playground.util;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Utility class for loading configuration from environment variables and .env file.
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    
    private static Dotenv dotenv;
    
    static {
        try {
            // Try to load .env file from current directory
            File envFile = new File(".env");
            if (envFile.exists()) {
                dotenv = Dotenv.configure()
                              .directory(".")
                              .ignoreIfMissing()
                              .load();
                logger.info("Loaded configuration from .env file");
            } else {
                logger.warn(".env file not found. Using system environment variables only.");
                dotenv = Dotenv.configure()
                              .ignoreIfMissing()
                              .load();
            }
        } catch (Exception e) {
            logger.error("Failed to load .env file", e);
            dotenv = Dotenv.configure()
                          .ignoreIfMissing()
                          .load();
        }
    }
    
    /**
     * Gets a configuration value by key.
     * First checks .env file, then falls back to system environment variables.
     *
     * @param key The configuration key
     * @return The configuration value, or null if not found
     */
    public static String get(String key) {
        String value = dotenv.get(key);
        if (value == null) {
            value = System.getenv(key);
        }
        return value;
    }
    
    /**
     * Gets a configuration value by key with a default value.
     *
     * @param key          The configuration key
     * @param defaultValue The default value if key is not found
     * @return The configuration value, or defaultValue if not found
     */
    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Gets a required configuration value. Throws exception if not found.
     *
     * @param key The configuration key
     * @return The configuration value
     * @throws IllegalStateException if the key is not found
     */
    public static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                String.format("Required configuration '%s' is not set. " +
                            "Please set it in .env file or as environment variable.", key)
            );
        }
        return value;
    }
    
    /**
     * Gets the Salesforce client ID.
     *
     * @return The client ID
     * @throws IllegalStateException if not configured
     */
    public static String getSalesforceClientId() {
        return getRequired("SALESFORCE_CLIENT_ID");
    }
    
    /**
     * Gets the Salesforce client secret.
     *
     * @return The client secret
     * @throws IllegalStateException if not configured
     */
    public static String getSalesforceClientSecret() {
        return getRequired("SALESFORCE_CLIENT_SECRET");
    }
    
    /**
     * Gets the Salesforce token URL.
     *
     * @return The token URL
     * @throws IllegalStateException if not configured
     */
    public static String getSalesforceTokenUrl() {
        return getRequired("SALESFORCE_TOKEN_URL");
    }
    
    /**
     * Gets the Salesforce API version.
     *
     * @return The API version (defaults to v59.0)
     */
    public static String getSalesforceApiVersion() {
        return get("SALESFORCE_API_VERSION", "v59.0");
    }
    
    /**
     * Gets the log level.
     *
     * @return The log level (defaults to INFO)
     */
    public static String getLogLevel() {
        return get("LOG_LEVEL", "INFO");
    }
    
    /**
     * Checks if all required Salesforce configurations are set.
     *
     * @return true if all required configs are set, false otherwise
     */
    public static boolean isConfigured() {
        try {
            getSalesforceClientId();
            getSalesforceClientSecret();
            getSalesforceTokenUrl();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
    
    /**
     * Prints the current configuration (masks sensitive values).
     */
    public static void printConfiguration() {
        logger.info("=== Salesforce Configuration ===");
        logger.info("Client ID: {}", maskSensitiveValue(get("SALESFORCE_CLIENT_ID")));
        logger.info("Client Secret: {}", maskSensitiveValue(get("SALESFORCE_CLIENT_SECRET")));
        logger.info("Token URL: {}", get("SALESFORCE_TOKEN_URL"));
        logger.info("API Version: {}", getSalesforceApiVersion());
        logger.info("Log Level: {}", getLogLevel());
        logger.info("================================");
    }
    
    /**
     * Masks a sensitive value for logging.
     */
    private static String maskSensitiveValue(String value) {
        if (value == null || value.isEmpty()) {
            return "<not set>";
        }
        if (value.length() <= 8) {
            return "****";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
    }
}

// Made with Bob

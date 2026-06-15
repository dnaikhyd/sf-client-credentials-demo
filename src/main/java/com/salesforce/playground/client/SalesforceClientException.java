package com.salesforce.playground.client;

/**
 * Exception thrown when Salesforce API operations fail.
 */
public class SalesforceClientException extends Exception {
    
    /**
     * Constructs a new SalesforceClientException with the specified detail message.
     *
     * @param message the detail message
     */
    public SalesforceClientException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new SalesforceClientException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public SalesforceClientException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob

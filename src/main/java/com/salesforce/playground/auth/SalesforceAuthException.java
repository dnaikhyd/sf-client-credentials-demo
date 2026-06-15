package com.salesforce.playground.auth;

/**
 * Exception thrown when Salesforce authentication fails.
 */
public class SalesforceAuthException extends Exception {
    
    /**
     * Constructs a new SalesforceAuthException with the specified detail message.
     *
     * @param message the detail message
     */
    public SalesforceAuthException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new SalesforceAuthException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public SalesforceAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}

// Made with Bob

package com.salesforce.playground.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles OAuth 2.0 Client Credentials Flow authentication with Salesforce.
 * This class manages the authentication process and token lifecycle.
 */
public class SalesforceAuth {
    private static final Logger logger = LoggerFactory.getLogger(SalesforceAuth.class);
    
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;
    
    private String accessToken;
    private String instanceUrl;
    private long tokenExpiryTime;
    
    /**
     * Creates a new SalesforceAuth instance.
     *
     * @param clientId     The Consumer Key from Salesforce Connected App
     * @param clientSecret The Consumer Secret from Salesforce Connected App
     * @param tokenUrl     The Salesforce token endpoint URL
     */
    public SalesforceAuth(String clientId, String clientSecret, String tokenUrl) {
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalArgumentException("Client ID cannot be null or empty");
        }
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new IllegalArgumentException("Client Secret cannot be null or empty");
        }
        if (tokenUrl == null || tokenUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Token URL cannot be null or empty");
        }
        
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
    }
    
    /**
     * Authenticates with Salesforce using OAuth 2.0 Client Credentials Flow.
     * This method obtains an access token that can be used for API calls.
     *
     * @return AuthenticationResponse containing access token and instance URL
     * @throws SalesforceAuthException if authentication fails
     */
    public AuthenticationResponse authenticate() throws SalesforceAuthException {
        logger.info("Initiating OAuth 2.0 Client Credentials Flow authentication");
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(tokenUrl);
            
            // Prepare form parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "client_credentials"));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            
            logger.debug("Sending authentication request to: {}", tokenUrl);
            
            // Execute request
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (statusCode == 200) {
                return handleSuccessfulAuthentication(responseBody);
            } else {
                return handleFailedAuthentication(statusCode, responseBody);
            }
            
        } catch (IOException e) {
            logger.error("IO Exception during authentication", e);
            throw new SalesforceAuthException("Failed to authenticate with Salesforce: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handles successful authentication response.
     */
    private AuthenticationResponse handleSuccessfulAuthentication(String responseBody) {
        logger.debug("Parsing authentication response");
        
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        
        this.accessToken = jsonResponse.get("access_token").getAsString();
        this.instanceUrl = jsonResponse.get("instance_url").getAsString();
        
        // Set token expiry time (default to 2 hours if not provided)
        if (jsonResponse.has("expires_in")) {
            int expiresIn = jsonResponse.get("expires_in").getAsInt();
            this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L);
        } else {
            this.tokenExpiryTime = System.currentTimeMillis() + (2 * 60 * 60 * 1000L); // 2 hours
        }
        
        logger.info("Authentication successful!");
        logger.info("Instance URL: {}", instanceUrl);
        logger.debug("Access token obtained (expires in {} seconds)", 
                    (tokenExpiryTime - System.currentTimeMillis()) / 1000);
        
        return new AuthenticationResponse(accessToken, instanceUrl, tokenExpiryTime);
    }
    
    /**
     * Handles failed authentication response.
     */
    private AuthenticationResponse handleFailedAuthentication(int statusCode, String responseBody) 
            throws SalesforceAuthException {
        logger.error("Authentication failed with status code: {}", statusCode);
        logger.error("Response body: {}", responseBody);
        
        String errorMessage = "Authentication failed";
        String errorDescription = responseBody;
        
        try {
            JsonObject errorJson = JsonParser.parseString(responseBody).getAsJsonObject();
            if (errorJson.has("error")) {
                errorMessage = errorJson.get("error").getAsString();
            }
            if (errorJson.has("error_description")) {
                errorDescription = errorJson.get("error_description").getAsString();
            }
        } catch (Exception e) {
            logger.debug("Could not parse error response as JSON", e);
        }
        
        throw new SalesforceAuthException(
            String.format("Authentication failed (HTTP %d): %s - %s", 
                         statusCode, errorMessage, errorDescription)
        );
    }
    
    /**
     * Checks if the current access token is valid (not expired).
     *
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid() {
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }
        
        // Add 5-minute buffer before actual expiry
        long bufferTime = 5 * 60 * 1000L;
        return System.currentTimeMillis() < (tokenExpiryTime - bufferTime);
    }
    
    /**
     * Gets the current access token. If the token is expired or not available,
     * it will automatically re-authenticate.
     *
     * @return The current valid access token
     * @throws SalesforceAuthException if authentication fails
     */
    public String getAccessToken() throws SalesforceAuthException {
        if (!isTokenValid()) {
            logger.info("Access token is invalid or expired, re-authenticating...");
            authenticate();
        }
        return accessToken;
    }
    
    /**
     * Gets the Salesforce instance URL.
     *
     * @return The instance URL
     */
    public String getInstanceUrl() {
        return instanceUrl;
    }
    
    /**
     * Clears the current authentication state.
     */
    public void clearAuthentication() {
        logger.info("Clearing authentication state");
        this.accessToken = null;
        this.instanceUrl = null;
        this.tokenExpiryTime = 0;
    }
    
    /**
     * Response object containing authentication details.
     */
    public static class AuthenticationResponse {
        private final String accessToken;
        private final String instanceUrl;
        private final long expiryTime;
        
        public AuthenticationResponse(String accessToken, String instanceUrl, long expiryTime) {
            this.accessToken = accessToken;
            this.instanceUrl = instanceUrl;
            this.expiryTime = expiryTime;
        }
        
        public String getAccessToken() {
            return accessToken;
        }
        
        public String getInstanceUrl() {
            return instanceUrl;
        }
        
        public long getExpiryTime() {
            return expiryTime;
        }
        
        @Override
        public String toString() {
            return String.format("AuthenticationResponse{instanceUrl='%s', expiryTime=%d}", 
                               instanceUrl, expiryTime);
        }
    }
}

// Made with Bob

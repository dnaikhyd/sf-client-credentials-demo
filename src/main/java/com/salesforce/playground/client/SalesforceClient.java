package com.salesforce.playground.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.salesforce.playground.auth.SalesforceAuth;
import com.salesforce.playground.auth.SalesforceAuthException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Client for making API calls to Salesforce.
 * Handles authentication and provides methods for common Salesforce operations.
 */
public class SalesforceClient {
    private static final Logger logger = LoggerFactory.getLogger(SalesforceClient.class);
    
    private final SalesforceAuth auth;
    private final String apiVersion;
    private final Gson gson;
    
    /**
     * Creates a new SalesforceClient with the specified authentication.
     *
     * @param auth       The SalesforceAuth instance for authentication
     * @param apiVersion The Salesforce API version (e.g., "v59.0")
     */
    public SalesforceClient(SalesforceAuth auth, String apiVersion) {
        if (auth == null) {
            throw new IllegalArgumentException("SalesforceAuth cannot be null");
        }
        if (apiVersion == null || apiVersion.trim().isEmpty()) {
            throw new IllegalArgumentException("API version cannot be null or empty");
        }
        
        this.auth = auth;
        this.apiVersion = apiVersion;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }
    
    /**
     * Creates a new SalesforceClient with default API version (v59.0).
     *
     * @param auth The SalesforceAuth instance for authentication
     */
    public SalesforceClient(SalesforceAuth auth) {
        this(auth, "v59.0");
    }
    
    /**
     * Executes a SOQL query.
     *
     * @param query The SOQL query string
     * @return The query result as a JsonObject
     * @throws SalesforceClientException if the query fails
     */
    public JsonObject query(String query) throws SalesforceClientException {
        logger.info("Executing SOQL query: {}", query);
        
        String endpoint = String.format("/services/data/%s/query?q=%s", 
                                       apiVersion, 
                                       encodeQueryString(query));
        
        return executeRequest(HttpMethod.GET, endpoint, null);
    }
    
    /**
     * Retrieves a record by ID.
     *
     * @param sobjectType The SObject type (e.g., "Account", "Contact")
     * @param recordId    The record ID
     * @return The record as a JsonObject
     * @throws SalesforceClientException if the retrieval fails
     */
    public JsonObject getRecord(String sobjectType, String recordId) throws SalesforceClientException {
        logger.info("Retrieving {} record with ID: {}", sobjectType, recordId);
        
        String endpoint = String.format("/services/data/%s/sobjects/%s/%s", 
                                       apiVersion, sobjectType, recordId);
        
        return executeRequest(HttpMethod.GET, endpoint, null);
    }
    
    /**
     * Creates a new record.
     *
     * @param sobjectType The SObject type (e.g., "Account", "Contact")
     * @param recordData  The record data as a JsonObject
     * @return The creation response containing the new record ID
     * @throws SalesforceClientException if the creation fails
     */
    public JsonObject createRecord(String sobjectType, JsonObject recordData) throws SalesforceClientException {
        logger.info("Creating {} record", sobjectType);
        
        String endpoint = String.format("/services/data/%s/sobjects/%s", 
                                       apiVersion, sobjectType);
        
        return executeRequest(HttpMethod.POST, endpoint, recordData.toString());
    }
    
    /**
     * Updates an existing record.
     *
     * @param sobjectType The SObject type (e.g., "Account", "Contact")
     * @param recordId    The record ID
     * @param recordData  The updated record data as a JsonObject
     * @throws SalesforceClientException if the update fails
     */
    public void updateRecord(String sobjectType, String recordId, JsonObject recordData) 
            throws SalesforceClientException {
        logger.info("Updating {} record with ID: {}", sobjectType, recordId);
        
        String endpoint = String.format("/services/data/%s/sobjects/%s/%s", 
                                       apiVersion, sobjectType, recordId);
        
        executeRequest(HttpMethod.PATCH, endpoint, recordData.toString());
    }
    
    /**
     * Deletes a record.
     *
     * @param sobjectType The SObject type (e.g., "Account", "Contact")
     * @param recordId    The record ID
     * @throws SalesforceClientException if the deletion fails
     */
    public void deleteRecord(String sobjectType, String recordId) throws SalesforceClientException {
        logger.info("Deleting {} record with ID: {}", sobjectType, recordId);
        
        String endpoint = String.format("/services/data/%s/sobjects/%s/%s", 
                                       apiVersion, sobjectType, recordId);
        
        executeRequest(HttpMethod.DELETE, endpoint, null);
    }
    
    /**
     * Describes an SObject type.
     *
     * @param sobjectType The SObject type (e.g., "Account", "Contact")
     * @return The describe metadata as a JsonObject
     * @throws SalesforceClientException if the describe fails
     */
    public JsonObject describeSObject(String sobjectType) throws SalesforceClientException {
        logger.info("Describing {} SObject", sobjectType);
        
        String endpoint = String.format("/services/data/%s/sobjects/%s/describe", 
                                       apiVersion, sobjectType);
        
        return executeRequest(HttpMethod.GET, endpoint, null);
    }
    
    /**
     * Gets available API versions.
     *
     * @return The available versions as a JsonObject
     * @throws SalesforceClientException if the request fails
     */
    public String getApiVersions() throws SalesforceClientException {
        logger.info("Retrieving available API versions");
        
        String endpoint = "/services/data/";
        JsonObject response = executeRequest(HttpMethod.GET, endpoint, null);
        
        return gson.toJson(response);
    }
    
    /**
     * Gets organization limits.
     *
     * @return The limits as a JsonObject
     * @throws SalesforceClientException if the request fails
     */
    public JsonObject getLimits() throws SalesforceClientException {
        logger.info("Retrieving organization limits");
        
        String endpoint = String.format("/services/data/%s/limits", apiVersion);
        
        return executeRequest(HttpMethod.GET, endpoint, null);
    }
    
    /**
     * Executes a custom REST API request.
     *
     * @param method   The HTTP method
     * @param endpoint The API endpoint (relative to instance URL)
     * @param body     The request body (can be null for GET/DELETE)
     * @return The response as a JsonObject
     * @throws SalesforceClientException if the request fails
     */
    public JsonObject executeCustomRequest(HttpMethod method, String endpoint, String body) 
            throws SalesforceClientException {
        return executeRequest(method, endpoint, body);
    }
    
    /**
     * Internal method to execute HTTP requests.
     */
    private JsonObject executeRequest(HttpMethod method, String endpoint, String body) 
            throws SalesforceClientException {
        try {
            String accessToken = auth.getAccessToken();
            String instanceUrl = auth.getInstanceUrl();
            
            if (instanceUrl == null) {
                throw new SalesforceClientException("Instance URL is not available. Please authenticate first.");
            }
            
            String fullUrl = instanceUrl + endpoint;
            logger.debug("Executing {} request to: {}", method, fullUrl);
            
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpRequestBase request = createHttpRequest(method, fullUrl);
                
                // Set headers
                request.setHeader("Authorization", "Bearer " + accessToken);
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Accept", "application/json");
                
                // Set body for POST/PATCH/PUT
                if (body != null && request instanceof HttpEntityEnclosingRequestBase) {
                    ((HttpEntityEnclosingRequestBase) request).setEntity(
                        new StringEntity(body, StandardCharsets.UTF_8)
                    );
                }
                
                // Execute request
                HttpResponse response = httpClient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null ? 
                                     EntityUtils.toString(response.getEntity()) : "";
                
                logger.debug("Response status code: {}", statusCode);
                
                // Handle response
                if (statusCode >= 200 && statusCode < 300) {
                    if (responseBody.isEmpty()) {
                        return new JsonObject(); // Empty response for successful DELETE/PATCH
                    }
                    return JsonParser.parseString(responseBody).getAsJsonObject();
                } else {
                    handleErrorResponse(statusCode, responseBody);
                    return null; // Never reached
                }
            }
            
        } catch (SalesforceAuthException e) {
            logger.error("Authentication error", e);
            throw new SalesforceClientException("Authentication failed: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IO error during API request", e);
            throw new SalesforceClientException("Failed to execute API request: " + e.getMessage(), e);
        }
    }
    
    /**
     * Creates the appropriate HttpRequest based on the method.
     */
    private HttpRequestBase createHttpRequest(HttpMethod method, String url) {
        switch (method) {
            case GET:
                return new HttpGet(url);
            case POST:
                return new HttpPost(url);
            case PUT:
                return new HttpPut(url);
            case PATCH:
                return new HttpPatch(url);
            case DELETE:
                return new HttpDelete(url);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
    
    /**
     * Handles error responses from Salesforce.
     */
    private void handleErrorResponse(int statusCode, String responseBody) throws SalesforceClientException {
        logger.error("API request failed with status code: {}", statusCode);
        logger.error("Response body: {}", responseBody);
        
        String errorMessage = "API request failed";
        
        try {
            JsonObject errorJson = JsonParser.parseString(responseBody).getAsJsonObject();
            if (errorJson.has("message")) {
                errorMessage = errorJson.get("message").getAsString();
            } else if (errorJson.isJsonArray()) {
                // Salesforce sometimes returns an array of errors
                errorMessage = responseBody;
            }
        } catch (Exception e) {
            logger.debug("Could not parse error response as JSON", e);
            errorMessage = responseBody;
        }
        
        throw new SalesforceClientException(
            String.format("API request failed (HTTP %d): %s", statusCode, errorMessage)
        );
    }
    
    /**
     * URL encodes a query string.
     */
    private String encodeQueryString(String query) {
        try {
            return java.net.URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            logger.warn("Failed to encode query string, using original", e);
            return query;
        }
    }
    
    /**
     * HTTP methods supported by the client.
     */
    public enum HttpMethod {
        GET, POST, PUT, PATCH, DELETE
    }
}

// Made with Bob

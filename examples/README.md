# Standalone Examples

These examples demonstrate Salesforce OAuth 2.0 Client Credentials Flow and REST API operations. They can be run **without Maven** - perfect for learning and quick testing!

## 📋 Prerequisites

- **Java 11 or higher**
  ```bash
  java -version
  ```
- **Salesforce account** with External Client App configured
- **Configured .env file** in the project root directory

## 🚀 Quick Start

### 1. Configure Credentials

Ensure you have a `.env` file in the project root (one level up from this directory):

```bash
cd ..
cp .env.template .env
# Edit .env with your Salesforce credentials
```

### 2. Compile an Example

```bash
cd examples
javac SimpleConnectionTest.java
```

### 3. Run the Example

```bash
# Normal mode
java SimpleConnectionTest

# Debug mode (shows REST API details)
java SimpleConnectionTest --debug
```

## 📚 Available Examples

### 1. SimpleConnectionTest.java

**Purpose**: Test basic OAuth 2.0 authentication and API connectivity

**What it does**:
- Authenticates with Salesforce using Client Credentials Flow
- Tests API connection by fetching available API versions
- Validates credentials and configuration

**Usage**:
```bash
javac SimpleConnectionTest.java
java SimpleConnectionTest              # Normal mode
java SimpleConnectionTest --debug      # Debug mode with REST API details
```

**Expected Output**:
```
=== Salesforce OAuth 2.0 Client Credentials Connection Test ===

Configuration loaded:
  Client ID: 3MVG****xyz123
  Client Secret: 1234****5678
  Token URL: https://your-domain.my.salesforce.com/services/oauth2/token

[Step 1] Authenticating with Salesforce...
[SUCCESS] Authentication successful!
  Access Token: 00D****xyz
  Instance URL: https://your-instance.salesforce.com
  Token Type: Bearer

[Step 2] Testing API connection...
[SUCCESS] API connection successful!
  Response Code: 200
  Available API versions:
    - v31.0
    - v32.0
    - v33.0

=== Connection test completed successfully! ===
```

---

### 2. AccountReader.java

**Purpose**: Query and display Account records with detailed REST API logging

**What it does**:
- Authenticates with Salesforce
- Executes SOQL query to retrieve 10 Account records
- Displays results in a formatted table
- Shows complete REST API details in debug mode

**Usage**:
```bash
javac AccountReader.java
java AccountReader              # Normal mode
java AccountReader --debug      # Debug mode with REST API details
```

**Expected Output**:
```
=== Salesforce Account Reader ===

[Step 1] Authenticating with Salesforce...
[SUCCESS] Authentication successful!

[Step 2] Querying 10 Account records...

Total Accounts found: 10

========================================================================================================
 #  | Account Name                    | Industry        | Type        | Phone          | City/State
========================================================================================================
 1  | Acme Corporation               | Technology      | Customer    | 555-0001       | San Francisco, CA
 2  | Global Media                   | Media           | Partner     | 555-0002       | New York, NY
 ...
========================================================================================================

Displayed 10 records

=== Account read completed! ===
```

---

### 3. BulkDataLoader.java

**Purpose**: Create sample data in Salesforce for testing

**What it does**:
- Authenticates with Salesforce
- Creates 10 Account records
- Creates 10 Contact records
- Creates 10 Product (Product2) records
- Shows progress for each record created

**Usage**:
```bash
javac BulkDataLoader.java
java BulkDataLoader              # Normal mode
java BulkDataLoader --debug      # Debug mode with REST API details
```

**Expected Output**:
```
=== Salesforce Bulk Data Loader ===

[Step 1] Authenticating with Salesforce...
[SUCCESS] Authentication successful!

[Step 2] Creating 10 Account records...
  ✓ Created Account 1
  ✓ Created Account 2
  ...
[SUCCESS] Created 10 Accounts

[Step 3] Creating 10 Contact records...
  ✓ Created Contact: John Smith
  ✓ Created Contact: Jane Johnson
  ...
[SUCCESS] Created 10 Contacts

[Step 4] Creating 10 Product records...
  ✓ Created Product: Enterprise Software License
  ✓ Created Product: Cloud Storage Plan
  ...
[SUCCESS] Created 10 Products

=== Summary ===
Accounts created: 10
Contacts created: 10
Products created: 10

=== Bulk data load completed! ===
```

---

### 4. DiagnosticTest.java

**Purpose**: Comprehensive diagnostic and troubleshooting tool

**What it does**:
- Validates .env file configuration
- Analyzes token URL format
- Tests endpoint connectivity
- Provides troubleshooting recommendations
- Shows common issues and solutions

**Usage**:
```bash
javac DiagnosticTest.java
java DiagnosticTest              # Normal mode
java DiagnosticTest --debug      # Debug mode with detailed info
```

**Expected Output**:
```
=== Salesforce OAuth 2.0 Client Credentials Diagnostic Tool ===

Configuration Check:
  Client ID: 3MVG****xyz123
  Client Secret: 1234****5678
  Token URL: https://your-domain.my.salesforce.com/services/oauth2/token

Token URL Analysis:
  Type: My Domain
  [INFO] You're using a My Domain URL. This is correct for External Client Apps.

Common Issues and Solutions:
[... detailed troubleshooting guide ...]

Testing connectivity to token endpoint...
  Endpoint reachable: YES
  Response code: 400
```

---

### 5. SetupWizard.java

**Purpose**: Interactive setup assistant for first-time configuration

**What it does**:
- Guides through Salesforce domain setup
- Provides step-by-step External Client App creation instructions
- Helps configure .env file
- Tests connection after setup
- Validates configuration

**Usage**:
```bash
javac SetupWizard.java
java SetupWizard              # Normal mode
java SetupWizard --debug      # Debug mode with detailed info
```

**Interactive Prompts**:
```
================================================================================
    SALESFORCE EXTERNAL CLIENT APP - INTERACTIVE SETUP WIZARD
================================================================================

This wizard will guide you through setting up OAuth 2.0 Client Credentials Flow
for Salesforce External Client Apps.

You will need:
  - Salesforce System Administrator access
  - Your Salesforce org URL
  - About 10-15 minutes

[... interactive setup process ...]
```

---

## 🐛 Debug Mode

All examples support `--debug` flag for detailed REST API logging:

```bash
java SimpleConnectionTest --debug
```

**Debug output includes**:
- Full REST API endpoint URLs
- HTTP methods (GET, POST)
- Request headers
- Request body (with masked credentials)
- Response codes
- Response body (with masked sensitive data)
- Exception details

**Example debug output**:
```
[DEBUG] ========== OAuth 2.0 Authentication REST API ==========
[DEBUG] REST API Endpoint: https://your-domain.my.salesforce.com/services/oauth2/token
[DEBUG] HTTP Method: POST
[DEBUG] Content-Type: application/x-www-form-urlencoded
[DEBUG] Request Body: grant_type=client_credentials&client_id=3MVG****&client_secret=****

[DEBUG] Response Code: 200
[DEBUG] Response Body: {"access_token":"****MASKED****","instance_url":"..."}
[DEBUG] ============================================================
```

---

## 🔧 Troubleshooting

### Compilation Errors

**Problem**: `javac: command not found`

**Solution**: Install Java JDK 11 or higher and ensure it's in your PATH

---

**Problem**: `error: cannot find symbol`

**Solution**: Ensure you're compiling from the `examples/` directory

---

### Runtime Errors

**Problem**: `.env file not found!`

**Solution**: The .env file must be in the project root (parent directory of examples/)
```bash
cd ..
cp .env.template .env
# Edit .env with your credentials
cd examples
```

---

**Problem**: `Authentication failed`

**Solution**: 
1. Verify credentials in .env file
2. Run DiagnosticTest.java for detailed analysis
3. Check External Client App configuration in Salesforce

---

**Problem**: `insufficient_access` error

**Solution**:
1. Ensure Permission Set is created with "API Enabled"
2. Assign Permission Set to External Client App
3. Grant necessary object permissions in Permission Set

---

## 📖 Learning Path

**Recommended order for learning**:

1. **Start here**: `SimpleConnectionTest.java`
   - Understand basic OAuth flow
   - Verify your setup works

2. **Learn REST APIs**: `AccountReader.java`
   - See how SOQL queries work
   - Understand REST API structure

3. **Create test data**: `BulkDataLoader.java`
   - Learn record creation
   - Understand different object types

4. **Troubleshoot**: `DiagnosticTest.java`
   - When things go wrong
   - Understand common issues

5. **Setup help**: `SetupWizard.java`
   - For initial configuration
   - Step-by-step guidance

---

## 🔑 Key Concepts Demonstrated

### OAuth 2.0 Client Credentials Flow
- No user interaction required
- Uses client ID and secret
- Suitable for server-to-server integration

### Salesforce REST API
- Token endpoint for authentication
- Query API for SOQL
- SObject API for CRUD operations

### Best Practices
- Credential masking in logs
- Error handling
- Debug logging
- Configuration management

---

## 📚 Additional Resources

- [Salesforce OAuth Documentation](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_flows.htm)
- [Salesforce REST API Guide](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/)
- [External Client Apps](https://help.salesforce.com/s/articleView?id=sf.external_client_apps.htm)
- [SOQL Reference](https://developer.salesforce.com/docs/atlas.en-us.soql_sosl.meta/soql_sosl/)

---

## 🤝 Contributing

Found a bug or want to add an example? See [CONTRIBUTING.md](../CONTRIBUTING.md)

---

**Happy Learning! 🚀**
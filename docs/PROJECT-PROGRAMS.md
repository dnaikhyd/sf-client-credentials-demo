# Project Programs Overview

This document provides a comprehensive overview of all programs in the SampleTestsSalesforce project.

---

## 📋 Standalone Test Programs (No Maven Required)

### 1. **SimpleConnectionTest.java**
**Purpose**: Basic OAuth 2.0 connection test  
**What it does**:
- Authenticates with Salesforce using OAuth 2.0 Client Credentials Flow
- Tests API connectivity by fetching available API versions
- Validates that credentials and configuration are correct

**Usage**:
```bash
javac SimpleConnectionTest.java
java SimpleConnectionTest
```

**Output**: Shows authentication success and available API versions

---

### 2. **DiagnosticTest.java**
**Purpose**: Comprehensive diagnostic and troubleshooting tool  
**What it does**:
- Performs detailed system checks
- Validates .env file configuration
- Tests OAuth authentication with detailed error reporting
- Checks API connectivity
- Provides troubleshooting recommendations

**Usage**:
```bash
javac DiagnosticTest.java
java DiagnosticTest
```

**Output**: Detailed diagnostic report with pass/fail status for each check

---

### 3. **SetupWizard.java**
**Purpose**: Interactive setup assistant  
**What it does**:
- Guides users through initial project setup
- Helps configure .env file with Salesforce credentials
- Validates configuration
- Tests connection
- Provides setup instructions

**Usage**:
```bash
javac SetupWizard.java
java SetupWizard
```

**Output**: Interactive prompts and setup guidance

---

### 4. **BulkDataLoader.java**
**Purpose**: Create sample data in Salesforce  
**What it does**:
- Authenticates with Salesforce
- Creates 10 Account records
- Creates 10 Contact records
- Creates 10 Product (Product2) records
- Uses Salesforce REST API for record creation

**Usage**:
```bash
javac BulkDataLoader.java
java BulkDataLoader
```

**Output**: Progress report showing records created

**Note**: User creation is skipped as it requires special admin permissions

---

### 5. **AccountReader.java** (with Debug Logs)
**Purpose**: Query and display Account records with detailed REST API logging  
**What it does**:
- Authenticates with Salesforce using OAuth 2.0
- Executes SOQL query to retrieve 10 Account records
- Displays results in a formatted table
- **Shows complete REST API details** including:
  - Full URLs (with encoded parameters)
  - HTTP methods
  - Request headers
  - Request body
  - Response codes
  - Response body (with sensitive data masked)

**Usage**:
```bash
javac AccountReader.java
java AccountReader
```

**Output**: 
- Debug logs showing REST API calls
- Formatted table of Account records

**Key Feature**: Perfect for learning how REST APIs work with Salesforce!

---

## 🏗️ Maven-Based Application

### 6. **SalesforcePlayground.java** (Main Application)
**Location**: `src/main/java/com/salesforce/playground/`  
**Purpose**: Full-featured Salesforce integration application  
**What it does**:
- Demonstrates various Salesforce operations
- Uses structured architecture with separate classes for:
  - Authentication (`SalesforceAuth.java`)
  - API client (`SalesforceClient.java`)
  - Configuration (`ConfigLoader.java`)
  - Exception handling (`SalesforceAuthException.java`, `SalesforceClientException.java`)

**Usage**:
```bash
mvn clean compile
mvn exec:java
```

**Architecture**:
```
src/main/java/com/salesforce/playground/
├── SalesforcePlayground.java          # Main application
├── auth/
│   ├── SalesforceAuth.java            # OAuth authentication
│   └── SalesforceAuthException.java   # Auth exceptions
├── client/
│   ├── SalesforceClient.java          # REST API client
│   └── SalesforceClientException.java # Client exceptions
└── util/
    └── ConfigLoader.java              # Configuration loader
```

---

## 📄 Configuration Files

### .env
**Purpose**: Store Salesforce credentials (NEVER commit to Git)  
**Contains**:
- `SALESFORCE_CLIENT_ID` - OAuth Consumer Key
- `SALESFORCE_CLIENT_SECRET` - OAuth Consumer Secret
- `SALESFORCE_TOKEN_URL` - OAuth token endpoint
- `SALESFORCE_API_VERSION` - API version to use

**Status**: ✅ Already in .gitignore (line 107)

### .env.template
**Purpose**: Template for creating .env file  
**Usage**: Copy to `.env` and fill in your credentials

---

## 📚 Documentation Files

### 1. **README.md**
Main project documentation

### 2. **QUICK-START.md**
Quick start guide for getting up and running

### 3. **SETUP-EXTERNAL-CLIENT-APP.md**
Complete guide for setting up External Client App in Salesforce  
**Recently Updated**: Added critical "Run As" user assignment step

### 4. **TROUBLESHOOTING.md**
Troubleshooting guide for common issues

### 5. **PROJECT-PROGRAMS.md** (This file)
Overview of all programs in the project

---

## 🎯 Recommended Learning Path

1. **Start with**: `SimpleConnectionTest.java` - Verify basic connectivity
2. **If issues**: `DiagnosticTest.java` - Diagnose problems
3. **Learn REST APIs**: `AccountReader.java` - See how REST APIs work
4. **Create data**: `BulkDataLoader.java` - Populate test data
5. **Advanced**: `SalesforcePlayground.java` - Full application

---

## 🔑 Key Technologies Used

- **OAuth 2.0 Client Credentials Flow** - Authentication
- **Salesforce REST API** - Data operations
- **SOQL** - Salesforce Object Query Language
- **Java HttpURLConnection** - HTTP client
- **Maven** - Build tool (for main application)

---

## 🛡️ Security Notes

- `.env` file is in `.gitignore` - credentials are protected
- Sensitive data is masked in debug output
- Never commit credentials to version control
- Use `.env.template` as a reference

---

## 📊 REST APIs Used

All programs use these Salesforce REST APIs:

1. **OAuth Token Endpoint**
   - URL: `{instance_url}/services/oauth2/token`
   - Method: POST
   - Purpose: Get access token

2. **Query API**
   - URL: `{instance_url}/services/data/v59.0/query`
   - Method: GET
   - Purpose: Execute SOQL queries

3. **SObject API**
   - URL: `{instance_url}/services/data/v59.0/sobjects/{object_type}`
   - Method: POST
   - Purpose: Create records

---

**Last Updated**: 2026-06-15  
**Project**: SampleTestsSalesforce  
**Made with Bob**
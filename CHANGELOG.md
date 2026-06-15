# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-06-15

### 🎉 Initial Public Release

This is the first public release of the Salesforce OAuth Playground project!

### ✨ Added

#### Core Features
- OAuth 2.0 Client Credentials Flow authentication
- Comprehensive Salesforce REST API client
- SOQL query execution
- CRUD operations (Create, Read, Update, Delete)
- SObject metadata retrieval
- Organization limits monitoring
- Configuration management via .env file

#### Standalone Examples (No Maven Required)
- **SimpleConnectionTest.java** - Basic OAuth connection test
- **AccountReader.java** - Query and display Account records
- **BulkDataLoader.java** - Create sample data (Accounts, Contacts, Products)
- **DiagnosticTest.java** - Comprehensive diagnostic tool
- **SetupWizard.java** - Interactive setup assistant

#### Debug Mode
- All programs support `--debug` flag
- Detailed REST API logging including:
  - Full endpoint URLs
  - HTTP methods and headers
  - Request/response bodies
  - Masked sensitive data
  - Exception details

#### Maven-Based Application
- Structured architecture with separate packages:
  - `auth/` - OAuth authentication
  - `client/` - REST API client
  - `util/` - Configuration utilities
- Exception handling with custom exceptions
- Automatic token management

#### Documentation
- Comprehensive README.md with setup instructions
- Quick Start guide
- External Client App setup guide
- Troubleshooting guide
- Project programs overview
- Setup checklist
- Starter kit recommendations
- Contributing guidelines
- GitHub release recommendations

#### Project Structure
- Organized directory structure:
  - `examples/` - Standalone Java programs
  - `docs/` - All documentation
  - `scripts/` - Helper scripts
  - `src/` - Maven-based application
- Clean separation of concerns
- Professional repository layout

#### Security
- .env file for credential management
- Credentials masked in all output
- .gitignore configured properly
- Security best practices documented

#### Developer Experience
- MIT License for maximum permissiveness
- Contributing guidelines
- Code style conventions
- Commit message guidelines
- Pull request templates

### 📚 Documentation

- README.md - Main project documentation
- QUICK-START.md - 5-minute setup guide
- SETUP-EXTERNAL-CLIENT-APP.md - Detailed Salesforce setup
- TROUBLESHOOTING.md - Common issues and solutions
- PROJECT-PROGRAMS.md - Overview of all programs
- CONTRIBUTING.md - Contribution guidelines
- examples/README.md - Standalone examples guide
- LICENSE - MIT License

### 🔧 Configuration

- .env.template - Template for credentials
- .gitignore - Comprehensive ignore rules
- pom.xml - Maven configuration

### 🎯 Use Cases

- Learning Salesforce OAuth 2.0 flows
- Testing Salesforce API integrations
- Prototyping Salesforce applications
- Educational purposes
- API exploration and experimentation

### 🔒 Security Features

- Credential masking in logs
- .env file in .gitignore
- No hardcoded credentials
- Secure configuration management
- Best practices documentation

### 🐛 Known Issues

None at this time.

### 📝 Notes

- Requires Java 11 or higher
- Requires Salesforce account with External Client App
- Supports both production and sandbox environments
- All examples tested and verified working

---

## [Unreleased]

### Planned Features
- Additional API examples
- Bulk API operations
- Composite API requests
- Streaming API integration
- Unit tests
- CI/CD pipeline
- Docker container
- Pre-compiled JAR releases

---

## Version History

- **1.0.0** (2026-06-15) - Initial public release

---

For more details on each version, see the [releases page](https://github.com/dnaikhyd/sf-client-credentials-demo/releases).
# Salesforce OAuth 2.0 Client Credentials Playground

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-red.svg)](https://maven.apache.org/)

A Java-based playground project for learning and testing Salesforce connectivity using **OAuth 2.0 Client Credentials Flow**. Perfect for developers learning Salesforce APIs, prototyping integrations, or exploring OAuth flows.

## 🚀 Quick Start (5 Minutes)

Get up and running in just a few steps:

```bash
# 1. Clone the repository
git clone https://github.com/dnaikhyd/sf-client-credentials-demo.git
cd sf-client-credentials-demo

# 2. Configure credentials
cp .env.template .env
# Edit .env with your Salesforce credentials

# 3. Run a simple test
cd examples
javac SimpleConnectionTest.java
java SimpleConnectionTest --debug
```

**Don't have Salesforce credentials yet?** See [Salesforce Setup Guide](docs/SETUP-EXTERNAL-CLIENT-APP.md)

## 📖 Table of Contents

- [Features](#-features)
- [Quick Start](#-quick-start-5-minutes)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Usage](#-usage)
- [Examples](#-examples)
- [Project Structure](#-project-structure)
- [Documentation](#-documentation)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

## 🎯 Features

### Core Capabilities
- ✅ **OAuth 2.0 Client Credentials Flow** - Server-to-server authentication
- ✅ **Comprehensive REST API Client** - Full Salesforce API support
- ✅ **SOQL Query Execution** - Query Salesforce data
- ✅ **CRUD Operations** - Create, Read, Update, Delete records
- ✅ **Debug Mode** - Detailed REST API logging for learning
- ✅ **No Maven Required** - Standalone examples work out of the box

### Learning Features
- 📚 **5 Standalone Examples** - Learn step-by-step
- 🔍 **Debug Mode** - See exactly what REST APIs are called
- 📝 **Comprehensive Documentation** - Guides for every step
- 🛠️ **Diagnostic Tools** - Troubleshoot issues easily
- 🎓 **Educational Focus** - Perfect for learning Salesforce APIs

### Developer Experience
- ⚡ **Quick Setup** - Running in 5 minutes
- 🔒 **Secure** - Credentials never committed to Git
- 🎨 **Clean Code** - Well-organized and documented
- 🤝 **Open Source** - MIT License

## 📋 Prerequisites

- **Java 11 or higher** - [Download JDK](https://www.oracle.com/java/technologies/downloads/)
- **Salesforce Account** - [Sign up for free Developer Edition](https://developer.salesforce.com/signup)
- **External Client App** - See [setup guide](docs/SETUP-EXTERNAL-CLIENT-APP.md)

**Optional:**
- **Maven 3.6+** - Only needed for the full application (not for examples)

## 🔧 Installation

### Option 1: Quick Start (Standalone Examples)

```bash
# Clone the repository
git clone https://github.com/dnaikhyd/sf-client-credentials-demo.git
cd sf-client-credentials-demo

# Configure credentials
cp .env.template .env
# Edit .env with your Salesforce credentials

# You're ready! Run examples from the examples/ directory
cd examples
javac SimpleConnectionTest.java
java SimpleConnectionTest
```

### Option 2: Full Maven Application

```bash
# Clone the repository
git clone https://github.com/dnaikhyd/sf-client-credentials-demo.git
cd sf-client-credentials-demo

# Configure credentials
cp .env.template .env
# Edit .env with your Salesforce credentials

# Build with Maven
mvn clean install

# Run the application
mvn exec:java
```

## 🎮 Usage

### Standalone Examples (No Maven)

Perfect for learning! Each example demonstrates a specific concept:

```bash
cd examples

# Test connection
javac SimpleConnectionTest.java
java SimpleConnectionTest --debug

# Query accounts
javac AccountReader.java
java AccountReader --debug

# Create sample data
javac BulkDataLoader.java
java BulkDataLoader --debug

# Run diagnostics
javac DiagnosticTest.java
java DiagnosticTest --debug

# Interactive setup
javac SetupWizard.java
java SetupWizard
```

See [examples/README.md](examples/README.md) for detailed documentation.

### Maven Application

Full-featured application with structured architecture:

```bash
# Run the main application
mvn exec:java -Dexec.mainClass="com.salesforce.playground.SalesforcePlayground"

# Or run the compiled JAR
java -jar target/salesforce-oauth-playground-1.0.0.jar
```

## 📚 Examples

### 1. SimpleConnectionTest
Test basic OAuth authentication and API connectivity.

```bash
java SimpleConnectionTest --debug
```

**What you'll learn:**
- OAuth 2.0 Client Credentials Flow
- REST API authentication
- Token management

### 2. AccountReader
Query and display Account records with detailed REST API logging.

```bash
java AccountReader --debug
```

**What you'll learn:**
- SOQL query execution
- REST API query endpoint
- Data retrieval and parsing

### 3. BulkDataLoader
Create sample data (Accounts, Contacts, Products).

```bash
java BulkDataLoader --debug
```

**What you'll learn:**
- Record creation via REST API
- Bulk operations
- Different object types

### 4. DiagnosticTest
Comprehensive diagnostic and troubleshooting tool.

```bash
java DiagnosticTest --debug
```

**What you'll learn:**
- Configuration validation
- Connectivity testing
- Common issues and solutions

### 5. SetupWizard
Interactive setup assistant for first-time configuration.

```bash
java SetupWizard
```

**What you'll learn:**
- External Client App setup
- Permission Set configuration
- Best practices

## 🏗️ Project Structure

```
salesforce-oauth-playground/
├── README.md                    # This file
├── LICENSE                      # MIT License
├── CHANGELOG.md                 # Version history
├── CONTRIBUTING.md              # Contribution guidelines
├── .env.template                # Credentials template
├── .gitignore                   # Git ignore rules
├── pom.xml                      # Maven configuration
│
├── docs/                        # 📚 Documentation
│   ├── QUICK-START.md          # Quick start guide
│   ├── SETUP-EXTERNAL-CLIENT-APP.md  # Salesforce setup
│   ├── TROUBLESHOOTING.md      # Common issues
│   ├── PROJECT-PROGRAMS.md     # Program overview
│   └── ...                     # More guides
│
├── examples/                    # 🎓 Standalone Examples
│   ├── README.md               # Examples documentation
│   ├── SimpleConnectionTest.java
│   ├── AccountReader.java
│   ├── BulkDataLoader.java
│   ├── DiagnosticTest.java
│   └── SetupWizard.java
│
├── scripts/                     # 🔧 Helper Scripts
│   └── update-env.bat          # Windows env updater
│
└── src/                         # 💻 Maven Application
    └── main/
        └── java/
            └── com/
                └── salesforce/
                    └── playground/
                        ├── SalesforcePlayground.java
                        ├── auth/           # Authentication
                        ├── client/         # API client
                        └── util/           # Utilities
```

## 📖 Documentation

- **[Quick Start Guide](docs/QUICK-START.md)** - Get started in 5 minutes
- **[Salesforce Setup](docs/SETUP-EXTERNAL-CLIENT-APP.md)** - Configure External Client App
- **[Troubleshooting](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- **[Examples Guide](examples/README.md)** - Detailed example documentation
- **[Contributing](CONTRIBUTING.md)** - How to contribute
- **[Changelog](CHANGELOG.md)** - Version history

## 🐛 Troubleshooting

### Common Issues

**"invalid_client_id" Error**
- Verify Consumer Key in .env file
- Wait 2-10 minutes after creating External Client App
- Ensure app is saved and active

**"insufficient_access" Error**
- Assign Permission Set to External Client App
- Enable "API Enabled" in Permission Set
- Grant object permissions (Read, Create, Edit, Delete)

**".env file not found"**
- Ensure .env is in project root (not in examples/)
- Copy from .env.template: `cp .env.template .env`

**More help:** See [Troubleshooting Guide](docs/TROUBLESHOOTING.md) or run `java DiagnosticTest`

## 🎓 Learning Path

**Recommended order for beginners:**

1. **Setup** → Run [SetupWizard.java](examples/SetupWizard.java) for guided setup
2. **Test** → Run [SimpleConnectionTest.java](examples/SimpleConnectionTest.java) to verify
3. **Learn** → Run [AccountReader.java](examples/AccountReader.java) with `--debug` flag
4. **Practice** → Run [BulkDataLoader.java](examples/BulkDataLoader.java) to create data
5. **Troubleshoot** → Use [DiagnosticTest.java](examples/DiagnosticTest.java) when needed

## 🔒 Security

- ✅ Credentials stored in `.env` file (never committed)
- ✅ Sensitive data masked in all output
- ✅ `.gitignore` properly configured
- ✅ Security best practices documented

**Never commit:**
- `.env` file
- Credentials or tokens
- Consumer keys or secrets

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

**Ways to contribute:**
- 🐛 Report bugs
- 💡 Suggest features
- 📝 Improve documentation
- 🔧 Submit pull requests
- ⭐ Star the repository

## 📊 Use Cases

- 🎓 **Learning** - Understand Salesforce OAuth and REST APIs
- 🧪 **Testing** - Test Salesforce integrations
- 🚀 **Prototyping** - Quickly prototype Salesforce applications
- 📚 **Education** - Teach Salesforce API concepts
- 🔍 **Exploration** - Explore Salesforce API capabilities

## 📚 Additional Resources

- [Salesforce OAuth Documentation](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_flows.htm)
- [Salesforce REST API Guide](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/)
- [External Client Apps](https://help.salesforce.com/s/articleView?id=sf.external_client_apps.htm)
- [SOQL Reference](https://developer.salesforce.com/docs/atlas.en-us.soql_sosl.meta/soql_sosl/)

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Salesforce Developer Community
- OAuth 2.0 specification authors
- All contributors to this project

## 📧 Support

- 📖 Check the [documentation](docs/)
- 🐛 [Report issues](https://github.com/dnaikhyd/sf-client-credentials-demo/issues)
- 💬 [Start a discussion](https://github.com/dnaikhyd/sf-client-credentials-demo/discussions)
- ⭐ Star the repository if you find it helpful!

---

**Made with ❤️ for the Salesforce Developer Community**

**Happy Learning! 🚀**
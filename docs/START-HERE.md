# 🚀 START HERE - Salesforce External Client App Starter Kit

Welcome! This is your starting point for learning Salesforce OAuth 2.0 Client Credentials Flow.

---

## ⚡ Quick Start (5 Minutes)

### Step 1: Run the Setup Wizard

```bash
javac SetupWizard.java
java SetupWizard
```

The wizard will guide you through:
- Creating a Permission Set in Salesforce
- Creating an External Client App
- Configuring your credentials
- Testing the connection

**That's it!** The wizard does everything for you.

---

## 📚 What's in This Starter Kit?

### 🔧 Setup & Testing Tools

| Program | Purpose | When to Use |
|---------|---------|-------------|
| **SetupWizard.java** | Interactive setup guide | First time setup |
| **SimpleConnectionTest.java** | Test OAuth connection | Verify setup works |
| **DiagnosticTest.java** | Troubleshoot issues | When something breaks |

### 📖 Example Programs

| Program | Purpose | Complexity |
|---------|---------|------------|
| **AccountReader.java** | Query and display accounts | ⭐ Beginner |
| **BulkDataLoader.java** | Create sample data | ⭐⭐ Intermediate |

### 📄 Documentation

| File | Purpose |
|------|---------|
| **QUICK-START.md** | Fast setup guide |
| **SETUP-EXTERNAL-CLIENT-APP.md** | Detailed manual setup |
| **TROUBLESHOOTING.md** | Common problems & solutions |
| **EXAMPLES.md** | Guide to all examples |
| **PROJECT-PROGRAMS.md** | Complete program inventory |

---

## 🎯 Your Learning Path

### Day 1: Get Connected
1. ✅ Run `SetupWizard.java`
2. ✅ Complete Salesforce setup
3. ✅ Run `SimpleConnectionTest.java`
4. ✅ See it work!

### Day 2: Try Examples
1. Run `AccountReader.java` - See your data
2. Run `BulkDataLoader.java` - Create test data
3. Try with `--debug` flag to see REST API details

### Week 2: Build Your Own
1. Study the example code
2. Modify examples for your needs
3. Build your own integrations

---

## 💡 Pro Tips

### Use Debug Mode
All programs support `--debug` flag for detailed REST API information:

```bash
# Normal mode - clean output
java SimpleConnectionTest

# Debug mode - see all REST API details
java SimpleConnectionTest --debug
```

### Quick Commands

```bash
# Test connection
java SimpleConnectionTest

# Diagnose issues
java DiagnosticTest

# Read accounts
java AccountReader

# Create sample data
java BulkDataLoader

# See REST API details (any program)
java AccountReader --debug
```

---

## ❓ Need Help?

### Common Issues

**"no client credentials user enabled"**
→ See TROUBLESHOOTING.md - Permission Set not assigned

**"invalid_grant: request not supported on this domain"**
→ See TROUBLESHOOTING.md - Wrong token URL

**Connection test fails**
→ Run: `java DiagnosticTest`

### Get More Help

1. Check **TROUBLESHOOTING.md** for common issues
2. Review **SETUP-EXTERNAL-CLIENT-APP.md** for setup details
3. Run `java DiagnosticTest` for automated diagnosis

---

## 🎓 Understanding the Code

### How OAuth 2.0 Client Credentials Works

```
1. Your App → Salesforce: "Here's my Client ID & Secret"
2. Salesforce → Your App: "Here's your Access Token"
3. Your App → Salesforce API: "Here's my Access Token + API request"
4. Salesforce → Your App: "Here's your data"
```

### What Each Program Does

**SetupWizard.java**
- Guides you through Salesforce setup
- Creates your .env configuration file
- Tests the connection

**SimpleConnectionTest.java**
- Authenticates with OAuth 2.0
- Tests API connectivity
- Shows available API versions

**AccountReader.java**
- Authenticates with OAuth 2.0
- Executes SOQL query
- Displays results in a table

**BulkDataLoader.java**
- Creates 10 Accounts
- Creates 10 Contacts
- Creates 10 Products

---

## 🔒 Security Notes

- ✅ `.env` file is in `.gitignore` - your credentials are safe
- ✅ Never commit credentials to version control
- ✅ Use `.env.template` as a reference
- ✅ Sensitive data is masked in debug output

---

## 📊 Success Checklist

- [ ] Ran SetupWizard
- [ ] Created Permission Set in Salesforce
- [ ] Created External Client App in Salesforce
- [ ] Assigned Permission Set to app
- [ ] `.env` file configured
- [ ] `SimpleConnectionTest` passes
- [ ] Tried at least one example program

---

## 🚀 Next Steps

Once you're comfortable with the basics:

1. **Explore Advanced Examples**
   - Check the `src/` folder for Maven-based application
   - More structured, production-ready code

2. **Build Your Integration**
   - Use the examples as templates
   - Customize for your specific needs
   - Add error handling and retry logic

3. **Learn More**
   - [Salesforce OAuth Documentation](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_flows.htm)
   - [Salesforce REST API Guide](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/)
   - [External Client Apps](https://help.salesforce.com/s/articleView?id=sf.external_client_apps.htm)

---

## 🎉 Ready to Start?

Run this command to begin:

```bash
javac SetupWizard.java
java SetupWizard
```

**Good luck! 🚀**

---

**Questions?** Check TROUBLESHOOTING.md or run `java DiagnosticTest`
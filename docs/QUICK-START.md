# 🚀 Quick Start Guide - Salesforce OAuth 2.0 Client Credentials Playground

## ⚡ Fastest Way to Get Started

### Option 1: Interactive Setup Wizard (Recommended)

Run the interactive wizard that guides you through the entire setup process:

```bash
java SetupWizard
```

The wizard will:
- ✅ Guide you through creating the Permission Set in Salesforce
- ✅ Guide you through creating the External Client App in Salesforce
- ✅ Collect your Consumer Key and Secret
- ✅ Automatically create and configure your .env file
- ✅ Test the connection for you

**Time required**: 10-15 minutes

---

### Option 2: Manual Setup

If you prefer to set up manually, follow these steps:

1. **Read the setup guide**:
   ```
   Open: SETUP-EXTERNAL-CLIENT-APP.md
   ```

2. **Create External Client App in Salesforce** (follow the guide)

3. **Update .env file** with your credentials

4. **Test connection**:
   ```bash
   java SimpleConnectionTest
   ```

**Time required**: 15-20 minutes

---

## 📋 Available Tools

### 1. Setup Wizard (Interactive)
```bash
java SetupWizard
```
**Purpose**: Guides you through the complete setup process step-by-step

### 2. Connection Test
```bash
java SimpleConnectionTest
```
**Purpose**: Tests OAuth 2.0 authentication and API connectivity

### 3. Diagnostic Tool
```bash
java DiagnosticTest
```
**Purpose**: Diagnoses configuration issues and provides troubleshooting tips

---

## 🎯 What You Need

Before starting, have ready:

1. **Salesforce Access**:
   - System Administrator permissions
   - Your Salesforce org URL (e.g., `https://yourcompany.my.salesforce.com`)

2. **Time**: 10-15 minutes

3. **This Project**: Already set up and ready to go!

---

## 📖 Documentation

- **QUICK-START.md** (this file) - Get started quickly
- **SETUP-EXTERNAL-CLIENT-APP.md** - Detailed manual setup instructions
- **TROUBLESHOOTING.md** - Solutions to common problems
- **README.md** - Complete project documentation

---

## 🔧 Troubleshooting

### Issue: "no client credentials user enabled"
**Solution**: You need to assign a Permission Set to your External Client App
- Run: `java SetupWizard` and follow Step 6

### Issue: "invalid_grant: request not supported on this domain"
**Solution**: You need to use your My Domain URL
- Run: `java SetupWizard` and it will help you configure the correct URL

### Issue: Connection test fails
**Solution**: Run diagnostics
```bash
java DiagnosticTest
```

For more help, see **TROUBLESHOOTING.md**

---

## ✅ Success Checklist

Your setup is complete when:
- [ ] External Client App created in Salesforce
- [ ] Permission Set created with "API Enabled"
- [ ] Permission Set assigned to External Client App
- [ ] .env file configured with credentials
- [ ] `java SimpleConnectionTest` runs successfully

---

## 🎉 Next Steps

Once your connection test passes:

1. **Explore the API**:
   ```bash
   # With Maven (if installed)
   mvn exec:java -Dexec.mainClass="com.salesforce.playground.SalesforcePlayground"
   ```

2. **Build your integration**:
   - Use `SalesforceClient` class for API operations
   - See examples in `SalesforcePlayground.java`

3. **Review security**:
   - Implement IP restrictions for production
   - Set up monitoring and logging
   - Rotate secrets regularly

---

## 💡 Pro Tips

1. **Use the Setup Wizard**: It's the fastest and easiest way to get started
2. **Keep credentials secure**: Never commit .env file to version control
3. **Test in sandbox first**: Use a sandbox org before production
4. **Monitor API limits**: Check your usage regularly with `client.getLimits()`

---

## 📞 Need Help?

1. **Run diagnostics**: `java DiagnosticTest`
2. **Check troubleshooting guide**: `TROUBLESHOOTING.md`
3. **Review setup guide**: `SETUP-EXTERNAL-CLIENT-APP.md`
4. **Check Salesforce docs**: [External Client Apps Documentation](https://help.salesforce.com/s/articleView?id=sf.external_client_apps.htm)

---

**Ready to start? Run the Setup Wizard:**

```bash
java SetupWizard
```

Good luck! 🚀
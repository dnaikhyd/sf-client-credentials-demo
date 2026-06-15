# ✅ Salesforce External Client App Setup Checklist

Use this checklist to track your setup progress. Print it out or keep it open while setting up.

---

## 📋 Pre-Setup

- [ ] I have Salesforce System Administrator access
- [ ] I know my Salesforce org URL (e.g., https://yourcompany.my.salesforce.com)
- [ ] I have 15-20 minutes available
- [ ] I have this project downloaded and ready

---

## 🔧 Part 1: Permission Set (5 minutes)

### Create Permission Set
- [ ] Logged into Salesforce
- [ ] Navigated to Setup → Permission Sets
- [ ] Clicked "New"
- [ ] Entered Label: `API Access for OAuth`
- [ ] Clicked "Save"

### Configure System Permissions
- [ ] Clicked "System Permissions"
- [ ] Clicked "Edit"
- [ ] ✅ Checked "API Enabled" (CRITICAL!)
- [ ] ✅ Checked "View All Data" (for testing)
- [ ] Clicked "Save"

### Add Object Permissions
- [ ] Clicked "Object Settings"
- [ ] Clicked "Account"
- [ ] Clicked "Edit"
- [ ] ✅ Checked: Read, Create, Edit
- [ ] Clicked "Save"
- [ ] (Optional) Repeated for Contact, Opportunity, etc.

**✅ Permission Set Complete!**

---

## 🔐 Part 2: External Client App (5 minutes)

### Create App
- [ ] Navigated to Setup → App Manager
- [ ] Clicked "New External Client App" (NOT "New Connected App")
- [ ] Entered Name: `OAuth Playground`
- [ ] Entered Description: `OAuth 2.0 Client Credentials Flow`

### Configure OAuth Settings
- [ ] ✅ Checked Grant Type: "Client Credentials"
- [ ] ✅ Selected Scopes:
  - [ ] Manage user data via APIs (api)
  - [ ] Perform requests at any time (refresh_token, offline_access)
- [ ] Selected Token Endpoint Auth Method: "Client Secret (Post)"
- [ ] Clicked "Save"

### Assign Run As User (CRITICAL!)
- [ ] Found "Client Credentials Flow" section on app detail page
- [ ] Clicked "Edit" next to "Run As"
- [ ] Selected a user (myself or integration user)
- [ ] Clicked "Save"

### Wait for Provisioning
- [ ] Waited 2-10 minutes for app to be provisioned
- [ ] App status shows as "Active"

**✅ External Client App Complete!**

---

## 🔑 Part 3: Get Credentials (2 minutes)

### Consumer Key (Client ID)
- [ ] On External Client App detail page
- [ ] Found "OAuth Settings" section
- [ ] Copied "Consumer Key"
- [ ] Saved it securely (you'll need it for .env file)

### Consumer Secret (Client Secret)
- [ ] Clicked "Manage Consumer Details" button
- [ ] Verified identity (checked email for code)
- [ ] Copied "Consumer Secret"
- [ ] Saved it securely (you'll need it for .env file)

**✅ Credentials Obtained!**

---

## 🔗 Part 4: Assign Permission Set (2 minutes)

### Link Permission Set to App
- [ ] On External Client App detail page
- [ ] Clicked "Manage" button (top right)
- [ ] Clicked "Manage Permission Sets"
- [ ] Clicked "Assign Permission Sets"
- [ ] Selected "API Access for OAuth"
- [ ] Clicked "Assign"
- [ ] Verified it appears in "Assigned Permission Sets" list

**✅ Permission Set Assigned!**

---

## 💾 Part 5: Configure Project (2 minutes)

### Update .env File
- [ ] Opened `.env` file in project directory
- [ ] Updated `SALESFORCE_CLIENT_ID` with Consumer Key
- [ ] Updated `SALESFORCE_CLIENT_SECRET` with Consumer Secret
- [ ] Updated `SALESFORCE_TOKEN_URL` with My Domain URL
  - Format: `https://YOUR_DOMAIN.my.salesforce.com/services/oauth2/token`
- [ ] Saved the file

**✅ Configuration Complete!**

---

## 🧪 Part 6: Test Connection (2 minutes)

### Run Connection Test
- [ ] Opened terminal/command prompt
- [ ] Navigated to project directory
- [ ] Compiled: `javac SimpleConnectionTest.java`
- [ ] Ran: `java SimpleConnectionTest`
- [ ] Saw "[SUCCESS] Authentication successful!"
- [ ] Saw "[SUCCESS] API connection successful!"

**✅ Connection Test Passed!**

---

## 🎉 Setup Complete!

If all checkboxes above are checked, congratulations! Your setup is complete.

### Next Steps:
- [ ] Try example programs (see START-HERE.md)
- [ ] Run `java AccountReader` to see your data
- [ ] Run `java BulkDataLoader` to create test data
- [ ] Read the documentation to learn more

---

## ❌ Troubleshooting

If connection test failed, check:

### Common Issues:

**"no client credentials user enabled"**
- [ ] Did you assign "Run As" user in Step 2?
- [ ] Did you assign Permission Set in Step 4?

**"invalid_grant: request not supported on this domain"**
- [ ] Is your Token URL using My Domain format?
- [ ] Format should be: `https://YOUR_DOMAIN.my.salesforce.com/services/oauth2/token`

**"invalid_client_id"**
- [ ] Is Consumer Key correct in .env file?
- [ ] Did you wait 2-10 minutes after creating the app?

**"invalid_client"**
- [ ] Is Consumer Secret correct in .env file?
- [ ] Is "Client Credentials" grant type selected?

### Get Help:
- Run: `java DiagnosticTest` for automated diagnosis
- Check: TROUBLESHOOTING.md for detailed solutions
- Review: SETUP-EXTERNAL-CLIENT-APP.md for step-by-step guide

---

## 📝 Notes Section

Use this space to write down your specific values:

**My Salesforce Domain:**
```
https://_________________________________.my.salesforce.com
```

**My Token URL:**
```
https://_________________________________.my.salesforce.com/services/oauth2/token
```

**Consumer Key (first/last 4 chars):**
```
____****____
```

**Setup Date:**
```
____________________
```

---

**Print this checklist and check off items as you complete them!**

**Total Time Required: 15-20 minutes**
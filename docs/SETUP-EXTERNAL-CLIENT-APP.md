# Complete Setup Guide: External Client App for OAuth 2.0 Client Credentials Flow

This guide provides **complete step-by-step instructions** to create and configure an External Client App in Salesforce for OAuth 2.0 Client Credentials Flow.

---

## 📋 Prerequisites

- Salesforce org with **Enterprise Edition or higher**
- System Administrator access
- My Domain enabled (should already be enabled in your org)

---

## Part 1: Create Permission Set

### Step 1.1: Navigate to Permission Sets

1. Log in to your Salesforce org: `https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com`
2. Click the **⚙️ Setup** icon (gear icon in top right)
3. Click **Setup**
4. In the **Quick Find** box (left sidebar), type: `Permission Sets`
5. Click **Permission Sets** under Users

### Step 1.2: Create New Permission Set

1. Click the **New** button
2. Fill in the following fields:
   - **Label**: `API Access for OAuth`
   - **API Name**: `API_Access_for_OAuth` (auto-filled)
   - **Description**: `Permission set for External Client App OAuth 2.0 Client Credentials Flow`
3. Click **Save**

### Step 1.3: Enable System Permissions

1. You should now be on the Permission Set detail page
2. Scroll down to find **System Permissions** section
3. Click **System Permissions** (or click Edit if you see it)
4. Find and check these permissions:
   - ✅ **API Enabled** (CRITICAL - Must be checked)
   - ✅ **View All Data** (for testing - you can restrict this later)
5. Click **Save**

### Step 1.4: Add Object Permissions

1. Still on the Permission Set page, find **Apps** section
2. Click **Object Settings**
3. Click **Account** from the list
4. Click **Edit**
5. Under **Object Permissions**, check:
   - ✅ **Read**
   - ✅ **Create**
   - ✅ **Edit**
   - ✅ **Delete** (optional)
   - ✅ **View All** (optional, for testing)
   - ✅ **Modify All** (optional, for testing)
6. Click **Save**
7. **Repeat for other objects** you need (Contact, Opportunity, etc.)

✅ **Permission Set is now ready!**

---

## Part 2: Create External Client App

### Step 2.1: Navigate to App Manager

1. In **Quick Find** box, type: `App Manager`
2. Click **App Manager** under Apps

### Step 2.2: Create New External Client App

1. Click the **New External Client App** button (top right)
   - ⚠️ **Important**: Make sure you click "New External Client App" NOT "New Connected App"
2. Fill in the **Basic Information**:
   - **External Client App Name**: `OAuth Playground`
   - **Description**: `External Client App for OAuth 2.0 Client Credentials Flow testing`

### Step 2.3: Configure OAuth Settings

1. In the **OAuth Settings** section:
   
   **Grant Types**:
   - ✅ Check **Client Credentials**
   
   **Scopes** (Select these from the Available Scopes list):
   - ✅ **Access the identity URL service (id, profile, email, address, phone)**
   - ✅ **Manage user data via APIs (api)**
   - ✅ **Perform requests at any time (refresh_token, offline_access)**
   
   **Token Endpoint Auth Method**:
   - Select: **Client Secret (Post)** (recommended)
   - Alternative: **Client Secret (Basic)** (also works)

2. Click **Save**

### Step 2.4: Assign Run As User (CRITICAL)

⚠️ **This step is REQUIRED for Client Credentials Flow to work!**

1. After saving the External Client App, you'll be on the app detail page
2. Scroll down to find **"Client Credentials Flow"** section
3. Click **Edit** next to "Run As"
4. In the **"Run As"** field, search for and select a user (typically yourself or a dedicated integration user)
   - This user will be the identity used for all API calls made through this app
   - The user must have the necessary permissions to access the data your app needs
5. Click **Save**

**Important Notes:**
- Without a "Run As" user, you'll get the error: "no client credentials user enabled"
- The selected user's permissions determine what the app can access
- For production, create a dedicated integration user with specific permissions

### Step 2.5: Wait for App Creation

1. You'll see a message: "Your External Client App has been created"
2. **Wait 2-10 minutes** for the app to be fully provisioned
3. You'll be redirected to the External Client App detail page

---

## Part 3: Get Client Credentials

### Step 3.1: View Consumer Key (Client ID)

1. On the External Client App detail page, find **OAuth Settings** section
2. You'll see **Consumer Key**
3. Click the **Copy** icon next to Consumer Key
4. **Save this value** - you'll need it for the .env file

### Step 3.2: Get Consumer Secret (Client Secret)

1. Click **Manage Consumer Details** button
2. You may need to verify your identity (enter verification code sent to your email)
3. You'll see the **Consumer Secret**
4. Click the **Copy** icon next to Consumer Secret
5. **Save this value** - you'll need it for the .env file
6. ⚠️ **Important**: Keep this secret secure! Don't share it or commit it to version control

---

## Part 4: Assign Permission Set to External Client App

### Step 4.1: Navigate to Permission Set Assignment

1. Still on the External Client App detail page
2. Click the **Manage** button (top right)
3. Click **Manage Permission Sets**

### Step 4.2: Assign the Permission Set

1. Click **Assign Permission Sets** button
2. Find and select **API Access for OAuth** (the permission set you created earlier)
3. Click **Assign**
4. You should see the permission set listed under "Assigned Permission Sets"

✅ **External Client App is now fully configured!**

---

## Part 5: Update Your .env File

### Step 5.1: Open .env File

1. Navigate to your project directory: `c:/Workspace/BobCode/SampleTestsSalesforce`
2. Open the `.env` file in a text editor

### Step 5.2: Update Configuration

Replace the values with your actual credentials:

```properties
# Salesforce External Client App Credentials
SALESFORCE_CLIENT_ID=YOUR_CONSUMER_KEY_HERE
SALESFORCE_CLIENT_SECRET=YOUR_CONSUMER_SECRET_HERE

# Salesforce Instance URL - Use Your My Domain
SALESFORCE_TOKEN_URL=https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com/services/oauth2/token

# API Version
SALESFORCE_API_VERSION=v59.0

# Logging Level
LOG_LEVEL=INFO
```

**Example** (with placeholder values):
```properties
SALESFORCE_CLIENT_ID=your_consumer_key_here
SALESFORCE_CLIENT_SECRET=your_consumer_secret_here
SALESFORCE_TOKEN_URL=https://login.salesforce.com/services/oauth2/token
SALESFORCE_API_VERSION=v59.0
LOG_LEVEL=INFO
```

### Step 5.3: Save the File

1. Save the `.env` file
2. Make sure the file is named exactly `.env` (not `.env.txt`)

---

## Part 6: Test the Connection

### Step 6.1: Run the Connection Test

Open PowerShell or Command Prompt in your project directory and run:

```bash
java SimpleConnectionTest
```

### Step 6.2: Expected Success Output

You should see:

```
=== Salesforce OAuth 2.0 Client Credentials Connection Test ===

Configuration loaded:
  Client ID: 3MVG****d39O
  Client Secret: A487****9A57
  Token URL: https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com/services/oauth2/token

[Step 1] Authenticating with Salesforce...
[SUCCESS] Authentication successful!
  Access Token: eyJr****xyz
  Instance URL: https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com
  Token Type: Bearer

[Step 2] Testing API connection...
[SUCCESS] API connection successful!
  Response Code: 200
  Available API versions:
    - v59.0
    - v60.0
    - v61.0

=== Connection test completed successfully! ===
```

---

## 🎯 Quick Reference Checklist

Use this checklist to verify your setup:

### Permission Set:
- [ ] Created Permission Set: "API Access for OAuth"
- [ ] Enabled "API Enabled" system permission
- [ ] Added object permissions (at minimum: Account with Read)
- [ ] Saved the Permission Set

### External Client App:
- [ ] Created External Client App (NOT Connected App)
- [ ] Selected "Client Credentials" grant type
- [ ] Added scopes: api, refresh_token, offline_access
- [ ] Selected Token Endpoint Auth Method
- [ ] **Assigned "Run As" user** (CRITICAL - without this you'll get "no client credentials user enabled" error)
- [ ] Saved the app
- [ ] Waited 2-10 minutes for provisioning

### Credentials:
- [ ] Copied Consumer Key (Client ID)
- [ ] Copied Consumer Secret (Client Secret)
- [ ] Saved credentials securely

### Permission Set Assignment:
- [ ] Navigated to External Client App → Manage → Manage Permission Sets
- [ ] Assigned "API Access for OAuth" permission set
- [ ] Verified assignment in the list

### Configuration:
- [ ] Updated .env file with Client ID
- [ ] Updated .env file with Client Secret
- [ ] Verified Token URL uses My Domain
- [ ] Saved .env file

### Testing:
- [ ] Ran: `java SimpleConnectionTest`
- [ ] Received successful authentication
- [ ] Received successful API connection

---

## 🔍 Troubleshooting

### Error: "no client credentials user enabled"
**Solution**: You need to assign a "Run As" user to the External Client App:
1. Go to your External Client App detail page
2. Find the "Client Credentials Flow" section
3. Click Edit next to "Run As"
4. Select a user (this user's permissions will be used for API calls)
5. Click Save
6. Also ensure you assigned the Permission Set to the External Client App (Part 4)

### Error: "invalid_grant: request not supported on this domain"
**Solution**: Make sure you're using your My Domain URL in SALESFORCE_TOKEN_URL

### Error: "invalid_client_id"
**Solution**: Verify the Consumer Key is correct and the app is Active

### Error: "invalid_client"
**Solution**: Verify the Consumer Secret is correct

### Can't find "New External Client App" button
**Solution**: 
- Make sure you're in App Manager (not Connected Apps)
- Verify your Salesforce edition supports External Client Apps (Enterprise+)
- Check if My Domain is enabled

---

## 📞 Additional Help

If you encounter issues:

1. **Check Salesforce Edition**: External Client Apps require Enterprise Edition or higher
2. **Verify My Domain**: Must be enabled (it is in your org)
3. **Review Setup Audit Trail**: Setup → Security → Setup Audit Trail
4. **Check Login History**: Setup → Security → Login History
5. **Wait for Propagation**: Changes can take 2-10 minutes

---

## ✅ Success Criteria

Your setup is complete when:
1. ✅ External Client App is created and Active
2. ✅ **"Run As" user is assigned to the External Client App** (CRITICAL)
3. ✅ Permission Set is created with "API Enabled"
4. ✅ Permission Set is assigned to External Client App
5. ✅ .env file is configured with correct credentials
6. ✅ `java SimpleConnectionTest` runs successfully

---

## 🎉 Next Steps

Once your connection test is successful:

1. **Explore the API**: Run more examples in `SalesforcePlayground.java`
2. **Add More Permissions**: Extend the Permission Set with additional object permissions
3. **Build Your Integration**: Use the `SalesforceClient` class to build your application
4. **Review Security**: Implement IP restrictions and monitoring for production

---

**Your My Domain**: `https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com`

**Token URL**: `https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com/services/oauth2/token`
# Troubleshooting Guide - OAuth 2.0 Client Credentials Flow

## Current Error: "no client credentials user enabled"

### ✅ What's Working:
- Configuration is correct
- Token URL is correct (using My Domain)
- Client ID and Secret are valid
- Network connectivity is good

### ❌ The Problem:
The External Client App does not have a Permission Set assigned, which is **required** for Client Credentials Flow.

---

## 🔧 Solution: Assign Permission Set to External Client App

### Step 1: Create a Permission Set (if not already created)

1. In Salesforce, go to **Setup** (gear icon)
2. In Quick Find, search for **"Permission Sets"**
3. Click **New**
4. Fill in:
   - **Label**: `Salesforce API Access`
   - **API Name**: `Salesforce_API_Access`
5. Click **Save**

### Step 2: Configure Permission Set Permissions

1. Open the Permission Set you just created
2. Click **System Permissions**
3. Click **Edit**
4. Enable these permissions:
   - ✅ **API Enabled** (REQUIRED)
   - ✅ **View All Data** (for testing - adjust for production)
5. Click **Save**

### Step 3: Add Object Permissions (Optional but Recommended)

1. In the Permission Set, click **Object Settings**
2. Find and click **Account**
3. Click **Edit**
4. Enable:
   - ✅ Read
   - ✅ Create
   - ✅ Edit
   - ✅ Delete (optional)
5. Click **Save**
6. Repeat for other objects you need (Contact, Opportunity, etc.)

### Step 4: Assign Permission Set to External Client App

1. Go to **Setup** → **Apps** → **App Manager**
2. Find your External Client App
3. Click the dropdown arrow → **Manage**
4. Click **Manage Permission Sets**
5. Click **Assign Permission Sets**
6. Select the **Salesforce API Access** permission set
7. Click **Assign**
8. Verify the permission set appears in the list

### Step 5: Verify the Configuration

1. Still in the External Client App management page
2. Verify these settings:
   - **Grant Types**: Client Credentials should be checked
   - **Scopes**: Should include `api`, `refresh_token`, `offline_access`
   - **Permission Sets**: Should show your assigned permission set
   - **Status**: Should be Active

---

## 🧪 Test Again

After completing the steps above, wait 2-5 minutes for changes to propagate, then run:

```bash
java SimpleConnectionTest
```

---

## 📋 Complete Checklist

Use this checklist to verify your setup:

### External Client App Configuration:
- [ ] External Client App created (not Connected App)
- [ ] Grant Type: Client Credentials is selected
- [ ] Scopes include: api, refresh_token, offline_access
- [ ] Consumer Key (Client ID) copied
- [ ] Consumer Secret copied
- [ ] App is Active

### Permission Set Configuration:
- [ ] Permission Set created
- [ ] "API Enabled" system permission is checked
- [ ] Object permissions configured (at minimum: Account with Read access)
- [ ] Permission Set is assigned to the External Client App

### .env File Configuration:
- [ ] SALESFORCE_CLIENT_ID is set (Consumer Key)
- [ ] SALESFORCE_CLIENT_SECRET is set (Consumer Secret)
- [ ] SALESFORCE_TOKEN_URL uses My Domain URL
- [ ] Token URL format: https://YOUR_DOMAIN.my.salesforce.com/services/oauth2/token

### Your Current Configuration:
```
Token URL: https://orgfarm-5a64afc6be-dev-ed.develop.my.salesforce.com/services/oauth2/token
Client ID: 3MVG****d39O (masked)
Client Secret: A487****9A57 (masked)
```

---

## 🔍 Common Issues and Solutions

### Issue 1: "invalid_grant: no client credentials user enabled"
**Cause**: No Permission Set assigned to External Client App  
**Solution**: Follow Step 4 above to assign a Permission Set

### Issue 2: "invalid_grant: request not supported on this domain"
**Cause**: Using wrong token URL (login.salesforce.com instead of My Domain)  
**Solution**: Use your My Domain URL (already fixed in your case)

### Issue 3: "invalid_client_id"
**Cause**: Wrong Client ID or External Client App not active  
**Solution**: Verify Client ID and ensure app is Active

### Issue 4: "invalid_client"
**Cause**: Wrong Client Secret or Client Credentials not enabled  
**Solution**: Verify Client Secret and grant type configuration

### Issue 5: "insufficient_access" (after successful authentication)
**Cause**: Permission Set doesn't have required object permissions  
**Solution**: Add object permissions to the Permission Set

---

## 📞 Need More Help?

If you're still having issues after following this guide:

1. **Verify External Client App Type**: Make sure you created an "External Client App" not a "Connected App"
2. **Check Salesforce Edition**: Client Credentials Flow requires Enterprise Edition or higher
3. **Review Audit Logs**: Setup → Security → Login History to see authentication attempts
4. **Wait for Propagation**: Changes can take 2-10 minutes to take effect

---

## ✅ Expected Success Output

When everything is configured correctly, you should see:

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
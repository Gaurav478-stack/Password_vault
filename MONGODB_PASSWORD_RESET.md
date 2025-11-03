# MongoDB Atlas - Password Reset Guide

## 📋 Step-by-Step: Reset Database User Password

### 1. Go to MongoDB Atlas
   URL: https://cloud.mongodb.com/
   Login with your account

### 2. Navigate to Database Access
   - Click "Database Access" in the left sidebar
   - OR go directly: https://cloud.mongodb.com/v2#/security/database/users

### 3. Find Your User
   - You should see your database user (e.g., "securepassUser")
   - Look for the user you created

### 4. Click "Edit" Button
   - Find the "Edit" button next to your username
   - Click it to open the edit dialog

### 5. Click "Edit Password"
   - In the edit dialog, click "Edit Password" button
   - You'll see two options:
     a) Autogenerate Secure Password (recommended)
     b) Enter your own password

### 6. Choose Password Method

   Option A - Autogenerate (Recommended):
   ✅ Click "Autogenerate Secure Password"
   ✅ MongoDB will generate a strong password
   ✅ Click "Copy" to copy it immediately
   ✅ SAVE THIS PASSWORD - You won't see it again!

   Option B - Custom Password:
   ✅ Enter your own password (must be strong)
   ✅ Confirm the password
   ✅ WRITE IT DOWN immediately!

### 7. Click "Update User"
   - Confirm the changes
   - Your password is now reset

### 8. Use New Password in Connection String
   Replace <password> in your connection string:
   
   mongodb+srv://securepassUser:YOUR_NEW_PASSWORD@cluster0.xxxxx.mongodb.net/securepass?retryWrites=true&w=majority

## ⚠️ IMPORTANT NOTES

- You CANNOT view an existing password in MongoDB Atlas
- You can only RESET it to a new one
- Always save your password immediately after creating/resetting
- Use a password manager to store it securely

## 🧪 Test Your New Password

After resetting, test the connection:
1. Update backend/.env with new MONGO_URI
2. Run: node test-mongodb.js
3. If successful, use the same MONGO_URI in Render

## 🔐 Password Requirements

MongoDB Atlas passwords must:
- Be at least 8 characters long
- Can contain letters, numbers, and special characters
- Avoid using < > : " / \ | ? * characters (they cause issues in URLs)

## 💡 Pro Tip

If you use special characters in your password, you need to URL-encode them:
- @ becomes %40
- # becomes %23
- $ becomes %24
- % becomes %25
- & becomes %26

Or use the autogenerate option to avoid this hassle!

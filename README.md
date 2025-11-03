# 🔐 SecurePass Vault

[![Live Demo](https://img.shields.io/badge/Demo-Live-success?style=for-the-badge&logo=github)](https://gaurav478-stack.github.io/Password_vault/)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-blue?style=for-the-badge&logo=github)](https://github.com/Gaurav478-stack/Password_vault)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

A secure password management system with encrypted storage, featuring both client-side and server-backed vault options, plus an advanced Java backend service demonstrating OOP concepts.

## 🌐 Live Links

- **🚀 Live Demo**: https://gaurav478-stack.github.io/Password_vault/
- **📱 Vault App**: https://gaurav478-stack.github.io/Password_vault/frontend/
- **📚 Documentation**: https://gaurav478-stack.github.io/Password_vault/docs/
- **🔧 API Backend**: https://password-vault-2ow2.onrender.com ✅ (Live on Render)

## Features

### 🔒 Client-Side Vault (Privacy-First)
- 🔐 **AES-256-GCM Encryption**: Military-grade authenticated encryption
- 🔑 **PBKDF2 Key Derivation**: 150,000 iterations for brute-force protection
- 🕵️ **Zero-Knowledge**: Master password never stored or transmitted
- 💾 **Encrypted Backups**: Export/import encrypted vault files
- 🚫 **No Server Required**: Works completely offline
- 💰 **Zero Cost**: No hosting, no subscriptions, completely free
- ⚡ **Instant Setup**: Open HTML file and start using
- 🔒 **Maximum Privacy**: Only you can decrypt your passwords

### 🌐 Server-Backed Vault (Multi-Device Sync)
- 🔐 **Strong Encryption**: AES-256-GCM authenticated encryption
- 🔑 **Secure Authentication**: JWT-based auth with bcrypt password hashing
- 📱 **Multi-Device Sync**: Access from phone, laptop, desktop
- 👥 **Team Collaboration**: Share passwords with teammates
- ☁️ **Cloud Backup**: Automatic database backups
- 🔍 **Advanced Features**: Audit logs, permissions, sharing

### ✨ Common Features (Both Vaults)
- 🎨 **Modern UI**: Clean, responsive interface with dark theme
- 📱 **Mobile-Friendly**: Works on all devices
- 🔍 **Search & Filter**: Quick search by service name or username
- 📂 **Categories**: Organize passwords (banking, shopping, email, work, gaming)
- 🎲 **Password Generator**: Generate strong random passwords (16 characters)
- 📊 **Password Strength Meter**: Visual feedback on password strength
- �️ **Reveal Password**: Temporarily show password (hides after 6 seconds)
- 📋 **Copy to Clipboard**: One-click password copying
- ✏️ **Edit & Delete**: Full CRUD operations

## Security Features

### Backend Security
- **AES-256-GCM encryption** for password storage (authenticated encryption)
- **Bcrypt password hashing** (12 rounds) for user authentication
- **JWT tokens** with 1-hour expiration
- **Input validation** and sanitization
- **Proper key derivation** using SHA-256
- **Environment variable validation** on startup

### Frontend Security (index.html - Client-side Vault)
- **PBKDF2** key derivation (150,000 iterations)
- **AES-GCM** authenticated encryption
- **Zero-knowledge architecture** - master password never leaves browser
- **localStorage encryption** - vault encrypted at rest

## Architecture

### Two Deployment Options

#### Option 1: Server-Backed Vault
- **Backend**: Node.js + Express + MongoDB
- **Frontend**: Uses `frontend/script.js` to communicate with API
- **Use Case**: Multi-device sync, team sharing
- **Security**: Server can decrypt passwords (consider security implications)

#### Option 2: Client-Only Vault
- **Frontend**: Self-contained `frontend/index.html`
- **Use Case**: Maximum privacy, single device, no server needed
- **Security**: Zero-knowledge - only you have the master password

## Installation

### 🔒 Client-Side Vault (Zero Setup!)

**No installation needed!** Just open and use:

```powershell
# Option 1: Use http-server
cd frontend
npx http-server -p 8080
# Open: http://127.0.0.1:8080/index.html

# Option 2: Just double-click
# Simply double-click frontend/index.html in File Explorer

# Option 3: Use any static server
cd frontend
python -m http.server 8080  # if Python installed
```

That's it! Open in browser and start using immediately. ✅

---

### ☕ Java Backend Service (Advanced Features)

**NEW!** Advanced password analysis and audit logging service using OOP concepts.

#### Prerequisites
- Java Development Kit (JDK) 8 or higher

#### Quick Start
```powershell
cd java-backend
run.bat  # Windows
# Or manually:
# javac -d bin src/main/java/com/securepass/**/*.java
# java -cp bin com.securepass.SecurePassApplication
```

#### Features
- 🎯 **Password Strength Analysis**: Entropy calculation, pattern detection
- 🔐 **Secure Password Generation**: Cryptographically strong passwords
- 📊 **Security Scanning**: Weak password detection, duplicate finder
- 📝 **Audit Logging**: Complete activity tracking with reports
- 📈 **Vault Statistics**: Usage metrics and analytics

#### OOP Concepts Demonstrated
- ✅ **Encapsulation**: Private fields, getters/setters
- ✅ **Abstraction**: Service interfaces (IPasswordAnalyzer, IAuditService)
- ✅ **Inheritance**: Interface implementations
- ✅ **Polymorphism**: Multiple analyzer implementations
- ✅ **Composition**: VaultManager composed of services
- ✅ **Design Patterns**: Builder, Facade, Dependency Injection

See [java-backend/README.md](java-backend/README.md) for detailed documentation.

---

### 🌐 Server-Backed Vault (Full Setup)

#### Prerequisites
- Node.js (v14 or higher)
- MongoDB (local or cloud instance)

#### Setup Steps

1. **Clone/Download the project**

2. **Install dependencies**
   ```powershell
   npm install
   ```

3. **Configure environment variables**
   
   Create a `.env` file in the root directory:
   ```powershell
   cp .env.example .env
   ```
   
   Edit `.env` and set your values:
   ```env
   MONGO_URI=mongodb://localhost:27017/securepass
   JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
   SECRET_KEY=your-strong-encryption-secret-key-change-this
   PORT=5000
   ```
   
   ⚠️ **IMPORTANT**: Use strong, random values for `JWT_SECRET` and `SECRET_KEY` in production!

4. **Start MongoDB** (if running locally)
   ```powershell
   mongod
   ```

5. **Start the server**
   
   Development mode (with auto-reload):
   ```powershell
   npm run dev
   ```
   
   Production mode:
   ```powershell
   npm start
   ```

6. **Access the application**
   - Server-backed vault: Open `http://localhost:5000/frontend/index.html`
   - Client-only vault: Open `frontend/index.html` directly in browser

## API Endpoints

### Authentication
- `POST /api/register` or `POST /api/auth/register` - Create new user
  - Body: `{ username, password }`
- `POST /api/login` or `POST /api/auth/login` - Login
  - Body: `{ username, password }`
  - Returns: `{ token }`

### Passwords (Requires Authentication)
- `GET /api/passwords` - Get all passwords for user
  - Header: `Authorization: Bearer <token>`
- `POST /api/passwords` - Create new password
  - Header: `Authorization: Bearer <token>`
  - Body: `{ service, url, username, password, category, icon }`
- `PUT /api/passwords/:id` - Update password
  - Header: `Authorization: Bearer <token>`
  - Body: `{ service, url, username, password, category, icon }`
- `DELETE /api/passwords/:id` - Delete password
  - Header: `Authorization: Bearer <token>`

## Project Structure

```
.
├── backend/
│   ├── server.js           # Main Express server
│   ├── cryptoUtils.js      # Encryption utilities (AES-GCM)
│   └── models/
│       ├── user.js         # User Mongoose model
│       └── password.js     # Password Mongoose model
├── frontend/
│   ├── index.html          # Client-side vault UI (can be standalone)
│   └── script.js           # Server API client code
├── package.json
├── .env.example
└── README.md
```

## Fixes Implemented

### Critical Security Fixes
1. ✅ **Upgraded encryption from AES-CBC to AES-GCM** (authenticated encryption)
2. ✅ **Proper key derivation** using SHA-256 for consistent 32-byte keys
3. ✅ **Fixed JWT Bearer token handling** (accepts both "Bearer token" and raw token)
4. ✅ **Input validation** and sanitization on all endpoints
5. ✅ **Environment variable validation** on server startup

### API & Architecture Fixes
6. ✅ **Consolidated models** - server now uses models from `backend/models/`
7. ✅ **Added missing PUT endpoint** for password updates
8. ✅ **Fixed route mismatches** - supports both `/api/*` and `/api/auth/*` endpoints
9. ✅ **Error handling** - proper try-catch blocks and error messages
10. ✅ **Added npm scripts** to package.json

### Code Quality
11. ✅ **Removed duplicate model definitions**
12. ✅ **Added comprehensive error handling**
13. ✅ **Better validation messages**
14. ✅ **Sorted password results by updatedAt**

## Security Considerations

### For Production Deployment

1. **Use HTTPS**: Never deploy without SSL/TLS
2. **Strong Secrets**: Generate cryptographically random JWT_SECRET and SECRET_KEY
   ```powershell
   node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
   ```
3. **Database Security**: 
   - Use MongoDB authentication
   - Restrict network access
   - Regular backups
4. **Rate Limiting**: Add rate limiting to auth endpoints
5. **CORS Configuration**: Restrict CORS to your domain only
6. **Regular Updates**: Keep dependencies updated
7. **Token Refresh**: Consider implementing refresh tokens
8. **Audit Logging**: Log authentication attempts and failures

### Known Trade-offs

- **Server-side decryption**: The server-backed approach means the server CAN decrypt passwords. For maximum security, use the client-only vault (index.html standalone).
- **Token expiry**: Tokens expire after 1 hour. Implement token refresh for better UX.
- **No password history**: Updates overwrite previous passwords.

## Usage

### 🔒 Client-Side Vault (Recommended for Privacy)

#### Quick Start
```powershell
# Start the vault server
cd frontend
npx http-server -p 8080

# Open in browser: http://127.0.0.1:8080/index.html
# Or simply double-click frontend/index.html
```

#### First Time Setup
1. **Create Master Password**
   - Open the vault in your browser
   - Enter a strong master password (12+ characters)
   - ⚠️ **CRITICAL**: Write this down and store safely - no recovery possible!
   - Click "Submit" to create your vault

2. **Add Your First Password**
   - Click **"+ Add"** button (top left)
   - Fill in: Service name, URL, Username, Password
   - Optional: Use **"Generate"** for strong random password
   - Select a category (Banking, Shopping, Email, etc.)
   - Click **"Save"**

#### Daily Usage
- **Search**: Type in search box to find passwords
- **Filter**: Click categories on left sidebar
- **Reveal**: Click 👁️ eye icon (hides after 6 seconds)
- **Copy**: Click 📋 copy icon to copy password
- **Edit**: Click ✏️ edit icon to modify
- **Delete**: Click 🗑️ trash icon to remove
- **Lock**: Click "Lock" button when leaving computer

#### 💾 Backup Your Vault (IMPORTANT!)

**How to Export:**
1. Click **"Export"** button (top right)
2. File `securepass-vault.json` downloads automatically
3. This file is encrypted - safe to store anywhere

**Where to Store Backups:**
- ✅ USB drive (in safe place)
- ✅ Cloud storage (Google Drive, Dropbox, OneDrive)
- ✅ Email to yourself
- ✅ Multiple locations (best practice!)

**How to Restore (Import):**
1. Click **"Import"** button
2. Select your backup file
3. Enter your master password
4. Vault restored! ✅

**Backup Schedule:**
- **Weekly**: Set a calendar reminder!
- **After important changes**: Export immediately
- **Multiple copies**: Store in 2-3 different places

#### 🔐 Security Features
- **AES-256-GCM** authenticated encryption
- **PBKDF2** key derivation (150,000 iterations)
- **Zero-knowledge** architecture - master password never stored
- **Local storage** - encrypted in browser localStorage
- **No server** - nothing leaves your device
- **🆕 Screenshot Prevention** - Detects and blocks screenshot attempts
- **🆕 Screen Recording Protection** - Blocks browser recording APIs
- **🆕 Auto-Lock on Screenshot** - Vault locks when capture detected
- **🆕 Visual Watermarking** - Makes screenshots identifiable
- **🆕 Focus Protection** - Blurs content when tab loses focus

#### ⚠️ Critical Reminders
- **Master password has NO recovery** - write it down safely!
- **Export weekly** - set a reminder now
- **Don't clear browser data** without exporting first
- **Backup is encrypted** - you still need master password to restore

---

### 🌐 Server-Backed Vault (For Multi-Device Sync)

#### Setup & Usage
1. Start MongoDB: `mongod`
2. Start server: `npm run dev`
3. Open browser: `http://localhost:5000/frontend/index.html`
4. Register a new account
5. Login with your credentials
6. Add passwords with service name, username, and password
7. Use search/filter to find passwords
8. Click reveal/copy icons to access passwords
9. Edit or delete as needed

**Note**: Server-backed vault syncs across devices but requires hosting and the server can decrypt passwords.

## Troubleshooting

### Client-Side Vault

**"I forgot my master password!"**
- Unfortunately, there's **no recovery possible**
- This is the privacy trade-off for zero-knowledge encryption
- Prevention: Write it down and store in a safe place

**"I cleared browser data and lost my vault!"**
- **Solution**: Import your backup file (this is why backups are critical!)
- **Prevention**: Export weekly and store backups safely

**"The vault is empty after browser update"**
- **Cause**: Browser data may have been cleared
- **Solution**: Click "Import" and restore from backup
- **Prevention**: Keep regular backups in multiple locations

**"Can't import - invalid format or wrong password"**
- Check that you're using the correct master password
- Ensure backup file isn't corrupted
- Try a different backup file if you have multiple

**"localStorage is full"**
- Browser limit: 5-10MB typically
- **Solution**: Export vault, start fresh, archive old passwords separately
- Client-side has size limitations

### Server-Backed Vault

**MongoDB connection fails**: Ensure MongoDB is running and MONGO_URI is correct

**Token invalid errors**: Token may have expired, login again

**Decryption errors**: If you see [DECRYPTION_ERROR], the SECRET_KEY may have changed or data is corrupted

**Port already in use**: Change PORT in .env or kill process using port 5000

## 🚀 Deployment

### Frontend (GitHub Pages)
Already deployed at: https://gaurav478-stack.github.io/Password_vault/

The client-side vault works 100% offline - no backend needed!

### Backend (Render - Free Tier)

Deploy your Node.js API to Render for multi-device sync:

1. **Quick Setup**:
   ```bash
   # Generate secrets for Render
   node generate-secrets.js
   
   # Push to GitHub (if not already done)
   git push origin master
   ```

2. **Deploy to Render**:
   - Go to https://render.com and sign up (free)
   - Click "New +" → "Web Service"
   - Connect your GitHub repo
   - Render will auto-detect `render.yaml`
   - Add environment variables (see generated secrets)
   - Deploy! ✅

3. **Full Instructions**: See [RENDER_DEPLOYMENT.md](RENDER_DEPLOYMENT.md)

### Java Microservice (Local Only)

The Java backend runs locally and provides:
- Password strength analysis
- Secure password generation
- Breach detection scanning
- Security audit reports

Currently designed for local development. For production Java deployment, consider:
- Docker containerization
- AWS Elastic Beanstalk
- Google Cloud Run
- Heroku (with Java buildpack)

## 📚 Documentation

- **[Complete Project Documentation](PROJECT_DOCUMENTATION.md)** - Overview, architecture, OOP concepts
- **[Render Deployment Guide](RENDER_DEPLOYMENT.md)** - Step-by-step API deployment
- **[API Reference](API_REFERENCE.md)** - Endpoint documentation
- **[Security Guide](SECURITY.md)** - Security features and best practices
- **[Online Docs](https://gaurav478-stack.github.io/Password_vault/docs/)** - Web-based documentation

## License

MIT

## Contributing

Feel free to submit issues and enhancement requests!

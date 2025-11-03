require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const bodyParser = require("body-parser");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { encrypt, decrypt } = require("./cryptoUtils");

// Import models from models directory
const User = require("./models/user");
const Password = require("./models/password");

const app = express();
app.use(cors());
app.use(bodyParser.json());

// Validate environment variables
if (!process.env.MONGO_URI) {
    console.error("❌ MONGO_URI is not defined in .env");
    process.exit(1);
}
if (!process.env.JWT_SECRET) {
    console.error("❌ JWT_SECRET is not defined in .env");
    process.exit(1);
}
if (!process.env.SECRET_KEY) {
    console.error("❌ SECRET_KEY is not defined in .env");
    process.exit(1);
}

// ✅ Connect MongoDB
mongoose.connect(process.env.MONGO_URI)
.then(() => console.log("✅ MongoDB connected"))
.catch(err => {
    console.error("❌ MongoDB error:", err);
    process.exit(1);
});

// ✅ Middleware: Auth check (supports both "Bearer token" and raw token)
function authMiddleware(req, res, next) {
    let token = req.headers["authorization"];
    if (!token) return res.status(401).json({ message: "No token provided" });

    // Strip "Bearer " prefix if present
    if (token.startsWith("Bearer ")) {
        token = token.slice(7);
    }

    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
        if (err) {
            return res.status(401).json({ message: "Invalid or expired token" });
        }
        req.userId = decoded.id;
        next();
    });
}

// ✅ Input validation helpers
function validateUsername(username) {
    if (!username || typeof username !== 'string') return false;
    if (username.length < 3 || username.length > 50) return false;
    // Allow alphanumeric, underscore, dash
    return /^[a-zA-Z0-9_-]+$/.test(username);
}

function validatePassword(password) {
    if (!password || typeof password !== 'string') return false;
    if (password.length < 6) return false;
    return true;
}

function sanitizeString(str, maxLength = 200) {
    if (!str) return '';
    return String(str).trim().slice(0, maxLength);
}

// ====================
// 🔐 AUTH ROUTES
// ====================

// Register (supports both /api/register and /api/auth/register)
async function handleRegister(req, res) {
    try {
        const { username, password } = req.body;

        // Validate input
        if (!validateUsername(username)) {
            return res.status(400).json({ 
                message: "Invalid username. Must be 3-50 characters, alphanumeric with underscores/dashes only" 
            });
        }

        if (!validatePassword(password)) {
            return res.status(400).json({ 
                message: "Invalid password. Must be at least 6 characters" 
            });
        }

        // Check if user exists
        const existingUser = await User.findOne({ username });
        if (existingUser) {
            return res.status(400).json({ message: "User already exists" });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 12);

        // Create user
        const newUser = new User({
            username,
            password: hashedPassword,
        });

        await newUser.save();
        res.json({ message: "User registered successfully" });
    } catch (err) {
        console.error("Register error:", err);
        res.status(500).json({ message: "Server error during registration" });
    }
}

// Login (supports both /api/login and /api/auth/login)
async function handleLogin(req, res) {
    try {
        const { username, password } = req.body;

        if (!username || !password) {
            return res.status(400).json({ message: "Username and password required" });
        }

        // Find user
        const user = await User.findOne({ username });
        if (!user) {
            return res.status(400).json({ message: "Invalid username or password" });
        }

        // Verify password
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: "Invalid username or password" });
        }

        // Create JWT
        const token = jwt.sign({ id: user._id }, process.env.JWT_SECRET, {
            expiresIn: "1h",
        });

        res.json({ token });
    } catch (err) {
        console.error("Login error:", err);
        res.status(500).json({ message: "Server error during login" });
    }
}

// Register routes at both endpoints for compatibility
app.post("/api/register", handleRegister);
app.post("/api/auth/register", handleRegister);
app.post("/api/login", handleLogin);
app.post("/api/auth/login", handleLogin);

// ====================
// 🔐 PASSWORD ROUTES
// ====================

// Get passwords for logged-in user
app.get("/api/passwords", authMiddleware, async (req, res) => {
    try {
        const passwords = await Password.find({ userId: req.userId }).sort({ updatedAt: -1 });
        
        // Decrypt passwords before sending to client
        const decryptedPasswords = passwords.map(p => {
            try {
                return {
                    ...p._doc,
                    password: decrypt(p.password, process.env.SECRET_KEY),
                };
            } catch (decryptErr) {
                console.error("Decryption error for password:", p._id, decryptErr);
                return {
                    ...p._doc,
                    password: "[DECRYPTION_ERROR]",
                };
            }
        });
        
        res.json(decryptedPasswords);
    } catch (err) {
        console.error("Get passwords error:", err);
        res.status(500).json({ message: "Failed to retrieve passwords" });
    }
});

// Save new password
app.post("/api/passwords", authMiddleware, async (req, res) => {
    try {
        const { service, url, username, password, category, icon } = req.body;

        // Validate required fields
        if (!service || !username || !password) {
            return res.status(400).json({ 
                message: "Service name, username, and password are required" 
            });
        }

        // Sanitize inputs
        const sanitizedData = {
            service: sanitizeString(service, 100),
            url: sanitizeString(url, 500),
            username: sanitizeString(username, 100),
            password: sanitizeString(password, 500),
            category: sanitizeString(category, 50),
            icon: sanitizeString(icon, 100),
        };

        // Encrypt password
        const encryptedPassword = encrypt(sanitizedData.password, process.env.SECRET_KEY);

        const newPassword = new Password({
            service: sanitizedData.service,
            url: sanitizedData.url,
            username: sanitizedData.username,
            password: encryptedPassword,
            category: sanitizedData.category,
            icon: sanitizedData.icon,
            userId: req.userId,
        });

        await newPassword.save();

        res.json({
            ...newPassword._doc,
            password: sanitizedData.password // return decrypted
        });
    } catch (err) {
        console.error("Create password error:", err);
        res.status(500).json({ message: "Failed to save password" });
    }
});

// Update password (PUT)
app.put("/api/passwords/:id", authMiddleware, async (req, res) => {
    try {
        const { service, url, username, password, category, icon } = req.body;

        // Find password and verify ownership
        const existingPassword = await Password.findOne({ 
            _id: req.params.id, 
            userId: req.userId 
        });

        if (!existingPassword) {
            return res.status(404).json({ message: "Password not found" });
        }

        // Validate required fields
        if (!service || !username || !password) {
            return res.status(400).json({ 
                message: "Service name, username, and password are required" 
            });
        }

        // Sanitize inputs
        const sanitizedData = {
            service: sanitizeString(service, 100),
            url: sanitizeString(url, 500),
            username: sanitizeString(username, 100),
            password: sanitizeString(password, 500),
            category: sanitizeString(category, 50),
            icon: sanitizeString(icon, 100),
        };

        // Encrypt new password
        const encryptedPassword = encrypt(sanitizedData.password, process.env.SECRET_KEY);

        // Update password
        existingPassword.service = sanitizedData.service;
        existingPassword.url = sanitizedData.url;
        existingPassword.username = sanitizedData.username;
        existingPassword.password = encryptedPassword;
        existingPassword.category = sanitizedData.category;
        existingPassword.icon = sanitizedData.icon;

        await existingPassword.save();

        res.json({
            ...existingPassword._doc,
            password: sanitizedData.password // return decrypted
        });
    } catch (err) {
        console.error("Update password error:", err);
        res.status(500).json({ message: "Failed to update password" });
    }
});

// Delete password
app.delete("/api/passwords/:id", authMiddleware, async (req, res) => {
    try {
        const result = await Password.findOneAndDelete({ 
            _id: req.params.id, 
            userId: req.userId 
        });

        if (!result) {
            return res.status(404).json({ message: "Password not found" });
        }

        res.json({ message: "Password deleted successfully" });
    } catch (err) {
        console.error("Delete password error:", err);
        res.status(500).json({ message: "Failed to delete password" });
    }
});

// ✅ Health check endpoint (for Render and monitoring)
app.get("/", (req, res) => {
    res.status(200).json({ 
        status: "healthy",
        message: "SecurePass Vault API is running",
        version: "1.0.0",
        timestamp: new Date().toISOString()
    });
});

app.get("/health", (req, res) => {
    res.status(200).json({ 
        status: "healthy",
        database: mongoose.connection.readyState === 1 ? "connected" : "disconnected",
        uptime: process.uptime()
    });
});

// Start server
const PORT = process.env.PORT || 5000;
app.listen(PORT, "0.0.0.0", () => {
    console.log(`🚀 Server running on port ${PORT}`);
    console.log(`🌍 Environment: ${process.env.NODE_ENV || 'development'}`);
    console.log(`📡 MongoDB: ${mongoose.connection.readyState === 1 ? 'Connected' : 'Connecting...'}`);
});

// --- Java microservice proxy endpoints (forward to Java API on port 8081) ---
const http = require('http');

function forwardToJava(path, method, body, callback) {
    const options = {
        hostname: '127.0.0.1',
        port: 8081,
        path: path,
        method: method || 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Content-Length': body ? Buffer.byteLength(body) : 0
        }
    };

    const req = http.request(options, (res) => {
        let data = '';
        res.on('data', chunk => data += chunk);
        res.on('end', () => callback(null, res.statusCode, data));
    });

    req.on('error', (err) => callback(err));
    if (body) req.write(body);
    req.end();
}

// POST /api/analyze -> forwards { password } to Java /analyze
app.post('/api/analyze', authMiddleware, (req, res) => {
    const payload = JSON.stringify({ password: req.body.password });
    forwardToJava('/analyze', 'POST', payload, (err, status, data) => {
        if (err) {
            console.error('Java API error:', err);
            return res.status(502).json({ message: 'Java microservice not available' });
        }
        try {
            return res.status(status).json(JSON.parse(data));
        } catch (e) {
            return res.status(status).send(data);
        }
    });
});

// POST /api/generate -> forwards { length, includeSpecial } to Java /generate
app.post('/api/generate', authMiddleware, (req, res) => {
    const payload = JSON.stringify({ length: req.body.length, includeSpecial: req.body.includeSpecial });
    forwardToJava('/generate', 'POST', payload, (err, status, data) => {
        if (err) {
            console.error('Java API error:', err);
            return res.status(502).json({ message: 'Java microservice not available' });
        }
        try {
            return res.status(status).json(JSON.parse(data));
        } catch (e) {
            return res.status(status).send(data);
        }
    });
});

// GET /api/scan -> forwards to Java /scan
app.get('/api/scan', authMiddleware, (req, res) => {
    forwardToJava('/scan', 'GET', null, (err, status, data) => {
        if (err) {
            console.error('Java API error:', err);
            return res.status(502).json({ message: 'Java microservice not available' });
        }
        try {
            return res.status(status).json(JSON.parse(data));
        } catch (e) {
            return res.status(status).send(data);
        }
    });
});

// GET /api/audit/report -> forwards to Java /audit/report
app.get('/api/audit/report', authMiddleware, (req, res) => {
    forwardToJava('/audit/report', 'GET', null, (err, status, data) => {
        if (err) {
            console.error('Java API error:', err);
            return res.status(502).json({ message: 'Java microservice not available' });
        }
        try {
            return res.status(status).json(JSON.parse(data));
        } catch (e) {
            return res.status(status).send(data);
        }
    });
});


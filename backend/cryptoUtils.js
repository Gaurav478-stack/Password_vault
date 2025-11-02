const crypto = require("crypto");

const algorithm = "aes-256-gcm";

// Derive a proper 32-byte key from the secret using SHA-256
function deriveKey(secret) {
    return crypto.createHash('sha256').update(secret).digest();
}

// Encrypt password using AES-256-GCM (authenticated encryption)
function encrypt(text, secretKey) {
    const key = deriveKey(secretKey);
    const iv = crypto.randomBytes(12); // 12 bytes for GCM
    const cipher = crypto.createCipheriv(algorithm, key, iv);
    
    let encrypted = cipher.update(text, "utf8", "hex");
    encrypted += cipher.final("hex");
    
    const authTag = cipher.getAuthTag().toString("hex");
    
    // Store IV:authTag:encrypted
    return iv.toString("hex") + ":" + authTag + ":" + encrypted;
}

// Decrypt password using AES-256-GCM
function decrypt(encryptedData, secretKey) {
    const key = deriveKey(secretKey);
    const [ivHex, authTagHex, encryptedText] = encryptedData.split(":");
    
    if (!ivHex || !authTagHex || !encryptedText) {
        throw new Error("Invalid encrypted data format");
    }
    
    const iv = Buffer.from(ivHex, "hex");
    const authTag = Buffer.from(authTagHex, "hex");
    const decipher = crypto.createDecipheriv(algorithm, key, iv);
    
    decipher.setAuthTag(authTag);
    
    let decrypted = decipher.update(encryptedText, "hex", "utf8");
    decrypted += decipher.final("utf8");
    return decrypted;
}

module.exports = { encrypt, decrypt };

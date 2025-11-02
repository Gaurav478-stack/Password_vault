package com.securepass.manager;

import com.securepass.model.AuditLog;
import com.securepass.model.Password;
import com.securepass.model.PasswordStrengthResult;
import com.securepass.service.IAuditService;
import com.securepass.service.IPasswordAnalyzer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Vault Manager - Main orchestration class
 * Demonstrates Composition, Dependency Injection, and Facade Pattern
 */
public class VaultManager {
    
    private final Map<String, Password> passwords;
    private final IPasswordAnalyzer passwordAnalyzer;
    private final IAuditService auditService;
    private final String userId;
    
    // Dependency Injection via Constructor
    public VaultManager(String userId, IPasswordAnalyzer passwordAnalyzer, IAuditService auditService) {
        this.userId = userId;
        this.passwords = new HashMap<>();
        this.passwordAnalyzer = passwordAnalyzer;
        this.auditService = auditService;
        
        logAudit("VAULT_INITIALIZED", null, "Vault manager initialized for user", true);
    }
    
    /**
     * Adds a new password to the vault
     */
    public void addPassword(Password password) {
        if (password == null) {
            logAudit("ADD_PASSWORD", null, "Attempted to add null password", false);
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        // Validate password strength before adding
        String decryptedPassword = password.getEncryptedPassword(); // In real scenario, decrypt first
        if (!passwordAnalyzer.validatePassword(decryptedPassword)) {
            logAudit("ADD_PASSWORD", password.getId(), "Password too weak", false);
            throw new IllegalArgumentException("Password does not meet security requirements");
        }
        
        passwords.put(password.getId(), password);
        logAudit("ADD_PASSWORD", password.getId(), 
                "Added password for service: " + password.getService(), true);
    }
    
    /**
     * Retrieves a password and records access
     */
    public Password getPassword(String id) {
        Password password = passwords.get(id);
        
        if (password == null) {
            logAudit("ACCESS_PASSWORD", id, "Password not found", false);
            return null;
        }
        
        password.recordAccess();
        logAudit("ACCESS_PASSWORD", id, "Password accessed for: " + password.getService(), true);
        return password;
    }
    
    /**
     * Updates an existing password
     */
    public void updatePassword(String id, String newEncryptedPassword) {
        Password password = passwords.get(id);
        
        if (password == null) {
            logAudit("UPDATE_PASSWORD", id, "Password not found", false);
            throw new IllegalArgumentException("Password not found: " + id);
        }
        
        password.setEncryptedPassword(newEncryptedPassword);
        logAudit("UPDATE_PASSWORD", id, "Password updated for: " + password.getService(), true);
    }
    
    /**
     * Deletes a password
     */
    public void deletePassword(String id) {
        Password password = passwords.remove(id);
        
        if (password == null) {
            logAudit("DELETE_PASSWORD", id, "Password not found", false);
        } else {
            logAudit("DELETE_PASSWORD", id, "Password deleted for: " + password.getService(), true);
        }
    }
    
    /**
     * Searches passwords by service name or username
     */
    public List<Password> searchPasswords(String query) {
        logAudit("SEARCH_PASSWORDS", null, "Search query: " + query, true);
        
        String lowerQuery = query.toLowerCase();
        return passwords.values().stream()
            .filter(p -> p.getService().toLowerCase().contains(lowerQuery) ||
                        p.getUsername().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets passwords by category
     */
    public List<Password> getPasswordsByCategory(String category) {
        return passwords.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }
    
    /**
     * Analyzes strength of a password
     */
    public PasswordStrengthResult analyzePasswordStrength(String password) {
        logAudit("ANALYZE_PASSWORD", null, "Password strength analyzed", true);
        return passwordAnalyzer.analyzeStrength(password);
    }
    
    /**
     * Generates a strong password
     */
    public String generatePassword(int length, boolean includeSpecialChars) {
        String password = passwordAnalyzer.generateStrongPassword(length, includeSpecialChars);
        logAudit("GENERATE_PASSWORD", null, "Strong password generated", true);
        return password;
    }
    
    /**
     * Gets vault statistics
     */
    public VaultStatistics getStatistics() {
        return new VaultStatistics(
            passwords.size(),
            passwords.values().stream().mapToInt(Password::getAccessCount).sum(),
            passwords.values().stream().collect(Collectors.groupingBy(Password::getCategory, Collectors.counting()))
        );
    }
    
    /**
     * Identifies weak passwords in vault
     */
    public List<Password> findWeakPasswords() {
        logAudit("SECURITY_SCAN", null, "Weak password scan initiated", true);
        
        List<Password> weakPasswords = new ArrayList<>();
        for (Password password : passwords.values()) {
            String decrypted = password.getEncryptedPassword(); // In real scenario, decrypt
            PasswordStrengthResult result = passwordAnalyzer.analyzeStrength(decrypted);
            if (result.getScore() < 50) {
                weakPasswords.add(password);
            }
        }
        
        logAudit("SECURITY_SCAN", null, "Found " + weakPasswords.size() + " weak passwords", true);
        return weakPasswords;
    }
    
    /**
     * Finds duplicate passwords
     */
    public Map<String, List<Password>> findDuplicatePasswords() {
        logAudit("SECURITY_SCAN", null, "Duplicate password scan initiated", true);
        
        Map<String, List<Password>> duplicates = new HashMap<>();
        Map<String, List<Password>> grouped = passwords.values().stream()
            .collect(Collectors.groupingBy(Password::getEncryptedPassword));
        
        grouped.forEach((pass, list) -> {
            if (list.size() > 1) {
                duplicates.put(pass, list);
            }
        });
        
        logAudit("SECURITY_SCAN", null, "Found " + duplicates.size() + " duplicate password groups", true);
        return duplicates;
    }
    
    /**
     * Generates security report
     */
    public String generateSecurityReport() {
        logAudit("GENERATE_REPORT", null, "Security report generated", true);
        return auditService.generateSecurityReport(userId);
    }
    
    // Private helper method
    private void logAudit(String action, String resourceId, String details, boolean success) {
        AuditLog log = new AuditLog.Builder()
            .id(UUID.randomUUID().toString())
            .action(action)
            .userId(userId)
            .resourceId(resourceId)
            .details(details)
            .ipAddress("127.0.0.1") // In real scenario, get from request
            .success(success)
            .build();
        
        auditService.logEvent(log);
    }
    
    /**
     * Inner class for vault statistics
     */
    public static class VaultStatistics {
        private final int totalPasswords;
        private final int totalAccesses;
        private final Map<String, Long> categoryBreakdown;
        
        public VaultStatistics(int totalPasswords, int totalAccesses, Map<String, Long> categoryBreakdown) {
            this.totalPasswords = totalPasswords;
            this.totalAccesses = totalAccesses;
            this.categoryBreakdown = categoryBreakdown;
        }
        
        public int getTotalPasswords() {
            return totalPasswords;
        }
        
        public int getTotalAccesses() {
            return totalAccesses;
        }
        
        public Map<String, Long> getCategoryBreakdown() {
            return categoryBreakdown;
        }
        
        @Override
        public String toString() {
            return "VaultStatistics{" +
                    "totalPasswords=" + totalPasswords +
                    ", totalAccesses=" + totalAccesses +
                    ", categories=" + categoryBreakdown.size() +
                    '}';
        }
    }
}

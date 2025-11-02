package com.securepass;

import com.securepass.manager.VaultManager;
import com.securepass.api.HttpApiServer;
import com.securepass.model.Password;
import com.securepass.model.PasswordStrengthResult;
import com.securepass.service.impl.AdvancedPasswordAnalyzer;
import com.securepass.service.impl.InMemoryAuditService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Main Application - Demonstrates all OOP features
 * Entry point for the SecurePass Vault backend service
 */
public class SecurePassApplication {
    
    private static VaultManager vaultManager;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("     SecurePass Vault - Java Backend Service");
        System.out.println("          Advanced Password Management");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();
        
        // Initialize dependencies (Dependency Injection)
        AdvancedPasswordAnalyzer passwordAnalyzer = new AdvancedPasswordAnalyzer();
        InMemoryAuditService auditService = new InMemoryAuditService();
        vaultManager = new VaultManager("user123", passwordAnalyzer, auditService);
        // Start the lightweight HTTP API (microservice) on port 8081
        try {
            HttpApiServer api = new HttpApiServer(vaultManager, 8081);
            // Start in a separate thread so demo output continues
            new Thread(() -> {
                try {
                    api.start();
                } catch (Exception e) {
                    System.err.println("Failed to start Java API server: " + e.getMessage());
                }
            }, "HttpApiServer-Thread").start();
        } catch (Exception e) {
            System.err.println("Error initializing API server: " + e.getMessage());
        }
        scanner = new Scanner(System.in);
        
        // Demo the features
        runDemo();
        
        // Interactive menu (commented out for auto-demo)
        // runInteractiveMenu();
        
        scanner.close();
    }
    
    private static void runDemo() {
        System.out.println("🎯 Running Feature Demonstration...\n");
        
        // 1. Password Strength Analysis
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1️⃣  PASSWORD STRENGTH ANALYSIS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] testPasswords = {
            "password123",
            "MyP@ssw0rd",
            "Tr0ub4dor&3",
            "correcthorsebatterystaple",
            "xK9#mP2$vN4@qL7!"
        };
        
        for (String pwd : testPasswords) {
            PasswordStrengthResult result = vaultManager.analyzePasswordStrength(pwd);
            System.out.println("Password: " + maskPassword(pwd));
            System.out.println("  Score: " + result.getScore() + "/100");
            System.out.println("  Level: " + result.getLevel());
            System.out.println("  Entropy: " + String.format("%.2f", result.getEntropy()) + " bits");
            System.out.println("  Feedback: " + result.getFeedback());
            System.out.println();
        }
        
        // 2. Password Generation
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("2️⃣  STRONG PASSWORD GENERATION");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (int i = 0; i < 3; i++) {
            String generated = vaultManager.generatePassword(16, true);
            PasswordStrengthResult result = vaultManager.analyzePasswordStrength(generated);
            System.out.println("Generated: " + generated);
            System.out.println("  Score: " + result.getScore() + "/100 | Level: " + result.getLevel());
            System.out.println();
        }
        
        // 3. Vault Operations
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("3️⃣  VAULT OPERATIONS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Add some passwords
        addSamplePasswords();
        
        // Search passwords
        System.out.println("\n🔍 Searching for 'google':");
        List<Password> searchResults = vaultManager.searchPasswords("google");
        searchResults.forEach(p -> System.out.println("  - " + p.getService() + " (" + p.getUsername() + ")"));
        
        // Get by category
        System.out.println("\n📁 Passwords in 'banking' category:");
        List<Password> bankingPasswords = vaultManager.getPasswordsByCategory("banking");
        bankingPasswords.forEach(p -> System.out.println("  - " + p.getService()));
        
        // 4. Security Analysis
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("4️⃣  SECURITY ANALYSIS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Find weak passwords
        System.out.println("⚠️  Weak Passwords:");
        List<Password> weakPasswords = vaultManager.findWeakPasswords();
        if (weakPasswords.isEmpty()) {
            System.out.println("  ✓ No weak passwords found!");
        } else {
            weakPasswords.forEach(p -> System.out.println("  - " + p.getService() + " (Score: weak)"));
        }
        
        // Find duplicates
        System.out.println("\n🔄 Duplicate Passwords:");
        Map<String, List<Password>> duplicates = vaultManager.findDuplicatePasswords();
        if (duplicates.isEmpty()) {
            System.out.println("  ✓ No duplicate passwords found!");
        } else {
            duplicates.forEach((pass, list) -> {
                System.out.println("  Used by " + list.size() + " services:");
                list.forEach(p -> System.out.println("    - " + p.getService()));
            });
        }
        
        // 5. Statistics
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("5️⃣  VAULT STATISTICS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        VaultManager.VaultStatistics stats = vaultManager.getStatistics();
        System.out.println("Total Passwords: " + stats.getTotalPasswords());
        System.out.println("Total Accesses: " + stats.getTotalAccesses());
        System.out.println("\nCategory Breakdown:");
        stats.getCategoryBreakdown().forEach((category, count) -> 
            System.out.println("  " + category + ": " + count));
        
        // 6. Audit Report
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("6️⃣  SECURITY AUDIT REPORT");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        String report = vaultManager.generateSecurityReport();
        System.out.println(report);
    }
    
    private static void addSamplePasswords() {
        System.out.println("Adding sample passwords to vault...");
        
        Password[] samplePasswords = {
            new Password(UUID.randomUUID().toString(), "Gmail", "john@gmail.com", 
                        "MyStr0ng!Pass", "email"),
            new Password(UUID.randomUUID().toString(), "Facebook", "john.doe", 
                        "F@ceb00k2024", "social"),
            new Password(UUID.randomUUID().toString(), "Chase Bank", "john.doe", 
                        "Ch@se$ecure99", "banking"),
            new Password(UUID.randomUUID().toString(), "Amazon", "john@email.com", 
                        "Amaz0n!Shop", "shopping"),
            new Password(UUID.randomUUID().toString(), "Google Workspace", "john@company.com", 
                        "W0rkG00gle#2024", "work")
        };
        
        for (Password password : samplePasswords) {
            try {
                vaultManager.addPassword(password);
                System.out.println("  ✓ Added: " + password.getService());
            } catch (Exception e) {
                System.out.println("  ✗ Failed to add " + password.getService() + ": " + e.getMessage());
            }
        }
        System.out.println();
    }
    
    private static String maskPassword(String password) {
        if (password.length() <= 4) {
            return "****";
        }
        return password.substring(0, 2) + "*".repeat(password.length() - 4) + password.substring(password.length() - 2);
    }
    
    // Interactive menu (optional - for manual testing)
    private static void runInteractiveMenu() {
        while (true) {
            System.out.println("\n═══════════════════════════════════════════════════");
            System.out.println("MENU:");
            System.out.println("1. Analyze Password Strength");
            System.out.println("2. Generate Strong Password");
            System.out.println("3. Add Password to Vault");
            System.out.println("4. Search Passwords");
            System.out.println("5. View Statistics");
            System.out.println("6. Security Scan");
            System.out.println("7. Generate Audit Report");
            System.out.println("0. Exit");
            System.out.println("═══════════════════════════════════════════════════");
            System.out.print("Choose option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 0:
                    System.out.println("Goodbye!");
                    return;
                case 1:
                    analyzePasswordInteractive();
                    break;
                case 2:
                    generatePasswordInteractive();
                    break;
                case 3:
                    addPasswordInteractive();
                    break;
                case 4:
                    searchPasswordsInteractive();
                    break;
                case 5:
                    viewStatistics();
                    break;
                case 6:
                    securityScan();
                    break;
                case 7:
                    System.out.println(vaultManager.generateSecurityReport());
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
    
    private static void analyzePasswordInteractive() {
        System.out.print("Enter password to analyze: ");
        String password = scanner.nextLine();
        PasswordStrengthResult result = vaultManager.analyzePasswordStrength(password);
        System.out.println("\n" + result);
    }
    
    private static void generatePasswordInteractive() {
        System.out.print("Enter length (8-32): ");
        int length = scanner.nextInt();
        System.out.print("Include special characters? (y/n): ");
        boolean includeSpecial = scanner.next().equalsIgnoreCase("y");
        String password = vaultManager.generatePassword(length, includeSpecial);
        System.out.println("Generated: " + password);
    }
    
    private static void addPasswordInteractive() {
        System.out.print("Service name: ");
        String service = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        
        Password newPassword = new Password(UUID.randomUUID().toString(), 
                                           service, username, password, category);
        vaultManager.addPassword(newPassword);
        System.out.println("✓ Password added successfully!");
    }
    
    private static void searchPasswordsInteractive() {
        System.out.print("Search query: ");
        String query = scanner.nextLine();
        List<Password> results = vaultManager.searchPasswords(query);
        System.out.println("Found " + results.size() + " results:");
        results.forEach(p -> System.out.println("  - " + p));
    }
    
    private static void viewStatistics() {
        VaultManager.VaultStatistics stats = vaultManager.getStatistics();
        System.out.println("\n" + stats);
    }
    
    private static void securityScan() {
        System.out.println("\n🔍 Running security scan...");
        List<Password> weak = vaultManager.findWeakPasswords();
        System.out.println("Weak passwords: " + weak.size());
        Map<String, List<Password>> duplicates = vaultManager.findDuplicatePasswords();
        System.out.println("Duplicate groups: " + duplicates.size());
    }
}

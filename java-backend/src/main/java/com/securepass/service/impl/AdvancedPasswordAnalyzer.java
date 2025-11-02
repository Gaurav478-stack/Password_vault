package com.securepass.service.impl;

import com.securepass.model.PasswordStrengthResult;
import com.securepass.service.IPasswordAnalyzer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Advanced Password Analyzer Implementation
 * Demonstrates Inheritance, Encapsulation, and Single Responsibility Principle
 */
public class AdvancedPasswordAnalyzer implements IPasswordAnalyzer {
    
    private static final Set<String> COMMON_PASSWORDS = new HashSet<>(Arrays.asList(
        "password", "123456", "123456789", "12345678", "12345", "1234567",
        "password1", "qwerty", "abc123", "111111", "123123", "admin",
        "letmein", "welcome", "monkey", "dragon", "master", "sunshine"
    ));
    
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    private final SecureRandom random;
    
    public AdvancedPasswordAnalyzer() {
        this.random = new SecureRandom();
    }
    
    @Override
    public PasswordStrengthResult analyzeStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrengthResult(0, "VERY_WEAK", "Password is empty", 
                false, false, false, false, 0, false, 0.0);
        }
        
        int length = password.length();
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0);
        boolean isCommon = COMMON_PASSWORDS.contains(password.toLowerCase());
        
        // Calculate entropy (bits of randomness)
        double entropy = calculateEntropy(password);
        
        // Calculate score (0-100)
        int score = 0;
        
        // Length contribution (max 40 points)
        if (length >= 8) score += 20;
        if (length >= 12) score += 10;
        if (length >= 16) score += 10;
        
        // Character variety (max 40 points)
        if (hasUpper) score += 10;
        if (hasLower) score += 10;
        if (hasDigit) score += 10;
        if (hasSpecial) score += 10;
        
        // Entropy bonus (max 20 points)
        if (entropy > 30) score += 5;
        if (entropy > 50) score += 5;
        if (entropy > 70) score += 5;
        if (entropy > 90) score += 5;
        
        // Penalties
        if (isCommon) score -= 50;
        if (hasSequentialChars(password)) score -= 10;
        if (hasRepeatedChars(password)) score -= 10;
        
        score = Math.max(0, Math.min(100, score));
        
        // Determine level
        String level;
        String feedback;
        if (score < 30) {
            level = "WEAK";
            feedback = "This password is too weak. Add more characters and variety.";
        } else if (score < 50) {
            level = "FAIR";
            feedback = "Password could be stronger. Consider adding special characters.";
        } else if (score < 70) {
            level = "GOOD";
            feedback = "Good password! Consider making it longer for better security.";
        } else if (score < 90) {
            level = "STRONG";
            feedback = "Strong password! Well done.";
        } else {
            level = "VERY_STRONG";
            feedback = "Excellent password! Maximum security achieved.";
        }
        
        if (isCommon) {
            feedback = "⚠️ This is a commonly used password! Please choose a unique one.";
        }
        
        return new PasswordStrengthResult(score, level, feedback, hasUpper, hasLower, 
                                         hasDigit, hasSpecial, length, isCommon, entropy);
    }
    
    @Override
    public boolean isCompromised(String password) {
        // Check against common passwords list
        // In production, this would check against HaveIBeenPwned API or similar
        return COMMON_PASSWORDS.contains(password.toLowerCase());
    }
    
    @Override
    public String generateStrongPassword(int length, boolean includeSpecialChars) {
        if (length < 8) {
            length = 8; // Minimum length
        }
        
        String charSet = UPPERCASE + LOWERCASE + NUMBERS;
        if (includeSpecialChars) {
            charSet += SPECIAL_CHARS;
        }
        
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one of each type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        
        if (includeSpecialChars) {
            password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        }
        
        // Fill remaining length
        while (password.length() < length) {
            password.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString());
    }
    
    @Override
    public boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        PasswordStrengthResult result = analyzeStrength(password);
        return result.getScore() >= 50 && !result.isCommonPassword();
    }
    
    // Private helper methods
    private double calculateEntropy(String password) {
        int charsetSize = 0;
        
        if (password.chars().anyMatch(Character::isUpperCase)) charsetSize += 26;
        if (password.chars().anyMatch(Character::isLowerCase)) charsetSize += 26;
        if (password.chars().anyMatch(Character::isDigit)) charsetSize += 10;
        if (password.chars().anyMatch(ch -> SPECIAL_CHARS.indexOf(ch) >= 0)) charsetSize += SPECIAL_CHARS.length();
        
        return password.length() * (Math.log(charsetSize) / Math.log(2));
    }
    
    private boolean hasSequentialChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            if (c2 == c1 + 1 && c3 == c2 + 1) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasRepeatedChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
    
    private String shuffleString(String str) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}

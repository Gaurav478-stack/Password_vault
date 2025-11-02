package com.securepass.service;

import com.securepass.model.PasswordStrengthResult;

/**
 * Interface for Password Analysis Services
 * Demonstrates Abstraction - defines contract without implementation
 */
public interface IPasswordAnalyzer {
    
    /**
     * Analyzes password strength and returns detailed result
     * @param password The password to analyze
     * @return PasswordStrengthResult containing analysis details
     */
    PasswordStrengthResult analyzeStrength(String password);
    
    /**
     * Checks if password has been compromised in known breaches
     * @param password The password to check
     * @return true if password appears in breach databases
     */
    boolean isCompromised(String password);
    
    /**
     * Generates a strong password with specified criteria
     * @param length Length of password to generate
     * @param includeSpecialChars Whether to include special characters
     * @return Generated password
     */
    String generateStrongPassword(int length, boolean includeSpecialChars);
    
    /**
     * Validates password against security policy rules
     * @param password The password to validate
     * @return true if password meets security requirements
     */
    boolean validatePassword(String password);
}

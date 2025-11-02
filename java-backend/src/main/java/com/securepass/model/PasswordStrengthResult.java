package com.securepass.model;

/**
 * Password Strength Analysis Result
 * Demonstrates Encapsulation and Data Transfer Object pattern
 */
public class PasswordStrengthResult {
    private final int score;
    private final String level;
    private final String feedback;
    private final boolean hasUpperCase;
    private final boolean hasLowerCase;
    private final boolean hasNumbers;
    private final boolean hasSpecialChars;
    private final int length;
    private final boolean isCommonPassword;
    private final double entropy;

    public PasswordStrengthResult(int score, String level, String feedback, 
                                  boolean hasUpperCase, boolean hasLowerCase, 
                                  boolean hasNumbers, boolean hasSpecialChars, 
                                  int length, boolean isCommonPassword, double entropy) {
        this.score = score;
        this.level = level;
        this.feedback = feedback;
        this.hasUpperCase = hasUpperCase;
        this.hasLowerCase = hasLowerCase;
        this.hasNumbers = hasNumbers;
        this.hasSpecialChars = hasSpecialChars;
        this.length = length;
        this.isCommonPassword = isCommonPassword;
        this.entropy = entropy;
    }

    public int getScore() {
        return score;
    }

    public String getLevel() {
        return level;
    }

    public String getFeedback() {
        return feedback;
    }

    public boolean hasUpperCase() {
        return hasUpperCase;
    }

    public boolean hasLowerCase() {
        return hasLowerCase;
    }

    public boolean hasNumbers() {
        return hasNumbers;
    }

    public boolean hasSpecialChars() {
        return hasSpecialChars;
    }

    public int getLength() {
        return length;
    }

    public boolean isCommonPassword() {
        return isCommonPassword;
    }

    public double getEntropy() {
        return entropy;
    }

    @Override
    public String toString() {
        return "PasswordStrengthResult{" +
                "score=" + score +
                ", level='" + level + '\'' +
                ", length=" + length +
                ", entropy=" + String.format("%.2f", entropy) +
                " bits}";
    }
}

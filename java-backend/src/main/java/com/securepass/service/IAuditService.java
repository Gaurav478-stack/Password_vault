package com.securepass.service;

import com.securepass.model.AuditLog;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for Audit Logging Service
 * Demonstrates Abstraction and Dependency Inversion Principle
 */
public interface IAuditService {
    
    /**
     * Logs an audit event
     * @param log The audit log entry to record
     */
    void logEvent(AuditLog log);
    
    /**
     * Retrieves audit logs for a specific user
     * @param userId The user ID
     * @param startDate Start date for filtering
     * @param endDate End date for filtering
     * @return List of audit logs
     */
    List<AuditLog> getLogsByUser(String userId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Retrieves audit logs by action type
     * @param action The action type (e.g., "LOGIN", "PASSWORD_ACCESSED")
     * @param limit Maximum number of logs to return
     * @return List of audit logs
     */
    List<AuditLog> getLogsByAction(String action, int limit);
    
    /**
     * Retrieves failed security events
     * @param limit Maximum number of logs to return
     * @return List of failed audit logs
     */
    List<AuditLog> getFailedEvents(int limit);
    
    /**
     * Generates security report for a user
     * @param userId The user ID
     * @return Security report as string
     */
    String generateSecurityReport(String userId);
}

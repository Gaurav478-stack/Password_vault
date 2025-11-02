package com.securepass.service.impl;

import com.securepass.model.AuditLog;
import com.securepass.service.IAuditService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-Memory Audit Service Implementation
 * Demonstrates Inheritance, Encapsulation, and Collection Management
 */
public class InMemoryAuditService implements IAuditService {
    
    private final List<AuditLog> auditLogs;
    private final DateTimeFormatter formatter;
    
    public InMemoryAuditService() {
        this.auditLogs = new ArrayList<>();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    @Override
    public void logEvent(AuditLog log) {
        if (log == null) {
            throw new IllegalArgumentException("Audit log cannot be null");
        }
        auditLogs.add(log);
        
        // Print to console for demonstration
        System.out.println("📝 AUDIT LOG: " + log.getAction() + 
                          " | User: " + log.getUserId() + 
                          " | Success: " + log.isSuccess() +
                          " | Time: " + log.getTimestamp().format(formatter));
    }
    
    @Override
    public List<AuditLog> getLogsByUser(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogs.stream()
            .filter(log -> log.getUserId().equals(userId))
            .filter(log -> log.getTimestamp().isAfter(startDate) && log.getTimestamp().isBefore(endDate))
            .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> getLogsByAction(String action, int limit) {
        return auditLogs.stream()
            .filter(log -> log.getAction().equalsIgnoreCase(action))
            .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AuditLog> getFailedEvents(int limit) {
        return auditLogs.stream()
            .filter(log -> !log.isSuccess())
            .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    @Override
    public String generateSecurityReport(String userId) {
        List<AuditLog> userLogs = auditLogs.stream()
            .filter(log -> log.getUserId().equals(userId))
            .collect(Collectors.toList());
        
        if (userLogs.isEmpty()) {
            return "No audit logs found for user: " + userId;
        }
        
        long totalEvents = userLogs.size();
        long successfulEvents = userLogs.stream().filter(AuditLog::isSuccess).count();
        long failedEvents = totalEvents - successfulEvents;
        
        Map<String, Long> actionCounts = userLogs.stream()
            .collect(Collectors.groupingBy(AuditLog::getAction, Collectors.counting()));
        
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════════════\n");
        report.append("          SECURITY AUDIT REPORT\n");
        report.append("═══════════════════════════════════════════════════\n");
        report.append("User ID: ").append(userId).append("\n");
        report.append("Generated: ").append(LocalDateTime.now().format(formatter)).append("\n");
        report.append("───────────────────────────────────────────────────\n");
        report.append("SUMMARY:\n");
        report.append("  Total Events: ").append(totalEvents).append("\n");
        report.append("  Successful: ").append(successfulEvents).append("\n");
        report.append("  Failed: ").append(failedEvents).append("\n");
        report.append("  Success Rate: ").append(String.format("%.1f%%", (successfulEvents * 100.0 / totalEvents))).append("\n");
        report.append("───────────────────────────────────────────────────\n");
        report.append("ACTIONS BREAKDOWN:\n");
        
        actionCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> report.append("  ")
                .append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append("\n"));
        
        report.append("───────────────────────────────────────────────────\n");
        report.append("RECENT ACTIVITY (Last 10 events):\n");
        
        userLogs.stream()
            .sorted(Comparator.comparing(AuditLog::getTimestamp).reversed())
            .limit(10)
            .forEach(log -> report.append("  [")
                .append(log.getTimestamp().format(formatter))
                .append("] ")
                .append(log.getAction())
                .append(log.isSuccess() ? " ✓" : " ✗")
                .append("\n"));
        
        report.append("═══════════════════════════════════════════════════\n");
        
        return report.toString();
    }
    
    // Additional utility methods
    public int getTotalLogCount() {
        return auditLogs.size();
    }
    
    public void clearLogs() {
        auditLogs.clear();
        System.out.println("🗑️ All audit logs cleared");
    }
    
    public List<AuditLog> getAllLogs() {
        return new ArrayList<>(auditLogs); // Return copy to maintain encapsulation
    }
}

package com.securepass.api;

import com.securepass.manager.VaultManager;
import com.securepass.model.Password;
import com.securepass.model.PasswordStrengthResult;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lightweight HTTP API wrapper for the SecurePass Vault using the built-in HttpServer.
 * Exposes a small set of endpoints for analysis, generation, scanning and audit reporting.
 */
public class HttpApiServer {

    private final VaultManager vaultManager;
    private final int port;
    private HttpServer server;

    public HttpApiServer(VaultManager vaultManager, int port) {
        this.vaultManager = vaultManager;
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/status", new StatusHandler());
        server.createContext("/analyze", new AnalyzeHandler());
        server.createContext("/generate", new GenerateHandler());
        server.createContext("/scan", new ScanHandler());
        server.createContext("/audit/report", new AuditHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Java API server running on http://localhost:" + port);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    private String readAll(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void writeJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String json = "{\"status\":\"ok\",\"service\":\"SecurePass Java API\"}";
            writeJson(exchange, 200, json);
        }
    }

    private class AnalyzeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String body = readAll(exchange.getRequestBody()).trim();
            // Expecting simple JSON: { "password":"..." }
            String pwd = extractJsonField(body, "password");
            if (pwd == null) {
                writeJson(exchange, 400, "{\"error\":\"password field required\"}");
                return;
            }
            PasswordStrengthResult result = vaultManager.analyzePasswordStrength(pwd);
            String json = String.format(
                "{\"score\":%d,\"level\":\"%s\",\"entropy\":%.2f,\"feedback\":\"%s\"}",
                result.getScore(), result.getLevel(), result.getEntropy(), escapeJson(result.getFeedback())
            );
            writeJson(exchange, 200, json);
        }
    }

    private class GenerateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String body = readAll(exchange.getRequestBody()).trim();
            String lengthStr = extractJsonField(body, "length");
            String includeSpecialStr = extractJsonField(body, "includeSpecial");
            int length = 16;
            boolean includeSpecial = true;
            try { if (lengthStr != null) length = Integer.parseInt(lengthStr); } catch (Exception ignored) {}
            try { if (includeSpecialStr != null) includeSpecial = Boolean.parseBoolean(includeSpecialStr); } catch (Exception ignored) {}

            String generated = vaultManager.generatePassword(length, includeSpecial);
            PasswordStrengthResult result = vaultManager.analyzePasswordStrength(generated);
            String json = String.format(
                "{\"password\":\"%s\",\"score\":%d,\"level\":\"%s\"}",
                escapeJson(generated), result.getScore(), result.getLevel()
            );
            writeJson(exchange, 200, json);
        }
    }

    private class ScanHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET") && !exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            List<Password> weak = vaultManager.findWeakPasswords();
            Map<String, List<Password>> duplicates = vaultManager.findDuplicatePasswords();

            String weakJson = weak.stream()
                .map(p -> String.format("{\"service\":\"%s\",\"username\":\"%s\"}", escapeJson(p.getService()), escapeJson(p.getUsername())))
                .collect(Collectors.joining(","));

            String dupJson = duplicates.entrySet().stream()
                .map(e -> {
                    String pass = escapeJson(e.getKey());
                    String services = e.getValue().stream()
                        .map(p -> String.format("\"%s\"", escapeJson(p.getService())))
                        .collect(Collectors.joining(","));
                    return String.format("{\"password\":\"%s\",\"services\":[%s]}", pass, services);
                })
                .collect(Collectors.joining(","));

            String json = String.format("{\"weak\":[%s],\"duplicates\":[%s]}", weakJson, dupJson);
            writeJson(exchange, 200, json);
        }
    }

    private class AuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String report = vaultManager.generateSecurityReport();
            // Return plain text in JSON field
            String json = String.format("{\"report\":\"%s\"}", escapeJson(report));
            writeJson(exchange, 200, json);
        }
    }

    // Very small helper to extract a simple top-level string or number field from JSON body
    private String extractJsonField(String body, String field) {
        if (body == null) return null;
        String key = '"' + field + '"';
        int idx = body.indexOf(key);
        if (idx == -1) return null;
        int colon = body.indexOf(':', idx);
        if (colon == -1) return null;
        int start = colon + 1;
        // Skip whitespace
        while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;
        if (start >= body.length()) return null;
        char c = body.charAt(start);
        if (c == '"') {
            int end = body.indexOf('"', start + 1);
            if (end == -1) return null;
            return body.substring(start + 1, end);
        } else {
            // number or boolean
            int end = start;
            while (end < body.length() && !Character.isWhitespace(body.charAt(end)) && body.charAt(end) != ',' && body.charAt(end) != '}') end++;
            return body.substring(start, end);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}

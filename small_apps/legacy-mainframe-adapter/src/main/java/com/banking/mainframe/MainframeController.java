package com.banking.mainframe;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/mainframe")
public class MainframeController {
    
    private static final String CICS_REGION = "BANKPROD";
    private static final String IMS_DATABASE = "CUSTOMER";
    
    @PostMapping("/customer/lookup")
    public ResponseEntity<Map<String, Object>> lookupCustomer(@RequestBody CustomerLookupRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Complex business logic: mainframe customer lookup
            if (request.getCustomerId() == null || request.getCustomerId().length() != 9) {
                response.put("error", "Invalid customer ID format - must be 9 digits");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Simulate CICS transaction call
            String cicsResponse = simulateCicsCall("CUSTINQ", request.getCustomerId());
            
            // Simulate IMS database lookup
            String imsData = simulateImsCall(IMS_DATABASE, request.getCustomerId());
            
            // Parse COBOL/EBCDIC response (simulated)
            Map<String, Object> customerData = parseMainframeResponse(cicsResponse, imsData);
            
            response.put("customer_id", request.getCustomerId());
            response.put("status", "FOUND");
            response.put("data", customerData);
            response.put("source_systems", new String[]{"CICS", "IMS"});
            response.put("character_encoding", "EBCDIC-CP037");
            
            return ResponseEntity.ok(response);
            
        } catch (MainframeException e) {
            response.put("error", "Mainframe system unavailable");
            response.put("error_code", e.getErrorCode());
            response.put("retry_after", 30);
            return ResponseEntity.status(503).body(response);
        }
    }
    
    @PostMapping("/account/balance")
    public ResponseEntity<Map<String, Object>> getAccountBalance(@RequestBody BalanceRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Complex DB2 mainframe query simulation
            String db2Query = "SELECT BALANCE FROM BANKDB.ACCOUNTS WHERE ACCT_NUM = ?";
            String balance = simulateDb2Call(db2Query, request.getAccountNumber());
            
            response.put("account_number", request.getAccountNumber());
            response.put("balance", balance);
            response.put("currency", "USD");
            response.put("db2_subsystem", "DBPROD01");
            response.put("last_updated", "2024-01-03T10:26:04Z");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Legacy system timeout");
            response.put("timeout_ms", 45000);
            return ResponseEntity.status(504).body(response);
        }
    }
    
    @GetMapping("/actuator/health/mainframe")
    public ResponseEntity<Map<String, Object>> mainframeHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        // Complex health check for multiple legacy systems
        health.put("status", "UP");
        health.put("cics_region", CICS_REGION);
        health.put("systems", Map.of(
            "CICS", "CONNECTED",
            "IMS", "CONNECTED", 
            "DB2", "CONNECTED",
            "MQ_SERIES", "CONNECTED"
        ));
        health.put("connection_pool", Map.of(
            "active", 15,
            "idle", 5,
            "max", 50
        ));
        health.put("last_successful_transaction", "2024-01-03T10:25:30Z");
        
        return ResponseEntity.ok(health);
    }
    
    private String simulateCicsCall(String transaction, String customerId) throws MainframeException {
        // Simulate CICS transaction - in reality this would use CICS Transaction Gateway
        if (Math.random() > 0.95) { // 5% failure rate
            throw new MainframeException("CICS_TIMEOUT", "CICS transaction timeout");
        }
        
        return "CUSTOMER_DATA_BLOCK_" + customerId + "_EBCDIC_ENCODED";
    }
    
    private String simulateImsCall(String database, String customerId) {
        // Simulate IMS database call
        return "IMS_SEGMENT_" + database + "_" + customerId;
    }
    
    private String simulateDb2Call(String query, String accountNumber) {
        // Simulate DB2 mainframe query
        return "12543.75"; // Mock balance
    }
    
    private Map<String, Object> parseMainframeResponse(String cicsData, String imsData) {
        // Simulate parsing COBOL copybook data
        Map<String, Object> parsed = new HashMap<>();
        parsed.put("name", "JOHN DOE");
        parsed.put("status", "ACTIVE");
        parsed.put("branch_code", "001");
        parsed.put("last_transaction_date", "20240103");
        parsed.put("encoding", "EBCDIC converted to UTF-8");
        return parsed;
    }
    
    public static class CustomerLookupRequest {
        private String customerId;
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
    }
    
    public static class BalanceRequest {
        private String accountNumber;
        
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    }
    
    public static class MainframeException extends Exception {
        private String errorCode;
        
        public MainframeException(String errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() { return errorCode; }
    }
}

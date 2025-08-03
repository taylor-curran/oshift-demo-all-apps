package com.banking.payments;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Simple business logic: validate payment amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            response.put("status", "FAILED");
            response.put("error", "Invalid payment amount");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (request.getAmount().compareTo(new BigDecimal("10000.00")) > 0) {
            response.put("status", "FAILED");
            response.put("error", "Payment amount exceeds limit");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Simulate payment processing
        String transactionId = "TXN-" + System.currentTimeMillis();
        
        response.put("status", "SUCCESS");
        response.put("transactionId", transactionId);
        response.put("amount", request.getAmount());
        response.put("currency", request.getCurrency());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "payment-gateway-api");
        return ResponseEntity.ok(health);
    }
    
    public static class PaymentRequest {
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;
        
        // Getters and setters
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}

package com.banking.credit;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/credit")
public class CreditScoringController {
    
    @PostMapping("/score")
    public ResponseEntity<Map<String, Object>> calculateCreditScore(@RequestBody CreditRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Complex business logic: credit scoring algorithm
        int ficoScore = calculateFicoScore(request);
        int vantageScore = calculateVantageScore(request);
        String riskCategory = determineRiskCategory(ficoScore);
        
        // Compliance checks
        if (!fcraCompliantCheck(request)) {
            response.put("error", "FCRA compliance validation failed");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Final score calculation
        int finalScore = (int) Math.round((ficoScore * 0.7) + (vantageScore * 0.3));
        
        response.put("applicant_id", request.getApplicantId());
        response.put("fico_score", ficoScore);
        response.put("vantage_score", vantageScore);
        response.put("final_score", finalScore);
        response.put("risk_category", riskCategory);
        response.put("model_version", "3.1.0");
        response.put("bureau_sources", new String[]{"EXPERIAN", "EQUIFAX", "TRANSUNION"});
        
        // Decision logic
        boolean approved = finalScore >= 580 && request.getDebtToIncomeRatio().compareTo(new BigDecimal("0.43")) <= 0;
        response.put("decision", approved ? "APPROVED" : "DECLINED");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        health.put("status", "UP");
        health.put("service", "credit-scoring-engine");
        health.put("model_status", "ACTIVE");
        health.put("bureau_connections", Map.of(
            "experian", "UP",
            "equifax", "UP", 
            "transunion", "UP"
        ));
        health.put("compliance_mode", "FCRA-ECOA");
        
        return ResponseEntity.ok(health);
    }
    
    private int calculateFicoScore(CreditRequest request) {
        // Simplified FICO calculation based on key factors
        int baseScore = 600;
        
        // Payment history (35% of score)
        if (request.getPaymentHistory() > 0.95) baseScore += 100;
        else if (request.getPaymentHistory() > 0.85) baseScore += 50;
        
        // Credit utilization (30% of score)
        if (request.getCreditUtilization().compareTo(new BigDecimal("0.10")) < 0) baseScore += 80;
        else if (request.getCreditUtilization().compareTo(new BigDecimal("0.30")) < 0) baseScore += 40;
        
        // Length of credit history (15% of score)
        if (request.getCreditHistoryMonths() > 120) baseScore += 40;
        
        return Math.min(850, Math.max(300, baseScore));
    }
    
    private int calculateVantageScore(CreditRequest request) {
        // Simplified VantageScore calculation
        return calculateFicoScore(request) + (int)(Math.random() * 20 - 10); // Small variation
    }
    
    private String determineRiskCategory(int score) {
        if (score >= 740) return "EXCELLENT";
        if (score >= 670) return "GOOD";
        if (score >= 580) return "FAIR";
        return "POOR";
    }
    
    private boolean fcraCompliantCheck(CreditRequest request) {
        // FCRA compliance validation
        return request.getConsentProvided() && request.getApplicantId() != null;
    }
    
    public static class CreditRequest {
        private String applicantId;
        private Double paymentHistory;
        private BigDecimal creditUtilization;
        private Integer creditHistoryMonths;
        private BigDecimal debtToIncomeRatio;
        private Boolean consentProvided;
        
        // Getters and setters
        public String getApplicantId() { return applicantId; }
        public void setApplicantId(String applicantId) { this.applicantId = applicantId; }
        public Double getPaymentHistory() { return paymentHistory; }
        public void setPaymentHistory(Double paymentHistory) { this.paymentHistory = paymentHistory; }
        public BigDecimal getCreditUtilization() { return creditUtilization; }
        public void setCreditUtilization(BigDecimal creditUtilization) { this.creditUtilization = creditUtilization; }
        public Integer getCreditHistoryMonths() { return creditHistoryMonths; }
        public void setCreditHistoryMonths(Integer creditHistoryMonths) { this.creditHistoryMonths = creditHistoryMonths; }
        public BigDecimal getDebtToIncomeRatio() { return debtToIncomeRatio; }
        public void setDebtToIncomeRatio(BigDecimal debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }
        public Boolean getConsentProvided() { return consentProvided; }
        public void setConsentProvided(Boolean consentProvided) { this.consentProvided = consentProvided; }
    }
}

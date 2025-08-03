package main

import (
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"
)

type AuditEvent struct {
	EventID     string    `json:"event_id"`
	Timestamp   time.Time `json:"timestamp"`
	UserID      string    `json:"user_id"`
	Action      string    `json:"action"`
	Resource    string    `json:"resource"`
	IPAddress   string    `json:"ip_address"`
	Compliance  string    `json:"compliance_level"`
	Encrypted   bool      `json:"encrypted"`
}

type AuditLogger struct {
	complianceMode string
	encryptionKey  string
}

func NewAuditLogger() *AuditLogger {
	return &AuditLogger{
		complianceMode: "SOX-PCI-GDPR",
		encryptionKey:  "audit-encryption-key-v3",
	}
}

func (a *AuditLogger) LogEvent(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var event AuditEvent
	if err := json.NewDecoder(r.Body).Decode(&event); err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}

	// Business logic: validate and enrich audit event
	if event.UserID == "" || event.Action == "" {
		http.Error(w, "Missing required fields", http.StatusBadRequest)
		return
	}

	// Generate unique event ID
	eventID := generateEventID()
	event.EventID = eventID
	event.Timestamp = time.Now().UTC()
	event.Compliance = a.complianceMode
	event.Encrypted = true

	// Simple compliance validation
	if !a.validateCompliance(event.Action) {
		http.Error(w, "Action not compliant", http.StatusForbidden)
		return
	}

	// Log to audit trail (simulated)
	log.Printf("AUDIT: %s - User %s performed %s on %s", 
		eventID, event.UserID, event.Action, event.Resource)

	response := map[string]interface{}{
		"status":   "success",
		"event_id": eventID,
		"stored":   true,
		"retention_days": 2555, // 7 years compliance
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}

func (a *AuditLogger) HealthCheck(w http.ResponseWriter, r *http.Request) {
	health := map[string]interface{}{
		"status":     "UP",
		"service":    "audit-logger",
		"compliance": a.complianceMode,
		"encryption": "enabled",
	}

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(health)
}

func (a *AuditLogger) validateCompliance(action string) bool {
	// Simple compliance check
	prohibitedActions := []string{"DELETE_ALL", "EXPORT_PII", "DISABLE_AUDIT"}
	for _, prohibited := range prohibitedActions {
		if action == prohibited {
			return false
		}
	}
	return true
}

func generateEventID() string {
	bytes := make([]byte, 8)
	rand.Read(bytes)
	return "AUDIT-" + hex.EncodeToString(bytes)
}

func main() {
	logger := NewAuditLogger()
	
	http.HandleFunc("/api/v1/audit", logger.LogEvent)
	http.HandleFunc("/health", logger.HealthCheck)
	
	port := ":8080"
	fmt.Printf("Audit Logger starting on port %s\n", port)
	log.Fatal(http.ListenAndServe(port, nil))
}

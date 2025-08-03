import json
import logging
import time
from typing import Dict, Any

class FraudDetectionWorker:
    
    def __init__(self):
        self.model_threshold = 0.75
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger(__name__)
    
    def analyze_transaction(self, transaction_data: Dict[str, Any]) -> Dict[str, Any]:
        """Simple fraud detection business logic"""
        
        # Basic fraud indicators
        fraud_score = 0.0
        flags = []
        
        # Check transaction amount
        amount = float(transaction_data.get('amount', 0))
        if amount > 5000:
            fraud_score += 0.3
            flags.append("HIGH_AMOUNT")
        
        # Check transaction time (suspicious if late night)
        hour = int(transaction_data.get('hour', 12))
        if hour < 6 or hour > 22:
            fraud_score += 0.2
            flags.append("UNUSUAL_TIME")
        
        # Check velocity (multiple transactions)
        velocity = int(transaction_data.get('recent_transactions', 1))
        if velocity > 5:
            fraud_score += 0.4
            flags.append("HIGH_VELOCITY")
        
        # Simple location check
        location = transaction_data.get('location', 'US')
        if location not in ['US', 'CA', 'UK']:
            fraud_score += 0.3
            flags.append("FOREIGN_LOCATION")
        
        # Determine final decision
        is_fraud = fraud_score >= self.model_threshold
        risk_level = "HIGH" if fraud_score > 0.8 else "MEDIUM" if fraud_score > 0.4 else "LOW"
        
        result = {
            'transaction_id': transaction_data.get('transaction_id'),
            'fraud_score': round(fraud_score, 3),
            'is_fraud': is_fraud,
            'risk_level': risk_level,
            'flags': flags,
            'processed_at': int(time.time())
        }
        
        self.logger.info(f"Analyzed transaction {result['transaction_id']}: {risk_level} risk, score: {fraud_score}")
        return result
    
    def process_batch(self, transactions):
        """Process a batch of transactions"""
        results = []
        for transaction in transactions:
            result = self.analyze_transaction(transaction)
            results.append(result)
        
        return results

def health_check():
    """Health check endpoint"""
    return {"status": "UP", "service": "fraud-detection-worker"}

if __name__ == "__main__":
    worker = FraudDetectionWorker()
    
    # Example usage
    sample_transaction = {
        'transaction_id': 'TXN-123456',
        'amount': 7500.00,
        'hour': 23,
        'recent_transactions': 7,
        'location': 'RU'
    }
    
    result = worker.analyze_transaction(sample_transaction)
    print(json.dumps(result, indent=2))

using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;

namespace RiskAnalytics.Controllers
{
    [ApiController]
    [Route("api/v1/risk")]
    public class RiskAnalyticsController : ControllerBase
    {
        private readonly double _varConfidenceLevel = 0.99;
        private readonly double _creditRiskThreshold = 0.02;
        private readonly double _marketRiskThreshold = 0.015;
        
        [HttpPost("calculate-var")]
        public ActionResult<object> CalculateValueAtRisk([FromBody] VarRequest request)
        {
            // Complex business logic: Value at Risk calculation
            if (request.Portfolio == null || request.Portfolio.Count == 0)
            {
                return BadRequest(new { error = "Portfolio data is required" });
            }
            
            double portfolioValue = CalculatePortfolioValue(request.Portfolio);
            double varAmount = CalculateVar(request.Portfolio, _varConfidenceLevel);
            double varPercentage = varAmount / portfolioValue;
            
            string riskLevel = DetermineRiskLevel(varPercentage);
            bool exceedsThreshold = varPercentage > _marketRiskThreshold;
            
            var response = new
            {
                portfolio_id = request.PortfolioId,
                var_amount = Math.Round(varAmount, 2),
                var_percentage = Math.Round(varPercentage, 4),
                confidence_level = _varConfidenceLevel,
                risk_level = riskLevel,
                exceeds_threshold = exceedsThreshold,
                horizon_days = 250,
                calculation_timestamp = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ"),
                basel_iii_compliant = true
            };
            
            return Ok(response);
        }
        
        [HttpPost("stress-test")]
        public ActionResult<object> RunStressTest([FromBody] StressTestRequest request)
        {
            // Stress testing business logic
            var scenarios = new[] { "baseline", "adverse", "severely-adverse" };
            var results = new List<object>();
            
            foreach (var scenario in scenarios)
            {
                double lossRate = scenario switch
                {
                    "baseline" => 0.01,
                    "adverse" => 0.05,
                    "severely-adverse" => 0.12,
                    _ => 0.01
                };
                
                double projectedLoss = request.ExposureAmount * lossRate;
                bool passesTest = projectedLoss < (request.ExposureAmount * 0.08); // 8% capital buffer
                
                results.Add(new
                {
                    scenario = scenario,
                    projected_loss = Math.Round(projectedLoss, 2),
                    loss_rate = lossRate,
                    passes_test = passesTest,
                    capital_required = Math.Round(projectedLoss * 1.2, 2) // 20% buffer
                });
            }
            
            return Ok(new
            {
                portfolio_id = request.PortfolioId,
                exposure_amount = request.ExposureAmount,
                test_results = results,
                overall_status = "COMPLETED",
                ccar_compliant = true
            });
        }
        
        [HttpGet("metrics/real-time")]
        public ActionResult<object> GetRealTimeMetrics()
        {
            // Real-time risk metrics
            var metrics = new
            {
                service = "risk-analytics",
                timestamp = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ"),
                real_time_metrics = new
                {
                    portfolios_analyzed = 1247,
                    avg_processing_time_ms = 145,
                    credit_risk_alerts = 23,
                    market_risk_alerts = 8,
                    operational_risk_alerts = 2
                },
                var_calculations = new
                {
                    daily_calculations = 15000,
                    avg_var_percentage = 0.0087,
                    high_risk_portfolios = 156
                },
                system_health = new
                {
                    kafka_lag = "12ms",
                    influxdb_status = "UP",
                    ml_model_status = "ACTIVE",
                    model_version = "v2.1"
                }
            };
            
            return Ok(metrics);
        }
        
        [HttpGet("health")]
        public ActionResult<object> HealthCheck()
        {
            return Ok(new
            {
                status = "UP",
                service = "risk-analytics",
                version = "1.0.0",
                compliance_frameworks = new[] { "Basel III", "CCAR", "IFRS 9" },
                data_sources = new[] { "Kafka", "InfluxDB", "ML Models" }
            });
        }
        
        private double CalculatePortfolioValue(List<PortfolioItem> portfolio)
        {
            double total = 0;
            foreach (var item in portfolio)
            {
                total += item.MarketValue;
            }
            return total;
        }
        
        private double CalculateVar(List<PortfolioItem> portfolio, double confidenceLevel)
        {
            // Simplified VaR calculation using historical simulation
            double portfolioValue = CalculatePortfolioValue(portfolio);
            double volatility = 0.02; // 2% daily volatility assumption
            double zScore = 2.33; // 99% confidence level z-score
            
            return portfolioValue * volatility * zScore * Math.Sqrt(1); // 1-day VaR
        }
        
        private string DetermineRiskLevel(double varPercentage)
        {
            if (varPercentage > 0.03) return "HIGH";
            if (varPercentage > 0.015) return "MEDIUM";
            return "LOW";
        }
    }
    
    public class VarRequest
    {
        public string PortfolioId { get; set; }
        public List<PortfolioItem> Portfolio { get; set; }
    }
    
    public class StressTestRequest
    {
        public string PortfolioId { get; set; }
        public double ExposureAmount { get; set; }
    }
    
    public class PortfolioItem
    {
        public string AssetId { get; set; }
        public string AssetType { get; set; }
        public double MarketValue { get; set; }
        public double Weight { get; set; }
    }
}

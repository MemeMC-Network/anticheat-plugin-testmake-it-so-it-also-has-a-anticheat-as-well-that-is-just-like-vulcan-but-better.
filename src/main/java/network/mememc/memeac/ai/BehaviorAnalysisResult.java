package network.mememc.memeac.ai;

import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.ViolationData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BehaviorAnalysisResult {
    
    private final Map<String, Double> componentScores;
    private final long timestamp;
    private int suspicionLevel;
    private String primaryReason;
    private boolean falsePositive;
    
    public BehaviorAnalysisResult() {
        this.componentScores = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
        this.suspicionLevel = 0;
        this.falsePositive = false;
    }
    
    public void addComponent(String component, double score) {
        componentScores.put(component, Math.max(0, Math.min(100, score)));
    }
    
    public double getComponentScore(String component) {
        return componentScores.getOrDefault(component, 0.0);
    }
    
    public void setSuspicionLevel(int level) {
        this.suspicionLevel = Math.max(0, Math.min(100, level));
        
        // Determine primary reason based on highest component score
        this.primaryReason = componentScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> formatReason(entry.getKey(), entry.getValue()))
            .orElse("Unknown behavioral anomaly");
    }
    
    private String formatReason(String component, double score) {
        switch (component) {
            case "movement":
                if (score > 80) return "Robotic movement patterns detected";
                if (score > 60) return "Unnatural movement consistency";
                return "Minor movement irregularities";
                
            case "combat":
                if (score > 80) return "Automated combat behavior detected";
                if (score > 60) return "Inhuman combat precision";
                return "Suspicious combat patterns";
                
            case "mining":
                if (score > 80) return "X-Ray-like mining efficiency";
                if (score > 60) return "Unnatural ore finding patterns";
                return "Suspicious mining behavior";
                
            case "temporal":
                if (score > 80) return "Bot-like session patterns";
                if (score > 60) return "Inhuman consistency over time";
                return "Temporal behavior anomalies";
                
            default:
                return "Behavioral anomaly in " + component;
        }
    }
    
    public ViolationData createViolationData(Check check, int certainty) {
        return new ViolationData(
            check,
            primaryReason + " (AI Analysis)",
            certainty,
            timestamp
        );
    }
    
    public boolean isHighRisk() {
        return suspicionLevel > 85;
    }
    
    public boolean isMediumRisk() {
        return suspicionLevel > 60 && suspicionLevel <= 85;
    }
    
    public boolean isLowRisk() {
        return suspicionLevel > 30 && suspicionLevel <= 60;
    }
    
    public String getDetailedAnalysis() {
        StringBuilder analysis = new StringBuilder();
        analysis.append("Behavioral Analysis Report\\n");
        analysis.append("Overall Suspicion Level: ").append(suspicionLevel).append("%\\n");
        analysis.append("Primary Concern: ").append(primaryReason).append("\\n\\n");
        
        analysis.append("Component Breakdown:\\n");
        componentScores.forEach((component, score) -> {
            analysis.append("- ").append(component.toUpperCase()).append(": ")
                     .append(String.format("%.1f", score)).append("%\\n");
        });
        
        analysis.append("\\nRisk Level: ");
        if (isHighRisk()) {
            analysis.append("HIGH - Immediate attention required");
        } else if (isMediumRisk()) {
            analysis.append("MEDIUM - Monitor closely");
        } else if (isLowRisk()) {
            analysis.append("LOW - Minor concerns");
        } else {
            analysis.append("MINIMAL - Normal behavior");
        }
        
        return analysis.toString();
    }
    
    public void markAsFalsePositive() {
        this.falsePositive = true;
    }
    
    public boolean wasFalsePositive() {
        return falsePositive;
    }
    
    // Getters
    public Map<String, Double> getComponents() {
        return new HashMap<>(componentScores);
    }
    
    public Set<String> getComponentNames() {
        return componentScores.keySet();
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public int getSuspicionLevel() {
        return suspicionLevel;
    }
    
    public String getPrimaryReason() {
        return primaryReason;
    }
    
    @Override
    public String toString() {
        return String.format("BehaviorAnalysisResult{suspicion=%d%%, reason='%s', components=%s}", 
                           suspicionLevel, primaryReason, componentScores);
    }
}
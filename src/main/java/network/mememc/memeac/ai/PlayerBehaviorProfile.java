package network.mememc.memeac.ai;

import network.mememc.memeac.data.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerBehaviorProfile {
    
    private final String playerName;
    private final long creationTime;
    private final Map<String, Double> behaviorBaseline;
    private final List<BehaviorAnalysisResult> analysisHistory;
    private final Map<String, Integer> actionCounts;
    
    private double movementConsistency = 0;
    private double combatConsistency = 0;
    private double miningConsistency = 0;
    private int sessionCount = 0;
    private long totalPlayTime = 0;
    
    public PlayerBehaviorProfile(String playerName) {
        this.playerName = playerName;
        this.creationTime = System.currentTimeMillis();
        this.behaviorBaseline = new HashMap<>();
        this.analysisHistory = new ArrayList<>();
        this.actionCounts = new HashMap<>();
        
        initializeBaseline();
    }
    
    private void initializeBaseline() {
        // Initialize baseline behavior metrics
        behaviorBaseline.put("avgMovementSpeed", 0.0);
        behaviorBaseline.put("avgCPS", 0.0);
        behaviorBaseline.put("avgMiningSpeed", 0.0);
        behaviorBaseline.put("movementVariance", 0.0);
        behaviorBaseline.put("combatAccuracy", 0.0);
        behaviorBaseline.put("reactionTime", 0.0);
    }
    
    public void updateMetrics(PlayerData data) {
        // Update movement metrics
        if (!data.getSpeedSamples().isEmpty()) {
            double avgSpeed = data.getSpeedSamples().stream()
                .mapToDouble(Double::doubleValue)
                .average().orElse(0);
            behaviorBaseline.put("avgMovementSpeed", avgSpeed);
            
            // Calculate movement variance
            double mean = avgSpeed;
            double variance = data.getSpeedSamples().stream()
                .mapToDouble(speed -> Math.pow(speed - mean, 2))
                .average().orElse(0);
            behaviorBaseline.put("movementVariance", variance);
        }
        
        // Update combat metrics
        double cps = data.getAverageCPS();
        behaviorBaseline.put("avgCPS", cps);
        behaviorBaseline.put("combatAccuracy", data.getHitAccuracy());
        behaviorBaseline.put("reactionTime", data.getAverageReactionTime());
        
        // Update mining metrics
        behaviorBaseline.put("avgMiningSpeed", data.getAverageMiningSpeed());
        
        // Increment session metrics
        sessionCount++;
        totalPlayTime = System.currentTimeMillis() - creationTime;
        
        // Update action counts
        actionCounts.put("totalMovements", actionCounts.getOrDefault("totalMovements", 0) + 1);
        actionCounts.put("totalAttacks", data.getTotalAttacks());
        actionCounts.put("totalBlocksBroken", data.getBlocksBroken());
    }
    
    public boolean hasRoboticSessionPattern() {
        if (sessionCount < 5) return false;
        
        // Check for very consistent session lengths
        long avgSessionTime = totalPlayTime / sessionCount;
        
        // If sessions are too consistent (within 5% variance), it might be a bot
        return analysisHistory.stream()
            .mapToLong(result -> result.getTimestamp())
            .distinct()
            .count() > 3 && movementConsistency > 95;
    }
    
    public double getActionConsistency() {
        if (analysisHistory.size() < 10) return 0;
        
        // Calculate consistency across different metrics
        double movementConsistency = calculateConsistency("movement");
        double combatConsistency = calculateConsistency("combat");
        double miningConsistency = calculateConsistency("mining");
        
        return (movementConsistency + combatConsistency + miningConsistency) / 3.0;
    }
    
    private double calculateConsistency(String component) {
        if (analysisHistory.size() < 5) return 0;
        
        List<Double> values = analysisHistory.stream()
            .mapToDouble(result -> result.getComponentScore(component))
            .boxed()
            .toList();
        
        if (values.isEmpty()) return 0;
        
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = values.stream()
            .mapToDouble(val -> Math.pow(val - mean, 2))
            .average().orElse(0);
        
        // Lower variance = higher consistency
        return Math.max(0, 100 - (variance * 10));
    }
    
    public void updateBaseline(BehaviorAnalysisResult result) {
        // Exponential moving average to update baseline
        double alpha = 0.1; // Learning rate
        
        for (String component : result.getComponents().keySet()) {
            double currentValue = behaviorBaseline.getOrDefault(component, 0.0);
            double newValue = result.getComponentScore(component);
            double updatedValue = alpha * newValue + (1 - alpha) * currentValue;
            behaviorBaseline.put(component, updatedValue);
        }
    }
    
    public void addAnalysisResult(BehaviorAnalysisResult result) {
        analysisHistory.add(result);
        
        // Keep only last 1000 results to prevent memory issues
        if (analysisHistory.size() > 1000) {
            analysisHistory.remove(0);
        }
    }
    
    public void optimizeThresholds() {
        // Analyze false positive rate and adjust thresholds
        long falsePositives = analysisHistory.stream()
            .filter(result -> result.getSuspicionLevel() > 80)
            .filter(BehaviorAnalysisResult::wasFalsePositive)
            .count();
        
        double falsePositiveRate = (double) falsePositives / analysisHistory.size();
        
        // If false positive rate is too high, increase tolerance
        if (falsePositiveRate > 0.1) { // 10% false positive rate
            adjustThresholds(1.1); // Make thresholds 10% more lenient
        } else if (falsePositiveRate < 0.02) { // Very low false positive rate
            adjustThresholds(0.95); // Make thresholds 5% stricter
        }
    }
    
    private void adjustThresholds(double factor) {
        // This would adjust internal thresholds based on learning
        // Implementation would depend on specific threshold storage
    }
    
    // Getters
    public String getPlayerName() { return playerName; }
    public long getCreationTime() { return creationTime; }
    public Map<String, Double> getBehaviorBaseline() { return new HashMap<>(behaviorBaseline); }
    public List<BehaviorAnalysisResult> getAnalysisHistory() { return new ArrayList<>(analysisHistory); }
    public int getSessionCount() { return sessionCount; }
    public long getTotalPlayTime() { return totalPlayTime; }
    public Map<String, Integer> getActionCounts() { return new HashMap<>(actionCounts); }
}
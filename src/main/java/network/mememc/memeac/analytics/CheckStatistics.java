package network.mememc.memeac.analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CheckStatistics {
    
    private final String checkName;
    private final AtomicLong violationCount = new AtomicLong(0);
    private final AtomicLong falsePositives = new AtomicLong(0);
    private final AtomicLong truePositives = new AtomicLong(0);
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    private final AtomicInteger executionCount = new AtomicInteger(0);
    
    // Certainty distribution
    private final AtomicInteger[] certaintyCounts = new AtomicInteger[11]; // 0-10 (0-100% in 10% increments)
    
    // Recent violations for trend analysis
    private final List<Long> recentViolationTimes = new ArrayList<>();
    private final Object recentViolationsLock = new Object();
    
    public CheckStatistics(String checkName) {
        this.checkName = checkName;
        
        // Initialize certainty counters
        for (int i = 0; i < certaintyCounts.length; i++) {
            certaintyCounts[i] = new AtomicInteger(0);
        }
    }
    
    public void recordViolation(int certainty) {
        violationCount.incrementAndGet();
        
        // Record certainty distribution
        int certIndex = Math.min(certainty / 10, 10);
        certaintyCounts[certIndex].incrementAndGet();
        
        // Add to recent violations
        synchronized (recentViolationsLock) {
            recentViolationTimes.add(System.currentTimeMillis());
            
            // Keep only last 100 violations
            if (recentViolationTimes.size() > 100) {
                recentViolationTimes.remove(0);
            }
        }
    }
    
    public void recordFalsePositive() {
        falsePositives.incrementAndGet();
    }
    
    public void recordTruePositive() {
        truePositives.incrementAndGet();
    }
    
    public void recordExecution(long executionTimeNanos) {
        totalExecutionTime.addAndGet(executionTimeNanos);
        executionCount.incrementAndGet();
    }
    
    public String getCheckName() {
        return checkName;
    }
    
    public long getViolationCount() {
        return violationCount.get();
    }
    
    public long getFalsePositives() {
        return falsePositives.get();
    }
    
    public long getTruePositives() {
        return truePositives.get();
    }
    
    public double getAccuracy() {
        long total = truePositives.get() + falsePositives.get();
        if (total == 0) return 99.0; // Default high accuracy if no data
        
        return (double) truePositives.get() / total * 100.0;
    }
    
    public double getAverageExecutionTime() {
        int executions = executionCount.get();
        if (executions == 0) return 0.0;
        
        return (double) totalExecutionTime.get() / executions / 1_000_000.0; // Convert to milliseconds
    }
    
    public int[] getCertaintyDistribution() {
        int[] distribution = new int[certaintyCounts.length];
        for (int i = 0; i < certaintyCounts.length; i++) {
            distribution[i] = certaintyCounts[i].get();
        }
        return distribution;
    }
    
    public double getViolationsPerHour() {
        synchronized (recentViolationsLock) {
            if (recentViolationTimes.isEmpty()) return 0.0;
            
            long currentTime = System.currentTimeMillis();
            long oneHourAgo = currentTime - (60 * 60 * 1000); // 1 hour in milliseconds
            
            long recentCount = recentViolationTimes.stream()
                .filter(time -> time > oneHourAgo)
                .count();
            
            return recentCount;
        }
    }
    
    public String getTrendAnalysis() {
        synchronized (recentViolationsLock) {
            if (recentViolationTimes.size() < 10) {
                return "Insufficient data";
            }
            
            long currentTime = System.currentTimeMillis();
            long halfHourAgo = currentTime - (30 * 60 * 1000); // 30 minutes
            long oneHourAgo = currentTime - (60 * 60 * 1000); // 1 hour
            
            long recentHalf = recentViolationTimes.stream()
                .filter(time -> time > halfHourAgo)
                .count();
            
            long previousHalf = recentViolationTimes.stream()
                .filter(time -> time > oneHourAgo && time <= halfHourAgo)
                .count();
            
            if (previousHalf == 0) {
                return recentHalf > 0 ? "Increasing" : "Stable";
            }
            
            double ratio = (double) recentHalf / previousHalf;
            
            if (ratio > 1.5) return "Rapidly Increasing";
            if (ratio > 1.1) return "Increasing";
            if (ratio < 0.5) return "Rapidly Decreasing";
            if (ratio < 0.9) return "Decreasing";
            return "Stable";
        }
    }
    
    public int getExecutionCount() {
        return executionCount.get();
    }
    
    public long getTotalExecutionTime() {
        return totalExecutionTime.get();
    }
    
    @Override
    public String toString() {
        return String.format("CheckStats{name='%s', violations=%d, accuracy=%.1f%%, avgTime=%.2fms, trend=%s}", 
                           checkName, violationCount.get(), getAccuracy(), getAverageExecutionTime(), getTrendAnalysis());
    }
}
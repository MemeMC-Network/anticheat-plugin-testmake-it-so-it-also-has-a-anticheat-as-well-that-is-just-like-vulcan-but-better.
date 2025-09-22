package network.mememc.memeac.analytics;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.data.ViolationData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StatisticsManager {
    
    private final MemeAC plugin;
    
    // Global statistics
    private final AtomicLong totalViolations = new AtomicLong(0);
    private final AtomicInteger totalPlayersTracked = new AtomicInteger(0);
    private final AtomicLong falsePositives = new AtomicLong(0);
    private final AtomicLong truePositives = new AtomicLong(0);
    
    // Check-specific statistics
    private final Map<String, CheckStatistics> checkStats = new ConcurrentHashMap<>();
    
    // Violation history (last 1000 violations)
    private final Queue<ViolationRecord> violationHistory = new LinkedList<>();
    private final int MAX_HISTORY_SIZE = 1000;
    
    // Performance metrics
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    private final AtomicInteger checksPerformed = new AtomicInteger(0);
    
    public StatisticsManager(MemeAC plugin) {
        this.plugin = plugin;
        initializeCheckStats();
    }
    
    private void initializeCheckStats() {
        for (Check check : plugin.getAntiCheatManager().getChecks()) {
            checkStats.put(check.getName(), new CheckStatistics(check.getName()));
        }
    }
    
    public void recordViolation(Player player, ViolationData violation) {
        totalViolations.incrementAndGet();
        
        // Record check-specific stats
        CheckStatistics stats = checkStats.get(violation.getCheck().getName());
        if (stats != null) {
            stats.recordViolation(violation.getCertainty());
        }
        
        // Add to violation history
        synchronized (violationHistory) {
            violationHistory.offer(new ViolationRecord(
                player.getName(),
                violation.getCheck().getName(),
                violation.getCertainty(),
                violation.getReason(),
                System.currentTimeMillis()
            ));
            
            if (violationHistory.size() > MAX_HISTORY_SIZE) {
                violationHistory.poll();
            }
        }
    }
    
    public void recordCheckExecution(String checkName, long executionTime) {
        checksPerformed.incrementAndGet();
        totalProcessingTime.addAndGet(executionTime);
        
        CheckStatistics stats = checkStats.get(checkName);
        if (stats != null) {
            stats.recordExecution(executionTime);
        }
    }
    
    public void recordFalsePositive(String checkName) {
        falsePositives.incrementAndGet();
        
        CheckStatistics stats = checkStats.get(checkName);
        if (stats != null) {
            stats.recordFalsePositive();
        }
    }
    
    public void recordTruePositive(String checkName) {
        truePositives.incrementAndGet();
        
        CheckStatistics stats = checkStats.get(checkName);
        if (stats != null) {
            stats.recordTruePositive();
        }
    }
    
    public GlobalStatistics getGlobalStatistics() {
        return new GlobalStatistics(
            totalViolations.get(),
            totalPlayersTracked.get(),
            falsePositives.get(),
            truePositives.get(),
            checksPerformed.get(),
            totalProcessingTime.get()
        );
    }
    
    public CheckStatistics getCheckStatistics(String checkName) {
        return checkStats.get(checkName);
    }
    
    public Map<String, CheckStatistics> getAllCheckStatistics() {
        return new HashMap<>(checkStats);
    }
    
    public List<ViolationRecord> getRecentViolations(int count) {
        synchronized (violationHistory) {
            return violationHistory.stream()
                .skip(Math.max(0, violationHistory.size() - count))
                .toList();
        }
    }
    
    public List<ViolationRecord> getViolationsByPlayer(String playerName) {
        synchronized (violationHistory) {
            return violationHistory.stream()
                .filter(record -> record.getPlayerName().equals(playerName))
                .toList();
        }
    }
    
    public List<ViolationRecord> getViolationsByCheck(String checkName) {
        synchronized (violationHistory) {
            return violationHistory.stream()
                .filter(record -> record.getCheckName().equals(checkName))
                .toList();
        }
    }
    
    public double getOverallAccuracy() {
        long total = truePositives.get() + falsePositives.get();
        if (total == 0) return 99.0; // Default accuracy
        
        return (double) truePositives.get() / total * 100.0;
    }
    
    public double getAverageProcessingTime() {
        int checks = checksPerformed.get();
        if (checks == 0) return 0.0;
        
        return (double) totalProcessingTime.get() / checks / 1_000_000.0; // Convert to milliseconds
    }
    
    public Map<String, Integer> getViolationsByHour() {
        Map<String, Integer> hourlyStats = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        
        synchronized (violationHistory) {
            for (ViolationRecord record : violationHistory) {
                long timeDiff = currentTime - record.getTimestamp();
                int hoursAgo = (int) (timeDiff / (1000 * 60 * 60));
                
                if (hoursAgo < 24) { // Last 24 hours
                    String hourKey = hoursAgo + "h ago";
                    hourlyStats.merge(hourKey, 1, Integer::sum);
                }
            }
        }
        
        return hourlyStats;
    }
    
    public void updatePlayerCount(int count) {
        totalPlayersTracked.set(count);
    }
    
    public void cleanup() {
        // Clean up old data to prevent memory issues
        long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        
        synchronized (violationHistory) {
            violationHistory.removeIf(record -> record.getTimestamp() < cutoffTime);
        }
    }
    
    // Inner classes for data structures
    public static class ViolationRecord {
        private final String playerName;
        private final String checkName;
        private final int certainty;
        private final String reason;
        private final long timestamp;
        
        public ViolationRecord(String playerName, String checkName, int certainty, String reason, long timestamp) {
            this.playerName = playerName;
            this.checkName = checkName;
            this.certainty = certainty;
            this.reason = reason;
            this.timestamp = timestamp;
        }
        
        public String getPlayerName() { return playerName; }
        public String getCheckName() { return checkName; }
        public int getCertainty() { return certainty; }
        public String getReason() { return reason; }
        public long getTimestamp() { return timestamp; }
    }
    
    public static class GlobalStatistics {
        private final long totalViolations;
        private final int totalPlayers;
        private final long falsePositives;
        private final long truePositives;
        private final int checksPerformed;
        private final long totalProcessingTime;
        
        public GlobalStatistics(long totalViolations, int totalPlayers, long falsePositives, 
                               long truePositives, int checksPerformed, long totalProcessingTime) {
            this.totalViolations = totalViolations;
            this.totalPlayers = totalPlayers;
            this.falsePositives = falsePositives;
            this.truePositives = truePositives;
            this.checksPerformed = checksPerformed;
            this.totalProcessingTime = totalProcessingTime;
        }
        
        public long getTotalViolations() { return totalViolations; }
        public int getTotalPlayers() { return totalPlayers; }
        public long getFalsePositives() { return falsePositives; }
        public long getTruePositives() { return truePositives; }
        public int getChecksPerformed() { return checksPerformed; }
        public long getTotalProcessingTime() { return totalProcessingTime; }
        
        public double getAccuracy() {
            long total = truePositives + falsePositives;
            return total > 0 ? (double) truePositives / total * 100.0 : 99.0;
        }
        
        public double getAverageProcessingTime() {
            return checksPerformed > 0 ? (double) totalProcessingTime / checksPerformed / 1_000_000.0 : 0.0;
        }
    }
}
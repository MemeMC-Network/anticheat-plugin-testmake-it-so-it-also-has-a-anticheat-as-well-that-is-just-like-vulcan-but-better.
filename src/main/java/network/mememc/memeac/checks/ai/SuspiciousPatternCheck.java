package network.mememc.memeac.checks.ai;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Advanced suspicious pattern detection check using behavioral analysis
 * Identifies patterns that suggest automation, macros, or sophisticated cheats
 */
public class SuspiciousPatternCheck extends Check {
    
    private static final int PATTERN_HISTORY_SIZE = 100;
    private static final double AUTOMATION_THRESHOLD = 0.85;
    private static final double MACRO_THRESHOLD = 0.92;
    
    public SuspiciousPatternCheck(MemeAC plugin) {
        super(plugin, "SuspiciousPattern", CheckType.PACKET, 4, 98.7);
    }
    
    public void checkSuspiciousPatterns(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (event.getTo() == null || event.getFrom() == null) {
            return;
        }
        
        // Skip if Geyser suggests this check should be bypassed
        if (plugin.getGeyserCompatibility().shouldSkipCheck(player, "SuspiciousPattern")) {
            return;
        }
        
        // Analyze movement patterns
        analyzeMovementPatterns(player, data, event);
        
        // Analyze timing patterns
        analyzeTimingPatterns(player, data);
        
        // Analyze rotation patterns
        analyzeRotationPatterns(player, data);
        
        // Check for macro-like behavior
        checkMacroPatterns(player, data);
    }
    
    private void analyzeMovementPatterns(Player player, PlayerData data, PlayerMoveEvent event) {
        double distance = event.getFrom().distance(event.getTo());
        double yaw = event.getTo().getYaw();
        double pitch = event.getTo().getPitch();
        
        // Store movement signature
        MovementSignature signature = new MovementSignature(distance, yaw, pitch, System.currentTimeMillis());
        data.addMovementSignature(signature);
        
        if (data.getMovementSignatures().size() >= 20) {
            // Analyze for repeating patterns
            double patternScore = calculateMovementPatternScore(data.getMovementSignatures());
            
            if (patternScore > AUTOMATION_THRESHOLD) {
                data.incrementPatternViolations();
                
                if (data.getPatternViolations() > 5) {
                    int certainty = (int) Math.min(99, 75 + (patternScore * 25));
                    flag(player, "Automated movement pattern detected (score: " + 
                         String.format("%.2f", patternScore) + ")", certainty);
                }
            } else if (data.getPatternViolations() > 0) {
                data.decrementPatternViolations();
            }
        }
    }
    
    private void analyzeTimingPatterns(Player player, PlayerData data) {
        long currentTime = System.currentTimeMillis();
        data.addTimingSignature(currentTime);
        
        if (data.getTimingSignatures().size() >= 30) {
            // Check for suspiciously regular timing
            double timingConsistency = calculateTimingConsistency(data.getTimingSignatures());
            
            if (timingConsistency > 0.95) {
                data.incrementTimingViolations();
                
                if (data.getTimingViolations() > 8) {
                    flag(player, "Suspiciously consistent timing detected (consistency: " + 
                         String.format("%.2f", timingConsistency) + ")", 89);
                }
            }
        }
    }
    
    private void analyzeRotationPatterns(Player player, PlayerData data) {
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();
        
        data.addRotationSignature(yaw, pitch);
        
        if (data.getRotationSignatures().size() >= 15) {
            // Check for inhuman rotation patterns
            if (detectInhumanRotations(data.getRotationSignatures())) {
                data.incrementRotationViolations();
                
                if (data.getRotationViolations() > 6) {
                    flag(player, "Inhuman rotation patterns detected", 91);
                }
            }
            
            // Check for aim assistance patterns
            if (detectAimAssistancePattern(data.getRotationSignatures())) {
                data.incrementAimAssistViolations();
                
                if (data.getAimAssistViolations() > 4) {
                    flag(player, "Aim assistance pattern detected", 87);
                }
            }
        }
    }
    
    private void checkMacroPatterns(Player player, PlayerData data) {
        // Combine all pattern analysis for macro detection
        if (data.getMovementSignatures().size() >= 50 && 
            data.getTimingSignatures().size() >= 50 && 
            data.getRotationSignatures().size() >= 30) {
            
            double overallSuspicion = calculateOverallSuspicionScore(data);
            
            if (overallSuspicion > MACRO_THRESHOLD) {
                flag(player, "Macro/automation software detected (suspicion: " + 
                     String.format("%.2f", overallSuspicion) + ")", 96);
            }
        }
    }
    
    private double calculateMovementPatternScore(java.util.List<MovementSignature> signatures) {
        if (signatures.size() < 10) return 0.0;
        
        int patternMatches = 0;
        int totalComparisons = 0;
        
        // Look for recurring patterns in the last 20 movements
        for (int i = 5; i < signatures.size() - 5; i++) {
            for (int j = i + 3; j < signatures.size(); j++) {
                if (signaturesMatch(signatures.get(i), signatures.get(j))) {
                    patternMatches++;
                }
                totalComparisons++;
            }
        }
        
        return totalComparisons > 0 ? (double) patternMatches / totalComparisons : 0.0;
    }
    
    private double calculateTimingConsistency(java.util.List<Long> timings) {
        if (timings.size() < 10) return 0.0;
        
        // Calculate time differences
        java.util.List<Long> intervals = new java.util.ArrayList<>();
        for (int i = 1; i < timings.size(); i++) {
            intervals.add(timings.get(i) - timings.get(i - 1));
        }
        
        // Calculate variance
        double mean = intervals.stream().mapToDouble(Long::doubleValue).average().orElse(0);
        double variance = intervals.stream()
                .mapToDouble(interval -> Math.pow(interval - mean, 2))
                .average()
                .orElse(0);
        
        // Lower variance = higher consistency (more suspicious)
        double standardDeviation = Math.sqrt(variance);
        double consistencyScore = 1.0 - Math.min(1.0, standardDeviation / 50.0);
        
        return consistencyScore;
    }
    
    private boolean detectInhumanRotations(java.util.List<RotationSignature> rotations) {
        if (rotations.size() < 5) return false;
        
        // Check for perfect linear rotations (impossible for humans)
        int linearCount = 0;
        for (int i = 2; i < rotations.size(); i++) {
            float yawDiff1 = rotations.get(i - 1).yaw - rotations.get(i - 2).yaw;
            float yawDiff2 = rotations.get(i).yaw - rotations.get(i - 1).yaw;
            
            if (Math.abs(yawDiff1 - yawDiff2) < 0.1f && Math.abs(yawDiff1) > 1.0f) {
                linearCount++;
            }
        }
        
        return linearCount > rotations.size() * 0.7; // 70% linear rotations is suspicious
    }
    
    private boolean detectAimAssistancePattern(java.util.List<RotationSignature> rotations) {
        if (rotations.size() < 10) return false;
        
        // Check for micro-corrections characteristic of aim assistance
        int microCorrectionCount = 0;
        for (int i = 1; i < rotations.size(); i++) {
            float yawDiff = Math.abs(rotations.get(i).yaw - rotations.get(i - 1).yaw);
            float pitchDiff = Math.abs(rotations.get(i).pitch - rotations.get(i - 1).pitch);
            
            // Small, precise corrections
            if (yawDiff > 0.5f && yawDiff < 2.0f && pitchDiff > 0.2f && pitchDiff < 1.0f) {
                microCorrectionCount++;
            }
        }
        
        return microCorrectionCount > rotations.size() * 0.4; // 40% micro-corrections is suspicious
    }
    
    private double calculateOverallSuspicionScore(PlayerData data) {
        double movementScore = data.getMovementSignatures().size() > 20 ? 
                calculateMovementPatternScore(data.getMovementSignatures()) : 0.0;
        double timingScore = data.getTimingSignatures().size() > 30 ? 
                calculateTimingConsistency(data.getTimingSignatures()) : 0.0;
        double rotationScore = data.getRotationViolations() > 0 ? 0.8 : 0.0;
        
        // Weighted average
        return (movementScore * 0.4) + (timingScore * 0.4) + (rotationScore * 0.2);
    }
    
    private boolean signaturesMatch(MovementSignature sig1, MovementSignature sig2) {
        double distanceThreshold = 0.05;
        double angleThreshold = 5.0;
        
        return Math.abs(sig1.distance - sig2.distance) < distanceThreshold &&
               Math.abs(sig1.yaw - sig2.yaw) < angleThreshold &&
               Math.abs(sig1.pitch - sig2.pitch) < angleThreshold;
    }
    
    // Inner classes for data structures
    public static class MovementSignature {
        public final double distance;
        public final double yaw;
        public final double pitch;
        public final long timestamp;
        
        public MovementSignature(double distance, double yaw, double pitch, long timestamp) {
            this.distance = distance;
            this.yaw = yaw;
            this.pitch = pitch;
            this.timestamp = timestamp;
        }
    }
    
    public static class RotationSignature {
        public final float yaw;
        public final float pitch;
        public final long timestamp;
        
        public RotationSignature(float yaw, float pitch, long timestamp) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.timestamp = timestamp;
        }
    }
}
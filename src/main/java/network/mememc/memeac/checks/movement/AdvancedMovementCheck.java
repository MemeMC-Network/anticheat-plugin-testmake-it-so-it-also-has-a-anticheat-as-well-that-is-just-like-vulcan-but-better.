package network.mememc.memeac.checks.movement;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Advanced movement check with Bedrock Edition compatibility
 * Uses machine learning inspired patterns and cross-platform support
 */
public class AdvancedMovementCheck extends Check {
    
    public AdvancedMovementCheck(MemeAC plugin) {
        super(plugin, "AdvancedMovement", CheckType.MOVEMENT, 6, 99.2);
    }
    
    public void checkAdvancedMovement(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (event.getTo() == null || event.getFrom() == null) {
            return;
        }
        
        // Get Geyser-adjusted thresholds
        double baseSpeedThreshold = 0.6;
        double adjustedThreshold = plugin.getGeyserCompatibility()
                .getAdjustedMovementThreshold(player, baseSpeedThreshold);
        
        // Calculate movement metrics
        double distance = event.getFrom().distance(event.getTo());
        double yDiff = event.getTo().getY() - event.getFrom().getY();
        
        // Advanced pattern analysis
        analyzeMovementPattern(player, data, distance, yDiff, adjustedThreshold);
        
        // Check for impossible acceleration
        checkAcceleration(player, data, distance);
        
        // Analyze consistency patterns (ML-inspired)
        analyzeBehavioralConsistency(player, data, distance, yDiff);
    }
    
    private void analyzeMovementPattern(Player player, PlayerData data, double distance, double yDiff, double threshold) {
        // Track movement history for pattern analysis
        data.addMovementSample(distance);
        
        if (distance > threshold && !player.isFlying() && !player.getAllowFlight()) {
            data.incrementSpeedViolations();
            
            if (data.getSpeedViolations() > getAdjustedMaxViolations(player)) {
                String platform = plugin.getGeyserCompatibility().isBedrockPlayer(player) ? "Bedrock" : "Java";
                flag(player, "Impossible movement speed detected (" + platform + " - " + 
                     String.format("%.2f", distance) + " > " + String.format("%.2f", threshold) + ")", 95);
            }
        } else if (data.getSpeedViolations() > 0) {
            data.decrementSpeedViolations();
        }
    }
    
    private void checkAcceleration(Player player, PlayerData data, double currentDistance) {
        double lastDistance = data.getLastDistance();
        
        if (lastDistance > 0) {
            double acceleration = currentDistance - lastDistance;
            
            // Impossible acceleration check
            if (Math.abs(acceleration) > 0.8 && !player.isFlying()) {
                data.incrementAccelerationViolations();
                
                if (data.getAccelerationViolations() > 4) {
                    flag(player, "Impossible acceleration pattern (accel: " + 
                         String.format("%.2f", acceleration) + ")", 92);
                }
            } else if (data.getAccelerationViolations() > 0) {
                data.decrementAccelerationViolations();
            }
        }
        
        data.setLastDistance(currentDistance);
    }
    
    private void analyzeBehavioralConsistency(Player player, PlayerData data, double distance, double yDiff) {
        // ML-inspired behavioral analysis
        data.updateMovementPrecision(calculateMovementPrecision(distance, yDiff));
        
        double avgPrecision = data.getAverageMovementPrecision();
        
        // Detect unnaturally consistent movement (bot-like behavior)
        if (avgPrecision > 98.5 && data.getMovementSamples().size() > 20) {
            flag(player, "Unnaturally consistent movement patterns detected (precision: " + 
                 String.format("%.1f", avgPrecision) + "%)", 88);
        }
        
        // Detect irregular patterns that suggest automation
        if (detectIrregularPatterns(data)) {
            flag(player, "Automated movement patterns detected", 90);
        }
    }
    
    private double calculateMovementPrecision(double distance, double yDiff) {
        // Calculate how "precise" the movement is (human movement has natural variation)
        double precision = 100.0;
        
        // Reduce precision for natural human-like variations
        if (Math.abs(yDiff) > 0.1) precision -= 5; // Natural jumping/falling
        if (distance > 0.2 && distance < 0.4) precision -= 3; // Normal walking speed
        
        // Very precise movements are suspicious
        if (distance > 0 && distance < 0.01) precision += 10; // Too precise
        
        return Math.max(0, Math.min(100, precision));
    }
    
    private boolean detectIrregularPatterns(PlayerData data) {
        // Analyze movement samples for bot-like patterns
        if (data.getMovementSamples().size() < 10) {
            return false;
        }
        
        // Check for repeating patterns (characteristic of bots)
        int patternCount = 0;
        double tolerance = 0.01;
        
        for (int i = 3; i < data.getMovementSamples().size(); i++) {
            double current = data.getMovementSamples().get(i);
            double previous = data.getMovementSamples().get(i - 3);
            
            if (Math.abs(current - previous) < tolerance) {
                patternCount++;
            }
        }
        
        // If more than 70% of movements follow a pattern, it's suspicious
        return (patternCount * 100.0 / data.getMovementSamples().size()) > 70;
    }
    
    private int getAdjustedMaxViolations(Player player) {
        // Get platform-adjusted violation threshold
        return plugin.getGeyserCompatibility()
                .getAdjustedViolationThreshold(player, maxViolations);
    }
}
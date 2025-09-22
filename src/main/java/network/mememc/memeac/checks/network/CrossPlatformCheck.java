package network.mememc.memeac.checks.network;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Cross-platform detection check that handles differences between Java and Bedrock Edition
 * Provides specialized detection for each platform type
 */
public class CrossPlatformCheck extends Check {
    
    public CrossPlatformCheck(MemeAC plugin) {
        super(plugin, "CrossPlatform", CheckType.NETWORK, 8, 97.8);
    }
    
    public void checkCrossPlatform(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Skip if this check should be bypassed for this platform
        if (plugin.getGeyserCompatibility().shouldSkipCheck(player, "CrossPlatform")) {
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (event.getTo() == null || event.getFrom() == null) {
            return;
        }
        
        boolean isBedrockPlayer = plugin.getGeyserCompatibility().isBedrockPlayer(player);
        
        if (isBedrockPlayer) {
            checkBedrockSpecific(player, data, event);
        } else {
            checkJavaSpecific(player, data, event);
        }
        
        // Universal checks that apply to both platforms
        checkUniversalAnomalies(player, data, event);
    }
    
    private void checkBedrockSpecific(Player player, PlayerData data, PlayerMoveEvent event) {
        // Bedrock-specific movement patterns and limitations
        double distance = event.getFrom().distance(event.getTo());
        
        // Bedrock has different sprint mechanics
        if (player.isSprinting() && distance > 0.8) {
            data.incrementBedrockSprintViolations();
            
            if (data.getBedrockSprintViolations() > 5) {
                flag(player, "Bedrock sprint speed exceeded (distance: " + 
                     String.format("%.2f", distance) + ")", 87);
            }
        }
        
        // Check for Java-only movement patterns on Bedrock players
        checkForJavaExploitsOnBedrock(player, data, event);
    }
    
    private void checkJavaSpecific(Player player, PlayerData data, PlayerMoveEvent event) {
        // Java-specific checks that don't apply to Bedrock
        double distance = event.getFrom().distance(event.getTo());
        double yDiff = event.getTo().getY() - event.getFrom().getY();
        
        // Java Edition precision movement checks
        if (isExcessivelyPrecise(distance, yDiff)) {
            data.incrementPrecisionViolations();
            
            if (data.getPrecisionViolations() > 7) {
                flag(player, "Excessive movement precision detected (Java)", 91);
            }
        }
        
        // Check for Bedrock-like movement on Java clients (suspicious)
        if (detectBedrockMovementOnJava(distance, yDiff)) {
            flag(player, "Suspicious cross-platform movement pattern", 85);
        }
    }
    
    private void checkUniversalAnomalies(Player player, PlayerData data, PlayerMoveEvent event) {
        // Checks that apply regardless of platform
        double distance = event.getFrom().distance(event.getTo());
        
        // Teleport-like movement detection
        if (distance > 10.0 && !player.isFlying() && !player.getAllowFlight()) {
            flag(player, "Teleport-like movement detected (distance: " + 
                 String.format("%.2f", distance) + ")", 99);
        }
        
        // Consistency analysis across platforms
        analyzeMovementConsistency(player, data, distance);
    }
    
    private void checkForJavaExploitsOnBedrock(Player player, PlayerData data, PlayerMoveEvent event) {
        // Look for Java-specific exploits being attempted on Bedrock
        double yDiff = event.getTo().getY() - event.getFrom().getY();
        
        // Bedrock doesn't support certain movement exploits
        if (yDiff > 1.5 && !player.isFlying()) {
            data.incrementInvalidJavaMovement();
            
            if (data.getInvalidJavaMovement() > 3) {
                flag(player, "Java exploit attempted on Bedrock client", 94);
            }
        }
    }
    
    private boolean isExcessivelyPrecise(double distance, double yDiff) {
        // Human movement naturally has small variations
        return distance > 0 && distance < 0.005 && Math.abs(yDiff) < 0.001;
    }
    
    private boolean detectBedrockMovementOnJava(double distance, double yDiff) {
        // Bedrock has characteristic movement patterns due to touch controls
        // This is a simplified detection - real implementation would be more complex
        return distance > 0.2 && distance < 0.35 && Math.abs(yDiff) > 0.05;
    }
    
    private void analyzeMovementConsistency(Player player, PlayerData data, double distance) {
        data.addCrossPlatformSample(distance);
        
        if (data.getCrossPlatformSamples().size() > 15) {
            double variance = calculateVariance(data.getCrossPlatformSamples());
            
            // Very low variance suggests automation
            if (variance < 0.001) {
                flag(player, "Movement variance too low (automated movement suspected)", 89);
            }
            
            // Very high variance might indicate speed hacks
            if (variance > 2.0) {
                flag(player, "Movement variance too high (speed modification suspected)", 88);
            }
        }
    }
    
    private double calculateVariance(java.util.List<Double> samples) {
        if (samples.size() < 2) return 0;
        
        double mean = samples.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = samples.stream()
                .mapToDouble(sample -> Math.pow(sample - mean, 2))
                .average()
                .orElse(0);
        
        return variance;
    }
}
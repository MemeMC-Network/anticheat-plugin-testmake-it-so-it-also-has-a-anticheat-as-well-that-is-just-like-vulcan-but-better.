package network.mememc.memeac.checks.network;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Network latency analysis check for detecting connection anomalies
 * Analyzes ping patterns, packet timing, and network consistency
 */
public class NetworkLatencyCheck extends Check {
    
    public NetworkLatencyCheck(MemeAC plugin) {
        super(plugin, "NetworkLatency", CheckType.NETWORK, 7, 96.5);
    }
    
    public void checkNetworkLatency(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (event.getTo() == null || event.getFrom() == null) {
            return;
        }
        
        // Get current ping
        int currentPing = player.spigot().getPing();
        
        // Analyze ping patterns
        analyzePingPatterns(player, data, currentPing);
        
        // Check for artificial latency manipulation
        checkLatencyManipulation(player, data, currentPing);
        
        // Analyze packet timing consistency
        analyzePacketTiming(player, data, event);
    }
    
    private void analyzePingPatterns(Player player, PlayerData data, int currentPing) {
        data.addPingSample(currentPing);
        
        if (data.getPingSamples().size() > 10) {
            // Check for suspiciously stable ping (impossible for real connections)
            long stablePingCount = data.getPingSamples().stream()
                    .filter(ping -> Math.abs(ping - currentPing) < 2)
                    .count();
            
            if (stablePingCount > 8) {
                flag(player, "Artificially stable ping detected (variance too low)", 85);
            }
            
            // Check for impossible ping values
            if (currentPing < 1 || currentPing > 2000) {
                flag(player, "Impossible ping value: " + currentPing + "ms", 95);
            }
            
            // Check for sudden ping spikes (lag switch detection)
            double avgPing = data.getPingSamples().stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(currentPing);
            
            if (currentPing > avgPing * 3 && currentPing > 200) {
                data.incrementLagSwitchViolations();
                
                if (data.getLagSwitchViolations() > 3) {
                    flag(player, "Lag switch detected (ping spike: " + currentPing + "ms)", 90);
                }
            }
        }
    }
    
    private void checkLatencyManipulation(Player player, PlayerData data, int currentPing) {
        // Check if ping is being artificially manipulated
        // Real connections have natural variance, fake ones often don't
        
        if (data.getPingSamples().size() > 15) {
            double variance = calculatePingVariance(data.getPingSamples());
            
            // Very low variance suggests artificial ping
            if (variance < 0.5 && currentPing > 10) {
                flag(player, "Artificial ping manipulation detected (variance: " + 
                     String.format("%.2f", variance) + ")", 87);
            }
            
            // Check for ping patterns that suggest automation
            if (detectPingPatterns(data.getPingSamples())) {
                flag(player, "Automated ping pattern detected", 89);
            }
        }
    }
    
    private void analyzePacketTiming(Player player, PlayerData data, PlayerMoveEvent event) {
        long currentTime = System.currentTimeMillis();
        long lastPacketTime = data.getLastPacketTime();
        
        if (lastPacketTime > 0) {
            long timeDiff = currentTime - lastPacketTime;
            data.addPacketTimingSample(timeDiff);
            
            // Check for suspiciously regular packet timing
            if (data.getPacketTimingSamples().size() > 20) {
                double variance = calculateTimingVariance(data.getPacketTimingSamples());
                
                // Human input has natural variance, bots often don't
                if (variance < 5.0) {
                    data.incrementRegularTimingViolations();
                    
                    if (data.getRegularTimingViolations() > 10) {
                        flag(player, "Automated packet timing detected (variance: " + 
                             String.format("%.2f", variance) + "ms)", 88);
                    }
                }
                
                // Check for impossible packet frequencies
                double avgTiming = data.getPacketTimingSamples().stream()
                        .mapToDouble(Long::doubleValue)
                        .average()
                        .orElse(50.0);
                
                if (avgTiming < 10) {
                    flag(player, "Impossible packet frequency (avg: " + 
                         String.format("%.1f", avgTiming) + "ms)", 93);
                }
            }
        }
        
        data.setLastPacketTime(currentTime);
    }
    
    private double calculatePingVariance(java.util.List<Integer> samples) {
        if (samples.size() < 2) return 0;
        
        double mean = samples.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
        double variance = samples.stream()
                .mapToDouble(sample -> Math.pow(sample - mean, 2))
                .average()
                .orElse(0);
        
        return variance;
    }
    
    private double calculateTimingVariance(java.util.List<Long> samples) {
        if (samples.size() < 2) return 0;
        
        double mean = samples.stream().mapToDouble(Long::doubleValue).average().orElse(0);
        double variance = samples.stream()
                .mapToDouble(sample -> Math.pow(sample - mean, 2))
                .average()
                .orElse(0);
        
        return variance;
    }
    
    private boolean detectPingPatterns(java.util.List<Integer> samples) {
        if (samples.size() < 8) return false;
        
        // Look for repeating patterns
        int patternLength = 3;
        for (int i = 0; i <= samples.size() - (patternLength * 2); i++) {
            boolean isPattern = true;
            for (int j = 0; j < patternLength; j++) {
                if (!samples.get(i + j).equals(samples.get(i + j + patternLength))) {
                    isPattern = false;
                    break;
                }
            }
            if (isPattern) return true;
        }
        
        return false;
    }
}
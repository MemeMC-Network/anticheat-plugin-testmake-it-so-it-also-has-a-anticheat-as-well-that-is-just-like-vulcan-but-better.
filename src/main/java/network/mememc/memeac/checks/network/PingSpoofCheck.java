package network.mememc.memeac.checks.network;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class PingSpoofCheck extends Check {
    
    private static final int SAMPLE_SIZE = 20;
    private static final double VARIANCE_THRESHOLD = 50.0; // milliseconds
    
    public PingSpoofCheck(MemeAC plugin) {
        super(plugin, "PingSpoof", CheckType.PACKET, 6, 94.2);
    }
    
    public void checkPingSpoof(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        long currentTime = System.currentTimeMillis();
        int currentPing = getPing(player);
        
        // Track ping samples
        data.addPingSample(currentPing);
        List<Integer> pingSamples = data.getPingSamples();
        
        if (pingSamples.size() < SAMPLE_SIZE) {
            return;
        }
        
        // Analyze ping patterns
        double avgPing = pingSamples.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = calculateVariance(pingSamples, avgPing);
        
        // Check for artificial ping consistency (ping spoofing)
        if (variance < 2.0 && avgPing > 50) { // Very low variance with reasonable ping
            data.incrementPingSpoofViolations();
            
            if (data.getPingSpoofViolations() > 3) {
                flag(player, "Artificial ping consistency detected (variance: " + String.format("%.1f", variance) + ")", 88);
            }
        } else if (variance > 2.0) {
            data.decrementPingSpoofViolations();
        }
        
        // Check for impossible ping values
        if (currentPing < 0 || currentPing > 2000) {
            flag(player, "Impossible ping value detected (" + currentPing + "ms)", 95);
        }
        
        // Check for ping manipulation patterns
        checkPingManipulation(player, data, pingSamples);
        
        // Check correlation with movement timing
        checkMovementPingCorrelation(player, data, currentPing, currentTime);
    }
    
    private void checkPingManipulation(Player player, PlayerData data, List<Integer> pingSamples) {
        if (pingSamples.size() < 10) return;
        
        // Check for sawtooth patterns (common in ping manipulation)
        int sawtoothCount = 0;
        for (int i = 2; i < pingSamples.size(); i++) {
            int prev2 = pingSamples.get(i - 2);
            int prev1 = pingSamples.get(i - 1);
            int current = pingSamples.get(i);
            
            // Detect sawtooth pattern: low-high-low or high-low-high
            if ((prev2 < prev1 && prev1 > current && Math.abs(prev1 - prev2) > 30 && Math.abs(prev1 - current) > 30) ||
                (prev2 > prev1 && prev1 < current && Math.abs(prev2 - prev1) > 30 && Math.abs(current - prev1) > 30)) {
                sawtoothCount++;
            }
        }
        
        if (sawtoothCount > 3) {
            flag(player, "Ping manipulation pattern detected (sawtooth)", 90);
        }
    }
    
    private void checkMovementPingCorrelation(Player player, PlayerData data, int ping, long currentTime) {
        // Check if movement timing correlates suspiciously with ping changes
        long lastMoveTime = data.getLastMoveTime();
        if (lastMoveTime > 0) {
            long moveInterval = currentTime - lastMoveTime;
            
            // If ping suddenly drops during combat/movement, it might be manipulation
            List<Integer> pingSamples = data.getPingSamples();
            if (pingSamples.size() >= 2) {
                int lastPing = pingSamples.get(pingSamples.size() - 2);
                int pingDrop = lastPing - ping;
                
                if (pingDrop > 100 && moveInterval < 200 && data.getTotalAttacks() > 0) {
                    data.incrementSuspiciousPingDrops();
                    
                    if (data.getSuspiciousPingDrops() > 2) {
                        flag(player, "Suspicious ping drops during combat (" + pingDrop + "ms drop)", 85);
                    }
                }
            }
        }
    }
    
    private double calculateVariance(List<Integer> values, double mean) {
        double variance = 0.0;
        for (int value : values) {
            variance += Math.pow(value - mean, 2);
        }
        return variance / values.size();
    }
    
    private int getPing(Player player) {
        try {
            // This would normally use reflection to get ping from NMS
            // For now, return a simulated value
            return (int) (Math.random() * 100) + 20;
        } catch (Exception e) {
            return 50; // Default ping
        }
    }
}
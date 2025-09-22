package network.mememc.memeac.ai;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.data.ViolationData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BehaviorAnalysisEngine {
    
    private final MemeAC plugin;
    private final Map<String, PlayerBehaviorProfile> playerProfiles;
    
    public BehaviorAnalysisEngine(MemeAC plugin) {
        this.plugin = plugin;
        this.playerProfiles = new HashMap<>();
    }
    
    public void analyzeBehavior(Player player) {
        PlayerBehaviorProfile profile = getOrCreateProfile(player);
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        // Update behavior metrics
        profile.updateMetrics(data);
        
        // Analyze patterns
        BehaviorAnalysisResult result = analyzePatterns(profile, data);
        
        if (result.getSuspicionLevel() > 80) {
            // Create a virtual AI check for violation handling
            Check aiCheck = new Check(plugin, "AI-Analysis", Check.CheckType.PACKET, 15, 97.8) {};
            
            // Create violation data
            ViolationData violation = new ViolationData(
                aiCheck,
                result.getPrimaryReason() + " (AI Analysis)",
                result.getSuspicionLevel(),
                System.currentTimeMillis()
            );
            
            // Handle violation
            plugin.getViolationManager().handleViolation(player, violation);
        }
        
        // Update learning model
        updateLearningModel(profile, result);
    }
    
    private PlayerBehaviorProfile getOrCreateProfile(Player player) {
        return playerProfiles.computeIfAbsent(player.getName(), 
            k -> new PlayerBehaviorProfile(player.getName()));
    }
    
    private BehaviorAnalysisResult analyzePatterns(PlayerBehaviorProfile profile, PlayerData data) {
        BehaviorAnalysisResult result = new BehaviorAnalysisResult();
        
        // Movement pattern analysis
        double movementSuspicion = analyzeMovementPatterns(profile, data);
        result.addComponent("movement", movementSuspicion);
        
        // Combat pattern analysis
        double combatSuspicion = analyzeCombatPatterns(profile, data);
        result.addComponent("combat", combatSuspicion);
        
        // Mining pattern analysis
        double miningSuspicion = analyzeMiningPatterns(profile, data);
        result.addComponent("mining", miningSuspicion);
        
        // Temporal pattern analysis
        double temporalSuspicion = analyzeTemporalPatterns(profile);
        result.addComponent("temporal", temporalSuspicion);
        
        // Calculate overall suspicion using weighted average
        double overallSuspicion = (movementSuspicion * 0.3) + 
                                 (combatSuspicion * 0.3) + 
                                 (miningSuspicion * 0.2) + 
                                 (temporalSuspicion * 0.2);
        
        result.setSuspicionLevel((int) overallSuspicion);
        
        return result;
    }
    
    private double analyzeMovementPatterns(PlayerBehaviorProfile profile, PlayerData data) {
        double suspicion = 0;
        
        // Check for robotic movement patterns
        List<Double> speeds = data.getSpeedSamples();
        if (speeds.size() > 20) {
            double variance = calculateVariance(speeds);
            
            // Very low variance indicates robotic movement
            if (variance < 0.01) {
                suspicion += 30;
            }
            
            // Check for perfectly consistent timing
            double avgSpeed = speeds.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            long consistentSpeedCount = speeds.stream()
                .mapToLong(speed -> Math.abs(speed - avgSpeed) < 0.001 ? 1 : 0)
                .sum();
            
            if (consistentSpeedCount > speeds.size() * 0.8) {
                suspicion += 25;
            }
        }
        
        // Check for impossible precision
        if (data.getMovementPrecision() > 95) {
            suspicion += 20;
        }
        
        return Math.min(suspicion, 100);
    }
    
    private double analyzeCombatPatterns(PlayerBehaviorProfile profile, PlayerData data) {
        double suspicion = 0;
        
        // Check for perfect timing patterns
        List<Long> attackTimes = new ArrayList<>(data.getAttackTimes());
        if (attackTimes.size() > 10) {
            List<Long> intervals = new ArrayList<>();
            for (int i = 1; i < attackTimes.size(); i++) {
                intervals.add(attackTimes.get(i) - attackTimes.get(i-1));
            }
            
            double intervalVariance = calculateVariance(intervals.stream()
                .mapToDouble(Long::doubleValue).boxed().toList());
            
            // Perfect timing indicates auto-clicker
            if (intervalVariance < 5) {
                suspicion += 40;
            }
        }
        
        // Check for impossible accuracy
        if (data.getHitAccuracy() > 98 && data.getTotalAttacks() > 50) {
            suspicion += 30;
        }
        
        // Check for inhuman reaction times
        if (data.getAverageReactionTime() < 100) { // Less than 100ms
            suspicion += 25;
        }
        
        return Math.min(suspicion, 100);
    }
    
    private double analyzeMiningPatterns(PlayerBehaviorProfile profile, PlayerData data) {
        double suspicion = 0;
        
        // Check ore finding efficiency
        if (data.getBlocksBroken() > 500) {
            double oreRatio = (double) data.getOresMined() / data.getBlocksBroken();
            
            if (oreRatio > 0.12) {
                suspicion += (oreRatio - 0.12) * 500; // Scale suspicion
            }
        }
        
        // Check for unnatural mining speeds
        double avgMiningSpeed = data.getAverageMiningSpeed();
        if (avgMiningSpeed > 2.5) { // Blocks per second
            suspicion += 20;
        }
        
        return Math.min(suspicion, 100);
    }
    
    private double analyzeTemporalPatterns(PlayerBehaviorProfile profile) {
        double suspicion = 0;
        
        // Check for bot-like session patterns
        if (profile.hasRoboticSessionPattern()) {
            suspicion += 15;
        }
        
        // Check for inhuman consistency
        if (profile.getActionConsistency() > 95) {
            suspicion += 20;
        }
        
        return Math.min(suspicion, 100);
    }
    
    private void updateLearningModel(PlayerBehaviorProfile profile, BehaviorAnalysisResult result) {
        // Update the player's behavior baseline
        profile.updateBaseline(result);
        
        // Store patterns for machine learning
        profile.addAnalysisResult(result);
        
        // Adaptive threshold adjustment
        if (profile.getAnalysisHistory().size() > 100) {
            profile.optimizeThresholds();
        }
    }
    
    private double calculateVariance(List<Double> values) {
        if (values.isEmpty()) return 0;
        
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = values.stream()
            .mapToDouble(val -> Math.pow(val - mean, 2))
            .average()
            .orElse(0);
        
        return variance;
    }
    
    public void cleanup() {
        playerProfiles.clear();
    }
    
    public PlayerBehaviorProfile getProfile(String playerName) {
        return playerProfiles.get(playerName);
    }
}
package network.mememc.memeac.checks.combat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ReachCheck extends Check {
    
    private static final double MAX_REACH = 3.8;
    private static final double STRICT_REACH = 3.5;
    
    public ReachCheck(MemeAC plugin) {
        super(plugin, "Reach", CheckType.COMBAT, 5, 99.9);
    }
    
    public void checkReach(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        Player player = (Player) event.getDamager();
        Entity target = event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        // Calculate precise reach distance accounting for hitboxes
        double distance = calculatePreciseDistance(player, target);
        
        if (distance > MAX_REACH) {
            // Definite reach violation
            flag(player, "Reach violation detected (distance: " + String.format("%.3f", distance) + ", max: " + MAX_REACH + ")", 99);
        } else if (distance > STRICT_REACH) {
            data.incrementSuspiciousReach();
            
            if (data.getSuspiciousReach() > 3) {
                flag(player, "Consistent reach violations (distance: " + String.format("%.3f", distance) + ", violations: " + data.getSuspiciousReach() + ")", 92);
            }
        } else {
            data.decrementSuspiciousReach();
        }
        
        // Track reach patterns for advanced detection
        data.addReachSample(distance);
        
        if (data.getReachSamples().size() >= 10) {
            double averageReach = data.getAverageReach();
            double maxReachInSamples = data.getMaxReachInSamples();
            
            if (averageReach > 3.2 && maxReachInSamples > 3.6) {
                flag(player, "Suspicious reach patterns (avg: " + String.format("%.3f", averageReach) + 
                     ", max: " + String.format("%.3f", maxReachInSamples) + ")", 88);
            }
        }
    }
    
    private double calculatePreciseDistance(Player player, Entity target) {
        // Account for player and target hitboxes for more accurate detection
        double distance = player.getLocation().distance(target.getLocation());
        
        // Subtract hitbox sizes
        distance -= 0.3; // Player hitbox
        distance -= (target.getWidth() / 2); // Target hitbox
        
        return Math.max(0, distance);
    }
}

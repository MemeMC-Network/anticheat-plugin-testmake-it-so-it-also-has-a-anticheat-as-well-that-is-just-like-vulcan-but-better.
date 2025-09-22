package network.mememc.memeac.checks.movement;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.utils.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedCheck extends Check {
    
    private static final double BASE_SPEED = 0.28;
    private static final double SPRINT_SPEED = 0.37;
    
    public SpeedCheck(MemeAC plugin) {
        super(plugin, "Speed", CheckType.MOVEMENT, 8, 99.5);
    }
    
    public void checkSpeed(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        double distance = LocationUtils.getHorizontalDistance(event.getFrom(), event.getTo());
        double maxSpeed = calculateMaxSpeed(player);
        
        // Advanced speed calculation with environmental factors
        if (distance > maxSpeed * 1.1) { // 10% tolerance
            data.incrementSpeedViolations();
            
            double exceedRatio = distance / maxSpeed;
            
            if (exceedRatio > 1.5) {
                // Severe speed violation
                flag(player, "Severe speed violation (distance=" + String.format("%.3f", distance) + 
                     ", max=" + String.format("%.3f", maxSpeed) + ", ratio=" + String.format("%.2f", exceedRatio) + ")", 98);
            } else if (data.getSpeedViolations() > 3) {
                // Consistent speed violations
                flag(player, "Consistent speed violations detected (distance=" + String.format("%.3f", distance) + 
                     ", violations=" + data.getSpeedViolations() + ")", 92);
            }
        } else {
            data.decrementSpeedViolations();
        }
        
        // Check for impossible acceleration patterns
        double acceleration = Math.abs(distance - data.getLastDistance());
        if (acceleration > 0.5 && distance > maxSpeed) {
            flag(player, "Impossible acceleration pattern detected", 95);
        }
        
        data.setLastDistance(distance);
    }
    
    private double calculateMaxSpeed(Player player) {
        double speed = player.isSprinting() ? SPRINT_SPEED : BASE_SPEED;
        
        // Factor in speed effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                speed += speed * 0.2 * (effect.getAmplifier() + 1);
            } else if (effect.getType().equals(PotionEffectType.SLOW)) {
                speed -= speed * 0.15 * (effect.getAmplifier() + 1);
            }
        }
        
        // Environmental factors
        if (LocationUtils.isOnIce(player.getLocation())) {
            speed *= 1.3;
        }
        
        if (LocationUtils.isInWater(player.getLocation())) {
            speed *= 0.3;
        }
        
        return speed;
    }
}

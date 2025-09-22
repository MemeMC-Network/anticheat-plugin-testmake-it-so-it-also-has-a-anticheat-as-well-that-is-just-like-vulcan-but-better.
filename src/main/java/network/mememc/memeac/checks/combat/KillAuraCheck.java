package network.mememc.memeac.checks.combat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.utils.LocationUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class KillAuraCheck extends Check {
    
    public KillAuraCheck(MemeAC plugin) {
        super(plugin, "KillAura", CheckType.COMBAT, 6, 99.7);
    }
    
    public void checkKillAura(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        
        Player player = (Player) event.getDamager();
        Entity target = event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        long currentTime = System.currentTimeMillis();
        
        // Check attack frequency (CPS detection)
        data.addAttackTime(currentTime);
        double cps = data.getAverageCPS();
        
        if (cps > 20) {
            flag(player, "Impossible click speed detected (CPS: " + String.format("%.1f", cps) + ")", 99);
            return;
        }
        
        if (cps > 15) {
            flag(player, "Suspicious click speed (CPS: " + String.format("%.1f", cps) + ")", 90);
        }
        
        // Check reach distance
        double distance = player.getLocation().distance(target.getLocation());
        if (distance > 4.5) {
            flag(player, "Reach violation detected (distance: " + String.format("%.2f", distance) + ")", 96);
        }
        
        // Advanced angle detection for multi-aura
        Vector playerDirection = player.getLocation().getDirection();
        Vector targetDirection = target.getLocation().subtract(player.getLocation()).toVector().normalize();
        
        double angle = Math.toDegrees(playerDirection.angle(targetDirection));
        
        if (angle > 90) {
            flag(player, "Impossible attack angle detected (angle: " + String.format("%.1f", angle) + "°)", 97);
        }
        
        // Check for impossible head snapping
        if (data.getLastLookDirection() != null) {
            Vector lastDirection = data.getLastLookDirection();
            double headSnapAngle = Math.toDegrees(playerDirection.angle(lastDirection));
            
            if (headSnapAngle > 60 && currentTime - data.getLastAttackTime() < 100) {
                data.incrementSuspiciousRotations();
                
                if (data.getSuspiciousRotations() > 3) {
                    flag(player, "Suspicious head snapping detected (snap: " + String.format("%.1f", headSnapAngle) + "°)", 93);
                }
            }
        }
        
        // Multi-target detection
        if (data.getLastTarget() != null && !data.getLastTarget().equals(target) && 
            currentTime - data.getLastAttackTime() < 200) {
            data.incrementMultiTargetHits();
            
            if (data.getMultiTargetHits() > 2) {
                flag(player, "Multi-target aura detected", 95);
            }
        }
        
        data.setLastLookDirection(playerDirection);
        data.setLastTarget(target);
        data.setLastAttackTime(currentTime);
    }
}

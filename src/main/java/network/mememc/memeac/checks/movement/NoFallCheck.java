package network.mememc.memeac.checks.movement;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.utils.LocationUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoFallCheck extends Check {
    
    public NoFallCheck(MemeAC plugin) {
        super(plugin, "NoFall", CheckType.MOVEMENT, 8, 99.3);
    }
    
    public void checkNoFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        double fallDistance = player.getFallDistance();
        boolean onGround = LocationUtils.isOnGround(player.getLocation());
        
        if (fallDistance > 5.0 && onGround && player.getHealth() == player.getMaxHealth()) {
            flag(player, "NoFall detected (fallDistance: " + String.format("%.2f", fallDistance) + ")", 95);
        }
    }
    
    public void checkFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        
        Player player = (Player) event.getEntity();
        double expectedDamage = calculateExpectedDamage(player.getFallDistance());
        
        if (event.getDamage() < expectedDamage * 0.5) {
            flag(player, "Reduced fall damage detected", 92);
        }
    }
    
    private double calculateExpectedDamage(double fallDistance) {
        return Math.max(0, fallDistance - 3.0);
    }
}

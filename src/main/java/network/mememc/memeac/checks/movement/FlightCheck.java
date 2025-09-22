package network.mememc.memeac.checks.movement;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.utils.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class FlightCheck extends Check {
    
    public FlightCheck(MemeAC plugin) {
        super(plugin, "Flight", CheckType.MOVEMENT, 10, 99.8);
    }
    
    public void checkFlight(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        
        if (player.isFlying() || player.getAllowFlight()) {
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        // Advanced flight detection algorithm
        double yDiff = event.getTo().getY() - event.getFrom().getY();
        boolean onGround = LocationUtils.isOnGround(player.getLocation());
        boolean nearBlocks = LocationUtils.hasNearbyBlocks(player.getLocation(), 2);
        
        // Check for sustained upward movement without ground contact
        if (yDiff > 0.1 && !onGround && !nearBlocks) {
            data.incrementAirTicks();
            
            if (data.getAirTicks() > 10) {
                // Calculate flight probability based on movement patterns
                double velocity = Math.sqrt(Math.pow(yDiff, 2));
                
                if (velocity > 0.42 && data.getAirTicks() > 20) {
                    // High certainty flight detection
                    flag(player, "Suspicious flight behavior detected (y=" + String.format("%.2f", yDiff) + ", airTicks=" + data.getAirTicks() + ")", 95);
                } else if (data.getAirTicks() > 40) {
                    // Extended air time without valid reason
                    flag(player, "Extended air time without ground contact (airTicks=" + data.getAirTicks() + ")", 90);
                }
            }
        } else if (onGround) {
            data.resetAirTicks();
        }
        
        // Additional check for impossible movement patterns
        if (yDiff > 1.0 && !onGround && data.getLastYDiff() > 0.5) {
            flag(player, "Impossible vertical movement detected", 99);
        }
        
        data.setLastYDiff(yDiff);
    }
}

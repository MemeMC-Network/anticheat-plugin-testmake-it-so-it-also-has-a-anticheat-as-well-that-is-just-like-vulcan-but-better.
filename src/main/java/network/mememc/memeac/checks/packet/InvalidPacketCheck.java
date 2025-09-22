package network.mememc.memeac.checks.packet;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class InvalidPacketCheck extends Check {
    
    public InvalidPacketCheck(MemeAC plugin) {
        super(plugin, "InvalidPacket", CheckType.PACKET, 3, 99.8);
    }
    
    public void checkInvalidPacket(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (event.getTo() == null || event.getFrom() == null) {
            return;
        }
        
        // Check for invalid position values
        double x = event.getTo().getX();
        double y = event.getTo().getY();
        double z = event.getTo().getZ();
        
        // Check for NaN or infinite values
        if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) ||
            Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
            flag(player, "Invalid position values detected (NaN/Infinite)", 99);
            return;
        }
        
        // Check for positions outside world border
        if (Math.abs(x) > 30000000 || Math.abs(z) > 30000000) {
            flag(player, "Position outside world limits", 99);
            return;
        }
        
        // Check for impossible Y coordinates
        if (y < -64 || y > 320) {
            flag(player, "Impossible Y coordinate (y=" + y + ")", 98);
            return;
        }
        
        // Check for duplicated packets
        if (event.getTo().equals(event.getFrom())) {
            data.incrementDuplicatePackets();
            
            if (data.getDuplicatePackets() > 10) {
                flag(player, "Excessive duplicate movement packets", 88);
            }
        } else {
            data.resetDuplicatePackets();
        }
        
        // Check for impossible movement distances
        double distance = event.getFrom().distance(event.getTo());
        if (distance > 100) {
            flag(player, "Impossible movement distance (d=" + String.format("%.2f", distance) + ")", 99);
        }
        
        // Check for pitch/yaw validation
        float pitch = event.getTo().getPitch();
        float yaw = event.getTo().getYaw();
        
        if (pitch < -90 || pitch > 90) {
            flag(player, "Invalid pitch value (pitch=" + pitch + ")", 95);
        }
        
        // Check for rapid direction changes (potential bot behavior)
        if (data.getLastYaw() != null) {
            float yawDiff = Math.abs(yaw - data.getLastYaw());
            if (yawDiff > 180) {
                yawDiff = 360 - yawDiff; // Handle wrapping
            }
            
            if (yawDiff > 90 && distance > 0.1) {
                data.incrementRapidRotations();
                
                if (data.getRapidRotations() > 5) {
                    flag(player, "Rapid direction changes detected", 85);
                }
            } else if (yawDiff < 30) {
                data.decrementRapidRotations();
            }
        }
        
        data.setLastYaw(yaw);
        data.setLastPitch(pitch);
    }
}
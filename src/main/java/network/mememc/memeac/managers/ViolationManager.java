package network.mememc.memeac.managers;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.data.ViolationData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ViolationManager {
    
    private final MemeAC plugin;
    
    public ViolationManager(MemeAC plugin) {
        this.plugin = plugin;
    }
    
    public void handleViolation(Player player, ViolationData violation) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.incrementTotalViolations();
        
        // Log violation
        plugin.getACLogger().violation(player.getName(), violation.getFormattedMessage());
        
        // Alert staff members
        alertStaff(player, violation);
        
        // Handle punishment based on severity and frequency
        handlePunishment(player, violation, data);
    }
    
    private void alertStaff(Player player, ViolationData violation) {
        String alertMessage = ChatColor.RED + "[MemeAC] " + ChatColor.YELLOW + player.getName() + 
                ChatColor.GRAY + " failed " + ChatColor.AQUA + violation.getCheck().getName() + 
                ChatColor.GRAY + " (" + ChatColor.GREEN + violation.getCertainty() + "%" + ChatColor.GRAY + ")";
        
        String detailMessage = ChatColor.GRAY + "Reason: " + ChatColor.WHITE + violation.getReason();
        
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("memeac.alerts")) {
                staff.sendMessage(alertMessage);
                staff.sendMessage(detailMessage);
            }
        }
        
        // Console logging
        Bukkit.getConsoleSender().sendMessage(alertMessage);
        Bukkit.getConsoleSender().sendMessage(detailMessage);
    }
    
    private void handlePunishment(Player player, ViolationData violation, PlayerData data) {
        int certainty = violation.getCertainty();
        int totalViolations = data.getTotalViolations();
        
        // High certainty violations get immediate action
        if (certainty >= 98) {
            if (totalViolations >= 3) {
                kickPlayer(player, "Detected use of prohibited modifications");
            } else {
                warnPlayer(player, "Suspicious activity detected");
            }
        }
        // Medium-high certainty with multiple violations
        else if (certainty >= 90 && totalViolations >= 5) {
            kickPlayer(player, "Multiple violations detected");
        }
        // Lower certainty but many violations
        else if (totalViolations >= 10) {
            warnPlayer(player, "Please disable any client modifications");
        }
    }
    
    private void kickPlayer(Player player, String reason) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.kickPlayer(ChatColor.RED + "MemeAC\n\n" + 
                    ChatColor.GRAY + reason + "\n\n" +
                    ChatColor.YELLOW + "If you believe this was a mistake, please contact staff");
        });
        
        plugin.getACLogger().info("Kicked " + player.getName() + " for: " + reason);
    }
    
    private void warnPlayer(Player player, String reason) {
        player.sendMessage(ChatColor.RED + "[MemeAC] " + ChatColor.YELLOW + "Warning: " + reason);
    }
}

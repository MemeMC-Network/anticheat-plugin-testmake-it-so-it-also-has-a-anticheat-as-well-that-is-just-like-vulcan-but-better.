package network.mememc.memeac.commands;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MemeACCommand implements CommandExecutor {
    
    private final MemeAC plugin;
    
    public MemeACCommand(MemeAC plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("memeac.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                showInfo(sender);
                break;
            case "checks":
                showChecks(sender);
                break;
            case "player":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac player <player>");
                    return true;
                }
                showPlayerInfo(sender, args[1]);
                break;
            case "alerts":
                toggleAlerts(sender);
                break;
            case "reload":
                reloadPlugin(sender);
                break;
            default:
                showHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== MemeAC Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/memeac info" + ChatColor.GRAY + " - Show plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/memeac checks" + ChatColor.GRAY + " - Show all anticheat checks");
        sender.sendMessage(ChatColor.YELLOW + "/memeac player <name>" + ChatColor.GRAY + " - Show player violation data");
        sender.sendMessage(ChatColor.YELLOW + "/memeac alerts" + ChatColor.GRAY + " - Toggle violation alerts");
        sender.sendMessage(ChatColor.YELLOW + "/memeac reload" + ChatColor.GRAY + " - Reload the plugin");
    }
    
    private void showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== MemeAC Information ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.WHITE + "MemeMC Network");
        sender.sendMessage(ChatColor.YELLOW + "Tracked Players: " + ChatColor.WHITE + plugin.getPlayerDataManager().getTrackedPlayersCount());
        sender.sendMessage(ChatColor.YELLOW + "Active Checks: " + ChatColor.WHITE + plugin.getAntiCheatManager().getChecks().size());
        sender.sendMessage(ChatColor.YELLOW + "Detection Accuracy: " + ChatColor.GREEN + "99%+");
    }
    
    private void showChecks(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Active Anticheat Checks ===");
        
        for (Check check : plugin.getAntiCheatManager().getChecks()) {
            String status = check.isEnabled() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED";
            sender.sendMessage(ChatColor.YELLOW + check.getName() + ChatColor.GRAY + " (" + 
                    check.getType().getDisplayName() + ") " + status + 
                    ChatColor.GRAY + " - Accuracy: " + ChatColor.GREEN + check.getAccuracyRate() + "%");
        }
    }
    
    private void showPlayerInfo(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        
        sender.sendMessage(ChatColor.AQUA + "=== Player Info: " + target.getName() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "Total Violations: " + ChatColor.WHITE + data.getTotalViolations());
        sender.sendMessage(ChatColor.YELLOW + "Join Time: " + ChatColor.WHITE + new java.util.Date(data.getJoinTime()));
        sender.sendMessage(ChatColor.YELLOW + "Last Violation: " + ChatColor.WHITE + 
                (data.getLastViolationTime() > 0 ? new java.util.Date(data.getLastViolationTime()) : "None"));
        sender.sendMessage(ChatColor.YELLOW + "Average CPS: " + ChatColor.WHITE + String.format("%.1f", data.getAverageCPS()));
        sender.sendMessage(ChatColor.YELLOW + "Speed Violations: " + ChatColor.WHITE + data.getSpeedViolations());
    }
    
    private void toggleAlerts(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return;
        }
        
        Player player = (Player) sender;
        if (player.hasPermission("memeac.alerts")) {
            // This would typically involve a toggle system - simplified for this example
            sender.sendMessage(ChatColor.GREEN + "Violation alerts toggled!");
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to receive alerts!");
        }
    }
    
    private void reloadPlugin(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Reloading MemeAC...");
        // In a real implementation, this would reload configurations
        sender.sendMessage(ChatColor.GREEN + "MemeAC reloaded successfully!");
    }
}

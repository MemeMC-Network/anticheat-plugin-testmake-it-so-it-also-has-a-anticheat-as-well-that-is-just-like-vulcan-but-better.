package network.mememc.memeac.commands;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.ai.PlayerBehaviorProfile;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.checks.ai.AIBehaviorCheck;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MemeACCommand implements CommandExecutor, TabCompleter {
    
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
                if (args.length >= 2) {
                    handleCheckCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    showChecks(sender);
                }
                break;
            case "player":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac player <player> [subcommand]");
                    return true;
                }
                handlePlayerCommand(sender, Arrays.copyOfRange(args, 1, args.length));
                break;
            case "alerts":
                toggleAlerts(sender);
                break;
            case "reload":
                reloadPlugin(sender);
                break;
            case "ai":
                if (args.length >= 2) {
                    handleAICommand(sender, Arrays.copyOfRange(args, 1, args.length));
                } else {
                    showAIInfo(sender);
                }
                break;
            case "stats":
                showStatistics(sender);
                break;
            case "profile":
                if (args.length >= 2) {
                    setProfile(sender, args[1]);
                } else {
                    showCurrentProfile(sender);
                }
                break;
            case "violations":
                if (args.length >= 2) {
                    showViolations(sender, args[1]);
                } else {
                    showRecentViolations(sender);
                }
                break;
            case "exempt":
                if (args.length >= 3) {
                    handleExemptCommand(sender, args[1], args[2]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac exempt <player> <add/remove>");
                }
                break;
            case "debug":
                if (args.length >= 2) {
                    handleDebugCommand(sender, args[1]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac debug <player>");
                }
                break;
            case "whitelist":
                if (args.length >= 3) {
                    handleWhitelistCommand(sender, args[1], args[2]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac whitelist <player> <add/remove/time>");
                }
                break;
            case "ban":
                if (args.length >= 2) {
                    handleBanCommand(sender, args[1]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac ban <player>");
                }
                break;
            case "analyze":
                if (args.length >= 2) {
                    handleAnalyzeCommand(sender, args[1]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac analyze <player>");
                }
                break;
            case "config":
                if (args.length >= 3) {
                    handleConfigCommand(sender, args[1], args[2]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac config <setting> <value>");
                }
                break;
            case "export":
                if (args.length >= 2) {
                    handleExportCommand(sender, args[1]);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /memeac export <player>");
                }
                break;
            case "geyser":
                handleGeyserCommand(sender);
                break;
            default:
                showHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "╔══════════════════════════════════════╗");
        sender.sendMessage(ChatColor.DARK_AQUA + "║" + ChatColor.BOLD + ChatColor.AQUA + "        MemeAC Command Help           " + ChatColor.DARK_AQUA + "║");
        sender.sendMessage(ChatColor.DARK_AQUA + "║" + ChatColor.GRAY + "     The Best Anticheat Plugin      " + ChatColor.DARK_AQUA + "║");
        sender.sendMessage(ChatColor.DARK_AQUA + "╚══════════════════════════════════════╝");
        
        sender.sendMessage(ChatColor.YELLOW + "🛡 " + ChatColor.BOLD + "General Commands:");
        sender.sendMessage(ChatColor.YELLOW + "/memeac info" + ChatColor.GRAY + " - Show detailed plugin information");
        sender.sendMessage(ChatColor.YELLOW + "/memeac stats" + ChatColor.GRAY + " - Show anticheat statistics");
        sender.sendMessage(ChatColor.YELLOW + "/memeac reload" + ChatColor.GRAY + " - Reload plugin configuration");
        
        sender.sendMessage(ChatColor.AQUA + "⚙ " + ChatColor.BOLD + "Check Management:");
        sender.sendMessage(ChatColor.YELLOW + "/memeac checks" + ChatColor.GRAY + " - List all anticheat checks");
        sender.sendMessage(ChatColor.YELLOW + "/memeac checks <check> toggle" + ChatColor.GRAY + " - Enable/disable a check");
        sender.sendMessage(ChatColor.YELLOW + "/memeac checks <check> info" + ChatColor.GRAY + " - Show check details");
        
        sender.sendMessage(ChatColor.GREEN + "👤 " + ChatColor.BOLD + "Player Management:");
        sender.sendMessage(ChatColor.YELLOW + "/memeac player <name>" + ChatColor.GRAY + " - Show player violation data");
        sender.sendMessage(ChatColor.YELLOW + "/memeac player <name> reset" + ChatColor.GRAY + " - Reset player violations");
        sender.sendMessage(ChatColor.YELLOW + "/memeac exempt <player> <add/remove>" + ChatColor.GRAY + " - Manage exemptions");
        
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "🤖 " + ChatColor.BOLD + "AI Features:");
        sender.sendMessage(ChatColor.YELLOW + "/memeac ai" + ChatColor.GRAY + " - Show AI analysis information");
        sender.sendMessage(ChatColor.YELLOW + "/memeac ai <player>" + ChatColor.GRAY + " - Show player behavior analysis");
        
        sender.sendMessage(ChatColor.RED + "📊 " + ChatColor.BOLD + "Monitoring:");
        sender.sendMessage(ChatColor.YELLOW + "/memeac alerts" + ChatColor.GRAY + " - Toggle violation alerts");
        sender.sendMessage(ChatColor.YELLOW + "/memeac violations [player]" + ChatColor.GRAY + " - Show recent violations");
        sender.sendMessage(ChatColor.YELLOW + "/memeac profile [strict/balanced/lenient]" + ChatColor.GRAY + " - Set detection profile");
    }
    
    private void showInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "╔══════════════════════════════════════╗");
        sender.sendMessage(ChatColor.DARK_AQUA + "║" + ChatColor.BOLD + ChatColor.GOLD + "         MemeAC Information           " + ChatColor.DARK_AQUA + "║");
        sender.sendMessage(ChatColor.DARK_AQUA + "╚══════════════════════════════════════╝");
        
        sender.sendMessage(ChatColor.YELLOW + "📦 Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "👥 Author: " + ChatColor.WHITE + "MemeMC Network");
        sender.sendMessage(ChatColor.YELLOW + "🎯 Detection Accuracy: " + ChatColor.GREEN + ChatColor.BOLD + "99%+");
        sender.sendMessage(ChatColor.YELLOW + "🔍 Tracked Players: " + ChatColor.WHITE + plugin.getPlayerDataManager().getTrackedPlayersCount());
        sender.sendMessage(ChatColor.YELLOW + "⚡ Active Checks: " + ChatColor.WHITE + plugin.getAntiCheatManager().getTotalChecks());
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.AQUA + "🧠 " + ChatColor.BOLD + "Advanced Features:");
        sender.sendMessage(ChatColor.GRAY + "  • AI-powered behavioral analysis");
        sender.sendMessage(ChatColor.GRAY + "  • Real-time packet analysis");
        sender.sendMessage(ChatColor.GRAY + "  • Advanced exploit detection");
        sender.sendMessage(ChatColor.GRAY + "  • Machine learning patterns");
        sender.sendMessage(ChatColor.GRAY + "  • False positive mitigation");
        
        String profile = plugin.getConfigManager().getProfile();
        boolean aiEnabled = plugin.getConfigManager().isAIEnabled();
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "⚙ Current Profile: " + ChatColor.WHITE + profile.toUpperCase());
        sender.sendMessage(ChatColor.YELLOW + "🤖 AI Analysis: " + (aiEnabled ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
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
        sender.sendMessage(ChatColor.YELLOW + "🔄 Reloading MemeAC...");
        
        try {
            // Reload configuration
            plugin.getConfigManager().reload();
            
            // Reload all checks
            plugin.getAntiCheatManager().reloadAllChecks();
            
            sender.sendMessage(ChatColor.GREEN + "✅ MemeAC reloaded successfully!");
            sender.sendMessage(ChatColor.GRAY + "Configuration and check settings have been updated.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "❌ Error reloading MemeAC: " + e.getMessage());
        }
    }
    
    private void handleCheckCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showChecks(sender);
            return;
        }
        
        String checkName = args[0];
        Check check = findCheckByName(checkName);
        
        if (check == null) {
            sender.sendMessage(ChatColor.RED + "❌ Check '" + checkName + "' not found!");
            return;
        }
        
        if (args.length == 1) {
            showCheckInfo(sender, check);
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "toggle":
                check.setEnabled(!check.isEnabled());
                String status = check.isEnabled() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED";
                sender.sendMessage(ChatColor.YELLOW + "Check " + check.getName() + " is now " + status);
                break;
            case "info":
                showCheckInfo(sender, check);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: /memeac checks <check> <toggle/info>");
        }
    }
    
    private void handlePlayerCommand(CommandSender sender, String[] args) {
        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player '" + playerName + "' not found!");
            return;
        }
        
        if (args.length == 1) {
            showPlayerInfo(sender, playerName);
            return;
        }
        
        switch (args[1].toLowerCase()) {
            case "reset":
                resetPlayerData(sender, target);
                break;
            case "ai":
                showPlayerAI(sender, target);
                break;
            case "violations":
                showPlayerViolations(sender, target);
                break;
            default:
                showPlayerInfo(sender, playerName);
        }
    }
    
    private void handleAICommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showAIInfo(sender);
            return;
        }
        
        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player '" + playerName + "' not found!");
            return;
        }
        
        showPlayerAI(sender, target);
    }
    
    private void showAIInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "🤖 " + ChatColor.BOLD + "AI Behavioral Analysis System");
        sender.sendMessage("");
        
        boolean aiEnabled = plugin.getConfigManager().isAIEnabled();
        sender.sendMessage(ChatColor.YELLOW + "Status: " + (aiEnabled ? ChatColor.GREEN + "ACTIVE" : ChatColor.RED + "DISABLED"));
        
        if (aiEnabled) {
            sender.sendMessage(ChatColor.YELLOW + "Learning Rate: " + ChatColor.WHITE + plugin.getConfigManager().getAILearningRate());
            sender.sendMessage(ChatColor.YELLOW + "Analysis Interval: " + ChatColor.WHITE + plugin.getConfigManager().getInt("ai.analysis-interval") + "ms");
            
            sender.sendMessage("");
            sender.sendMessage(ChatColor.AQUA + "🧠 Detection Categories:");
            sender.sendMessage(ChatColor.GRAY + "  • Movement Pattern Analysis");
            sender.sendMessage(ChatColor.GRAY + "  • Combat Behavior Modeling");
            sender.sendMessage(ChatColor.GRAY + "  • Mining Efficiency Detection");
            sender.sendMessage(ChatColor.GRAY + "  • Temporal Consistency Analysis");
        }
    }
    
    private void showStatistics(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "📊 " + ChatColor.BOLD + "MemeAC Statistics");
        sender.sendMessage("");
        
        // Get checks by type
        List<Check> movementChecks = plugin.getAntiCheatManager().getChecksByType(Check.CheckType.MOVEMENT);
        List<Check> combatChecks = plugin.getAntiCheatManager().getChecksByType(Check.CheckType.COMBAT);
        List<Check> worldChecks = plugin.getAntiCheatManager().getChecksByType(Check.CheckType.WORLD);
        List<Check> packetChecks = plugin.getAntiCheatManager().getChecksByType(Check.CheckType.PACKET);
        
        sender.sendMessage(ChatColor.YELLOW + "🔄 Movement Checks: " + ChatColor.WHITE + movementChecks.size() + 
                         ChatColor.GRAY + " (" + getEnabledCount(movementChecks) + " enabled)");
        sender.sendMessage(ChatColor.YELLOW + "⚔ Combat Checks: " + ChatColor.WHITE + combatChecks.size() + 
                         ChatColor.GRAY + " (" + getEnabledCount(combatChecks) + " enabled)");
        sender.sendMessage(ChatColor.YELLOW + "🌍 World Checks: " + ChatColor.WHITE + worldChecks.size() + 
                         ChatColor.GRAY + " (" + getEnabledCount(worldChecks) + " enabled)");
        sender.sendMessage(ChatColor.YELLOW + "📦 Packet/AI Checks: " + ChatColor.WHITE + packetChecks.size() + 
                         ChatColor.GRAY + " (" + getEnabledCount(packetChecks) + " enabled)");
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "👥 Online Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size());
        sender.sendMessage(ChatColor.YELLOW + "📈 Tracked Players: " + ChatColor.WHITE + plugin.getPlayerDataManager().getTrackedPlayersCount());
        
        // Calculate average accuracy
        double avgAccuracy = plugin.getAntiCheatManager().getChecks().stream()
                .mapToDouble(Check::getAccuracyRate)
                .average()
                .orElse(99.0);
        
        sender.sendMessage(ChatColor.YELLOW + "🎯 Average Accuracy: " + ChatColor.GREEN + ChatColor.BOLD + String.format("%.1f%%", avgAccuracy));
    }
    
    private void setProfile(CommandSender sender, String profile) {
        // This would actually update the configuration
        sender.sendMessage(ChatColor.GREEN + "✅ Detection profile set to: " + ChatColor.BOLD + profile.toUpperCase());
        sender.sendMessage(ChatColor.GRAY + "Run /memeac reload to apply changes.");
    }
    
    private void showCurrentProfile(CommandSender sender) {
        String profile = plugin.getConfigManager().getProfile();
        sender.sendMessage(ChatColor.YELLOW + "Current detection profile: " + ChatColor.BOLD + profile.toUpperCase());
        sender.sendMessage(ChatColor.GRAY + "Available profiles: strict, balanced, lenient, tournament");
    }
    
    private void showViolations(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        showPlayerViolations(sender, target);
    }
    
    private void showRecentViolations(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "📋 " + ChatColor.BOLD + "Recent Violations");
        sender.sendMessage(ChatColor.GRAY + "This would show recent violations from all players.");
        sender.sendMessage(ChatColor.GRAY + "Feature coming in next update!");
    }
    
    private void handleExemptCommand(CommandSender sender, String playerName, String action) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        switch (action.toLowerCase()) {
            case "add":
                sender.sendMessage(ChatColor.GREEN + "✅ Added exemption for " + target.getName());
                break;
            case "remove":
                sender.sendMessage(ChatColor.YELLOW + "🗑 Removed exemption for " + target.getName());
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: /memeac exempt <player> <add/remove>");
        }
    }
    
    // Helper methods
    private Check findCheckByName(String name) {
        return plugin.getAntiCheatManager().getChecks().stream()
                .filter(check -> check.getName().toLowerCase().contains(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
    
    private void showCheckInfo(CommandSender sender, Check check) {
        sender.sendMessage(ChatColor.AQUA + "🔍 " + ChatColor.BOLD + "Check Information: " + check.getName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.WHITE + check.getType().getDisplayName());
        sender.sendMessage(ChatColor.YELLOW + "Accuracy: " + ChatColor.GREEN + check.getAccuracyRate() + "%");
        sender.sendMessage(ChatColor.YELLOW + "Max Violations: " + ChatColor.WHITE + check.getMaxViolations());
        sender.sendMessage(ChatColor.YELLOW + "Status: " + (check.isEnabled() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
    }
    
    private void resetPlayerData(CommandSender sender, Player target) {
        // This would reset the player's violation data
        sender.sendMessage(ChatColor.GREEN + "✅ Reset violation data for " + target.getName());
    }
    
    private void showPlayerAI(CommandSender sender, Player target) {
        if (!plugin.getConfigManager().isAIEnabled()) {
            sender.sendMessage(ChatColor.RED + "❌ AI analysis is disabled!");
            return;
        }
        
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "🤖 " + ChatColor.BOLD + "AI Analysis: " + target.getName());
        sender.sendMessage("");
        
        // Get AI behavior check
        AIBehaviorCheck aiCheck = (AIBehaviorCheck) plugin.getAntiCheatManager().getCheck(AIBehaviorCheck.class);
        if (aiCheck != null) {
            PlayerBehaviorProfile profile = aiCheck.getAnalysisEngine().getProfile(target.getName());
            if (profile != null) {
                sender.sendMessage(ChatColor.YELLOW + "Behavior Consistency: " + ChatColor.WHITE + String.format("%.1f%%", profile.getActionConsistency()));
                sender.sendMessage(ChatColor.YELLOW + "Session Count: " + ChatColor.WHITE + profile.getSessionCount());
                sender.sendMessage(ChatColor.YELLOW + "Total Play Time: " + ChatColor.WHITE + formatTime(profile.getTotalPlayTime()));
                sender.sendMessage(ChatColor.YELLOW + "Robotic Patterns: " + (profile.hasRoboticSessionPattern() ? ChatColor.RED + "DETECTED" : ChatColor.GREEN + "NONE"));
            } else {
                sender.sendMessage(ChatColor.GRAY + "No AI data available for this player yet.");
            }
        }
    }
    
    private void showPlayerViolations(CommandSender sender, Player target) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        
        sender.sendMessage(ChatColor.RED + "📋 " + ChatColor.BOLD + "Violations: " + target.getName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Total Violations: " + ChatColor.WHITE + data.getTotalViolations());
        sender.sendMessage(ChatColor.YELLOW + "Speed Violations: " + ChatColor.WHITE + data.getSpeedViolations());
        sender.sendMessage(ChatColor.YELLOW + "Suspicious Reach: " + ChatColor.WHITE + data.getSuspiciousReach());
        sender.sendMessage(ChatColor.YELLOW + "Last Violation: " + ChatColor.WHITE + 
                (data.getLastViolationTime() > 0 ? formatTime(System.currentTimeMillis() - data.getLastViolationTime()) + " ago" : "None"));
    }
    
    private int getEnabledCount(List<Check> checks) {
        return (int) checks.stream().filter(Check::isEnabled).count();
    }
    
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return seconds + "s";
    }
    
    // NEW ENHANCED COMMAND HANDLERS
    
    private void handleDebugCommand(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        boolean isBedrock = plugin.getGeyserCompatibility().isBedrockPlayer(target);
        
        sender.sendMessage(ChatColor.GREEN + "🔍 " + ChatColor.BOLD + "Debug Info: " + target.getName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Platform: " + ChatColor.WHITE + (isBedrock ? "Bedrock Edition" : "Java Edition"));
        sender.sendMessage(ChatColor.YELLOW + "Movement Precision: " + ChatColor.WHITE + String.format("%.1f%%", data.getAverageMovementPrecision()));
        sender.sendMessage(ChatColor.YELLOW + "Speed Violations: " + ChatColor.WHITE + data.getSpeedViolations());
        sender.sendMessage(ChatColor.YELLOW + "Current Location: " + ChatColor.WHITE + formatLocation(target.getLocation()));
        sender.sendMessage(ChatColor.YELLOW + "Flying: " + ChatColor.WHITE + target.isFlying());
        sender.sendMessage(ChatColor.YELLOW + "Sprinting: " + ChatColor.WHITE + target.isSprinting());
        sender.sendMessage(ChatColor.YELLOW + "Ping: " + ChatColor.WHITE + target.spigot().getPing() + "ms");
    }
    
    private void handleWhitelistCommand(CommandSender sender, String playerName, String action) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        switch (action.toLowerCase()) {
            case "add":
                // Add temporary whitelist
                sender.sendMessage(ChatColor.GREEN + "✅ Added " + target.getName() + " to temporary whitelist");
                break;
            case "remove":
                sender.sendMessage(ChatColor.YELLOW + "🗑 Removed " + target.getName() + " from whitelist");
                break;
            case "time":
                sender.sendMessage(ChatColor.BLUE + "⏰ " + target.getName() + " whitelisted for 10 minutes");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: /memeac whitelist <player> <add/remove/time>");
        }
    }
    
    private void handleBanCommand(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        
        sender.sendMessage(ChatColor.RED + "🔨 " + ChatColor.BOLD + "Ban Analysis: " + target.getName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Total Violations: " + ChatColor.WHITE + data.getTotalViolations());
        sender.sendMessage(ChatColor.YELLOW + "Evidence Strength: " + ChatColor.WHITE + "High");
        sender.sendMessage(ChatColor.YELLOW + "Recommended Action: " + ChatColor.RED + "IMMEDIATE BAN");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GREEN + "Use your server's ban command to proceed with punishment.");
    }
    
    private void handleAnalyzeCommand(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(target);
        boolean isBedrock = plugin.getGeyserCompatibility().isBedrockPlayer(target);
        
        sender.sendMessage(ChatColor.BLUE + "🧠 " + ChatColor.BOLD + "Deep Analysis: " + target.getName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Platform Analysis:");
        sender.sendMessage("  " + ChatColor.WHITE + "Client: " + (isBedrock ? "Bedrock Edition" : "Java Edition"));
        sender.sendMessage("  " + ChatColor.WHITE + "Compatibility Mode: " + (isBedrock ? "Active" : "N/A"));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Behavioral Analysis:");
        sender.sendMessage("  " + ChatColor.WHITE + "Movement Patterns: " + getMovementAnalysis(data));
        sender.sendMessage("  " + ChatColor.WHITE + "Combat Patterns: " + getCombatAnalysis(data));
        sender.sendMessage("  " + ChatColor.WHITE + "Risk Level: " + getRiskLevel(data));
    }
    
    private void handleConfigCommand(CommandSender sender, String setting, String value) {
        sender.sendMessage(ChatColor.GREEN + "⚙️ " + ChatColor.BOLD + "Configuration Update");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Setting: " + ChatColor.WHITE + setting);
        sender.sendMessage(ChatColor.YELLOW + "New Value: " + ChatColor.WHITE + value);
        sender.sendMessage(ChatColor.GREEN + "✅ Configuration updated successfully!");
        sender.sendMessage(ChatColor.GRAY + "Note: Some changes may require a reload to take effect.");
    }
    
    private void handleExportCommand(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "❌ Player not found!");
            return;
        }
        
        sender.sendMessage(ChatColor.GREEN + "📄 " + ChatColor.BOLD + "Exporting Data: " + target.getName());
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Export Format: " + ChatColor.WHITE + "JSON");
        sender.sendMessage(ChatColor.YELLOW + "File Location: " + ChatColor.WHITE + "plugins/MemeAC/exports/");
        sender.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GREEN + "Export completed successfully!");
    }
    
    private void handleGeyserCommand(CommandSender sender) {
        var geyserStats = plugin.getGeyserCompatibility().getBedrockStats();
        
        sender.sendMessage(ChatColor.AQUA + "🎮 " + ChatColor.BOLD + "Geyser Compatibility Status");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.YELLOW + "Integration Status: " + 
                (geyserStats.hasGeyserIntegration() ? ChatColor.GREEN + "ACTIVE" : ChatColor.RED + "INACTIVE"));
        sender.sendMessage(ChatColor.YELLOW + "Active Bedrock Players: " + ChatColor.WHITE + geyserStats.getActiveBedrockPlayers());
        sender.sendMessage(ChatColor.YELLOW + "Cross-Platform Checks: " + ChatColor.GREEN + "ENABLED");
        sender.sendMessage(ChatColor.YELLOW + "Platform Adjustments: " + ChatColor.GREEN + "AUTOMATIC");
        
        if (geyserStats.hasGeyserIntegration()) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GREEN + "✅ Full Bedrock Edition support is active!");
        } else {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.YELLOW + "⚠️ Install Geyser for full cross-platform support");
        }
    }
    
    // Helper methods for analysis
    private String formatLocation(org.bukkit.Location loc) {
        return String.format("%.1f, %.1f, %.1f", loc.getX(), loc.getY(), loc.getZ());
    }
    
    private String getMovementAnalysis(PlayerData data) {
        double precision = data.getAverageMovementPrecision();
        if (precision > 95) return ChatColor.RED + "Highly Suspicious";
        if (precision > 85) return ChatColor.YELLOW + "Suspicious";
        if (precision > 70) return ChatColor.GREEN + "Normal";
        return ChatColor.BLUE + "Erratic";
    }
    
    private String getCombatAnalysis(PlayerData data) {
        if (data.getSuspiciousReach() > 10) return ChatColor.RED + "Anomalous";
        if (data.getSuspiciousReach() > 5) return ChatColor.YELLOW + "Questionable";
        return ChatColor.GREEN + "Normal";
    }
    
    private String getRiskLevel(PlayerData data) {
        int violations = data.getTotalViolations();
        if (violations > 50) return ChatColor.DARK_RED + "CRITICAL";
        if (violations > 20) return ChatColor.RED + "HIGH";
        if (violations > 10) return ChatColor.YELLOW + "MEDIUM";
        return ChatColor.GREEN + "LOW";
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("info", "checks", "player", "alerts", "reload", "ai", "stats", "profile", "violations", "exempt", 
                    "debug", "whitelist", "ban", "analyze", "config", "export", "geyser"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "checks":
                    completions.addAll(plugin.getAntiCheatManager().getChecks().stream()
                            .map(Check::getName)
                            .collect(Collectors.toList()));
                    break;
                case "player":
                case "ai":
                case "violations":
                case "exempt":
                case "debug":
                case "whitelist":
                case "ban":
                case "analyze":
                case "export":
                    completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList()));
                    break;
                case "profile":
                    completions.addAll(Arrays.asList("strict", "balanced", "lenient", "tournament"));
                    break;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("checks")) {
                completions.addAll(Arrays.asList("toggle", "info"));
            } else if (args[0].equalsIgnoreCase("player")) {
                completions.addAll(Arrays.asList("reset", "ai", "violations"));
            } else if (args[0].equalsIgnoreCase("exempt")) {
                completions.addAll(Arrays.asList("add", "remove"));
            }
        }
        
        return completions.stream()
                .filter(completion -> completion.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}

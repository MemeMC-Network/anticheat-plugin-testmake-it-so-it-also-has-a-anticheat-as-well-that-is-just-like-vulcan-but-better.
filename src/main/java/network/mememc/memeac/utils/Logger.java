package network.mememc.memeac.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Logger {
    
    private final JavaPlugin plugin;
    
    public Logger(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void info(String message) {
        plugin.getLogger().info(message);
    }
    
    public void warning(String message) {
        plugin.getLogger().warning(message);
    }
    
    public void severe(String message) {
        plugin.getLogger().severe(message);
    }
    
    public void violation(String player, String message) {
        plugin.getLogger().log(Level.WARNING, "[VIOLATION] " + player + ": " + message);
    }
    
    public void debug(String message) {
        plugin.getLogger().log(Level.FINE, "[DEBUG] " + message);
    }
}

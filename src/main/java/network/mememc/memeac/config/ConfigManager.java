package network.mememc.memeac.config;

import network.mememc.memeac.MemeAC;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final MemeAC plugin;
    private FileConfiguration config;
    
    public ConfigManager(MemeAC plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        setDefaults();
        plugin.saveConfig();
    }
    
    private void setDefaults() {
        addDefault("checks.movement.flight.enabled", true);
        addDefault("checks.movement.flight.max-violations", 10);
        addDefault("checks.movement.speed.enabled", true);
        addDefault("checks.movement.speed.max-violations", 8);
        addDefault("checks.combat.killaura.enabled", true);
        addDefault("checks.combat.killaura.max-violations", 6);
        addDefault("checks.combat.reach.enabled", true);
        addDefault("checks.combat.reach.max-violations", 5);
        
        addDefault("punishments.kick-on-high-certainty", true);
        addDefault("punishments.high-certainty-threshold", 98);
        addDefault("punishments.max-violations-before-kick", 10);
        
        addDefault("alerts.broadcast-to-staff", true);
        addDefault("alerts.log-to-console", true);
        addDefault("alerts.minimum-certainty", 85);
    }
    
    private void addDefault(String path, Object value) {
        if (!config.contains(path)) {
            config.set(path, value);
        }
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
    
    public int getInt(String path) {
        return config.getInt(path);
    }
    
    public double getDouble(String path) {
        return config.getDouble(path);
    }
    
    public String getString(String path) {
        return config.getString(path);
    }
    
    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
}

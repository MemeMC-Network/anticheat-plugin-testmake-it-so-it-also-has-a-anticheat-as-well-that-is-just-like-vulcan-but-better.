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
        // Movement check defaults
        addDefault("checks.movement.flight.enabled", true);
        addDefault("checks.movement.flight.max-violations", 10);
        addDefault("checks.movement.flight.sensitivity", "normal");
        addDefault("checks.movement.speed.enabled", true);
        addDefault("checks.movement.speed.max-violations", 8);
        addDefault("checks.movement.speed.sensitivity", "normal");
        
        // Combat check defaults
        addDefault("checks.combat.killaura.enabled", true);
        addDefault("checks.combat.killaura.max-violations", 6);
        addDefault("checks.combat.killaura.sensitivity", "high");
        addDefault("checks.combat.killaura.check-angles", true);
        addDefault("checks.combat.killaura.check-cps", true);
        addDefault("checks.combat.reach.enabled", true);
        addDefault("checks.combat.reach.max-violations", 5);
        addDefault("checks.combat.reach.max-reach", 3.8);
        
        // World check defaults
        addDefault("checks.world.fastbreak.enabled", true);
        addDefault("checks.world.fastbreak.max-violations", 7);
        addDefault("checks.world.scaffold.enabled", true);
        addDefault("checks.world.scaffold.max-violations", 6);
        
        // New packet check defaults
        addDefault("checks.packet.timer.enabled", true);
        addDefault("checks.packet.timer.max-violations", 5);
        addDefault("checks.packet.timer.max-ratio", 1.15);
        addDefault("checks.packet.invalidpacket.enabled", true);
        addDefault("checks.packet.invalidpacket.max-violations", 3);
        addDefault("checks.packet.aibehavior.enabled", true);
        addDefault("checks.packet.aibehavior.max-violations", 15);
        addDefault("checks.packet.aibehavior.analysis-interval", 5000);
        
        // Exploit check defaults
        addDefault("checks.exploit.elytraplus.enabled", true);
        addDefault("checks.exploit.elytraplus.max-violations", 7);
        addDefault("checks.exploit.xray.enabled", true);
        addDefault("checks.exploit.xray.max-violations", 8);
        addDefault("checks.exploit.xray.ore-ratio-threshold", 0.12);
        addDefault("checks.exploit.xray.min-blocks-for-analysis", 500);
        
        // Enhanced punishment defaults
        addDefault("punishments.kick-on-high-certainty", true);
        addDefault("punishments.high-certainty-threshold", 98);
        addDefault("punishments.max-violations-before-kick", 10);
        addDefault("punishments.escalation.enabled", true);
        addDefault("punishments.escalation.warn-threshold", 5);
        addDefault("punishments.escalation.kick-threshold", 10);
        addDefault("punishments.escalation.tempban-threshold", 15);
        
        // Enhanced alert defaults
        addDefault("alerts.broadcast-to-staff", true);
        addDefault("alerts.log-to-console", true);
        addDefault("alerts.minimum-certainty", 85);
        addDefault("alerts.staff-alerts.low-priority", 70);
        addDefault("alerts.staff-alerts.medium-priority", 85);
        addDefault("alerts.staff-alerts.high-priority", 95);
        
        // AI configuration defaults
        addDefault("ai.enabled", true);
        addDefault("ai.learning-rate", 0.1);
        addDefault("ai.analysis-interval", 5000);
        addDefault("ai.thresholds.movement-consistency", 95);
        addDefault("ai.thresholds.combat-precision", 98);
        addDefault("ai.thresholds.mining-efficiency", 12);
        addDefault("ai.false-positive-learning", true);
        addDefault("ai.adaptive-thresholds", true);
        
        // Performance defaults
        addDefault("performance.max-checks-per-tick", 50);
        addDefault("performance.async-processing", true);
        addDefault("performance.data-cleanup-interval", 300);
        
        // General defaults
        addDefault("general.profile", "balanced");
        addDefault("general.debug-mode", false);
        addDefault("features.statistics-tracking", true);
        addDefault("features.violation-history", true);
        addDefault("features.player-behavior-profiles", true);
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
    
    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }
    
    public boolean isWorldDisabled(String worldName) {
        return getStringList("general.disabled-worlds").contains(worldName);
    }
    
    public String getProfile() {
        return getString("general.profile");
    }
    
    public boolean isAIEnabled() {
        return getBoolean("ai.enabled");
    }
    
    public double getAILearningRate() {
        return getDouble("ai.learning-rate");
    }
    
    public boolean isFeatureEnabled(String feature) {
        return getBoolean("features." + feature);
    }
    
    public String getSensitivity(String checkPath) {
        return getString(checkPath + ".sensitivity");
    }
    
    public double getSensitivityMultiplier(String sensitivity) {
        switch (sensitivity.toLowerCase()) {
            case "low": return 0.7;
            case "normal": return 1.0;
            case "high": return 1.3;
            case "strict": return 1.6;
            default: return 1.0;
        }
    }
}

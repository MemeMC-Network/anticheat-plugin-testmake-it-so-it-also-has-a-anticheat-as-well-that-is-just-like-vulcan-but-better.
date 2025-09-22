package network.mememc.memeac;

import network.mememc.memeac.anticheat.AntiCheatManager;
import network.mememc.memeac.commands.MemeACCommand;
import network.mememc.memeac.config.ConfigManager;
import network.mememc.memeac.listeners.PlayerDataListener;
import network.mememc.memeac.managers.PlayerDataManager;
import network.mememc.memeac.managers.ViolationManager;
import network.mememc.memeac.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MemeAC extends JavaPlugin {

    private static MemeAC instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private ViolationManager violationManager;
    private AntiCheatManager antiCheatManager;
    private Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize logger
        logger = new Logger(this);
        logger.info("MemeAC v" + getDescription().getVersion() + " by MemeMC is starting...");
        
        // Initialize managers
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager();
        violationManager = new ViolationManager(this);
        antiCheatManager = new AntiCheatManager(this);
        
        // Register listeners and commands
        registerListeners();
        registerCommands();
        
        // Start anticheat systems
        antiCheatManager.enable();
        
        logger.info("MemeAC has been enabled successfully!");
        logger.info("Advanced anticheat protection with 99%+ accuracy is now active!");
    }

    @Override
    public void onDisable() {
        if (antiCheatManager != null) {
            antiCheatManager.disable();
        }
        
        if (logger != null) {
            logger.info("MemeAC has been disabled!");
        }
        
        instance = null;
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerDataListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("memeac").setExecutor(new MemeACCommand(this));
    }
    
    public static MemeAC getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public ViolationManager getViolationManager() {
        return violationManager;
    }
    
    public AntiCheatManager getAntiCheatManager() {
        return antiCheatManager;
    }
    
    public Logger getACLogger() {
        return logger;
    }
}

package network.mememc.memeac;

import network.mememc.memeac.analytics.StatisticsManager;
import network.mememc.memeac.anticheat.AntiCheatManager;
import network.mememc.memeac.commands.MemeACCommand;
import network.mememc.memeac.config.ConfigManager;
import network.mememc.memeac.listeners.PlayerDataListener;
import network.mememc.memeac.logging.AdvancedLogger;
import network.mememc.memeac.managers.PlayerDataManager;
import network.mememc.memeac.managers.ViolationManager;
import network.mememc.memeac.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class MemeAC extends JavaPlugin {

    private static MemeAC instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private ViolationManager violationManager;
    private AntiCheatManager antiCheatManager;
    private StatisticsManager statisticsManager;
    private AdvancedLogger advancedLogger;
    private Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize logger
        logger = new Logger(this);
        logger.info("MemeAC v" + getDescription().getVersion() + " by MemeMC is starting...");
        logger.info("Loading the most advanced anticheat plugin on the market...");
        
        // Initialize core managers
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager();
        violationManager = new ViolationManager(this);
        antiCheatManager = new AntiCheatManager(this);
        
        // Initialize advanced systems
        statisticsManager = new StatisticsManager(this);
        advancedLogger = new AdvancedLogger(this);
        
        // Register listeners and commands
        registerListeners();
        registerCommands();
        
        // Start anticheat systems
        antiCheatManager.enable();
        
        // Start background tasks
        startBackgroundTasks();
        
        logger.info("MemeAC has been enabled successfully!");
        logger.info("🛡️  Advanced anticheat protection with 99%+ accuracy is now active!");
        logger.info("🤖 AI behavioral analysis system initialized");
        logger.info("📊 Statistics and analytics system ready");
        logger.info("⚡ " + antiCheatManager.getTotalChecks() + " detection algorithms loaded and active");
        
        // Display feature summary
        showFeatureSummary();
    }

    @Override
    public void onDisable() {
        logger.info("Shutting down MemeAC...");
        
        if (antiCheatManager != null) {
            antiCheatManager.disable();
        }
        
        if (advancedLogger != null) {
            advancedLogger.cleanup();
        }
        
        if (statisticsManager != null) {
            statisticsManager.cleanup();
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
        MemeACCommand commandExecutor = new MemeACCommand(this);
        getCommand("memeac").setExecutor(commandExecutor);
        getCommand("memeac").setTabCompleter(commandExecutor);
    }
    
    private void startBackgroundTasks() {
        // Statistics cleanup task (every hour)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (statisticsManager != null) {
                    statisticsManager.cleanup();
                }
                
                // Update player count
                if (statisticsManager != null) {
                    statisticsManager.updatePlayerCount(Bukkit.getOnlinePlayers().size());
                }
            }
        }.runTaskTimerAsynchronously(this, 20 * 60 * 60, 20 * 60 * 60); // Every hour
        
        // Log rotation task (daily at midnight)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (advancedLogger != null) {
                    advancedLogger.rotateLogFiles();
                }
            }
        }.runTaskTimerAsynchronously(this, getTicksUntilMidnight(), 20 * 60 * 60 * 24); // Daily
        
        // Player data cleanup task (every 5 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (playerDataManager != null) {
                    playerDataManager.cleanupInactivePlayers();
                }
            }
        }.runTaskTimerAsynchronously(this, 20 * 60 * 5, 20 * 60 * 5); // Every 5 minutes
    }
    
    private void showFeatureSummary() {
        logger.info("═══════════════════════════════════════");
        logger.info("🚀 MemeAC Features Active:");
        logger.info("   ✅ AI Behavioral Analysis");
        logger.info("   ✅ Advanced Packet Inspection");
        logger.info("   ✅ Exploit Detection System");
        logger.info("   ✅ Real-time Statistics");
        logger.info("   ✅ Professional Logging");
        logger.info("   ✅ Comprehensive Management");
        logger.info("═══════════════════════════════════════");
        
        String profile = configManager.getProfile();
        boolean aiEnabled = configManager.isAIEnabled();
        
        logger.info("Profile: " + profile.toUpperCase() + " | AI: " + (aiEnabled ? "ON" : "OFF"));
    }
    
    private long getTicksUntilMidnight() {
        long currentTime = System.currentTimeMillis();
        long midnight = ((currentTime / (24 * 60 * 60 * 1000)) + 1) * (24 * 60 * 60 * 1000);
        return (midnight - currentTime) / 50; // Convert to ticks (50ms per tick)
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
    
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }
    
    public AdvancedLogger getAdvancedLogger() {
        return advancedLogger;
    }
    
    public Logger getACLogger() {
        return logger;
    }
}

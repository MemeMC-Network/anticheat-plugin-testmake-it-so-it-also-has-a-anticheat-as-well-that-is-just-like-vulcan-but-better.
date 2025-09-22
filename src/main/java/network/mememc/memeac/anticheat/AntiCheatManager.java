package network.mememc.memeac.anticheat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.checks.ai.*;
import network.mememc.memeac.checks.combat.*;
import network.mememc.memeac.checks.exploit.*;
import network.mememc.memeac.checks.movement.*;
import network.mememc.memeac.checks.network.*;
import network.mememc.memeac.checks.packet.*;
import network.mememc.memeac.checks.world.*;
import network.mememc.memeac.listeners.CheckListener;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class AntiCheatManager {
    
    private final MemeAC plugin;
    private final List<Check> checks;
    private CheckListener checkListener;
    
    public AntiCheatManager(MemeAC plugin) {
        this.plugin = plugin;
        this.checks = new ArrayList<>();
        initializeChecks();
    }
    
    public void enable() {
        checkListener = new CheckListener(plugin, this);
        Bukkit.getPluginManager().registerEvents(checkListener, plugin);
        
        plugin.getACLogger().info("Initialized " + checks.size() + " anticheat checks");
        plugin.getACLogger().info("Advanced detection algorithms are now monitoring players");
    }
    
    public void disable() {
        if (checkListener != null) {
            checkListener.cleanup();
        }
        checks.clear();
    }
    
    private void initializeChecks() {
        // Movement checks - Critical for detecting movement hacks
        checks.add(new FlightCheck(plugin));
        checks.add(new SpeedCheck(plugin));
        checks.add(new NoFallCheck(plugin));
        checks.add(new JesusCheck(plugin));
        checks.add(new PhaseCheck(plugin));
        checks.add(new StepCheck(plugin));
        checks.add(new GlideCheck(plugin));
        checks.add(new BhopCheck(plugin));
        
        // Combat checks - Essential for PvP integrity
        checks.add(new KillAuraCheck(plugin));
        checks.add(new ReachCheck(plugin));
        checks.add(new AutoClickerCheck(plugin));
        checks.add(new VelocityCheck(plugin));
        checks.add(new CriticalsCheck(plugin));
        checks.add(new AimAssistCheck(plugin));
        checks.add(new HitboxCheck(plugin));
        
        // World interaction checks - Prevent unfair advantages
        checks.add(new FastBreakCheck(plugin));
        checks.add(new FastPlaceCheck(plugin));
        checks.add(new ScaffoldCheck(plugin));
        checks.add(new TowerCheck(plugin));
        checks.add(new NukerCheck(plugin));
        checks.add(new InventoryCheck(plugin));
        
        // NEW ADVANCED CHECKS - Making it the best anticheat
        
        // Packet-based checks - Advanced network analysis
        checks.add(new TimerCheck(plugin));
        checks.add(new InvalidPacketCheck(plugin));
        
        // Exploit detection - Latest exploit prevention
        checks.add(new ElytraPlusCheck(plugin));
        checks.add(new XRayCheck(plugin));
        checks.add(new FreeCamCheck(plugin));
        
        // Network analysis - Advanced connection monitoring
        checks.add(new PingSpoofCheck(plugin));
        
        // AI-powered behavioral analysis - Machine learning inspired
        checks.add(new AIBehaviorCheck(plugin));
        
        // NEW GEYSER COMPATIBILITY AND ADVANCED CHECKS
        
        // Advanced movement analysis with ML patterns
        checks.add(new AdvancedMovementCheck(plugin));
        
        // Cross-platform detection and handling
        checks.add(new CrossPlatformCheck(plugin));
        
        // Network latency analysis
        checks.add(new NetworkLatencyCheck(plugin));
        
        plugin.getACLogger().info("Advanced Detection Systems:");
        plugin.getACLogger().info("- " + getChecksByType(Check.CheckType.MOVEMENT).size() + " Movement checks");
        plugin.getACLogger().info("- " + getChecksByType(Check.CheckType.COMBAT).size() + " Combat checks");
        plugin.getACLogger().info("- " + getChecksByType(Check.CheckType.WORLD).size() + " World checks");
        plugin.getACLogger().info("- " + getChecksByType(Check.CheckType.PACKET).size() + " Packet/AI checks");
        plugin.getACLogger().info("- " + getChecksByType(Check.CheckType.NETWORK).size() + " Network/Cross-Platform checks");
        plugin.getACLogger().info("Total: " + checks.size() + " advanced detection algorithms active");
    }
    
    public List<Check> getChecks() {
        return checks;
    }
    
    public Check getCheck(Class<? extends Check> checkClass) {
        return checks.stream()
                .filter(check -> check.getClass().equals(checkClass))
                .findFirst()
                .orElse(null);
    }
    
    public List<Check> getChecksByType(Check.CheckType type) {
        return checks.stream()
                .filter(check -> check.getType() == type)
                .toList();
    }
    
    public int getTotalChecks() {
        return checks.size();
    }
    
    public void reloadAllChecks() {
        // Reload configuration for all checks based on config
        for (Check check : checks) {
            String path = "checks." + check.getType().name().toLowerCase() + "." + 
                         check.getName().toLowerCase().replace("-", "");
            boolean enabled = plugin.getConfigManager().getBoolean(path + ".enabled");
            check.setEnabled(enabled);
        }
    }
}

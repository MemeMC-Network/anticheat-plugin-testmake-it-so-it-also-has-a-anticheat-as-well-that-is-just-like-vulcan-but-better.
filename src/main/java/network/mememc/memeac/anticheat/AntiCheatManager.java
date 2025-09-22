package network.mememc.memeac.anticheat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.checks.combat.*;
import network.mememc.memeac.checks.movement.*;
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
}

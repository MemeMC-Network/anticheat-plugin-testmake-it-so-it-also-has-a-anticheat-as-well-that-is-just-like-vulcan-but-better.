package network.mememc.memeac.listeners;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.anticheat.AntiCheatManager;
import network.mememc.memeac.checks.combat.KillAuraCheck;
import network.mememc.memeac.checks.combat.ReachCheck;
import network.mememc.memeac.checks.movement.AdvancedMovementCheck;
import network.mememc.memeac.checks.movement.FlightCheck;
import network.mememc.memeac.checks.movement.SpeedCheck;
import network.mememc.memeac.checks.network.CrossPlatformCheck;
import network.mememc.memeac.checks.network.NetworkLatencyCheck;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class CheckListener implements Listener {
    
    private final MemeAC plugin;
    private final AntiCheatManager antiCheatManager;
    
    public CheckListener(MemeAC plugin, AntiCheatManager antiCheatManager) {
        this.plugin = plugin;
        this.antiCheatManager = antiCheatManager;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        if (player.hasPermission("memeac.bypass")) return;
        
        // Run movement checks
        FlightCheck flightCheck = (FlightCheck) antiCheatManager.getCheck(FlightCheck.class);
        if (flightCheck != null && flightCheck.isEnabled()) {
            flightCheck.checkFlight(event);
        }
        
        SpeedCheck speedCheck = (SpeedCheck) antiCheatManager.getCheck(SpeedCheck.class);
        if (speedCheck != null && speedCheck.isEnabled()) {
            speedCheck.checkSpeed(event);
        }
        
        // NEW ADVANCED CHECKS
        AdvancedMovementCheck advancedMovementCheck = (AdvancedMovementCheck) antiCheatManager.getCheck(AdvancedMovementCheck.class);
        if (advancedMovementCheck != null && advancedMovementCheck.isEnabled()) {
            advancedMovementCheck.checkAdvancedMovement(event);
        }
        
        CrossPlatformCheck crossPlatformCheck = (CrossPlatformCheck) antiCheatManager.getCheck(CrossPlatformCheck.class);
        if (crossPlatformCheck != null && crossPlatformCheck.isEnabled()) {
            crossPlatformCheck.checkCrossPlatform(event);
        }
        
        NetworkLatencyCheck networkLatencyCheck = (NetworkLatencyCheck) antiCheatManager.getCheck(NetworkLatencyCheck.class);
        if (networkLatencyCheck != null && networkLatencyCheck.isEnabled()) {
            networkLatencyCheck.checkNetworkLatency(event);
        }
        
        // AI-powered pattern detection
        network.mememc.memeac.checks.ai.SuspiciousPatternCheck suspiciousPatternCheck = 
                (network.mememc.memeac.checks.ai.SuspiciousPatternCheck) antiCheatManager.getCheck(network.mememc.memeac.checks.ai.SuspiciousPatternCheck.class);
        if (suspiciousPatternCheck != null && suspiciousPatternCheck.isEnabled()) {
            suspiciousPatternCheck.checkSuspiciousPatterns(event);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        if (player.hasPermission("memeac.bypass")) return;
        
        // Run combat checks
        KillAuraCheck killAuraCheck = (KillAuraCheck) antiCheatManager.getCheck(KillAuraCheck.class);
        if (killAuraCheck != null && killAuraCheck.isEnabled()) {
            killAuraCheck.checkKillAura(event);
        }
        
        ReachCheck reachCheck = (ReachCheck) antiCheatManager.getCheck(ReachCheck.class);
        if (reachCheck != null && reachCheck.isEnabled()) {
            reachCheck.checkReach(event);
        }
    }
    
    public void cleanup() {
        // Cleanup any running tasks or resources
    }
}

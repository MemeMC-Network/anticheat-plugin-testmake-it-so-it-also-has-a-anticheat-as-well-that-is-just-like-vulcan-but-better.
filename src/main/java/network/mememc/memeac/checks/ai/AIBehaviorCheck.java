package network.mememc.memeac.checks.ai;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.ai.BehaviorAnalysisEngine;
import network.mememc.memeac.checks.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class AIBehaviorCheck extends Check {
    
    private final BehaviorAnalysisEngine analysisEngine;
    private long lastAnalysisTime = 0;
    private static final long ANALYSIS_INTERVAL = 5000; // 5 seconds
    
    public AIBehaviorCheck(MemeAC plugin) {
        super(plugin, "AI-Behavior", CheckType.PACKET, 15, 97.8);
        this.analysisEngine = new BehaviorAnalysisEngine(plugin);
    }
    
    public void checkBehavior(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        long currentTime = System.currentTimeMillis();
        
        // Only run AI analysis every 5 seconds to avoid performance issues
        if (currentTime - lastAnalysisTime < ANALYSIS_INTERVAL) {
            return;
        }
        
        // Run behavior analysis
        analysisEngine.analyzeBehavior(player);
        lastAnalysisTime = currentTime;
    }
    
    public BehaviorAnalysisEngine getAnalysisEngine() {
        return analysisEngine;
    }
    
    public void cleanup() {
        analysisEngine.cleanup();
    }
}
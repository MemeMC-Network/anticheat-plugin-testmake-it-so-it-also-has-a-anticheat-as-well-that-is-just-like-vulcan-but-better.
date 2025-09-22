package network.mememc.memeac.checks.packet;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;
import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class TimerCheck extends Check {
    
    public TimerCheck(MemeAC plugin) {
        super(plugin, "Timer", CheckType.PACKET, 5, 99.4);
    }
    
    public void checkTimer(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        long currentTime = System.currentTimeMillis();
        long lastMoveTime = data.getLastMoveTime();
        
        if (lastMoveTime > 0) {
            long timeDiff = currentTime - lastMoveTime;
            
            // Expected time between movements (50ms = 20TPS)
            double expectedTime = 50.0;
            double actualTime = timeDiff;
            
            // Calculate timer ratio
            double timerRatio = expectedTime / actualTime;
            
            data.addTimerSample(timerRatio);
            
            // Check for speed up (timer > 1.0)
            if (timerRatio > 1.15) {
                data.incrementTimerViolations();
                
                if (data.getTimerViolations() > 3) {
                    int certainty = (int) Math.min(99, 80 + (timerRatio - 1.0) * 50);
                    flag(player, "Timer speedup detected (ratio: " + String.format("%.2f", timerRatio) + ")", certainty);
                }
            } else if (timerRatio < 1.15) {
                data.decrementTimerViolations();
            }
            
            // Advanced pattern detection for sophisticated timer
            double avgRatio = data.getAverageTimerRatio();
            if (avgRatio > 1.08 && data.getTimerSamples().size() > 10) {
                flag(player, "Consistent timer usage detected (avg ratio: " + String.format("%.2f", avgRatio) + ")", 92);
            }
        }
        
        data.setLastMoveTime(currentTime);
    }
}
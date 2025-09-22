package network.mememc.memeac.listeners;

import network.mememc.memeac.MemeAC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {
    
    private final MemeAC plugin;
    
    public PlayerDataListener(MemeAC plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player data
        plugin.getPlayerDataManager().getPlayerData(event.getPlayer());
        
        // Check if this is a Bedrock player (delayed check to allow Geyser to process)
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            boolean isBedrock = plugin.getGeyserCompatibility().isBedrockPlayer(event.getPlayer());
            if (isBedrock) {
                plugin.getACLogger().info("Bedrock player joined: " + event.getPlayer().getName());
            }
        }, 20L); // 1 second delay
        
        plugin.getACLogger().debug("Initialized data for player: " + event.getPlayer().getName());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up player data
        plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
        
        // Clean up Geyser data
        plugin.getGeyserCompatibility().removePlayer(event.getPlayer());
        
        plugin.getACLogger().debug("Cleaned up data for player: " + event.getPlayer().getName());
    }
}

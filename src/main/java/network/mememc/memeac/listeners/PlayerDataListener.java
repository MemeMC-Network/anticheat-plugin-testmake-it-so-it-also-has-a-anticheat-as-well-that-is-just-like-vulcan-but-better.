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
        
        plugin.getACLogger().debug("Initialized data for player: " + event.getPlayer().getName());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up player data
        plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
        
        plugin.getACLogger().debug("Cleaned up data for player: " + event.getPlayer().getName());
    }
}

package network.mememc.memeac.managers;

import network.mememc.memeac.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    
    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), 
                uuid -> new PlayerData(player.getName()));
    }
    
    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }
    
    public void clearAllData() {
        playerDataMap.clear();
    }
    
    public int getTrackedPlayersCount() {
        return playerDataMap.size();
    }
    
    public void cleanupInactivePlayers() {
        // Remove data for players who are no longer online
        Iterator<Map.Entry<UUID, PlayerData>> iterator = playerDataMap.entrySet().iterator();
        int removed = 0;
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, PlayerData> entry = iterator.next();
            UUID uuid = entry.getKey();
            PlayerData data = entry.getValue();
            
            // Check if player is still online
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                // Check if data is old (more than 1 hour since last seen)
                long timeSinceJoin = System.currentTimeMillis() - data.getJoinTime();
                if (timeSinceJoin > 60 * 60 * 1000) { // 1 hour
                    iterator.remove();
                    removed++;
                }
            }
        }
        
        if (removed > 0) {
            // Log cleanup if debug mode is enabled
            System.out.println("[MemeAC] Cleaned up " + removed + " inactive player data entries");
        }
    }
    
    public Map<UUID, PlayerData> getAllPlayerData() {
        return new HashMap<>(playerDataMap);
    }
}

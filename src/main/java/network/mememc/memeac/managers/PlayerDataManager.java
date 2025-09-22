package network.mememc.memeac.managers;

import network.mememc.memeac.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
}

package network.mememc.memeac.checks;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.data.PlayerData;
import network.mememc.memeac.data.ViolationData;
import org.bukkit.entity.Player;

public abstract class Check {
    
    protected final MemeAC plugin;
    protected final String name;
    protected final CheckType type;
    protected final int maxViolations;
    protected final double accuracyRate;
    protected boolean enabled;
    
    public Check(MemeAC plugin, String name, CheckType type, int maxViolations, double accuracyRate) {
        this.plugin = plugin;
        this.name = name;
        this.type = type;
        this.maxViolations = maxViolations;
        this.accuracyRate = accuracyRate;
        this.enabled = true;
    }
    
    public void flag(Player player, String reason, int certainty) {
        if (!enabled || player.hasPermission("memeac.bypass")) return;
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        ViolationData violation = new ViolationData(this, reason, certainty, System.currentTimeMillis());
        
        plugin.getViolationManager().handleViolation(player, violation);
    }
    
    public void flag(Player player, String reason) {
        flag(player, reason, 100);
    }
    
    public String getName() {
        return name;
    }
    
    public CheckType getType() {
        return type;
    }
    
    public int getMaxViolations() {
        return maxViolations;
    }
    
    public double getAccuracyRate() {
        return accuracyRate;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public enum CheckType {
        MOVEMENT("Movement"),
        COMBAT("Combat"),
        WORLD("World"),
        PACKET("Packet"),
        NETWORK("Network");
        
        private final String displayName;
        
        CheckType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}

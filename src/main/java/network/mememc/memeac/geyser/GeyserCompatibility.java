package network.mememc.memeac.geyser;

import network.mememc.memeac.MemeAC;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Geyser compatibility manager for handling Bedrock Edition players
 * Provides detection and special handling for cross-platform players
 */
public class GeyserCompatibility {
    
    private final MemeAC plugin;
    private final Set<UUID> bedrockPlayers = new HashSet<>();
    private boolean geyserEnabled = false;
    private Object geyserApi = null;
    
    public GeyserCompatibility(MemeAC plugin) {
        this.plugin = plugin;
        initializeGeyserIntegration();
    }
    
    /**
     * Initialize Geyser integration if available
     */
    private void initializeGeyserIntegration() {
        Plugin geyser = plugin.getServer().getPluginManager().getPlugin("Geyser-Spigot");
        if (geyser != null && geyser.isEnabled()) {
            try {
                // Try to load Geyser API
                Class<?> geyserApiClass = Class.forName("org.geysermc.geyser.api.GeyserApi");
                geyserApi = geyserApiClass.getMethod("api").invoke(null);
                geyserEnabled = true;
                plugin.getACLogger().info("✅ Geyser compatibility enabled - Bedrock Edition support active!");
            } catch (Exception e) {
                plugin.getACLogger().warning("⚠️ Geyser detected but API integration failed: " + e.getMessage());
            }
        } else {
            plugin.getACLogger().info("ℹ️ Geyser not detected - Java Edition only mode");
        }
    }
    
    /**
     * Check if a player is connecting from Bedrock Edition
     */
    public boolean isBedrockPlayer(Player player) {
        if (!geyserEnabled || geyserApi == null) {
            return false;
        }
        
        try {
            // Check if player is in our cache first
            if (bedrockPlayers.contains(player.getUniqueId())) {
                return true;
            }
            
            // Use Geyser API to check if player is from Bedrock
            Class<?> geyserApiClass = geyserApi.getClass();
            Object connectionByUuid = geyserApiClass.getMethod("connectionByUuid", UUID.class)
                    .invoke(geyserApi, player.getUniqueId());
            
            boolean isBedrock = connectionByUuid != null;
            
            if (isBedrock) {
                bedrockPlayers.add(player.getUniqueId());
                plugin.getACLogger().info("🎮 Bedrock player detected: " + player.getName());
            }
            
            return isBedrock;
        } catch (Exception e) {
            // Fallback detection methods
            return detectBedrockByFallback(player);
        }
    }
    
    /**
     * Fallback method to detect Bedrock players when Geyser API is unavailable
     */
    private boolean detectBedrockByFallback(Player player) {
        // Check common Bedrock player patterns
        String playerName = player.getName();
        
        // Geyser prefix check
        if (playerName.startsWith(".") || playerName.contains("*")) {
            bedrockPlayers.add(player.getUniqueId());
            return true;
        }
        
        // Check client brand (if available)
        try {
            // This is a simplified check - in real implementation we'd hook into packet handling
            String clientBrand = player.getClientBrandName();
            if (clientBrand != null && (clientBrand.contains("mcpe") || clientBrand.contains("bedrock"))) {
                bedrockPlayers.add(player.getUniqueId());
                return true;
            }
        } catch (Exception ignored) {
            // Client brand not available in all server versions
        }
        
        return false;
    }
    
    /**
     * Get adjusted movement threshold for Bedrock players
     * Bedrock Edition has different movement mechanics
     */
    public double getAdjustedMovementThreshold(Player player, double originalThreshold) {
        if (isBedrockPlayer(player)) {
            // Bedrock players have slightly different movement patterns
            return originalThreshold * 1.15; // 15% more lenient
        }
        return originalThreshold;
    }
    
    /**
     * Get adjusted reach distance for Bedrock players
     * Bedrock Edition has different reach mechanics
     */
    public double getAdjustedReachDistance(Player player, double originalReach) {
        if (isBedrockPlayer(player)) {
            // Bedrock has slightly longer reach in some cases
            return originalReach + 0.2;
        }
        return originalReach;
    }
    
    /**
     * Check if we should skip certain checks for Bedrock players
     */
    public boolean shouldSkipCheck(Player player, String checkName) {
        if (!isBedrockPlayer(player)) {
            return false;
        }
        
        // Skip checks that are incompatible with Bedrock Edition
        switch (checkName.toLowerCase()) {
            case "invalidpacket":
                return true; // Bedrock packets are fundamentally different
            case "timer":
                return true; // Bedrock has different timing
            case "freecam":
                return true; // Not applicable to Bedrock
            default:
                return false;
        }
    }
    
    /**
     * Get platform-specific violation threshold
     */
    public int getAdjustedViolationThreshold(Player player, int originalThreshold) {
        if (isBedrockPlayer(player)) {
            // Be more lenient with Bedrock players due to platform differences
            return Math.max(1, originalThreshold + 2);
        }
        return originalThreshold;
    }
    
    /**
     * Remove player from Bedrock tracking on disconnect
     */
    public void removePlayer(Player player) {
        bedrockPlayers.remove(player.getUniqueId());
    }
    
    /**
     * Get Bedrock player statistics
     */
    public BedrockStats getBedrockStats() {
        return new BedrockStats(bedrockPlayers.size(), geyserEnabled);
    }
    
    /**
     * Check if Geyser integration is available
     */
    public boolean isGeyserEnabled() {
        return geyserEnabled;
    }
    
    /**
     * Statistics class for Bedrock players
     */
    public static class BedrockStats {
        private final int activeBedrockPlayers;
        private final boolean geyserIntegration;
        
        public BedrockStats(int activeBedrockPlayers, boolean geyserIntegration) {
            this.activeBedrockPlayers = activeBedrockPlayers;
            this.geyserIntegration = geyserIntegration;
        }
        
        public int getActiveBedrockPlayers() {
            return activeBedrockPlayers;
        }
        
        public boolean hasGeyserIntegration() {
            return geyserIntegration;
        }
    }
}
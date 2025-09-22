package network.mememc.memeac.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class LocationUtils {
    
    public static boolean isOnGround(Location location) {
        Block below = location.getBlock().getRelative(0, -1, 0);
        return below.getType().isSolid();
    }
    
    public static boolean hasNearbyBlocks(Location location, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    
                    Block block = location.getBlock().getRelative(x, y, z);
                    if (block.getType().isSolid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static double getHorizontalDistance(Location from, Location to) {
        double deltaX = to.getX() - from.getX();
        double deltaZ = to.getZ() - from.getZ();
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }
    
    public static boolean isOnIce(Location location) {
        Block below = location.getBlock().getRelative(0, -1, 0);
        Material type = below.getType();
        return type == Material.ICE || type == Material.PACKED_ICE || 
               type.name().equals("BLUE_ICE") || type.name().equals("FROSTED_ICE");
    }
    
    public static boolean isInWater(Location location) {
        Block block = location.getBlock();
        return block.getType() == Material.WATER || block.isLiquid();
    }
    
    public static boolean isClimbable(Location location) {
        Block block = location.getBlock();
        Material type = block.getType();
        return type == Material.LADDER || type == Material.VINE || 
               type.name().equals("SCAFFOLDING");
    }
    
    public static boolean hasLiquidBelow(Location location, int depth) {
        for (int i = 1; i <= depth; i++) {
            Block below = location.getBlock().getRelative(0, -i, 0);
            if (below.isLiquid()) {
                return true;
            }
        }
        return false;
    }
}

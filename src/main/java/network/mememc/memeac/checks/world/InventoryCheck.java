package network.mememc.memeac.checks.world;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;

public class InventoryCheck extends Check {
    public InventoryCheck(MemeAC plugin) { super(plugin, "Inventory", CheckType.WORLD, 8, 98.6); }
}

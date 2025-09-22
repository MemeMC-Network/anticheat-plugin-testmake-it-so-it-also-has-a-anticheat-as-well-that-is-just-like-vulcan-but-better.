package network.mememc.memeac.checks.combat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;

public class HitboxCheck extends Check {
    public HitboxCheck(MemeAC plugin) { super(plugin, "Hitbox", CheckType.COMBAT, 5, 99.8); }
}

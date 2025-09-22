package network.mememc.memeac.checks.combat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;

public class VelocityCheck extends Check {
    public VelocityCheck(MemeAC plugin) { super(plugin, "Velocity", CheckType.COMBAT, 8, 98.8); }
}

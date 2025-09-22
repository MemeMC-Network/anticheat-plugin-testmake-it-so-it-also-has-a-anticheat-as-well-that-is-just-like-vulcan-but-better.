package network.mememc.memeac.checks.movement;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;

public class PhaseCheck extends Check {
    public PhaseCheck(MemeAC plugin) { super(plugin, "Phase", CheckType.MOVEMENT, 6, 99.4); }
}

package network.mememc.memeac.checks.combat;

import network.mememc.memeac.MemeAC;
import network.mememc.memeac.checks.Check;

public class CriticalsCheck extends Check {
    public CriticalsCheck(MemeAC plugin) { super(plugin, "Criticals", CheckType.COMBAT, 6, 99.3); }
}

package net.lugocorp.kingdom.ai.stats;
import net.lugocorp.kingdom.game.player.Player;

/**
 * This class tracks various stats about the CompPlayer's performance to help
 * guide decision-making.
 */
public class Statistics {
    public final DiffStat income = new DiffStat(Player.INITIAL_GOLD);
    // TODO AI track enemies killed per turn
    // TODO AI track units lost per turn
    // TODO AI track unit points gained per turn
}

package net.lugocorp.kingdom.ai.stats;
import net.lugocorp.kingdom.game.player.Player;

/**
 * This class tracks various stats about the CompPlayer's performance to help
 * guide decision-making.
 */
public class Statistics {
    public final DiffStat income = new DiffStat(Player.INITIAL_GOLD);
}

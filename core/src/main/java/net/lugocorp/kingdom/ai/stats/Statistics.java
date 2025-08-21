package net.lugocorp.kingdom.ai.stats;
import net.lugocorp.kingdom.game.player.Player;

/**
 * This class tracks various stats about the CompPlayer's performance to help
 * guide decision-making.
 */
public class Statistics {
    public final DiffStat income = new DiffStat(Player.INITIAL_GOLD);
    public final DiffStat unitPoints = new DiffStat(0);
    public final Stat naturalHarvest = new Stat();
    public final Stat otherHarvest = new Stat();
    public final Stat enemiesKilled = new Stat();
    public final Stat unitsLost = new Stat();

    /**
     * Commits all the stats we track in this class
     */
    public void commit() {
        this.income.commit();
        this.unitPoints.commit();
        this.naturalHarvest.commit();
        this.otherHarvest.commit();
        this.enemiesKilled.commit();
        this.unitsLost.commit();
    }
}

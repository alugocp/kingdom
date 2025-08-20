package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.utils.code.Lambda;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class tracks various stats about the CompPlayer's performance to help
 * guide decision-making.
 */
public class Stats {
    private static final int WINDOW_SIZE = 50;
    private final List<Integer> income = new ArrayList<>();
    private Optional<Integer> previousGoldAmount = Optional.empty();

    /**
     * Keeps track of the given gold value for this CompPlayer
     */
    public void trackGold(int g) {
        int diff = g - this.previousGoldAmount.map((Integer i) -> i).orElse(Player.INITIAL_GOLD);
        this.income.add(diff);
        if (this.income.size() > Stats.WINDOW_SIZE) {
            this.income.remove(0);
        }
    }

    /**
     * Returns the CompPlayer's recent average gold income
     */
    public int getAverageIncome() {
        // TODO do not account for purchases
        return Lambda.fold((Integer acc, Integer g) -> acc + g, 0, this.income) / this.income.size();
    }
}

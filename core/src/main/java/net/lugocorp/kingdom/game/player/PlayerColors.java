package net.lugocorp.kingdom.game.player;
import net.lugocorp.kingdom.utils.logic.Colors;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all the possible Player colors
 */
public class PlayerColors {
    private final List<Color> pool = new ArrayList<>();

    public PlayerColors() {
        this.pool.add(Colors.fromHex(0x00ff00)); // Green
        this.pool.add(Colors.fromHex(0xff0000)); // Red
        this.pool.add(Colors.fromHex(0x0000ff)); // Blue
        this.pool.add(Colors.fromHex(0x880088)); // Purple
        this.pool.add(Colors.fromHex(0xffff00)); // Yellow
        this.pool.add(Colors.fromHex(0xff7d00)); // Orange
        this.pool.add(Colors.fromHex(0x00ffff)); // Cyan
        this.pool.add(Colors.fromHex(0xff00ff)); // Pink
    }

    /**
     * Retrieves a Color from the pool
     */
    public final Color getFromPool() {
        if (this.pool.size() == 0) {
            System.err.println("Warning: The color pool has run dry");
            return Color.BLACK;
        }
        return this.pool.remove(0);
    }

    /**
     * Returns a Color back to the pool
     */
    public final void releaseToPool(Color c) {
        this.pool.add(c);
    }
}

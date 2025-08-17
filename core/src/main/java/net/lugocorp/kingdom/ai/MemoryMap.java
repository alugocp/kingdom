package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * This map represents the Actor's "vision" of the World. It tracks what the
 * Actor "saw" at each Tile the last time it had visibility there.
 */
public class MemoryMap {
    // TODO get this class to record what the Actor last saw on each Cell
    private final Cell[][] grid;

    public MemoryMap(Point p) {
        this.grid = new Cell[p.x][p.y];
    }

    /**
     * Returns the Cell at the given coordinate in the grid
     */
    private Cell getCell(Point p) {
        return this.grid[p.x][p.y];
    }

    /**
     * Adds visibility to this MemoryMap on the given Cell
     */
    public void incrementVisibility(Point p) {
        this.getCell(p).hasBeenSeen = true;
        this.getCell(p).visibility++;
    }

    /**
     * Removes visibility from this MemoryMap on the given Cell
     */
    public void decrementVisibility(Point p) {
        this.getCell(p).visibility--;
    }

    /**
     * This class details what our MemoryMap "remembers" about each Tile
     */
    private static class Cell {
        private Optional<String> building = Optional.empty();
        private Optional<Player> owner = Optional.empty();
        private boolean hasBeenSeen = false;
        private int visibility = 0;
    }
}

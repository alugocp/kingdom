package net.lugocorp.kingdom.ai.memory;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * This map represents the Actor's "vision" of the World. It tracks what the
 * Actor "saw" at each Tile the last time it had visibility there.
 */
public class MemoryMap {
    private final MemoryCell[][] grid;

    public MemoryMap(Point p) {
        this.grid = new MemoryCell[p.x][p.y];
    }

    /**
     * Returns the MemoryCell at the given coordinate in the grid
     */
    public MemoryCell getCell(Point p) {
        return this.grid[p.x][p.y];
    }

    /**
     * Adds visibility to this MemoryMap on the given MemoryCell
     */
    public void incrementVisibility(Tile t) {
        MemoryCell cell = this.getCell(t.getPoint());
        cell.hasBeenSeen = true;
        cell.visibility++;
        cell.building = t.building.isPresent() ? Optional.of(t.building.get().getStratifier()) : Optional.empty();
        cell.unit = t.unit.isPresent() ? Optional.of(t.unit.get().getStratifier()) : Optional.empty();
        cell.owner = t.leader;
    }

    /**
     * Removes visibility from this MemoryMap on the given MemoryCell
     */
    public void decrementVisibility(Tile t) {
        MemoryCell cell = this.getCell(t.getPoint());
        cell.visibility--;
        if (cell.visibility == 0) {
            cell.unit = Optional.empty();
        }
    }
}

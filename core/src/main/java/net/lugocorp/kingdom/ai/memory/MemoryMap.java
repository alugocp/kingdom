package net.lugocorp.kingdom.ai.memory;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * This map represents the Actor's "vision" of the World. It tracks what the
 * Actor "saw" at each Tile the last time it had vision there.
 */
public class MemoryMap {
    private final MemoryCell[][] grid;

    public MemoryMap(Point p) {
        this.grid = new MemoryCell[p.x][p.y];
        for (int a = 0; a < p.x; a++) {
            for (int b = 0; b < p.y; b++) {
                this.grid[a][b] = new MemoryCell();
            }
        }
    }

    /**
     * Returns the MemoryCell at the given coordinate in the grid
     */
    public MemoryCell getCell(Point p) {
        return this.grid[p.x][p.y];
    }

    /**
     * Adds vision to this MemoryMap on the given MemoryCell
     */
    public void incrementVision(Tile t) {
        MemoryCell cell = this.getCell(t.getPoint());
        cell.hasBeenSeen = true;
        cell.vision++;
        cell.building = t.building.isPresent() ? Optional.of(t.building.get().getStratifier()) : Optional.empty();
        cell.unit = t.unit.isPresent() ? Optional.of(t.unit.get().getStratifier()) : Optional.empty();
        cell.glyph = t.getGlyph();
        cell.owner = t.leader;
    }

    /**
     * Removes vision from this MemoryMap on the given MemoryCell
     */
    public void decrementVision(Tile t) {
        MemoryCell cell = this.getCell(t.getPoint());
        cell.vision--;
        if (cell.vision == 0) {
            cell.unit = Optional.empty();
        }
    }
}

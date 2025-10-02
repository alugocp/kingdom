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
    private final Point size;

    public MemoryMap(Point p) {
        this.size = p;
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
    public Optional<MemoryCell> getCell(Point p) {
        return p.x >= 0 && p.y >= 0 && p.x < this.size.x && p.y < this.size.y
                ? Optional.of(this.grid[p.x][p.y])
                : Optional.empty();
    }

    /**
     * Adds vision to this MemoryMap on the given MemoryCell
     */
    public void incrementVision(Tile t) {
        this.getCell(t.getPoint()).ifPresent((MemoryCell cell) -> {
            cell.hasBeenSeen = true;
            cell.vision++;
            cell.building = t.building.isPresent() ? Optional.of(t.building.get().getStratifier()) : Optional.empty();
            cell.unit = t.unit.isPresent() ? Optional.of(t.unit.get().getStratifier()) : Optional.empty();
            cell.glyph = t.getGlyph();
            cell.owner = t.leader;
        });
    }

    /**
     * Removes vision from this MemoryMap on the given MemoryCell
     */
    public void decrementVision(Tile t) {
        this.getCell(t.getPoint()).ifPresent((MemoryCell cell) -> {
            cell.vision--;
            if (cell.vision == 0) {
                cell.unit = Optional.empty();
            }
        });
    }
}

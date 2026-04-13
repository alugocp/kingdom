package net.lugocorp.kingdom.ai.memory;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This map represents the Actor's "vision" of the World. It tracks what the
 * Actor "saw" at each Tile the last time it had vision there.
 */
public class MemoryMap {
    private final Set<Point> knownCells = new HashSet<>();
    private final MemoryCell[][] grid;
    private final Point size;

    public MemoryMap(int w, int h) {
        this.size = new Point(w, h);
        this.grid = new MemoryCell[w][h];
        for (int a = 0; a < w; a++) {
            for (int b = 0; b < h; b++) {
                this.grid[a][b] = new MemoryCell();
            }
        }
    }

    /**
     * Refreshes each currently visible MemoryCell
     */
    public void refresh(GameView view) {
        // TODO optimize this when the maps get larger
        for (int a = 0; a < this.size.x; a++) {
            for (int b = 0; b < this.size.y; b++) {
                final MemoryCell m = this.grid[a][b];
                if (m.vision > 0) {
                    final Tile t = view.game.world.getTile(a, b).get();
                    this.decrementVision(t);
                    this.incrementVision(t);
                }
            }
        }
    }

    /**
     * Returns a Set of Points that have been seen before
     */
    public Set<Point> getKnownCells() {
        return this.knownCells;
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
            if (!cell.hasBeenSeen) {
                this.knownCells.add(t.getPoint());
                cell.hasBeenSeen = true;
            }
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

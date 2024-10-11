package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Properties and logic for the physical game map
 */
public class World {
    private final List<List<Tile>> grid = new ArrayList<List<Tile>>();
    private final int w;
    private final int h;

    public World(int w, int h) {
        this.w = w;
        this.h = h;

        for (int x = 0; x < w; x++) {
            List<Tile> column = new ArrayList<Tile>();
            for (int y = 0; y < h; y++) {
                column.add(null);
            }
            this.grid.add(column);
        }
    }

    /**
     * Sets the Tile at the specified position in the grid
     */
    public void setTile(Tile t, int x, int y) {
        this.grid.get(x).set(y, t);
    }

    /**
     * Calls into the other isInBounds() method
     */
    public boolean isInBounds(Point p) {
        return this.isInBounds(p.x, p.y);
    }

    /**
     * Returns true if the coordinate points to a valid Tile in the World
     */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < this.w && y < this.h;
    }

    /**
     * Calls into the other getTile() method
     */
    public Optional<Tile> getTile(Point p) {
        return this.getTile(p.x, p.y);
    }

    /**
     * Returns the Tile at this point in the World
     */
    public Optional<Tile> getTile(int x, int y) {
        if (!this.isInBounds(x, y)) {
            return Optional.empty();
        }
        Tile t = this.grid.get(x).get(y);
        if (t == null) {
            return Optional.empty();
        }
        return Optional.of(t);
    }

    /**
     * Returns World width
     */
    public int getWidth() {
        return this.w;
    }

    /**
     * Returns World height
     */
    public int getHeight() {
        return this.h;
    }

    /**
     * Returns a set of all Models to be rendered for this World
     */
    public Array<ModelInstance> getModelInstances() {
        Array<ModelInstance> models = new Array<>();
        for (int x = 0; x < this.w; x++) {
            for (int y = 0; y < this.h; y++) {
                Optional<Tile> tile = this.getTile(x, y);
                if (!tile.isPresent()) {
                    continue;
                }
                tile.get().building.flatMap((Building b) -> b.getModelInstance())
                        .ifPresent((ModelInstance m) -> models.add(m));
                tile.get().unit.flatMap((Unit u) -> u.getModelInstance()).ifPresent((ModelInstance m) -> models.add(m));
                tile.get().getModelInstance().ifPresent((ModelInstance m) -> models.add(m));
            }
        }
        return models;
    }
}

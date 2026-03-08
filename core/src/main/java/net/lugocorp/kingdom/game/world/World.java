package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Properties and logic for the physical game map
 */
public class World implements Iterable<Tile> {
    private final List<List<Tile>> grid = new ArrayList<List<Tile>>();
    private WorldGenOptions options = null;

    public void init(WorldGenOptions options) {
        this.options = options;

        for (int x = 0; x < options.size.w; x++) {
            List<Tile> column = new ArrayList<Tile>();
            for (int y = 0; y < options.size.h; y++) {
                column.add(null);
            }
            this.grid.add(column);
        }
    }

    /**
     * Returns an Iterator for the Tiles in this World
     */
    @Override
    public Iterator<Tile> iterator() {
        final World that = this;

        return new Iterator<Tile>() {
            private int x = 0;
            private int y = 0;

            /** {@inheritdoc} */
            @Override
            public boolean hasNext() {
                return this.x < that.options.size.w && this.y < that.options.size.h;
            }

            /** {@inheritdoc} */
            @Override
            public Tile next() {
                final Tile t = that.getTile(this.x, this.y).get();
                if (++this.x == that.options.size.w) {
                    this.x = 0;
                    this.y++;
                }
                return t;
            }
        };
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
        return x >= 0 && y >= 0 && x < this.options.size.w && y < this.options.size.h;
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
     * Returns the Unit at this point in the World
     */
    public Optional<Unit> getUnit(Point p) {
        return this.getTile(p).flatMap((Tile t) -> t.unit);
    }

    /**
     * Returns the Building at this point in the World
     */
    public Optional<Building> getBuilding(Point p) {
        return this.getTile(p).flatMap((Tile t) -> t.building);
    }

    /**
     * Returns World width
     */
    public int getWidth() {
        return this.options.size.w;
    }

    /**
     * Returns World height
     */
    public int getHeight() {
        return this.options.size.h;
    }

    /**
     * Returns the World seed
     */
    public long getSeed() {
        return this.options.seed;
    }

    /**
     * Returns all Modellable instances present in the World
     */
    public Array<Modellable> getModellables(boolean renderTiles, Optional<Rect> bounds) {
        final Array<Modellable> models = new Array<>();
        final int left = Math.max(bounds.map((Rect r) -> r.x).orElse(0), 0);
        final int top = Math.max(bounds.map((Rect r) -> r.y).orElse(0), 0);
        final int right = Math.min(bounds.map((Rect r) -> r.w).orElse(this.options.size.w), this.options.size.w);
        final int bottom = Math.min(bounds.map((Rect r) -> r.h).orElse(this.options.size.h), this.options.size.h);
        for (int x = left; x < right; x++) {
            for (int y = top; y < bottom; y++) {
                final Optional<Tile> tile = this.getTile(x, y);
                if (!tile.isPresent()) {
                    continue;
                }
                if (renderTiles) {
                    models.add(tile.get());
                } else if (tile.get().isVisible()) {
                    tile.get().building.ifPresent((Modellable m) -> models.add(m));
                    tile.get().unit.ifPresent((Modellable m) -> models.add(m));
                }
            }
        }
        return models;
    }

    /**
     * Returns a set of all Models to be rendered for this World, filtered by
     * justTiles. If true then this method will only return the ModelInstances of
     * Tiles, and if false then it will return all others.
     */
    public Array<ModelInstance> getModelInstances(boolean renderTiles, Optional<Rect> bounds) {
        final Array<ModelInstance> models = new Array<>();
        this.getModellables(renderTiles, bounds)
                .forEach((Modellable m) -> m.getModelInstance().ifPresent((ModelInstance m1) -> models.add(m1)));
        for (int x = 0; x < this.options.size.w; x++) {
            for (int y = 0; y < this.options.size.h; y++) {
                final Optional<Tile> tile = this.getTile(x, y);
                if (!tile.isPresent()) {
                    continue;
                }
                if (tile.map((Tile t) -> t.hasBeenSeen() && !t.isVisible()).orElse(false)) {
                    tile.get().getPlaceholderBuildingModel().ifPresent((ModelInstance m) -> models.add(m));
                }
            }
        }
        return models;
    }
}

package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.engine.render.Modellable;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Point;
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
    private int w = 0;
    private int h = 0;

    public void init(WorldSize size) {
        this.w = size.w;
        this.h = size.h;

        for (int x = 0; x < w; x++) {
            List<Tile> column = new ArrayList<Tile>();
            for (int y = 0; y < h; y++) {
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
                return this.x < that.w && this.y < that.h;
            }

            /** {@inheritdoc} */
            @Override
            public Tile next() {
                final Tile t = that.getTile(this.x, this.y).get();
                if (++this.x == that.w) {
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
        return this.w;
    }

    /**
     * Returns World height
     */
    public int getHeight() {
        return this.h;
    }

    /**
     * Returns World size
     */
    public Point getSize() {
        return new Point(this.w, this.h);
    }

    /**
     * Returns all Modellable instances present in the World
     */
    public Array<Modellable> getModellables(boolean includeTiles) {
        // TODO includeTiles -> "justTiles vs justEntities" when we split the Toon
        // shader
        // TODO include placeholder building models somehow
        final Array<Modellable> models = new Array<>();
        for (int x = 0; x < this.w; x++) {
            for (int y = 0; y < this.h; y++) {
                final Optional<Tile> tile = this.getTile(x, y);
                if (!tile.isPresent()) {
                    continue;
                }
                if (includeTiles) {
                    models.add(tile.get());
                }
                if (tile.get().isVisible()) {
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
    public Array<ModelInstance> getModelInstances(boolean includeTiles) {
        final Array<ModelInstance> models = new Array<>();
        this.getModellables(includeTiles)
                .forEach((Modellable m) -> m.getModelInstance().ifPresent((ModelInstance m1) -> models.add(m1)));
        for (int x = 0; x < this.w; x++) {
            for (int y = 0; y < this.h; y++) {
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

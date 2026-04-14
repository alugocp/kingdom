package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class wraps the TileSelectMode logic for external use
 */
public class TileSelector {
    private final GameView view;
    private TileSelectMode mode = new TileMenuSelectMode();
    private Optional<Point> hovered = Optional.empty();
    private Point hoveredPoint = new Point(0, 0);

    public TileSelector(GameView view) {
        this.view = view;
    }

    /**
     * Returns the currently hovered Point
     */
    public Optional<Point> getHovered() {
        return this.hovered;
    }

    /**
     * Sets the current TileSelectMode
     */
    private void setMode(TileSelectMode mode) {
        this.mode.dispel(this.view);
        this.mode = mode;
        this.mode.init(this.view);
    }

    /**
     * Resets the TileSelectMode back to default
     */
    public void resetMode() {
        if (!(this.mode instanceof TileMenuSelectMode)) {
            this.setMode(new TileMenuSelectMode());
        }
    }

    /**
     * Sets TileSetSelectMode mode
     */
    public final void select(Set<Point> points, String error, Consumer<Point> action) {
        if (points.size() == 0) {
            this.view.hud.logger.error(error);
            return;
        }
        this.setMode(new TileSetSelectMode(points, action));
    }

    /**
     * Sets TileMoveSelectMode mode
     */
    public final void move(Unit unit) {
        this.setMode(new TileMoveSelectMode(unit));
    }

    /**
     * Sets TileSetSelectMode mode for the express purpose of depositing a Unit's
     * items
     */
    public final void deposit(Unit unit) {
        final Set<Point> points = Lambda.filter((Point p) -> Hexagons.areNeighbors(p, unit.getPoint()),
                this.view.game.getVaultBuildings(unit.getLeader().get()));
        this.select(points, "Nowhere to deposit this unit's items", (Point p) -> {
            final Building b = this.view.game.world.getTile(p).flatMap((Tile t) -> t.building).get();
            unit.haul.transferMaximum(b.items.get());
        });
    }

    /**
     * Keeps track of the currently hovered Tile
     */
    public final void hover(Point p) {
        if (this.hovered.map((Point h) -> h.equals(p)).orElse(false)) {
            return;
        }
        this.hovered
                .ifPresent((Point h) -> this.view.game.world.getTile(h).ifPresent((Tile t) -> t.changeHovered(false)));
        if (this.view.game.world.isInBounds(p)) {
            this.view.game.world.getTile(p).ifPresent((Tile t) -> {
                this.mode.hoverTile(this.view, p);
                t.changeHovered(true);
            });
            this.hovered = Optional.of(this.hoveredPoint);
            this.hoveredPoint.set(p);
        } else {
            this.hovered = Optional.empty();
        }
    }

    /**
     * Performs some action on the currently selected Tile
     */
    public final void click() {
        final TileSelectMode m = this.mode;
        if (this.hovered.isPresent()) {
            if (this.mode.isValidTile(this.view, this.hovered.get())) {
                this.mode.clickedValidPoint(this.view, this.hovered.get());
            } else {
                this.mode.clickedInvalidPoint(this.view);
                return;
            }
        }

        // Do not go back to TileMenuSelectMode if we've changed
        // modes already. This enables Abilities or Actions that
        // involve multiple selections.
        if (this.mode == m) {
            this.resetMode();
        }
    }
}

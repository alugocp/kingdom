package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
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

    public TileSelector(GameView view) {
        this.view = view;
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
     * Sets TileSetSelectMode mode
     */
    public final void select(Set<Point> points, String error, Consumer<Point> action) {
        if (points.size() == 0) {
            this.view.logger.error(error);
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
     * Keeps track of the currently hovered Tile
     */
    public final void hover(Point p) {
        this.hovered
                .ifPresent((Point h) -> this.view.game.world.getTile(h).ifPresent((Tile t) -> t.decrementSelection()));
        if (this.view.game.world.isInBounds(p)) {
            this.view.game.world.getTile(p).ifPresent((Tile t) -> t.incrementSelection());
            this.hovered = Optional.of(p);
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
            }
        }

        // Do not go back to TileMenuSelectMode if we've changed
        // modes already. This enables Abilities or Actions that
        // involve multiple selections.
        if (this.mode == m && !(m instanceof TileMenuSelectMode)) {
            this.setMode(new TileMenuSelectMode());
        }
    }
}

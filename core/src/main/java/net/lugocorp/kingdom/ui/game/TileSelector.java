package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class contains logic for Tile selection by the human Player
 */
public class TileSelector {
    private final GameView view;
    private Optional<TileSelection> selection = Optional.empty();
    private Optional<Point> hovered = Optional.empty();

    public TileSelector(GameView view) {
        this.view = view;
    }

    /**
     * Keeps track of the currently hovered Tile
     */
    public void hover(Point p) {
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
     * Returns the hovered Point in the World (if any)
     */
    public Optional<Point> getHovered() {
        return this.hovered;
    }

    /**
     * Sets the currently selected Tiles
     */
    public void select(Set<Point> points, String error, Consumer<Point> action) {
        if (!this.view.game.mechanics.turns.canHumanPlayerAct()) {
            this.view.logger.log("You cannot act outside your turn");
            return;
        }
        if (points.size() == 0) {
            this.view.logger.log(error);
            return;
        }
        this.selection = Optional.of(new TileSelection(points, action));
        for (Point p : points) {
            this.view.game.world.getTile(p).ifPresent((Tile t) -> t.incrementSelection());
        }
        this.view.menu.close();
    }

    /**
     * Cancels the current TileSelection
     */
    public void deselect() {
        this.selection.ifPresent((TileSelection selection) -> {
            for (Point p : selection.points) {
                this.view.game.world.getTile(p).ifPresent((Tile t) -> t.decrementSelection());
            }
        });
        this.selection = Optional.empty();
    }

    /**
     * Returns true if the player is hovering over a Tile in the current
     * TileSelection (if any)
     */
    public boolean isHoveringSelectedTile() {
        return this.selection.isPresent() && this.hovered.isPresent()
                && this.selection.get().points.contains(this.hovered.get());
    }

    /**
     * Returns true if the player is hovering over a visible Tile
     */
    public boolean isHoveringVisibleTile() {
        return this.hovered.flatMap((Point p) -> this.view.game.world.getTile(p)).map((Tile t) -> t.isVisible())
                .orElse(false);
    }

    /**
     * Confirms the user's selected Tile and kicks off the associated action
     */
    public void runSelectionAction() {
        this.view.av.loaders.sounds.play("sfx/arrow");
        this.selection.get().action.accept(this.hovered.get());
        this.deselect();
    }

    /**
     * This nested class represents a selection of Tiles with some action to perform
     * on the result
     */
    private static class TileSelection {
        private final Consumer<Point> action;
        private final Set<Point> points;

        private TileSelection(Set<Point> points, Consumer<Point> action) {
            this.points = points;
            this.action = action;
        }
    }
}

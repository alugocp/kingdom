package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Consumer;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import java.util.Optional;
import java.util.Set;

/**
 * This class contains logic for Tile selection by the human Player
 */
public class TileSelector {
    private final ModelInstance highlight;
    private final GameView view;
    private Optional<TileSelection> selection = Optional.empty();
    private Optional<Point> hovered = Optional.empty();

    public TileSelector(GameView view) {
        this.highlight = view.game.graphics.loaders.assets.createModelInstance("Selector");
        this.highlight.materials.first().set(new BlendingAttribute(0.5f));
        this.view = view;
    }

    /**
     * Keeps track of the currently hovered Tile
     */
    public void hover(Point p) {
        if (this.view.game.world.isInBounds(p)) {
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
        if (!this.view.game.canHumanPlayerAct()) {
            this.view.logger.log("You cannot act outside your turn");
            return;
        }
        if (points.size() == 0) {
            this.view.logger.log(error);
            return;
        }
        this.selection = Optional.of(new TileSelection(points, action));
        this.view.closeMenu();
    }

    /**
     * Cancels the current TileSelection
     */
    public void deselect() {
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
     * Confirms the user's selected Tile and kicks off the associated action
     */
    public void runSelectionAction() {
        this.selection.get().action.run(this.hovered.get());
        this.selection = Optional.empty();
    }

    /**
     * Draws all UI elements associated with Tile hovering/selection
     */
    public void render(Environment environment) {
        if (this.selection.isPresent()) {
            for (Point p : this.selection.get().points) {
                this.highlight.transform.setTranslation(
                        Coords.grid.vector(p.x, p.y).add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.05f, 0f)));
                this.view.game.graphics.models.render(this.highlight, environment);
            }
        }
        if (this.hovered.isPresent()) {
            this.highlight.transform.setTranslation(Coords.grid.vector(this.hovered.get().x, this.hovered.get().y)
                    .add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.1f, 0f)));
            this.view.game.graphics.models.render(this.highlight, environment);
        }
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

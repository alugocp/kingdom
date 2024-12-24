package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class contains logic for Tile selection by the human Player
 */
public class TileSelector {
    private final TileSelector.HighlightModellable highlight = new HighlightModellable();
    private final GameView view;
    private Optional<TileSelection> selection = Optional.empty();
    private Optional<Point> hovered = Optional.empty();

    public TileSelector(GameView view) {
        this.highlight.setModelInstance(view.av, "ui/selector");
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
        if (!this.view.game.mechanics.turns.canHumanPlayerAct()) {
            this.view.logger.log("You cannot act outside your turn");
            return;
        }
        if (points.size() == 0) {
            this.view.logger.log(error);
            return;
        }
        this.selection = Optional.of(new TileSelection(points, action));
        this.view.menu.close();
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
        this.view.av.loaders.sounds.play("ui/arrow");
        this.selection.get().action.accept(this.hovered.get());
        this.selection = Optional.empty();
    }

    /**
     * Draws all UI elements associated with Tile hovering/selection
     */
    public void render(Environment environment) {
        if (this.selection.isPresent()) {
            for (Point p : this.selection.get().points) {
                this.highlight
                        .move(Coords.grid.vector(p.x, p.y).add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.01f, 0f)));
                this.highlight.render(this.view.av.models, environment);
            }
        }
        if (this.hovered.isPresent()) {
            this.highlight.move(Coords.grid.vector(this.hovered.get().x, this.hovered.get().y)
                    .add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.02f, 0f)));
            this.highlight.render(this.view.av.models, environment);
        }
        this.view.menu.getCoords().ifPresent((Point p) -> {
            this.highlight.move(Coords.grid.vector(p.x, p.y).add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.01f, 0f)));
            this.highlight.render(this.view.av.models, environment);
        });
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

    /**
     * This nested class extends Modellable so we can model Tile highlights
     */
    private static class HighlightModellable extends DynamicModellable {
        private Vector3 position = new Vector3(0f, 0f, 0f);

        private HighlightModellable() {
            super(0, 0);
        }

        /** {@inheritdoc} */
        @Override
        public Vector3 getPositionVector() {
            return this.position;
        }

        /** {@inheritdoc} */
        @Override
        protected void applyAlpha(ModelInstance model) {
            for (Material m : this.model.get().materials) {
                m.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
            }
        }

        /**
         * Changes the in-world position of this highlight object
         */
        public void move(Vector3 position) {
            this.position = position;
            this.resetModelPosition();
        }
    }
}

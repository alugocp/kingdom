package net.lugocorp.kingdom.ui.views;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import java.util.Optional;
import java.util.Set;
import net.lugocorp.kingdom.engine.GameViewController;
import net.lugocorp.kingdom.engine.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.Tile;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.Hud;
import net.lugocorp.kingdom.ui.Logger;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.utils.Consumer;

public class GameView implements View {
    private final ModelInstance tileHighlight;
    private Optional<Point> hoveredTile = Optional.empty();
    private Optional<Menu> menu = Optional.empty();
    private Optional<TileSelection> selection = Optional.empty();
    private Point menuCoords = new Point(0, 0);
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    public final Logger logger;
    public final Game game;
    public final Hud hud;

    GameView(Game game) {
        this.game = game;
        this.logger = new Logger(game.graphics);
        this.hud = new Hud(this);

        // Tile highlight
        this.tileHighlight = this.game.graphics.loaders.assets.createModelInstance("Selector");
        this.tileHighlight.materials.get(0).set(new BlendingAttribute(0.5f));
    }

    /**
     * Keeps track of the currently hovered Tile
     */
    public void setHoveredTile(Point p) {
        if (this.game.world.isInBounds(p)) {
            this.hoveredTile = Optional.of(p);
        } else {
            this.hoveredTile = Optional.empty();
        }
    }

    /**
     * Sets the currently selected Tiles
     */
    public void selectTiles(Set<Point> points, String error, Consumer<Point> action) {
        if (!this.game.canHumanPlayerAct()) {
            this.logger.log("You cannot act outside your turn");
            return;
        }
        if (points.size() == 0) {
            this.logger.log(error);
            return;
        }
        this.selection = Optional.of(new TileSelection(points, action));
        this.menu = Optional.empty();
    }

    /**
     * Returns true if the player is hovering over a Tile in the current
     * TileSelection (if any)
     */
    public boolean isHoveringSelectionTile() {
        return this.selection.isPresent() && this.hoveredTile.isPresent()
                && this.selection.get().points.contains(this.hoveredTile.get());
    }

    /**
     * Confirms the user's selected Tile and kicks off the associated action
     */
    public void triggerSelectionAction() {
        this.selection.get().action.run(this.hoveredTile.get());
        this.selection = Optional.empty();
    }

    /**
     * Handles click logic on a Tile (open a Menu for said Tile)
     */
    public void openTileMenu() {
        this.selection = Optional.empty();
        if (this.menu.isPresent()) {
            this.menu = Optional.empty();
        }
        if (!this.hoveredTile.isPresent()) {
            return;
        }
        this.menuCoords = this.hoveredTile.get();
        this.refreshMenu(false);
    }

    /**
     * Opens the Menu that is set in this View's recent memory
     */
    public void refreshMenu(boolean onlyIfCurrentlyOpen) {
        if (onlyIfCurrentlyOpen && !this.menu.isPresent()) {
            return;
        }
        Optional<Tile> t = this.game.world.getTile(this.menuCoords);
        if (!t.isPresent()) {
            return;
        }
        this.menu = Optional.of(
                new Menu(0, Hud.HEIGHT, 250, true, t.get().getMenuContent(this, this.menuCoords.x, this.menuCoords.y)));
    }

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return new Color(0.8f, 1.0f, 1.0f, 1f);
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        // 3D setup
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Menus
        MenuController menuController = new MenuController(() -> this.menu);

        // Camera
        this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camController = new GameViewController(this, menuController, this.camera);
        Gdx.input.setInputProcessor(this.camController);
        this.camera.position.set(5f, 5f, 0f);
        this.camera.lookAt(0, 0, 0);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();

        // Kick off the Player's turn
        this.game.kickOffTurn();
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        this.camController.update();

        // Draw 3D assets
        this.game.graphics.models.begin(this.camera);
        this.game.graphics.models.render(this.game.world.getModelInstances(), this.environment);
        if (this.selection.isPresent()) {
            for (Point p : this.selection.get().points) {
                this.tileHighlight.transform.setTranslation(
                        Coords.grid.vector(p.x, p.y).add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.05f, 0f)));
                this.game.graphics.models.render(this.tileHighlight, this.environment);
            }
        }
        if (this.hoveredTile.isPresent()) {
            this.tileHighlight.transform
                    .setTranslation(Coords.grid.vector(this.hoveredTile.get().x, this.hoveredTile.get().y)
                            .add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.1f, 0f)));
            this.game.graphics.models.render(this.tileHighlight, this.environment);
        }
        this.game.graphics.models.end();

        // Draw 2D assets
        this.menu.ifPresent((Menu m) -> m.draw(this.game.graphics));
        this.hud.render();
        this.logger.render();
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
        this.camera.viewportWidth = w;
        this.camera.viewportHeight = h;
        this.camera.update();
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.game.graphics.dispose();
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

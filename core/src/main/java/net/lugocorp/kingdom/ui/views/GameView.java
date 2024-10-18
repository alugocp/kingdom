package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.GameViewController;
import net.lugocorp.kingdom.engine.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.ui.Hud;
import net.lugocorp.kingdom.ui.Logger;
import net.lugocorp.kingdom.ui.TileSelector;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.MenuNode;
import net.lugocorp.kingdom.utils.Consumer;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

/**
 * This class handles all the Game runtime logic
 */
public class GameView implements View {
    private Optional<Menu> menu = Optional.empty();
    private Point menuCoords = new Point(0, 0);
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    public final Popups popups = new Popups();
    public final TileSelector selector;
    public final Logger logger;
    public final Game game;
    public final Hud hud;

    GameView(Game game) {
        this.game = game;
        this.logger = new Logger(game.graphics);
        this.hud = new Hud(this);
        this.selector = new TileSelector(this);
    }

    /**
     * Handles click logic on a Tile (open a Menu for said Tile)
     */
    public void openTileMenu() {
        Optional<Point> p = this.selector.getHovered();
        this.selector.deselect();
        this.closeMenu();
        if (!p.isPresent()) {
            return;
        }
        this.menuCoords = p.get();
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
        MenuNode node = t.get().getMenuContent(this, Optional.of(this.menuCoords));
        this.menu = Optional.of(new Menu(0, Hud.HEIGHT, 250, true, node));
    }

    /**
     * Closes the currently open Menu
     */
    public void closeMenu() {
        this.menu = Optional.empty();
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
        this.game.kickOffTurn(this);
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        this.camController.update();

        // Draw 3D assets
        this.game.graphics.models.begin(this.camera);
        this.game.graphics.models.render(this.game.world.getModelInstances(), this.environment);
        this.selector.render(this.environment);
        this.game.graphics.models.end();

        // Draw 2D assets
        this.menu.ifPresent((Menu m) -> m.draw(this.game.graphics));
        this.hud.render();
        this.logger.render();
        if (this.popups.isDisplayed()) {
            this.popups.queue.peek().draw(this.game.graphics);
        }
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
     * This nested class handles popup Menu logic
     */
    public static class Popups {
        private final Queue<Menu> queue = new ArrayDeque<>();
        private boolean display = false;

        /**
         * Retrieves the first popup Menu in the queue, if any
         */
        public Optional<Menu> get() {
            return this.queue.isEmpty() ? Optional.empty() : Optional.of(this.queue.peek());
        }

        /**
         * Adds a popup Menu to the state
         */
        public void add(Menu menu) {
            this.queue.add(menu);
            this.display = true;
        }

        /**
         * Removes a popup Menu from the queue
         */
        public void complete() {
            this.queue.remove();
            if (this.queue.isEmpty()) {
                this.display = false;
            }
        }

        /**
         * Shows or hides popup Menus
         */
        public void setDisplay(boolean display) {
            this.display = display && !this.queue.isEmpty();
        }

        /**
         * Returns true if popup Menus are on screen
         */
        public boolean isDisplayed() {
            return this.display && !this.queue.isEmpty();
        }
    }
}

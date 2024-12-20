package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.controllers.GameViewController;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.ui.game.Hud;
import net.lugocorp.kingdom.ui.game.Logger;
import net.lugocorp.kingdom.ui.game.TileMenu;
import net.lugocorp.kingdom.ui.game.TileSelector;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class handles all the Game runtime logic
 */
public class GameView implements View {
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    public final Popups popups = new Popups();
    public final Graphics graphics;
    public final TileSelector selector;
    public final TileMenu menu;
    public final Logger logger;
    public final Game game;
    public final Hud hud;
    private Consumer<View> navigate = (View v) -> {
    };

    GameView(Game game, Graphics graphics) {
        this.game = game;
        this.graphics = graphics;
        this.hud = new Hud(this);
        this.logger = new Logger(this);
        this.selector = new TileSelector(this);
        this.menu = new TileMenu(this);
    }

    /**
     * Returns the PerspectiveCamera object
     */
    public PerspectiveCamera getCamera() {
        return this.camera;
    }

    /**
     * Returns the Environment object
     */
    public Environment getEnvironment() {
        return this.environment;
    }

    /**
     * Returns to the StartMenuView
     */
    public void close() {
        this.navigate.accept(new StartMenuView(this.graphics, this.game.events));
    }

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return this.game.mechanics.dayNight.getSkyboxColor();
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;

        // 3D setup
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0f, 0f, 0f, -0.6f, -0.4f, 0f));

        // Menus
        MenuController menuController = new MenuController(() -> this.menu.get());

        // Camera
        this.camera = new PerspectiveCamera(67, Coords.SIZE.x, Coords.SIZE.y);
        this.camController = new GameViewController(this, menuController, this.camera);
        Gdx.input.setInputProcessor(this.camController);
        this.camera.position.set(5f, 5f, 0f);
        this.camera.lookAt(0, 0, 0);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();

        // Kick off the Player's turn
        this.game.mechanics.turns.kickOffTurn(this);
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        this.camController.update();

        // Set initial OpenGL state
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
        Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
        Gdx.gl.glStencilMask(0xFF);

        // Draw all 3D models that shouldn't have outlines
        Gdx.gl.glStencilMask(0x00);
        this.graphics.models.begin(this.camera);
        this.graphics.models.render(this.game.world.getModelInstances(true), this.environment);
        this.selector.render(this.environment);
        this.graphics.models.end();

        // Draw all 3D models that should have outlines
        Gdx.gl.glStencilMask(0xFF);
        this.graphics.models.begin(this.camera);
        this.graphics.models.render(this.game.world.getModelInstances(false), this.environment);
        this.graphics.models.end();

        // Run the outline shaders
        Gdx.gl.glStencilFunc(GL20.GL_NOTEQUAL, 1, 0xFF);
        Gdx.gl.glStencilMask(0x00);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        this.graphics.outlines.begin(this.camera);
        this.graphics.outlines.render(this.game.world.getModelInstances(false), this.environment);
        this.graphics.outlines.end();

        // If we don't set the stencil mask here then the buffer won't clear
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glStencilMask(0x00);

        // Draw 2D assets
        this.menu.get().ifPresent((Menu m) -> m.draw(this.graphics));
        if (this.popups.isDisplayed()) {
            this.popups.queue.get(0).draw(this.graphics);
        }
        this.logger.render();
        this.hud.draw(this.graphics);
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
    }

    /**
     * This nested class handles popup Menu logic
     */
    public static class Popups {
        private final List<Boolean> required = new ArrayList<>();
        private final List<Menu> queue = new ArrayList<>();
        private boolean display = false;

        /**
         * Retrieves the first popup Menu in the queue, if any
         */
        public Optional<Menu> get() {
            return this.queue.isEmpty() ? Optional.empty() : Optional.of(this.queue.get(0));
        }

        /**
         * Adds a popup Menu to the state (at the end of the list)
         */
        public void add(Menu menu) {
            this.required.add(0, true);
            this.queue.add(menu);
            this.display = true;
        }

        /**
         * Adds a popup Menu to the state (at the front of the list)
         */
        public void addNext(Menu menu) {
            this.required.add(0, true);
            this.queue.add(0, menu);
            this.display = true;
        }

        /**
         * Adds an unrequired popup Menu to the state (at the front of the list)
         */
        public void addNextUnrequired(Menu menu) {
            this.required.add(0, false);
            this.queue.add(0, menu);
            this.display = true;
        }

        /**
         * Removes a popup Menu from the queue
         */
        public void complete() {
            this.queue.remove(0);
            this.required.remove(0);
            if (this.queue.isEmpty()) {
                this.display = false;
            }
        }

        /**
         * Shows or hides popup Menus
         */
        public void setDisplay(boolean display) {
            while (!display && this.required.size() > 0 && !this.required.get(0)) {
                this.complete();
            }
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

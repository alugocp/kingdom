package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.GameViewController;
import net.lugocorp.kingdom.engine.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.ui.game.Hud;
import net.lugocorp.kingdom.ui.game.Logger;
import net.lugocorp.kingdom.ui.game.TileMenu;
import net.lugocorp.kingdom.ui.game.TileSelector;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.utils.Consumer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    public final Popups popups = new Popups();
    public final TileSelector selector;
    public final TileMenu menu;
    public final Logger logger;
    public final Game game;
    public final Hud hud;

    GameView(Game game) {
        this.game = game;
        this.logger = new Logger(game.graphics);
        this.hud = new Hud(this);
        this.selector = new TileSelector(this);
        this.menu = new TileMenu(this);
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
        this.environment.add(new DirectionalLight().set(0f, 0f, 0f, -0.6f, -0.4f, 0f));

        // Menus
        MenuController menuController = new MenuController(() -> this.menu.get());

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
        this.game.graphics.models.begin(this.camera);
        this.game.graphics.models.render(this.game.world.getModelInstances(true), this.environment);
        this.selector.render(this.environment);
        this.selector.render(this.environment);
        this.game.graphics.models.end();

        // Draw all 3D models that should have outlines
        Gdx.gl.glStencilMask(0xFF);
        this.game.graphics.models.begin(this.camera);
        this.game.graphics.models.render(this.game.world.getModelInstances(false), this.environment);
        this.game.graphics.models.end();

        // Run the outline shaders
        Gdx.gl.glStencilFunc(GL20.GL_NOTEQUAL, 1, 0xFF);
        Gdx.gl.glStencilMask(0x00);
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        this.game.graphics.outlines.begin(this.camera);
        this.game.graphics.outlines.render(this.game.world.getModelInstances(false), this.environment);
        this.game.graphics.outlines.end();

        // If we don't set the stencil mask here then the buffer won't clear
        Gdx.gl.glStencilMask(0xFF);
        Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glStencilMask(0x00);

        // Draw 2D assets
        this.menu.get().ifPresent((Menu m) -> m.draw(this.game.graphics));
        if (this.popups.isDisplayed()) {
            this.popups.queue.peek().draw(this.game.graphics);
        }
        this.logger.render();
        this.hud.render();
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

package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.controllers.GameViewController;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.ui.game.Hud;
import net.lugocorp.kingdom.ui.game.Logger;
import net.lugocorp.kingdom.ui.game.TileMenu;
import net.lugocorp.kingdom.ui.game.TileSelector;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.serial.SaveLoad;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * This class handles all the Game runtime logic
 */
public class GameView implements View {
    private final StartMenuView.Params params;
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    public final Popups popups = new Popups();
    public final AudioVideo av;
    public final TileSelector selector;
    public final TileMenu menu;
    public final Logger logger;
    public final Game game;
    public final Hud hud;
    private Consumer<View> navigate = (View v) -> {
    };

    GameView(StartMenuView.Params params, Game game) {
        this.game = game;
        this.av = params.av;
        this.params = params;
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
     * Returns a SaveLoad instance to use through the Hud
     */
    public SaveLoad getSerial() {
        return this.params.serial;
    }

    /**
     * Returns to the StartMenuView
     */
    public void close() {
        this.navigate.accept(new StartMenuView(this.params));
    }

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return this.game.mechanics.dayNight.getSkyboxColor();
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.hud.minimap.init(this.game.world);
        this.navigate = navigate;

        // 3D setup
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0f, 0f, 0f, 0f, -0.4f, -0.6f));

        // Menus
        MenuController menuController = new MenuController(() -> this.menu.get());

        // Camera
        this.camera = new PerspectiveCamera(67, Coords.SIZE.x, Coords.SIZE.y);
        this.camController = new GameViewController(this, menuController, this.camera);
        Gdx.input.setInputProcessor(this.camController);
        this.camera.position.set(0f, 5f, 5f);
        this.camera.lookAt(0, 0, 0);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();

        // Kick off the Player's turn
        this.game.mechanics.turns.kickOffTurn(this);
        this.centerOnPoint(this.game.human.units.iterator().next().getPoint());
    }

    /**
     * Returns the Point in the World that the Camera is currently centered on
     */
    public Point getCenteredPoint() {
        return this.camController.getCoordUnderScreenPoint(Coords.SIZE.x / 2, Coords.SIZE.y / 2);
    }

    /**
     * Centers the Camera on the given Point in the World
     */
    public void centerOnPoint(Point p) {
        this.camController.centerCameraOn(p);
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        this.camController.update();

        // Render normals to a FrameBuffer
        this.av.frameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.av.outlines.begin(this.camera);
        this.av.outlines.render(this.game.world.getModelInstances(false), this.environment);
        this.av.outlines.end();
        this.av.frameBuffer.end();

        // Draw all 3D models with the ToonShader
        this.av.models.begin(this.camera);
        this.av.models.render(this.game.world.getModelInstances(true), this.environment);
        this.av.models.end();

        // Draw 2D assets
        this.menu.get().ifPresent((Menu m) -> m.draw(this.av));
        if (this.popups.isDisplayed()) {
            this.popups.queue.get(0).draw(this.av);
        }
        this.logger.render();
        this.hud.draw(this.av);
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

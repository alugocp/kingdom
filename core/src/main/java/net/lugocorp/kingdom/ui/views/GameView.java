package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.builtin.animation.CameraMoveAnimation;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.animation.AnimationQueue;
import net.lugocorp.kingdom.engine.controllers.GameViewController;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.ui.hud.Hud;
import net.lugocorp.kingdom.ui.hud.TileMenu;
import net.lugocorp.kingdom.ui.logger.Logger;
import net.lugocorp.kingdom.ui.overlay.OverlayLayer;
import net.lugocorp.kingdom.ui.selection.TileSelector;
import net.lugocorp.kingdom.utils.logic.CameraLogic;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class handles all the Game runtime logic
 */
public class GameView implements View {
    private final StartMenuView.Params params;
    private Point frameBufferMappedPoint = new Point(-1, -1);
    private GameViewController controller;
    private PerspectiveCamera camera;
    private Environment environment;
    private FitViewport viewport;
    public final AnimationQueue animations = new AnimationQueue();
    public final OverlayLayer overlays;
    public final TileSelector selector;
    public final AudioVideo av;
    public final TileMenu menu;
    public final Popups popups;
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
        this.overlays = new OverlayLayer(this);
        this.menu = new TileMenu(this);
        this.popups = new Popups(this.menu);
        this.av.getToonShader().setTileSelector(this.selector);
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

    /**
     * Gets the grid Point associated with the moused over screen Point from the
     * FrameBuffer
     */
    public Point getFrameBufferMappedPoint() {
        return this.frameBufferMappedPoint;
    }

    /**
     * Sets the grid Point associated with the moused over screen Point from the
     * FrameBuffer
     */
    private void setFrameBufferMappedPoint() {
        // TODO right now we can only have map sizes up to 254, then the outline shader
        // will break. We should eventually implement some sort of modulo operation
        // there, and find the closest possible point using some algorithm implemented
        // here
        final int x = Gdx.input.getX();
        final int y = Gdx.input.getY();
        if (x >= 0 && y >= 0 && x < Coords.SIZE.x && y < Coords.SIZE.y) {
            final byte[] data = ScreenUtils.getFrameBufferPixels(x, Coords.SIZE.y - y - 1, 1, 1, false);
            if (data[0] < 255 && data[1] < 255) {
                this.frameBufferMappedPoint.set(data[0], data[1]);
                return;
            }
        }
        this.frameBufferMappedPoint.set(-1, -1);
    }

    /**
     * Returns the Point in the World that the Camera is currently centered on
     */
    public Point getCenteredPoint() {
        return CameraLogic.getCoordUnderScreenPoint(this.camera, Coords.SIZE.x / 2, Coords.SIZE.y / 2);
    }

    /**
     * Centers the Camera on the given Point in the World
     */
    public void centerOnPoint(Point p, boolean instant) {
        if (instant) {
            this.controller.centerCameraOn(p);
        } else {
            this.animations.add(new CameraMoveAnimation(this.controller, this.getCenteredPoint(), p));
        }
    }

    /**
     * Sets up HUD state at the beginning of the Game to help guide new Players
     */
    private void initHudMessages() {
        final Unit u = this.game.human.units.iterator().next();
        final Optional<Building> b = this.game.world.getTile(u.getPoint()).flatMap((Tile t) -> t.building);
        this.logger.log(b.isPresent()
                ? String.format("%s joined your ranks on the %s", u.name, b.get().name)
                : String.format("%s joined your ranks", u.name));
        this.hud.update(this.game);
        this.centerOnPoint(u.getPoint(), true);
        this.menu.open(u.getPoint());
    }

    /** {@inheritdoc} */
    @Override
    public Viewport getViewport() {
        return this.viewport;
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

        // Camera and input
        final MenuController menuController = new MenuController(this.params.av.settings, () -> this.menu.get());
        this.camera = new PerspectiveCamera(67, Coords.SIZE.x, Coords.SIZE.y);
        this.viewport = new FitViewport(Coords.SIZE.x, Coords.SIZE.y, this.camera);
        this.controller = new GameViewController(this, this.params.av.settings, menuController, this.camera);
        Gdx.input.setInputProcessor(this.controller);
        this.camera.position.set(0f, 5f, 5f);
        this.camera.lookAt(0, 0, 0);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();

        // Kick off the Player's turn
        this.game.mechanics.turns.kickOffTurn(this);
        this.initHudMessages();
    }

    /** {@inheritdoc} */
    @Override
    public void render(int dt) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
        this.controller.keys.performActions();

        // Update Animations
        this.animations.update(this, dt);

        // Render normals to a FrameBuffer
        // TODO RESIZE we need to translate this by the viewport screen x / screen y
        this.av.frameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.av.outlines.begin(this.camera);
        this.av.outlines.render(this.game.world.getModelInstances(false), this.environment);
        this.av.outlines.end();
        this.setFrameBufferMappedPoint();
        this.av.frameBuffer.end();

        // Reapply the or it won't work
        this.viewport.apply();

        // Draw all 3D models with the ToonShader
        this.av.models.begin(this.camera);
        this.av.models.render(this.game.world.getModelInstances(true), this.environment);
        this.av.models.end();

        // Draw 2D assets
        this.overlays.render(dt);
        this.menu.get().ifPresent((Menu m) -> m.draw(this.av));
        if (this.popups.isDisplayed()) {
            this.popups.queue.get(0).draw(this.av);
        }
        this.logger.render(dt);
        this.hud.draw(this.av);
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
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
        private final TileMenu tileMenu;
        private boolean display = false;

        private Popups(TileMenu tileMenu) {
            this.tileMenu = tileMenu;
        }

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
            menu.outline();
        }

        /**
         * Adds a popup Menu to the state (at the front of the list)
         */
        public void addNext(Menu menu) {
            this.required.add(0, true);
            this.queue.add(0, menu);
            this.display = true;
            menu.outline();
        }

        /**
         * Adds an unrequired popup Menu to the state (at the front of the list)
         */
        public void addNextUnrequired(Menu menu) {
            this.tileMenu.close();
            this.required.add(0, false);
            this.queue.add(0, menu);
            this.display = true;
            menu.outline();
        }

        /**
         * Replaces the currently open Menu with another unrequired one
         */
        public void replaceUnrequired(Menu menu) {
            this.complete();
            this.addNextUnrequired(menu);
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

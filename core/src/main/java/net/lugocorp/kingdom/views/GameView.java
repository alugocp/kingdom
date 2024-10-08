package net.lugocorp.kingdom.views;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import java.util.Optional;
import java.util.function.Function;
import net.lugocorp.kingdom.engine.GameViewController;
import net.lugocorp.kingdom.engine.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.Tile;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.Menu;

public class GameView implements View {
    private final ModelInstance tileHighlight;
    private Optional<Point> hoveredTile = Optional.empty();
    private Optional<Menu> menu = Optional.empty();
    private Point menuCoords = new Point(0, 0);
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    public final Game game;

    GameView(Game game) {
        this.game = game;

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
     * Handles click logic on a Tile (open a Menu for said Tile)
     */
    public void openTileMenu() {
        if (this.menu.isPresent()) {
            this.menu = Optional.empty();
            return;
        }
        if (!this.hoveredTile.isPresent()) {
            return;
        }
        this.menuCoords = this.hoveredTile.get();
        this.refreshMenu();
    }

    /**
     * Opens the Menu that is set in this View's recent memory
     */
    public void refreshMenu() {
        Optional<Tile> t = this.game.world.getTile(this.menuCoords);
        if (!t.isPresent()) {
            return;
        }
        this.menu = Optional
                .of(new Menu(0, 0, 250, true, t.get().getMenuContent(this, this.menuCoords.x, this.menuCoords.y)));
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(0.8f, 1.0f, 1.0f, 1f);
    }

    @Override
    public void start(Function<View, Void> navigate) {
        // 3D setup
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Menus
        MenuController menuController = new MenuController((Void _nope) -> this.menu);

        // Camera
        this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camController = new GameViewController(this, menuController, this.camera);
        Gdx.input.setInputProcessor(this.camController);
        this.camera.position.set(5f, 5f, 0f);
        this.camera.lookAt(0, 0, 0);
        this.camera.near = 1f;
        this.camera.far = 300f;
        this.camera.update();
    }

    @Override
    public void render() {
        this.camController.update();

        // Draw 3D assets
        this.game.graphics.models.begin(this.camera);
        this.game.graphics.models.render(this.game.world.getModelInstances(), this.environment);
        if (this.hoveredTile.isPresent()) {
            this.tileHighlight.transform
                    .setTranslation(Coords.grid.vector(this.hoveredTile.get().x, this.hoveredTile.get().y)
                            .add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.1f, 0f)));
            this.game.graphics.models.render(this.tileHighlight, this.environment);
        }
        this.game.graphics.models.end();

        // Draw 2D assets
        this.menu.ifPresent((Menu m) -> m.draw(this.game.graphics));
    }

    @Override
    public void resize(int w, int h) {
        this.camera.viewportWidth = w;
        this.camera.viewportHeight = h;
        this.camera.update();
    }

    @Override
    public void dispose() {
        this.game.graphics.dispose();
    }
}

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
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.ui.ListNode;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.TextNode;

public class GameView implements View {
    private final Game game;
    private final Graphics graphics;
    private final ModelInstance tileHighlight;
    private GameViewController camController;
    private PerspectiveCamera camera;
    private Environment environment;
    private Menu menu;

    GameView(Graphics graphics, Game game) {
        this.graphics = graphics;
        this.game = game;

        // Tile highlight
        this.tileHighlight = this.game.assets.createModelInstance("Selector");
        this.tileHighlight.materials.get(0).set(new BlendingAttribute(0.5f));
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(0.8f, 1.0f, 1.0f, 1f);
    }

    @Override
    public void start(Function<View, Void> navigate) {
        // 2D setup
        this.menu = new Menu(0, 0, 100, true, new ListNode().add(new TextNode(this.graphics.fonts.basic, "Hello,"))
                .add(new TextNode(this.graphics.fonts.basic, "  World!")));

        // 3D setup
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Menus
        MenuController menuController = new MenuController((Void _nope) -> Optional.of(this.menu));

        // Camera
        this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camController = new GameViewController(menuController, this.camera, this.game::setHoveredTile);
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
        this.graphics.models.begin(this.camera);
        this.graphics.models.render(this.game.world.getModelInstances(), this.environment);
        if (this.game.hoveredTile.isPresent()) {
            this.tileHighlight.transform
                    .setTranslation(Coords.grid.vector(this.game.hoveredTile.get().x, this.game.hoveredTile.get().y)
                            .add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.1f, 0f)));
            this.graphics.models.render(this.tileHighlight, this.environment);
        }
        this.graphics.models.end();

        // Draw 2D assets
        this.menu.draw(this.graphics.sprites, this.graphics.shapes);
    }

    @Override
    public void dispose() {
        this.game.assets.dispose();
    }
}

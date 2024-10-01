package net.lugocorp.kingdom.views;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import java.util.function.Function;
import net.lugocorp.kingdom.engine.GameCameraController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;

public class GameView implements View {
    private SpriteBatch sprites;
    private PerspectiveCamera camera;
    private GameCameraController camController;
    private ModelBatch models;
    private Environment environment;
    private final ModelInstance tileHighlight;
    private final Game game;

    GameView(Game game) {
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
        this.sprites = new SpriteBatch();

        // 3D setup
        this.models = new ModelBatch();
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Camera
        this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camController = new GameCameraController(this.camera, this.game::setHoveredTile);
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
        this.models.begin(this.camera);
        this.models.render(this.game.world.getModelInstances(), this.environment);
        if (this.game.hoveredTile.isPresent()) {
            this.tileHighlight.transform
                    .setTranslation(Coords.grid.vector(this.game.hoveredTile.get().x, this.game.hoveredTile.get().y)
                            .add(Coords.raw.vector(0f, Hexagons.HEIGHT * 1.1f, 0f)));
            this.models.render(this.tileHighlight, this.environment);
        }
        this.models.end();

        // Draw 2D assets
        this.sprites.begin();
        this.sprites.end();
    }

    @Override
    public void dispose() {
        this.sprites.dispose();
        this.models.dispose();
        this.game.assets.dispose();
    }
}

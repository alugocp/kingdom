package net.lugocorp.kingdom.views;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Optional;
import java.util.function.Function;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.GameCameraController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.world.World;
import net.lugocorp.kingdom.world.WorldGenerator;

public class GameView implements View {
    private SpriteBatch spriteBatch;
    private PerspectiveCamera cam;
    private GameCameraController camController;
    private ModelBatch modelBatch;
    private Environment environment;
    private final AssetsLoader assets;
    private final Game game;

    GameView(AssetsLoader assets, Game game) {
        this.assets = assets;
        this.game = game;
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(0.8f, 1.0f, 1.0f, 1f);
    }

    @Override
    public void start(Function<View, Void> navigate) {
        // 2D setup
        this.spriteBatch = new SpriteBatch();

        // 3D setup
        this.modelBatch = new ModelBatch();
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        this.environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        // Camera
        this.cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camController = new GameCameraController(this.cam);
        Gdx.input.setInputProcessor(this.camController);
        this.cam.position.set(5f, 5f, 0f);
        this.cam.lookAt(0, 0, 0);
        this.cam.near = 1f;
        this.cam.far = 300f;
        this.cam.update();
    }

    @Override
    public void render() {
        this.camController.update();

        // Draw 3D assets
        this.modelBatch.begin(this.cam);
        this.modelBatch.render(this.game.world.getModelInstances(), this.environment);
        this.modelBatch.end();

        // Draw 2D assets
        this.spriteBatch.begin();
        this.spriteBatch.end();
    }

    @Override
    public void dispose() {
        this.spriteBatch.dispose();
        this.modelBatch.dispose();
        this.assets.dispose();
    }
}

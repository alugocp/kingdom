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
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;
import java.util.function.Function;
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.GameCameraController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.world.World;
import net.lugocorp.kingdom.world.WorldGenerator;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;

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

        Vector3 v3 = new Vector3();
        System.out.println("350, 263");
        System.out.println(this.game.world.getTile(0, 1).get().model.get().transform.getTranslation(v3));
        Vector3 near = this.cam.unproject(new Vector3(350, 263, 0));
        Vector3 far = this.cam.unproject(new Vector3(350, 263, 1));
        System.out.println(near);
        System.out.println(far);
        float weight = near.y / (near.y - far.y);
        Vector3 middle = new Vector3(
            near.x - weight * (near.x - far.x),
            near.y - weight * (near.y - far.y),
            near.z - weight * (near.z - far.z)
        );
        System.out.println(middle);
        int minZ = (int)Math.floor(middle.x / (Hexagons.DEPTH - Hexagons.DEPTH_DIFF));
        int minX = (int)Math.floor(middle.z - (minZ % 2 == 0 ? 0 : (Hexagons.WIDTH / 2)) / Hexagons.WIDTH);
        float lowestDist2 = Integer.MAX_VALUE;
        Point closestPoint = null;
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                System.out.println(String.format("%d, %d", minX + a, minZ + b));
                float dist = Coords.grid.vector(minX + a, 0, minZ + b).dst2(middle);
                if (dist < lowestDist2) {
                    lowestDist2 = dist;
                    closestPoint = new Point(minX + a, minZ + b);
                }
            }
        }
        System.out.println(closestPoint);
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

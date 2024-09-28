package net.lugocorp.kingdom;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import net.lugocorp.kingdom.assets.AssetsLoader;
import net.lugocorp.kingdom.engine.GameCameraController;
import net.lugocorp.kingdom.views.LoadingGameView;
import net.lugocorp.kingdom.views.View;
import net.lugocorp.kingdom.world.World;
import net.lugocorp.kingdom.world.WorldGenerator;

public class Main implements ApplicationListener {
    private View view = new LoadingGameView();

    public Void navigate(View v) {
        this.view.dispose();
        this.view = v;
        v.start(this::navigate);
        return null;
    }

    @Override
    public void create() {
        this.view.start(this::navigate);
    }

    @Override
    public void render() {
        ScreenUtils.clear(this.view.getBackgroundColor());
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.view.render();
    }

    @Override
    public void dispose() {
        this.view.dispose();
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}

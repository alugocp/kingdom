package net.lugocorp.kingdom;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.ui.views.LoadingGameView;
import net.lugocorp.kingdom.ui.views.View;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    private Graphics graphics;
    private View view;

    /**
     * Causes the application to swap out its current View
     */
    public void navigate(View v) {
        this.view.dispose();
        this.view = v;
        v.start((View v1) -> this.navigate(v1));
    }

    /** {@inheritdoc} */
    @Override
    public void create() {
        Gdx.graphics.setResizable(true);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.graphics = new Graphics(new AssetManager());
        this.view = new LoadingGameView(this.graphics);
        this.view.start(this::navigate);
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        ScreenUtils.clear(this.view.getBackgroundColor());
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.view.render();
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.view.dispose();
        this.graphics.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
        view.resize(w, h);
    }

    /** {@inheritdoc} */
    @Override
    public void pause() {
    }

    /** {@inheritdoc} */
    @Override
    public void resume() {
    }
}

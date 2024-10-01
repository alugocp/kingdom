package net.lugocorp.kingdom;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import net.lugocorp.kingdom.views.LoadingGameView;
import net.lugocorp.kingdom.views.View;

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

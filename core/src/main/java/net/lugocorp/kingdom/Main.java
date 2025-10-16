package net.lugocorp.kingdom;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.ui.views.LoadingGameView;
import net.lugocorp.kingdom.utils.code.Semver;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * This main class kicks off all game logic
 */
public class Main implements ApplicationListener {
    public static final Semver VERSION = new Semver(1, 0, 0);
    private static Viewport viewport = null;
    private long prevTime = System.currentTimeMillis();
    private View view = null;
    private AudioVideo av;

    /**
     * Returns the given screen coordinate in "world units" (a LibGDX term)
     */
    public static Point unproject(int x, int y) {
        final Viewport v = Main.viewport;
        final float scaleX = Coords.SIZE.x / (float) v.getScreenWidth();
        final float scaleY = Coords.SIZE.y / (float) v.getScreenHeight();
        return new Point((int) ((x - v.getScreenX()) * scaleX), (int) ((y - v.getScreenY()) * scaleY));
    }

    /**
     * Returns the given "world units" (a LibGDX term) in screen coordinates
     */
    public static Point project(int x, int y) {
        final Viewport v = Main.viewport;
        final float scaleX = v.getScreenWidth() / (float) Coords.SIZE.x;
        final float scaleY = v.getScreenHeight() / (float) Coords.SIZE.y;
        return new Point((int) (x * scaleX) + v.getScreenX(), (int) (y * scaleY) + v.getScreenY());
    }

    /**
     * Causes the application to swap out its current View
     */
    public void navigate(View v) {
        // Swap out the View
        if (this.view != null) {
            this.view.dispose();
        }
        this.view = v;
        v.start((View v1) -> this.navigate(v1));

        // Set up the new viewport
        final Viewport vp = v.getViewport();
        vp.update(Coords.SIZE.x, Coords.SIZE.y);
        vp.apply();
        Main.viewport = vp;
    }

    /** {@inheritdoc} */
    @Override
    public void create() {
        Gdx.graphics.setResizable(true);
        Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        this.av = new AudioVideo();
        this.navigate(new LoadingGameView(this.av));
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        // Play any queued audio
        av.loaders.sounds.checkQueuedSounds();
        av.loaders.music.checkQueuedMusic();

        // Render the current View
        final long time = System.currentTimeMillis();
        ScreenUtils.clear(this.view.getBackgroundColor());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        this.view.render((int) (time - this.prevTime));
        this.prevTime = time;
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.view.dispose();
        this.av.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
        Main.viewport.update(w, h);
        this.view.resize(w, h);
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

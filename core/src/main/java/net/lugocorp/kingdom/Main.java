package net.lugocorp.kingdom;
import net.lugocorp.kingdom.context.BaseContext;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.projection.ViewportLogic;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.serial.SaveLoad;
import net.lugocorp.kingdom.settings.SettingsIO;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.ui.views.LoadingGameView;
import net.lugocorp.kingdom.utils.Log;
import net.lugocorp.kingdom.utils.Semver;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;

/**
 * This main class kicks off all game logic
 */
public class Main implements ApplicationListener {
    public static final Semver VERSION = new Semver(0, 0, 0, Semver.BETA);
    private long prevTime = System.currentTimeMillis();
    private View view = null;

    /**
     * Causes the application to swap out its current View
     */
    public void navigate(View v) {
        // Swap out the View
        if (this.view != null) {
            this.view.dispose();
        }
        this.view = v;
        v.start();

        // Set up the new viewport
        final Viewport vp = v.getViewport();
        vp.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        vp.apply();
        ViewportLogic.setViewport(vp);

        // Print out some window debug info
        Log.log("Screen size: (%d, %d)", vp.getScreenWidth(), vp.getScreenHeight());
        Log.log("Back buffer: (%d, %d)", Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    /** {@inheritdoc} */
    @Override
    public void create() {
        Gdx.graphics.setResizable(true);
        Context.setContext(new BaseContext(new AllEventHandlers(), new Generator(), (View v) -> this.navigate(v),
                new SaveLoad(), new AudioVideo(SettingsIO.readOrDefault()), new ArrayList<GameMod>()));
        this.navigate(new LoadingGameView());
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        // Play any queued audio
        final AudioVideo av = Context.getContext().av;
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
        Context.getContext().av.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
        ViewportLogic.getViewport().update(w, h);
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

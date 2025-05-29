package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Consumer;

/**
 * View for when we're loading a new game
 */
public abstract class ThreadedTaskView implements View {
    private boolean loaded = false;
    protected final AudioVideo av;
    protected Consumer<View> navigate;

    ThreadedTaskView(AudioVideo av) {
        this.av = av;
    }

    /**
     * The threaded task that this View performs
     */
    protected abstract void performTask();

    /**
     * Text to display as the task is performed
     */
    protected abstract String getLoadingText();

    /**
     * View to display after the task is performed
     */
    protected abstract View getNextView();

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return new Color(0f, 0f, 0f, 1f);
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;

        // Initiate mod loading in a separate Thread
        new Thread(() -> {
            this.performTask();

            // Pause for dramatic effect
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.loaded = true;
        }).start();
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        if (this.loaded) {
            this.navigate.accept(this.getNextView());
        }
        this.av.sprites.begin();
        this.av.fonts.basic.draw(this.av.sprites, this.getLoadingText(), Coords.SIZE.y / 3, Coords.SIZE.y / 2);
        this.av.sprites.end();
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
    }
}

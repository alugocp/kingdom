package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.function.Consumer;

/**
 * View for when we're loading a new game
 */
public abstract class ThreadedTaskView implements View {
    private final GlyphLayout layout = new GlyphLayout();
    private final Drawable background;
    private final String loadingText;
    private final BitmapFont font;
    private boolean loaded = false;
    private int progress = 0;
    protected final AudioVideo av;
    protected Consumer<View> navigate;

    ThreadedTaskView(AudioVideo av, String loadingText) {
        this.font = av.fonts.getFont(
                new FontParam().setSize(28).setColor(ColorScheme.TEXT.color).setBorder(ColorScheme.BLACK.color));
        this.background = new Drawable(av.loaders.sprites, "loading-screen");
        this.loadingText = loadingText;
        this.av = av;
    }

    /**
     * The threaded task that this View performs
     */
    protected abstract void performTask();

    /**
     * View to display after the task is performed
     */
    protected abstract View getNextView();

    /**
     * Sets the load progress
     */
    protected void setProgress(int progress) {
        this.progress = progress;
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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.loaded = true;
        }).start();
    }

    /** {@inheritdoc} */
    @Override
    public void render(int dt) {
        if (this.loaded) {
            this.navigate.accept(this.getNextView());
        }

        // Render the loading screen graphic
        this.av.sprites.begin();
        this.background.render(this.av.sprites, 0, 0);
        this.av.sprites.end();

        // Render the loading bar
        final Rect flip = Coords.screen.flip(505, 605, 590 * this.progress / 100, 40);
        this.av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(ColorScheme.GREEN.color);
        av.shapes.rect(flip.x, flip.y, flip.w, flip.h);
        this.av.shapes.end();

        // Render the text
        final String text = String.format("%s...%d%%", this.loadingText, this.progress);
        this.layout.setText(this.font, text);
        this.av.sprites.begin();
        this.font.draw(this.av.sprites, text, (Coords.SIZE.x - this.layout.width) / 2, 350);
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

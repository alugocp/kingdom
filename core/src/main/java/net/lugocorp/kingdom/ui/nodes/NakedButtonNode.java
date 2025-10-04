package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Optional;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class NakedButtonNode extends TextNode {
    private final Runnable action;
    private Optional<String> ping = Optional.of("sfx/arrow");
    private boolean hovered = false;

    public NakedButtonNode(AudioVideo av, String message, Runnable action) {
        super(av, message);
        this.action = action;
    }

    /**
     * Returns the current background Color
     */
    protected Color getColor() {
        return this.hovered ? ColorScheme.HOVER : ColorScheme.BUTTON;
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(24, ColorScheme.TEXT);
    }

    /**
     * Returns whether or not the mouse is hovering over this ButtonNode
     */
    protected boolean isHovered() {
        return this.hovered;
    }

    /**
     * Sets the noise that plays when you click this ButtonNode
     */
    public NakedButtonNode setNoise(String ping) {
        this.ping = Optional.of(ping);
        return this;
    }

    /**
     * Sets no noise to play when you click this ButtonNode
     */
    public NakedButtonNode disableNoise() {
        this.ping = Optional.empty();
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (bounds.contains(p)) {
            this.ping.ifPresent((String sound) -> this.av.loaders.sounds.play(sound));
            this.action.run();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        final boolean prevIn = bounds.contains(prev);
        final boolean currIn = bounds.contains(curr);
        if (!prevIn && currIn) {
            this.hovered = true;
        }
        if (prevIn && !currIn) {
            this.hovered = false;
        }
    }
}

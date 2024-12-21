package net.lugocorp.kingdom.ui.menu;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.SoundLoader;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Like a TextNode but it stands out and does something when you click it
 */
public class ButtonNode extends TextNode {
    private final BitmapFont disabledFont;
    private final BitmapFont enabledFont;
    private final SoundLoader sounds;
    private final Runnable action;
    private Optional<Supplier<Boolean>> criteria = Optional.empty();
    private boolean disabled = false;

    public ButtonNode(AudioVideo av, String message, Runnable action) {
        super(av, message);
        this.disabledFont = av.fonts.disabled;
        this.enabledFont = av.fonts.button;
        this.sounds = av.loaders.sounds;
        this.font = av.fonts.button;
        this.action = action;
    }

    /**
     * Enables this ButtonNode so it can be clicked
     */
    public void enable() {
        this.font = this.enabledFont;
        this.disabled = false;
    }

    /**
     * Disables this ButtonNode so it cannot be clicked
     */
    public void disable() {
        this.font = this.disabledFont;
        this.disabled = true;
    }

    /**
     * Disables this ButtonNode based on some criteria
     */
    public ButtonNode disable(boolean criteria) {
        if (criteria) {
            this.disable();
        }
        return this;
    }

    /**
     * Sets a dynamic criteria that sets this ButtonNode's enabled/disabled state
     */
    public ButtonNode setEnabledCriteria(Supplier<Boolean> supplier) {
        this.criteria = Optional.of(supplier);
        return this;
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        this.criteria.ifPresent((Supplier<Boolean> supplier) -> {
            if (supplier.get()) {
                this.enable();
            } else {
                this.disable();
            }
        });
        super.draw(av, bounds);
    }

    /** {@inheritdoc} */
    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        if (!this.disabled) {
            this.sounds.play("ui/arrow");
            this.action.run();
        }
    }
}

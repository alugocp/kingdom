package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents a Unit's Action state
 */
public class ActionOverlay extends Overlay {
    private static final float DURATION = 2000f;
    private static final float AMPLITUDE = 5f;
    private static final int DIFF = 15;
    private boolean alive = true;
    private String single;
    private float x = 0f;

    public ActionOverlay(GameView view, Unit u, char single) {
        super(u.getPoint(), new Vector3(0f, view.av.loaders.models.getModelHeight(u.getModelName()), 0f));
        this.setChar(single);
    }

    /**
     * Sets the char that this Overlay renders
     */
    public void setChar(char single) {
        this.single = Character.toString(single);
    }

    /**
     * Ends this Overlay's lifetime
     */
    public void dispel() {
        this.alive = false;
    }

    /** {@inheritdoc} */
    @Override
    public boolean isDone() {
        return !this.alive;
    }

    /** {@inheritdoc} */
    @Override
    public void update(int dt) {
        final float period = ((float) Math.PI) * 2f;
        this.x += period * dt / ActionOverlay.DURATION;
        if (this.x >= period) {
            this.x -= period;
        }
    }

    /** {@inheritdoc} */
    @Override
    public void render(GameView view) {
        final BitmapFont font = view.av.fonts.getFont(30, ColorScheme.BUTTON.color);
        final float[] pos = this.getPosition(view);
        final int y = (int) (pos[1] + font.getLineHeight());
        final float diff = (float) Math.PI / 2f;
        view.av.sprites.begin();
        font.draw(view.av.sprites, this.single, (int) (pos[0] - ActionOverlay.DIFF),
                y + (int) (Math.sin(this.x) * ActionOverlay.AMPLITUDE));
        font.draw(view.av.sprites, this.single, (int) pos[0],
                y + (int) (Math.sin(this.x + diff) * ActionOverlay.AMPLITUDE));
        font.draw(view.av.sprites, this.single, (int) (pos[0] + ActionOverlay.DIFF),
                y + (int) (Math.sin(this.x + diff + diff) * ActionOverlay.AMPLITUDE));
        view.av.sprites.end();
    }
}

package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.Color;

/**
 * This Menu contains a single special button to advance the Game
 */
public class TurnButton extends Menu {
    private final ButtonNode button;
    private boolean pulsate = false;

    public TurnButton(GameView view) {
        super(Coords.SIZE.x - Minimap.MAX_W, Coords.SIZE.y, Minimap.MAX_W, true, new ListNode());
        final TurnButton that = this;
        this.button = (ButtonNode) (new ButtonNode(view.av, "Waiting...", () -> that.finishTurn(view, true)) {
            private float timer = 0f;

            /** {@inheritdoc} */
            @Override
            protected Color getColor() {
                if (!this.isEnabled()) {
                    return ColorScheme.DISABLE.color;
                }
                if (this.isHovered()) {
                    return ColorScheme.SPECIAL_HOVER.color;
                }
                if (!that.pulsate) {
                    return ColorScheme.SPECIAL_BUTTON.color;
                }
                return Colors.interpolate(ColorScheme.SPECIAL_BUTTON.hex, ColorScheme.SPECIAL_HOVER.hex,
                        1f - Math.abs(this.timer - 1f));
            }

            /** {@inheritdoc} */
            @Override
            public void draw(AudioVideo av, Rect bounds) {
                this.timer = (this.timer + 0.05f) % 2f;
                super.draw(av, bounds);
            }
        }.enable(false).disableNoise());
        this.setRoot(this.button);
        this.setMargin(0);
    }

    /**
     * Modifies the button based on the current Player's turn
     */
    public void update(boolean isHumanPlayer, boolean isComplete) {
        if (isHumanPlayer) {
            this.button.enable(true).setText(isComplete ? "Finish Turn" : "Continue");
        } else {
            this.button.enable(false).setText("Waiting...");
        }
        this.pulsate = isHumanPlayer && isComplete;
    }

    /**
     * Attempts to end the current turn
     */
    public void finishTurn(GameView view, boolean clicked) {
        if (view.hud.popups.get().isPresent()) {
            if (clicked) {
                view.av.loaders.sounds.play("sfx/error");
            }
            view.hud.popups.setDisplay(true);
        } else if (view.game.actions.goToNextUnit(view)) {
            if (clicked) {
                view.av.loaders.sounds.play("sfx/error");
            }
        } else {
            view.av.loaders.sounds.play("sfx/end-turn");
            view.hud.logger.log("You have ended your turn");
            view.game.mechanics.turns.nextTurn(view);
            view.hud.bot.tileMenu.refresh();
        }
    }
}

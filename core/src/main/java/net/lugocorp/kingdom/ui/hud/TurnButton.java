package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.nodes.ButtonNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.graphics.Color;

/**
 * This Menu contains a single special button to advance the Game
 */
public class TurnButton extends Menu {
    private final ButtonNode button;

    public TurnButton(GameView view) {
        super(Coords.SIZE.x - Minimap.MAX_W, Coords.SIZE.y, Minimap.MAX_W, true, new ListNode());
        this.button = (ButtonNode) (new ButtonNode(view.av, "Complete Turn", () -> {
            if (view.hud.popups.get().isPresent()) {
                view.av.loaders.sounds.play("sfx/error");
                view.hud.popups.setDisplay(true);
            } else if (view.game.actions.goToNextUnit(view)) {
                view.av.loaders.sounds.play("sfx/error");
            } else {
                view.av.loaders.sounds.play("sfx/end-turn");
                view.hud.logger.log("You have ended your turn");
                view.game.mechanics.turns.iterateTurnPlayer(view);
                view.hud.bot.tileMenu.refresh();
            }
        }) {
            /** {@inheritdoc} */
            @Override
            protected Color getColor() {
                if (!this.isEnabled()) {
                    return ColorScheme.DISABLE.color;
                }
                return this.isHovered() ? ColorScheme.SPECIAL_HOVER.color : ColorScheme.SPECIAL_BUTTON.color;
            }
        }.setEnabledCriteria(() -> view.game.mechanics.turns.canHumanPlayerAct()).disableNoise());
        this.setRoot(this.button);
        this.setMargin(0);
    }

    /**
     * Modifies the button based on the current Player's turn
     */
    public void update(boolean isHumanPlayer) {
        if (isHumanPlayer) {
            this.button.enable(true).setText("Complete Turn");
        } else {
            this.button.enable(false).setText("Waiting...");
        }
    }
}

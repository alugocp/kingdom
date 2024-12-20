package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HudInfoNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;

/**
 * This class handles rendering the Player's HUD UI
 */
public class Hud extends Menu {
    private final HudInfoNode info;
    private final Game game;

    public Hud(GameView view) {
        super(0, 0, Coords.SIZE.x, false, new ListNode());
        this.info = new HudInfoNode(view.av);
        this.game = view.game;
        ((ListNode) this.root)
                .add(this.info).add(
                        new RowNode()
                                .add(new ButtonNode(view.av, "View Fate",
                                        () -> view.popups
                                                .addNextUnrequired(view.game.mechanics.fates.getPlayerFateMenu(view))))
                                .add(new ButtonNode(view.av, "View Artifacts",
                                        () -> view.popups.addNextUnrequired(
                                                view.game.mechanics.auction.getOwnedArtifactsMenu(view))))
                                .add(new ButtonNode(view.av, "Complete Turn", () -> {
                                    if (view.popups.get().isPresent()) {
                                        view.popups.setDisplay(true);
                                    } else {
                                        view.logger.log("You have ended your turn");
                                        view.game.mechanics.turns.iterateTurnPlayer(view);
                                        view.menu.refresh(true);
                                    }
                                }).setEnabledCriteria(() -> view.game.mechanics.turns.canHumanPlayerAct())));
        this.pack();
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av) {
        this.info.updateInfo(this.game);
        super.draw(av);
    }
}

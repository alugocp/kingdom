package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HudInfoNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.ui.views.SettingsView;
import net.lugocorp.kingdom.utils.math.Coords;

/**
 * This class handles rendering the Player's HUD UI
 */
public class Hud extends Menu {
    private final HudInfoNode info;
    private final GameView view;
    private boolean showMinimap = true;
    public final Minimap minimap = new Minimap();

    public Hud(GameView view) {
        super(0, 0, Coords.SIZE.x, false, new ListNode());
        this.info = new HudInfoNode(view.av);
        this.view = view;
        ((ListNode) this.root).add(this.info).add(new RowNode()
                .add(new ButtonNode(view.av, "Minimap", () -> this.toggleMinimap()))
                .add(new ButtonNode(view.av, "View Fate",
                        () -> view.popups.addNextUnrequired(view.game.mechanics.fates.getPlayerFateMenu(view))))
                .add(new ButtonNode(view.av, "View Artifacts",
                        () -> view.popups.addNextUnrequired(view.game.mechanics.auction.getOwnedArtifactsMenu(view))))
                .add(new ButtonNode(view.av, "Settings",
                        () -> view.popups.addNextUnrequired(this.getSettingsMenu(view))))
                .add(new ButtonNode(view.av, "Complete Turn", () -> {
                    if (view.popups.get().isPresent()) {
                        view.popups.setDisplay(true);
                    } else if (!view.game.mechanics.turns.goToNextUnit(view)) {
                        view.logger.log("You have ended your turn");
                        view.game.mechanics.turns.iterateTurnPlayer(view);
                        view.menu.refresh(true);
                    }
                }).setEnabledCriteria(() -> view.game.mechanics.turns.canHumanPlayerAct())));
        this.pack();
    }

    /**
     * Internal syntactic sugar
     */
    private void toggleMinimap() {
        this.showMinimap = !this.showMinimap;
    }

    /**
     * Returns a Menu that allows the player to adjust settings
     */
    private Menu getSettingsMenu(GameView view) {
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                SettingsView
                        .addSettingsMenuNodes(view.av,
                                new ListNode().add(new ButtonNode(view.av, "x", () -> view.popups.setDisplay(false))))
                        .add(new SpacerNode()).add(new ButtonNode(view.av, "Save game", () -> {
                            try {
                                view.getSerial().saveGame(view.game);
                                view.logger.log("Game has been saved");
                            } catch (Exception e) {
                                view.logger.log("Could not save game");
                                e.printStackTrace();
                            }
                        })));
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av) {
        this.info.updateInfo(this.view.game);
        super.draw(av);
        if (this.showMinimap && !this.view.menu.get().isPresent()) {
            // TODO don't show Minimap when GameView has a popup menu either
            this.minimap.draw(av, this.view.game.world, 0, this.getHeight());
        }
    }
}

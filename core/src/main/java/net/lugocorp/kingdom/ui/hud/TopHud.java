package net.lugocorp.kingdom.ui.hud;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.mechanics.Mechanics;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.nodes.ButtonNode;
import net.lugocorp.kingdom.ui.nodes.HudInfoNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.nodes.NakedButtonNode;
import net.lugocorp.kingdom.ui.nodes.RowNode;
import net.lugocorp.kingdom.ui.nodes.SpacerNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.ui.views.SettingsView;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;

/**
 * Represents the top half of the HUD UI
 */
public class TopHud extends Menu {
    private final HudInfoNode info;
    private final GameView view;

    public TopHud(GameView view) {
        super(0, 0, Coords.SIZE.x, false, new ListNode());
        this.info = new HudInfoNode(view.av);
        this.view = view;
        ((ListNode) this.root).add(this.info)
                .add(new RowNode()
                        .add(new ButtonNode(view.av, "Fates",
                                () -> view.hud.popups.addNextUnrequired(
                                        view.game.mechanics.fates.getViewFatesMenu(view, view.game.human))))
                        .add(new ButtonNode(view.av, "Artifacts",
                                () -> view.hud.popups.addNextUnrequired(view.game.mechanics.auction
                                        .getArtifactsMenu(view, Optional.of(view.game.human)))))
                        .add(new ButtonNode(view.av, "Settings",
                                () -> view.hud.popups.addNextUnrequired(this.getSettingsMenu(view)))));
        this.pack();
    }

    /**
     * Updates the Hud info so we can get an accurate height value
     */
    public void update(Game g) {
        this.info.updateInfo(g);
    }

    /**
     * Returns a Menu that allows the player to adjust settings
     */
    private Menu getSettingsMenu(GameView view) {
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2),
                false,
                SettingsView
                        .addSettingsMenuNodes(view.av,
                                new ListNode().add(
                                        new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false))))
                        .add(new SpacerNode()).add(new ButtonNode(view.av, "Exit Game", () -> view.close()))
        /*
         * .add(new ButtonNode(view.av, "Save game", () -> { try {
         * view.getSerial().saveGame(view.game); view.logger.log("Game has been saved");
         * } catch (Exception e) { view.logger.error("Could not save game");
         * e.printStackTrace(); } }))
         */
        );
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av) {
        super.draw(av);

        // Draw a white bar at the bottom of the HUD
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        av.shapes.rect(0, Coords.SIZE.y - this.getHeight(), Coords.SIZE.x, 1);
        av.shapes.end();
    }
}

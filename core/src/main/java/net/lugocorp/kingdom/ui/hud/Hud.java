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
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Optional;

/**
 * This class handles rendering the Player's HUD UI
 */
public class Hud extends Menu {
    private final ButtonNode completeTurnButton;
    private final HudInfoNode info;
    private final GameView view;
    private boolean minimapActive = true;
    public final Minimap minimap = new Minimap();

    public Hud(GameView view) {
        super(0, 0, Coords.SIZE.x, false, new ListNode());
        this.info = new HudInfoNode(view.av);
        this.view = view;
        this.completeTurnButton = (ButtonNode) (new ButtonNode(view.av, "Complete Turn", () -> {
            if (view.popups.get().isPresent()) {
                view.av.loaders.sounds.play("sfx/error");
                view.popups.setDisplay(true);
            } else if (view.game.actions.goToNextUnit(view)) {
                view.av.loaders.sounds.play("sfx/error");
            } else {
                view.av.loaders.sounds.play("sfx/end-turn");
                view.logger.log("You have ended your turn");
                view.game.mechanics.turns.iterateTurnPlayer(view);
                view.menu.refresh();
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
        ((ListNode) this.root).add(this.info).add(new RowNode()
                .add(new ButtonNode(view.av, "Minimap", () -> this.toggleMinimap()))
                .add(new ButtonNode(view.av, "Fates",
                        () -> view.popups
                                .addNextUnrequired(view.game.mechanics.fates.getViewFatesMenu(view, view.game.human))))
                .add(new ButtonNode(view.av, "Artifacts",
                        () -> view.popups.addNextUnrequired(
                                view.game.mechanics.auction.getArtifactsMenu(view, Optional.of(view.game.human)))))
                .add(new ButtonNode(view.av, "Settings",
                        () -> view.popups.addNextUnrequired(this.getSettingsMenu(view))))
                .add(this.completeTurnButton));
        this.pack();
    }

    /**
     * Updates the Hud info so we can get an accurate height value
     */
    public void update(Game g) {
        this.info.updateInfo(g);
    }

    /**
     * Enables the "Complete Turn" button while it is the human Player's turn
     */
    public void humanPlayerTurn() {
        this.completeTurnButton.enable(true).setText("Complete Turn");
    }

    /**
     * Disables the "Complete Turn" button while it's not the human Player's turn
     */
    public void notHumanPlayerTurn() {
        this.completeTurnButton.enable(false).setText("Waiting...");
    }

    /**
     * Internal syntactic sugar
     */
    private void toggleMinimap() {
        this.minimapActive = !this.minimapActive;
    }

    /**
     * Returns a Menu that allows the player to adjust settings
     */
    private Menu getSettingsMenu(GameView view) {
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                SettingsView
                        .addSettingsMenuNodes(view.av,
                                new ListNode()
                                        .add(new NakedButtonNode(view.av, "x", () -> view.popups.setDisplay(false))))
                        .add(new SpacerNode()).add(new ButtonNode(view.av, "Exit Game", () -> view.close()))
        /*
         * .add(new ButtonNode(view.av, "Save game", () -> { try {
         * view.getSerial().saveGame(view.game); view.logger.log("Game has been saved");
         * } catch (Exception e) { view.logger.error("Could not save game");
         * e.printStackTrace(); } }))
         */
        );
    }

    /**
     * Returns true if we are showing the Minimap
     */
    private boolean displayMinimap() {
        return this.minimapActive && !this.view.popups.isDisplayed();
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av) {
        this.minimap.setPoint(0, this.getHeight());
        super.draw(av);

        // Draw a white bar at the bottom of the HUD
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        av.shapes.rect(0, Coords.SIZE.y - this.getHeight(), Coords.SIZE.x, 1);
        av.shapes.end();

        // Draw the minimap (if applicable)
        if (this.displayMinimap()) {
            this.minimap.draw(av, this.view.getCenteredPoint());
        }
    }

    /** {@inheritdoc} */
    public boolean click(Point p) {
        return super.click(p) || (this.displayMinimap() && this.minimap.click(this.view, p));
    }
}

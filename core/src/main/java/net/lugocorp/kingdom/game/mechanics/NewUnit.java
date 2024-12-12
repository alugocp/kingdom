package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.model.Glyph;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.ui.game.Hud;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.ModelNode;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class manages the logic for new Unit acquisition
 */
public class NewUnit {
    public static final int MAX_UNIT_POINTS = 100;

    /**
     * Returns the number of unit points that a Player should get each turn
     */
    public int getUnitPointsYield(int bareTiles, int tiles) {
        return (int) Math.floor(20f * bareTiles / tiles);
    }

    /**
     * Returns the Menu to handle new Unit placement
     */
    public Menu getNewUnitMenu(GameView view) {
        ListNode node = new ListNode()
                .add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)));
        node.add(new HeaderNode(view.game.graphics, "Recruit New Unit"))
                .add(new TextNode(view.game.graphics, "Select a tile to recruit your new unit?"))
                .add(new RowNode().add(new ButtonNode(view.game.graphics, "Yes", () -> {
                    String error = "You have no space to recruit a new unit";
                    Set<Point> tiles = view.game.getRecruitmentTiles(view.game.human);
                    if (tiles.size() == 0) {
                        view.logger.log(error);
                        view.popups.complete();
                        return;
                    }
                    view.popups.setDisplay(false);
                    view.logger.log("Please select a tile to recruit your new unit");
                    view.selector.select(tiles, error, (Point p) -> {
                        view.popups.complete();
                        view.popups.addNext(this.getUnitSelectionMenu(view, p));
                    });
                })).add(new ButtonNode(view.game.graphics, "No", () -> view.popups.complete())));
        return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false, node);
    }

    /**
     * Returns the Menu to handle new Unit selection
     */
    private Menu getUnitSelectionMenu(GameView view, Point p) {
        // Retrieve the selected Tile's Glyph. This should exist (as per the definition
        // of
        // view.game.getRecruitmentTiles()) so if we hit this error then something is
        // wrong.
        Optional<Glyph> glyph = view.game.world.getTile(p).flatMap((Tile t) -> t.glyph);
        if (!glyph.isPresent()) {
            throw new RuntimeException("Attempt to recruit onto a tile without a glyph");
        }

        // Create the Menu content for Unit recruitment
        List<Unit> options = this.getRecruitmentOptions(view, glyph.get(), p);
        ListNode node = new ListNode().add(new ButtonNode(view.game.graphics, "x", () -> view.popups.setDisplay(false)))
                .add(new HeaderNode(view.game.graphics, "Recruit New Unit"))
                .add(new ButtonNode(view.game.graphics, "Do not recruit any unit", () -> view.popups.complete()));
        RowNode previews = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        RowNode units = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        RowNode buttons = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        for (Unit u : options) {
            // TODO grab the right model for this preview
            previews.add(new ModelNode(view.getCamera(), view.getEnvironment(), view.game.graphics.loaders.assets,
                    "crystal"));
            units.add(u.getMenuContent(view, Optional.empty()));
            buttons.add(new ButtonNode(view.game.graphics, "Choose", () -> this.choose(view, u)));
        }
        node.add(previews);
        node.add(units);
        node.add(buttons);
        return new Menu(Hud.BUTTON_WIDTH, Hud.HEIGHT, Gdx.graphics.getWidth() - (Hud.BUTTON_WIDTH * 2), false, node);
    }

    /**
     * Returns the Unit options for recruitment
     */
    private List<Unit> getRecruitmentOptions(GameView view, Glyph g, Point p) {
        List<Unit> options = new ArrayList<>();
        GlyphPools pools = view.game.mechanics.pools;
        String[] names = pools.random(g, Math.min(pools.remaining(g), view.game.human.numRecruitmentOptions));
        for (String name : names) {
            options.add(view.game.generator.unit(name, p.x, p.y));
        }
        return options;
    }

    /**
     * Completes the associated popup Menu and spawns a new Unit in the World
     */
    private void choose(GameView view, Unit u) {
        view.popups.complete();
        view.game.mechanics.pools.remove(u);
        view.game.world.getTile(u.getX(), u.getY()).ifPresent((Tile t) -> {
            if (t.unit.isPresent()) {
                // We should never hit this, as per the definition of
                // view.game.getRecruitmentTiles()
                throw new RuntimeException("Cannot recruit onto an occupied tile");
            }
            t.glyph = Optional.empty();
            view.game.human.unitPoints -= NewUnit.MAX_UNIT_POINTS;
            view.game.setLeader(u, view.game.human);
            u.spawn(view);
        });
    }
}

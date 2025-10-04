package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.nodes.ButtonNode;
import net.lugocorp.kingdom.ui.nodes.GlyphBadgeNode;
import net.lugocorp.kingdom.ui.nodes.HeaderNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.nodes.ModelNode;
import net.lugocorp.kingdom.ui.nodes.NakedButtonNode;
import net.lugocorp.kingdom.ui.nodes.RowNode;
import net.lugocorp.kingdom.ui.nodes.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
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
    public int getUnitPointsYield(Player p) {
        return (int) Math.floor(20f * p.getBareTiles() / p.tiles);
    }

    /**
     * Returns the Menu to handle new Unit placement
     */
    public Menu getNewUnitMenu(GameView view) {
        ListNode node = new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.popups.setDisplay(false)));
        node.add(new HeaderNode(view.av, "Recruit New Unit"))
                .add(new TextNode(view.av, "Select a tile to recruit your new unit?"))
                .add(new RowNode().add(new ButtonNode(view.av, "Yes", () -> {
                    String error = "You have no space to recruit a new unit";
                    Set<Point> tiles = view.game.getRecruitmentTiles(view.game.human);
                    if (tiles.size() == 0) {
                        view.logger.error(error);
                        view.popups.complete();
                        return;
                    }
                    view.popups.setDisplay(false);
                    view.logger.log("Please select a tile to recruit your new unit");
                    view.selector.select(tiles, error, (Point p) -> {
                        view.popups.complete();
                        view.popups.addNext(this.getUnitSelectionMenu(view, p));
                    });
                })).add(new ButtonNode(view.av, "No", () -> view.popups.complete())));
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                node);
    }

    /**
     * Returns the Menu to handle new Unit selection
     */
    private Menu getUnitSelectionMenu(GameView view, Point p) {
        // Retrieve the selected Tile's GlyphCategory. This should exist (as per the
        // definition of
        // view.game.getRecruitmentTiles()) so if we hit this error then something is
        // wrong.
        Optional<GlyphCategory> category = view.game.world.getTile(p).flatMap((Tile t) -> t.getGlyph());
        if (!category.isPresent()) {
            throw new RuntimeException("Attempt to recruit onto a tile without a glyph");
        }

        // Create the Menu content for Glyph selection
        ListNode node = new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.popups.setDisplay(false)))
                .add(new HeaderNode(view.av, "Recruit New Unit").center())
                .add(new ButtonNode(view.av, "Do not recruit any unit", () -> view.popups.complete()));
        RowNode glyphs = new RowNode().setColumns(category.get().glyphs.length);
        RowNode badges = new RowNode().setColumns(category.get().glyphs.length);
        RowNode buttons = new RowNode().setColumns(category.get().glyphs.length);
        for (int a = 0; a < category.get().glyphs.length; a++) {
            final Glyph glyph = category.get().glyphs[a];
            glyphs.add(new HeaderNode(view.av, glyph.toString()).center());
            badges.add(new GlyphBadgeNode(view.av, glyph));
            buttons.add(new ButtonNode(view.av, "Choose", () -> {
                view.popups.complete();
                view.popups.add(this.getGlyphUnitSelectionMenu(view, glyph, p));
            }).enable(view.game.mechanics.pools.remaining(glyph) > 0));
        }
        node.add(glyphs);
        node.add(badges);
        node.add(buttons);
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                node);
    }

    /**
     * Returns the Unit selection Menu once the user has selected a Glyph
     */
    private Menu getGlyphUnitSelectionMenu(GameView view, Glyph glyph, Point p) {
        List<Unit> options = this.getRecruitmentOptions(view, glyph, p, view.game.human.numRecruitmentOptions);
        ListNode node = new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.popups.setDisplay(false)))
                .add(new HeaderNode(view.av, "Recruit New Unit"))
                .add(new ButtonNode(view.av, "Do not recruit any unit", () -> view.popups.complete()));
        RowNode previews = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        RowNode units = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        RowNode buttons = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        for (Unit u : options) {
            previews.add(new ModelNode(view.av, view.getCamera(), view.getEnvironment(), u.getModelName()));
            units.add(u.getMenuContent(view, Optional.empty()));
            buttons.add(new ButtonNode(view.av, "Choose", () -> {
                view.popups.complete();
                this.choose(view, view.game.human, u);
            }));
        }
        node.add(previews);
        node.add(units);
        node.add(buttons);
        return new Menu(Mechanics.MENU_MARGIN, view.hud.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                node);
    }

    /**
     * Returns n Unit options for recruitment
     */
    public List<Unit> getRecruitmentOptions(GameView view, Glyph g, Point p, int n) {
        List<Unit> options = new ArrayList<>();
        GlyphPools pools = view.game.mechanics.pools;
        String[] names = pools.random(g, Math.min(pools.remaining(g), n));
        for (String name : names) {
            options.add(view.game.generator.unit(name, p.x, p.y));
        }
        return options;
    }

    /**
     * Completes the associated popup Menu and spawns a new Unit in the World
     */
    public void choose(GameView view, Player p, Unit u) {
        view.game.mechanics.pools.remove(u);
        view.game.world.getTile(u.getX(), u.getY()).ifPresent((Tile t) -> {
            if (t.unit.isPresent()) {
                // We should never hit this, as per the definition of
                // view.game.getRecruitmentTiles()
                throw new RuntimeException("Cannot recruit onto an occupied tile");
            }
            t.setGlyph(Optional.empty());
            p.unitPoints -= NewUnit.MAX_UNIT_POINTS;
            view.game.setLeader(view, u, p);
            u.spawn(view);
            p.getFate().handleEvent(view, new Events.RecruitNewUnitEvent(u)).execute();
        });
    }
}

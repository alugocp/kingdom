package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.engine.projection.CameraLogic;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.game.GlyphBadgeNode;
import net.lugocorp.kingdom.menu.game.ModelNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.HelperNode;
import net.lugocorp.kingdom.menu.text.NakedButtonNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Tuple;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class manages the logic for new Unit acquisition
 */
public class NewUnit {
    public static final int MAX_UNIT_POINTS = 100;

    /**
     * Assigns unit points to the given Player and returns that value
     */
    public int giveUnitPointsYield(GameView view, Player player) {
        final int points = (int) Math.floor(20f * player.getBareTiles() / player.tiles);
        if (!player.isHumanPlayer()) {
            player.addUnitPoints(view, points);
            return points;
        }

        // Find out which empty Tiles owned by the Player are on screen
        final Set<Point> candidates = new HashSet<>();
        final Point center = CameraLogic.getCoordUnderScreenPoint(Coords.SIZE.x / 2, Coords.SIZE.y / 2);
        for (Point p : Hexagons.getNeighbors(center, 6)) {
            if (view.game.world.getTile(p).map(
                    (Tile t) -> !t.building.isPresent() && t.leader.map((Player p1) -> p1.equals(player)).orElse(false))
                    .orElse(false)) {
                candidates.add(p);
            }
        }

        // No fancy Overlays if we don't have any candidates on screen :(
        if (candidates.size() == 0) {
            player.addUnitPoints(view, points);
            return points;
        }

        // Split up the unit point yield amongst the candidates
        final int leftover = points % candidates.size();
        final int base = points / candidates.size();
        int a = 0;
        for (Point p : candidates) {
            player.addUnitPoints(view, p, base + (a++ < leftover ? 1 : 0));
            if (base == 0 && a == leftover) {
                break;
            }
        }
        return points;
    }

    /**
     * Returns the Menu to handle new Unit placement
     */
    public Menu getNewUnitMenu(GameView view) {
        ListNode node = new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false)));
        node.add(new HeaderNode(view.av, "Recruit New Unit"))
                .add(new TextNode(view.av, "Select a tile to recruit your new unit?"))
                .add(new RowNode().add(new ButtonNode(view.av, "Yes", () -> {
                    String error = "You have no space to recruit a new unit";
                    Set<Point> tiles = view.game.getRecruitmentTiles(view.game.human);
                    if (tiles.size() == 0) {
                        view.hud.logger.error(error);
                        view.hud.popups.complete();
                        return;
                    }
                    view.hud.popups.setDisplay(false);
                    view.hud.logger.log("Please select a tile to recruit your new unit");
                    this.scrollToNearestCandidate(view, tiles);
                    view.selector.select(tiles, error, (Point p) -> {
                        view.hud.popups.complete();
                        view.hud.popups.addNext(this.getUnitSelectionMenu(view, p));
                    });
                })).add(new ButtonNode(view.av, "No", () -> view.hud.popups.complete())));
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2),
                false, node);
    }

    /**
     * Returns the Menu to handle new Unit selection
     */
    private Menu getUnitSelectionMenu(GameView view, Point p) {
        // Retrieve the selected Tile's GlyphCategory. This should exist (as per the
        // definition of view.game.getRecruitmentTiles()) so if we hit this error then
        // something is wrong.
        Optional<GlyphCategory> category = view.game.world.getTile(p).flatMap((Tile t) -> t.getGlyph());
        if (!category.isPresent()) {
            throw new RuntimeException("Attempt to recruit onto a tile without a glyph");
        }

        // Create the Menu content for Glyph selection
        ListNode node = new ListNode().add(new RowNode()
                .add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false)))
                .add(new HeaderNode(view.av, "Recruit New Unit").center())
                .add(new HelperNode(view.av,
                        "Glyphs are general categories that units fall under. They help narrow down your search when recruiting a new unit. A unit can have either one or two glyphs. Tiles also have glyphs - the tile you selected determines the glyphs that appear in this screen. The sword (combat glyphs) means battle, defense, and healing. The hammer (worker glyphs) means nature, mining and trade."))
                .add(new ButtonNode(view.av, "Do not recruit any unit", () -> view.hud.popups.complete())))
                .add(new SpacerNode());
        RowNode glyphs = new RowNode().setColumns(category.get().glyphs.length);
        RowNode badges = new RowNode().setColumns(category.get().glyphs.length);
        RowNode descs = new RowNode().setColumns(category.get().glyphs.length);
        RowNode buttons = new RowNode().setColumns(category.get().glyphs.length);
        for (int a = 0; a < category.get().glyphs.length; a++) {
            final Glyph glyph = category.get().glyphs[a];
            glyphs.add(new HeaderNode(view.av, glyph.toString()).center());
            badges.add(new GlyphBadgeNode(view.av, glyph));
            descs.add(new TextNode(view.av, this.getGlyphDescription(glyph)));
            buttons.add(new ButtonNode(view.av, "Choose", () -> {
                view.hud.popups.complete();
                view.hud.popups.add(this.getGlyphUnitSelectionMenu(view, glyph, p));
            }).enable(view.game.mechanics.pools.remaining(glyph) > 0));
        }
        node.add(glyphs);
        node.add(badges);
        node.add(descs);
        node.add(buttons);
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2),
                false, node);
    }

    /**
     * Returns the Unit selection Menu once the user has selected a Glyph
     */
    private Menu getGlyphUnitSelectionMenu(GameView view, Glyph glyph, Point p) {
        List<Unit> options = this.getRecruitmentOptions(view, glyph, p, view.game.human.numRecruitmentOptions);
        ListNode node = new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false)))
                .add(new HeaderNode(view.av, "Recruit New Unit"))
                .add(new ButtonNode(view.av, "Do not recruit any unit", () -> view.hud.popups.complete()));
        RowNode previews = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        RowNode units = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        RowNode buttons = new RowNode().setColumns(view.game.human.numRecruitmentOptions);
        for (Unit u : options) {
            previews.add(new ModelNode(view.av, view.getCamera(), view.getEnvironment(), u.getModelName()));
            units.add(((RowNode) u.getMenuContent(view, Optional.empty())).toListNode());
            buttons.add(new ButtonNode(view.av, "Choose", () -> {
                view.hud.popups.complete();
                this.choose(view, view.game.human, u);
            }));
        }
        node.add(previews);
        node.add(units);
        node.add(buttons);
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2),
                false, node);
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
            p.addUnitPoints(view, -NewUnit.MAX_UNIT_POINTS);
            view.game.setLeader(view, u, p);
            u.spawn(view);
            p.getFate().handleEvent(view, new Events.RecruitNewUnitEvent(u)).execute();
            view.hud.logger.log(String.format("You recruited %s", u.name));
        });
    }

    /**
     * Scrolls the Game camera to the nearest possible recruitment Tile
     */
    private void scrollToNearestCandidate(GameView view, Set<Point> tiles) {
        final Point p = CameraLogic.getCoordUnderScreenPoint(Coords.SIZE.x / 2, Coords.SIZE.y / 2);
        Optional<Tuple<Point, Float>> best = Optional.empty();
        for (Point p1 : tiles) {
            final float distance = Coords.grid.distance(p, p1);
            if (best.map((Tuple<Point, Float> t) -> distance < t.b).orElse(true)) {
                best = Optional.of(new Tuple<Point, Float>(p1, distance));
            }
        }
        best.ifPresent((Tuple<Point, Float> t) -> view.centerOnPoint(t.a, false));
    }

    /**
     * Returns a description of the current Glyph
     */
    private String getGlyphDescription(Glyph g) {
        switch (g) {
            case BATTLE :
                return "Battle glyph units are good at quickly destroying enemy units and structures";
            case DEFENSE :
                return "Defense glyphs units have high health pools and armor, and can guard your besieged buildings until backup arrives";
            case HEALING :
                return "Healing glyph units provide rest and recovery to the rest of your army";
            case NATURE :
                return "Nature glyph units plant natural buildings and harvest food to keep your army fed and loyal";
            case MINING :
                return "Mining glyph units harvest gold and other valuable items from mines";
            case TRADE :
                return "Trade glyph units generate auction points as they transfer items across your kingdom";
        }
        return "If you're seeing this, please file a bug ticket";
    }
}

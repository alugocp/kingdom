package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Decision;
import net.lugocorp.kingdom.ai.DecisionChannel;
import net.lugocorp.kingdom.ai.DecisionClass;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.GoalUtils;
import net.lugocorp.kingdom.ai.Priority;
import net.lugocorp.kingdom.ai.behaviors.ActivateAbilityBehavior;
import net.lugocorp.kingdom.ai.behaviors.ConsumeItemBehavior;
import net.lugocorp.kingdom.ai.behaviors.ListBehavior;
import net.lugocorp.kingdom.ai.behaviors.MoveUnitBehavior;
import net.lugocorp.kingdom.ai.behaviors.RecruitUnitBehavior;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Chooser;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This causes the CompPlayer to gather food Items for its Units
 */
public class HarvestGoldGoal extends Goal {

    /** {@inheritdoc} */
    @Override
    protected Decision makeDecision(GameView view, CompPlayer player, DecisionChannel channel) {

        // Recruit unit handler (recruit a Trade or Mining Unit)
        if (channel.is(DecisionClass.RECRUIT_UNIT)) {
            final Map<Glyph, Integer> unitsByGlyph = GoalUtils.getUnitsByGlyph(player);
            final int mines = GoalUtils.getNumberOfBuildings(player, Labels.building_mine);

            // Recruit a Unit that can build a Mine
            if (mines < 2 || mines < unitsByGlyph.get(Glyph.MINING)) {
                return new Decision(channel, this, mines < 2 ? Priority.OPTIMAL : Priority.GOOD_IDEA,
                        new RecruitUnitBehavior(player, Glyph.MINING,
                                (Unit u) -> GoalUtils.canUnitSpawnBuilding(view, player, u, Labels.building_mine)
                                        ? Priority.GOOD_IDEA
                                        : Priority.NEUTRAL,
                                (Set<Point> options) -> Lambda.random(options)));
            }

            // Recruit a Unit that can harvest gold from a Mine or Marketplace
            Priority priority = Priority.GOOD_IDEA;
            if (unitsByGlyph.get(Glyph.TRADE) + unitsByGlyph.get(Glyph.MINING) < 3) {
                priority = Priority.OPTIMAL;
            } else if (unitsByGlyph.get(Glyph.TRADE) > 2 && unitsByGlyph.get(Glyph.MINING) > 2) {
                priority = Priority.NEUTRAL;
            }

            // Return our Decision with the chosen Glyph and Priority
            return new Decision(channel, this, priority,
                    new RecruitUnitBehavior(player,
                            unitsByGlyph.get(Glyph.TRADE) < unitsByGlyph.get(Glyph.MINING) ? Glyph.TRADE : Glyph.MINING,
                            (Unit u) -> this.doesUnitGenerateGoldOnBuilding(view, player, u).isPresent()
                                    ? Priority.GOOD_IDEA
                                    : Priority.NEUTRAL,
                            (Set<Point> options) -> {
                                final Chooser<Point> chooser = new Chooser<>((Integer old, Integer next) -> next < old);
                                for (Point p : options) {
                                    final Optional<Point> mine = GoalUtils.findNearbyBuilding(view, player, p,
                                            Labels.building_mine);
                                    mine.ifPresent((Point p1) -> {
                                        chooser.choice(p, p.distance(p1));
                                    });
                                }
                                return chooser.has() ? chooser.get() : Lambda.random(options);
                            }));
        }

        // Unit handler
        if (channel.is(DecisionClass.UNIT)) {
            final Unit unit = channel.getUnit();

            // Consume any valuable Items in this Unit's haul Inventory
            for (Item item : unit.haul) {
                if (unit.hunger.canEat(view, item)) {
                    continue;
                }
                final Priority priority = player.actor.analyze.itemConsumption(view, unit, item, (List<Event> log) -> {
                    for (Event e : log) {
                        if (e.getClass() == Events.YieldGoldEvent.class) {
                            return Priority.GOOD_IDEA;
                        }
                    }
                    return Priority.FATAL;
                });
                if (priority != Priority.FATAL) {
                    return new Decision(channel, this, Priority.NECESSITY, new ConsumeItemBehavior(unit, item));
                }
            }

            // Move the Unit to a Mine (or build a Mine)
            final Optional<String> building = this.doesUnitGenerateGoldOnBuilding(view, player, unit);
            if (building.isPresent()) {

                // Find a nearby Building that interacts with this Unit
                final Optional<Point> closest = GoalUtils.findNearbyBuilding(view, player, unit.getPoint(),
                        building.get());
                if (closest.isPresent()) {

                    // Check if there's a path to the closest Building
                    final Pathfinder pathfinder = new Pathfinder(unit);
                    final List<Point> path = pathfinder.getPath(view, closest.get());
                    if (path.size() > 0) {

                        // We can go to the Building if it's close by
                        if (path.size() < 6) {
                            return new Decision(channel, this, Priority.OPTIMAL, new MoveUnitBehavior(unit, path));
                        } else {
                            // Try to spawn the Building if one is not close enough
                            for (Ability active : unit.abilities.getActives()) {
                                final Optional<Path> selections = GoalUtils.canAbilitySpawnBuilding(view, player,
                                        active, Labels.building_mine);
                                if (selections.isPresent()) {
                                    final Optional<Point> dest = GoalUtils.findNearbyEmptyTile(view, player,
                                            unit.getPoint());
                                    if (dest.isPresent()) {
                                        final List<Point> tilePath = pathfinder.getPath(view, dest.get());
                                        if (dest.get().equals(unit.getPoint())
                                                || (tilePath.size() > 0 && tilePath.size() < 6)) {
                                            return new Decision(channel, this, Priority.OPTIMAL, new ListBehavior(
                                                    new MoveUnitBehavior(unit, tilePath),
                                                    new ActivateAbilityBehavior(unit, active, selections.get())));
                                        }
                                        if (tilePath.size() > 0 && tilePath.size() < 12) {
                                            return new Decision(channel, this, Priority.GOOD_IDEA, new ListBehavior(
                                                    new MoveUnitBehavior(unit, tilePath),
                                                    new ActivateAbilityBehavior(unit, active, selections.get())));
                                        }
                                    }
                                }
                            }

                            // Go to the Building anyways
                            return new Decision(channel, this, Priority.GOOD_IDEA, new MoveUnitBehavior(unit, path));
                        }
                    }
                }
            }
        }

        // No decision fallback
        return this.noDecision(channel);
    }

    /**
     * Returns the name of a Building that the given Unit generates gold on
     */
    private Optional<String> doesUnitGenerateGoldOnBuilding(GameView view, CompPlayer player, Unit unit) {
        return GoalUtils.checkUnitOnBuilding(unit,
                (Ability passive) -> this.doesAbilityGenerateGoldOnBuilding(view, player, passive));
    }

    /**
     * Returns the name of a Building that the given Ability generates gold on
     */
    private Optional<String> doesAbilityGenerateGoldOnBuilding(GameView view, CompPlayer player, Ability ability) {
        // TODO cache the results of this so we don't rerun it every turn
        return GoalUtils.checkAbilityOnBuilding(view, player, ability,
                new String[]{Labels.building_marketplace, Labels.building_mine}, (Event e) -> {
                    if (e.getClass() == Events.GenerateItemEvent.class) {
                        final Item item = ((Events.GenerateItemEvent) e).blob;
                        if (item.gold > 1) {
                            return true;
                        }
                    } else if (e.getClass() == Events.YieldGoldEvent.class) {
                        return true;
                    }
                    return false;
                });
    }
}

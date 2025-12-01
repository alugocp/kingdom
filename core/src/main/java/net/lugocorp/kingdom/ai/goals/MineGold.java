package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.action.Goal;
import net.lugocorp.kingdom.ai.action.Plan;
import net.lugocorp.kingdom.ai.action.PlanNode;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.EventLog;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.glyph.Glyph;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Path;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import java.util.Optional;
import java.util.Set;

/**
 * This class tells the Actor to harvest valuables from Buildings
 */
public class MineGold extends Goal {

    /** {@inheritdoc} */
    @Override
    public Optional<Plan> suggestPlan(GameView view, Unit u) {
        final Set<Point> targets = Hexagons.getNeighbors(u.getPoint(), 4);
        return this.getBestPlan(Lambda.map((Point p) -> this.wrapPlanNode(view, new MoveNode(u, p)), targets));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        final MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        final Point dest = ((MoveNode) root).dest;
        final Optional<MemoryCell> cell = memory.getCell(dest);
        if (!cell.flatMap((MemoryCell c) -> c.getBuilding()).isPresent()) {
            return 0f;
        }

        // Do not attempt to harvest food from non-Building Entities
        if (!view.game.events.building.hasEventHandler(cell.get().getBuilding().get(), "GenerateBuildingEvent")) {
            return 0f;
        }

        // Get Building that we may move to
        final Building b = view.game.generator.building(cell.flatMap((MemoryCell c) -> c.getBuilding()).get(), 0, 0);
        CapturedEvents.instance.setFakePoint(dest);
        for (Ability ability : root.unit.abilities.getPassives()) {
            if (!ability.hasTickHandler(view)) {
                continue;
            }

            // Trigger Tick channel on the passive Ability and capture resulting Events
            CapturedEvents.instance.on();
            Events.RepeatedEvent e = new Events.RepeatedEvent("Tick", 1, false);
            ability.handleEvent(view, e);
            EventLog log = CapturedEvents.instance.off();

            // Check if we can get gold from this Building by analyzing the resulting Events
            for (Path p : log.getTargetPaths()) {
                for (Event event : log.getEvents(p)) {
                    if (!event.channel.equals(Events.GenerateItemEvent.class)) {
                        continue;
                    }
                    Events.GenerateItemEvent event1 = (Events.GenerateItemEvent) event;
                    if (event1.blob.gold > 0) {
                        CapturedEvents.instance.clearFakePoint();
                        return 1f;
                    }
                }
            }
        }
        CapturedEvents.instance.clearFakePoint();
        return 0f;
    }

    /** {@inheritdoc} */
    @Override
    public boolean likesGlyph(Glyph glyph) {
        return glyph == Glyph.MINING;
    }
}

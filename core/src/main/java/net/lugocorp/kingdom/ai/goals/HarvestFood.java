package net.lugocorp.kingdom.ai.goals;
import net.lugocorp.kingdom.ai.Goal;
import net.lugocorp.kingdom.ai.Plan;
import net.lugocorp.kingdom.ai.PlanNode;
import net.lugocorp.kingdom.ai.memory.MemoryCell;
import net.lugocorp.kingdom.ai.memory.MemoryMap;
import net.lugocorp.kingdom.ai.plans.MoveNode;
import net.lugocorp.kingdom.ai.prediction.CapturedEvents;
import net.lugocorp.kingdom.ai.prediction.EventLog;
import net.lugocorp.kingdom.game.core.Events;
import net.lugocorp.kingdom.game.events.Event;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import net.lugocorp.kingdom.utils.math.Path;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;
import java.util.Set;

/**
 * This class tells the Actor to harvest food from Buildings
 */
public class HarvestFood extends Goal {

    /** {@inheritdoc} */
    @Override
    public Optional<Plan> suggestPlan(GameView view, Unit u) {
        Set<Point> targets = u.getMoveTargets(view);
        return this.getBestPlan(Lambda.map((Point p) -> this.wrapPlanNode(view, new MoveNode(u, p)), targets));
    }

    /** {@inheritdoc} */
    @Override
    protected float getScore(GameView view, PlanNode root) {
        MemoryMap memory = ((CompPlayer) root.unit.getLeader().get()).memory;
        Point dest = ((MoveNode) root).dest;
        MemoryCell cell = memory.getCell(dest);
        if (!cell.getBuilding().isPresent()) {
            return 0f;
        }

        // Get Building that we may move to
        Building b = view.game.generator.building(cell.getBuilding().get(), 0, 0);
        CapturedEvents.instance.setFakePoint(dest);
        for (Ability ability : root.unit.passives) {
            if (!ability.hasEventHandler(view, "TickEvent")) {
                continue;
            }

            // Trigger TickEvent on the passive Ability and capture resulting Events
            CapturedEvents.instance.on();
            Events.RepeatedEvent e = new Events.RepeatedEvent("TickEvent", 1, false);
            ability.handleEvent(view, e);
            EventLog log = CapturedEvents.instance.off();

            // Check if we can get an edible Item from this Building by analyzing the
            // resulting Events
            for (Path p : log.getTargetPaths()) {
                for (Event event : log.getEvents(p)) {
                    if (!event.channel.equals("GenerateItemEvent")) {
                        continue;
                    }
                    Events.GenerateItemEvent event1 = (Events.GenerateItemEvent) event;
                    Events.CanEatEvent event2 = new Events.CanEatEvent(root.unit, event1.blob);
                    if (event2.edible) {
                        CapturedEvents.instance.clearFakePoint();
                        return 1f;
                    }
                }
            }
        }
        CapturedEvents.instance.clearFakePoint();
        return 0f;
    }
}

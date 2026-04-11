package net.lugocorp.kingdom.tests.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.gameplay.events.EventHandlerBundle;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for the event system
 */
public class EventTests {

    @Test
    public void testEventHandlerBundleLogic() throws Exception {
        final EventHandlerBundle<Unit> bundle = new EventHandlerBundle<>();
        final String stratifier = "Batatita";
        final String event1 = "GenerateUnitEvent";
        final String event2 = "TakeDamageEvent";
        assertEquals(bundle.hasEventHandler(stratifier, event1), false);
        assertEquals(bundle.hasEventHandler(stratifier, event2), false);

        // Add some EventHandlers
        bundle.addEventHandler(stratifier, event1, (GameView view, Unit receiver, Event event) -> new SideEffect());
        bundle.setDefaultHandler(event2, (GameView view, Unit receiver, Event event) -> new SideEffect());
        assertEquals(bundle.hasEventHandler(stratifier, event1), true);
        assertEquals(bundle.hasEventHandler(stratifier, event2), true);

        // Check registered data
        // TODO include default handlers in the channel list here (if we don't remove
        // default handlers)
        assertEquals(1, bundle.getChannels(stratifier).size());
        assertEquals(1, bundle.getStratifiers().size());
    }
}

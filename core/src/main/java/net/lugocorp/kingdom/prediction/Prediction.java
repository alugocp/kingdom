package net.lugocorp.kingdom.prediction;
import net.lugocorp.kingdom.gameplay.events.Event;
import net.lugocorp.kingdom.math.Path;
import java.util.List;

/**
 * This class contains a selection Path and an associated Event log (a complete
 * prediction of what will happen in future scenarios)
 */
public class Prediction {
    public final List<Event> log;
    public final Path path;

    public Prediction(Path path, List<Event> log) {
        this.path = path;
        this.log = log;
    }
}

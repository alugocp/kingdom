package net.lugocorp.kingdom.utils.code;
import java.util.ArrayList;
import java.util.List;

/**
 * SideEffects are callables that are designed for the AI system. The game's AI
 * needs to "look ahead" into the game to predict its best possible move. It
 * does this by triggering an Event and assessing the Events that cascade
 * afterwards. Some EventHandlers mutate game state. They should only do this
 * inside a SideEffect object, otherwise AI predictions would affect the actual
 * game. Here are rules for SideEffects: 1) Methods that mutate state and call a
 * potentially mutating event must use SideEffect; 2) Methods that mutate state
 * but do not call mutating events should be called within SideEffect; 3)
 * Methods that return SideEffect called inside another SideEffect method should
 * have their result propagated
 */
public interface SideEffect {

    /**
     * Combines multiple SideEffects into one
     */
    public static SideEffect all(SideEffect... effects) {
        return () -> {
            for (SideEffect effect : effects) {
                effect.execute();
            }
        };
    }

    /**
     * Combines multiple SideEffects into one
     */
    public static SideEffect all(Iterable<SideEffect> effects) {
        return () -> {
            for (SideEffect effect : effects) {
                effect.execute();
            }
        };
    }

    /**
     * Returns a List (possible populated with SideEffects) that you can add more
     * SideEffects to before calling SideEffect.all()
     */
    public static List<SideEffect> list(SideEffect... effects) {
        final List<SideEffect> list = new ArrayList<>();
        for (SideEffect e : effects) {
            list.add(e);
        }
        return list;
    }

    /**
     * Placeholder SideEffect, like a null value
     */
    public static SideEffect none = () -> {
    };

    /**
     * Unimplemented function that the SideEffect will trigger later
     */
    public void execute();
}

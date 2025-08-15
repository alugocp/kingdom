package net.lugocorp.kingdom.utils;
import java.util.List;

/**
 * SideEffects are callables that are designed for the AI system. The game's AI
 * needs to "look ahead" into the game to predict its best possible move. It
 * does this by triggering an Event and assessing the Events that cascade
 * afterwards. Some EventHandlers mutate game state. They should only do this
 * inside a SideEffect object, otherwise AI predictions would affect the actual
 * game. Here are rules for SideEffects: - Methods that mutate state and call a
 * potentially mutating event must use SideEffect - Methods that mutate state
 * but do not call mutating events should be called within SideEffect - Methods
 * that return SideEffect called inside another SideEffect method should have
 * their result propagated
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
     * Combines a List<SideEffects> into one
     */
    public static SideEffect all(List<SideEffect> effects) {
        return () -> {
            for (SideEffect effect : effects) {
                effect.execute();
            }
        };
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

package net.lugocorp.kingdom.engine;
import net.lugocorp.kingdom.game.events.AllEventHandlers;

public interface GameMod {

    /**
     * Returns a unique ID for this mod
     */
    public String getName();

    /**
     * Returns a description for this mod
     */
    public String getDescription();

    /**
     * This function is called when the mod is successfully loaded
     */
    public void load(AllEventHandlers events);
}

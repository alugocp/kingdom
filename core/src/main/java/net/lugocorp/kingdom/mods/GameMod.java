package net.lugocorp.kingdom.mods;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.events.AllEventHandlers;

/**
 * Interface that allows the game to load mods
 */
public interface GameMod {

    /**
     * Returns all information surrounding this mod
     */
    public ModProfile getProfile();

    /**
     * This function is called when the mod must load its spritesheets
     */
    public void registerSprites(SpriteLoader sprites);

    /**
     * This function is called when the mod must load its event handlers
     */
    public void registerEvents(AllEventHandlers events);
}

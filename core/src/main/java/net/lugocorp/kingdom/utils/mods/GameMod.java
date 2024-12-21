package net.lugocorp.kingdom.utils.mods;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.game.events.AllEventHandlers;

public interface GameMod {

    /**
     * Returns a unique ID for this mod
     */
    public String getKey();

    /**
     * Returns a display name for this mod
     */
    public String getName();

    /**
     * Returns a description for this mod
     */
    public String getDescription();

    /**
     * This function is called when the mod must load its spritesheets
     */
    public void registerSprites(SpriteLoader sprites);

    /**
     * This function is called when the mod must load its event handlers
     */
    public void registerEvents(AllEventHandlers events);
}

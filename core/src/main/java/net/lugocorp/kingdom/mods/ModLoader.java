package net.lugocorp.kingdom.mods;
import net.lugocorp.kingdom.content.vanilla.VanillaMod;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;

/**
 * This class handles loading mods
 */
public class ModLoader {
    private static final GameMod[] mods = {new VanillaMod()};

    /**
     * Returns a list of mods to load
     */
    public GameMod[] getMods() {
        return ModLoader.mods;
    }

    /**
     * Loads the given mod
     */
    public void loadMod(GameMod mod, AllEventHandlers events, SpriteLoader sprites) {
        mod.registerEvents(events);
        mod.registerSprites(sprites);
    }
}

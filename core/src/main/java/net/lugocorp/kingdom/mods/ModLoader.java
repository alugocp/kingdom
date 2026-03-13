package net.lugocorp.kingdom.mods;
import net.lugocorp.kingdom.Main;
import net.lugocorp.kingdom.engine.assets.SpriteLoader;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.lugocorp.kingdom.content.vanilla.VanillaMod;

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
    public void loadMod(GameMod mod, AllEventHandlers events, SpriteLoader sprites, ModAssetsMap modAssetsMap) {
        mod.registerEvents(events);
        mod.registerSprites(sprites);
    }
}

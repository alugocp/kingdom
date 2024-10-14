package net.lugocorp.kingdom.utils;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles loading mods
 */
public class ModLoader {

    /**
     * Returns a list of mods to load
     */
    public List<String> getMods() {
        List<String> mods = new ArrayList<>();
        for (File f : new File("../mods").listFiles()) {
            if (f.isDirectory()) {
                continue;
            }
            mods.add(f.getPath());
        }
        return mods;
    }

    /**
     * Loads the given mod
     */
    public void loadMod(String filepath, AllEventHandlers events) throws Exception {
        URLClassLoader child = new URLClassLoader(new URL[]{new File(filepath).toURI().toURL()},
                this.getClass().getClassLoader());
        Class mod = Class.forName("net.lugocorp.kingdom.mod.KingdomMod", true, child);
        Method loadMethod = mod.getDeclaredMethod("load", AllEventHandlers.class);
        loadMethod.invoke(mod.newInstance(), events);
    }
}

package net.lugocorp.kingdom.utils;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import com.badlogic.gdx.Gdx;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
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

/**
 * This class handles loading mods
 */
public class ModLoader {
    public static final String ASSETS_BASE = "kingdom/extracted";
    private final Set<String> loaded = new HashSet<>();

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
     * Returns a simplified filename for the given mod
     */
    public String getModKey(String filepath) {
        Path path = new File(filepath).toPath();
        return path.getName(path.getNameCount() - 1).toString();
    }

    /**
     * Loads the given mod
     */
    public void loadMod(String key, String filepath, AllEventHandlers events) throws Exception {
        if (this.loaded.contains(key)) {
            throw new RuntimeException(String.format("Mod '%s' has already been loaded", key));
        }
        this.loaded.add(key);
        URLClassLoader child = new URLClassLoader(new URL[]{new File(filepath).toURI().toURL()},
                this.getClass().getClassLoader());
        Class mod = Class.forName("net.lugocorp.kingdom.mod.KingdomMod", true, child);
        Method loadMethod = mod.getDeclaredMethod("load", AllEventHandlers.class);
        loadMethod.invoke(mod.newInstance(), events);
    }

    /**
     * Sets up the mod unzip site
     */
    public void resetModAssetsLocation() throws Exception {
        File site = Gdx.files.external(ModLoader.ASSETS_BASE).file();
        if (site.exists()) {
            // TODO actually delete the folder
            site.delete();
        }
        site.mkdirs();
    }

    /**
     * Returns the given File's relative path from the /assets folder, if there is
     * any
     */
    private Optional<String> getAssetRelativePath(String filepath) {
        Path path = new File(filepath).toPath();
        if (path.getName(0).toString().equals("assets") && path.getNameCount() > 1) {
            return Optional.of(path.subpath(1, path.getNameCount()).toString());
        }
        return Optional.empty();
    }

    /**
     * Extracts the assets associated with the given mod
     */
    public void unzipAssets(String key, String filepath, ModAssetManager modAssetsMap) throws Exception {
        ZipEntry entry = null;
        ZipInputStream input = new ZipInputStream(new FileInputStream(filepath));
        while ((entry = input.getNextEntry()) != null) {
            Optional<String> path = this.getAssetRelativePath(entry.getName());
            if (!path.isPresent()) {
                continue;
            }
            modAssetsMap.put(path.get(), key);
            File file = Gdx.files.external(String.format("%s/%s/%s", ModLoader.ASSETS_BASE, key, path.get())).file();
            if (!entry.isDirectory()) {
                // Create parent directories
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                // Extract the file
                file.createNewFile();
                int len = 0;
                byte[] buffer = new byte[1024];
                FileOutputStream output = new FileOutputStream(file);
                while ((len = input.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                output.close();
            }
        }
        input.close();
    }
}

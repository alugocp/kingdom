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
        final List<String> mods = new ArrayList<>();
        final FileHandle root = Gdx.files.local("mods");
        if (root.exists()) {
            for (FileHandle f : root.list()) {
                if (f.isDirectory()) {
                    continue;
                }
                mods.add(f.path());
            }
        }
        return mods;
    }

    /**
     * Loads the given mod
     */
    public GameMod loadMod(String filepath, AllEventHandlers events, SpriteLoader sprites, ModAssetsMap modAssetsMap)
            throws Exception {
        // Load mod data
        URLClassLoader child = new URLClassLoader(new URL[]{new File(filepath).toURI().toURL()},
                this.getClass().getClassLoader());
        Class definition = Class.forName("net.lugocorp.kingdom.mod.KingdomMod", true, child);
        Constructor<GameMod> construct = definition.getConstructor();
        GameMod mod = construct.newInstance();
        ModProfile profile = mod.getProfile();

        // Check for mod legality
        // TODO add fonts, game, sfx, and textures?
        if (profile.key.equals("shaders") || profile.key.equals("ui")) {
            throw new RuntimeException(String.format("Illegal mod key '%s'", profile.key));
        }
        if (profile.minimumGameVersion.isNewerThan(Main.VERSION)) {
            throw new RuntimeException(String.format("Mod '%s' requires version %s, but you are running %s",
                    profile.key, profile.minimumGameVersion, Main.VERSION));
        }
        if (this.loaded.contains(profile.key)) {
            throw new RuntimeException(String.format("Mod '%s' has already been loaded", profile.key));
        }

        // Register mod into the game
        mod.registerEvents(events);
        mod.registerSprites(sprites);
        this.unzipAssets(profile.key, filepath, modAssetsMap);
        this.loaded.add(profile.key);
        return mod;
    }

    /**
     * Copies the vanilla mod out from the game's assets folder into the filesystem
     */
    public void createDefaultMod() {
        final byte[] data = Gdx.files.internal("DEFAULT.jar").readBytes();
        Gdx.files.local("mods/vanilla.jar").writeBytes(data, false);
    }

    /**
     * Sets up the mod unzip site
     */
    public void resetModAssetsLocation() throws Exception {
        final FileHandle site = Gdx.files.external(ModLoader.ASSETS_BASE);
        if (site.exists()) {
            this.recursiveFileDelete(site);
        }
        site.mkdirs();
    }

    /**
     * Recursively deletes a folder and all its contents from the filesystem
     */
    private void recursiveFileDelete(FileHandle f) {
        if (f.isDirectory()) {
            for (FileHandle f1 : f.list()) {
                this.recursiveFileDelete(f1);
            }
        }
        f.delete();
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
    private void unzipAssets(String key, String filepath, ModAssetsMap modAssetsMap) throws Exception {
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

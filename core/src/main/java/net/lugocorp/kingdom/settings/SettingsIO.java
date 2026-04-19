package net.lugocorp.kingdom.settings;
import net.lugocorp.kingdom.utils.Files;
import com.badlogic.gdx.files.FileHandle;

/**
 * This class handles reading and writing a Settings file
 */
public final class SettingsIO {
    private static final String FILENAME = "settings.json";

    /**
     * Saves Settings to a local file
     */
    public static final void write(Settings s) {
        try {
            Files.ensureExistence();
            final FileHandle f = Files.getFile(SettingsIO.FILENAME);
            f.writeString(SettingsJson.toJson(s), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads Settings from a local file or returns a default instance otherwise
     */
    public static final Settings readOrDefault() {
        try {
            Files.ensureExistence();
            final FileHandle f = Files.getFile(SettingsIO.FILENAME);
            if (f.exists()) {
                return SettingsJson.fromJson(f.readString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Settings();
    }
}

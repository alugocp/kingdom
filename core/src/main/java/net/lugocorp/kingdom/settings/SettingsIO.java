package net.lugocorp.kingdom.settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * This class handles reading and writing a Settings file
 */
public final class SettingsIO {
    private static final String filepath = "settings.json";

    /**
     * Saves Settings to a local file
     */
    public static final void write(Settings s) {
        final FileHandle f = Gdx.files.local(SettingsIO.filepath);
        f.writeString(SettingsJson.toJson(s), false);
    }

    /**
     * Reads Settings from a local file or returns a default instance otherwise
     */
    public static final Settings readOrDefault() {
        final FileHandle f = Gdx.files.local(SettingsIO.filepath);
        return f.exists() ? SettingsJson.fromJson(f.readString()) : new Settings();
    }
}

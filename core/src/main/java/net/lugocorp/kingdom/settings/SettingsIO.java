package net.lugocorp.kingdom.settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.json.JSONObject;

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
        final JSONObject data = new JSONObject().put("soundVolume", s.getSoundVolume())
                .put("musicVolume", s.getMusicVolume()).put("autoComplete", s.getAutoComplete())
                .put("reverse", s.getReversedScrollDirection()).put("tutorial", s.isTutorialEnabled());
        f.writeString(data.toString(), false);
    }

    /**
     * Reads Settings from a local file or returns a default instance otherwise
     */
    public static final Settings readOrDefault() {
        final FileHandle f = Gdx.files.local(SettingsIO.filepath);
        final Settings s = new Settings();
        if (f.exists()) {
            final JSONObject data = new JSONObject(f.readString());
            s.setSoundVolume(data.getFloat("soundVolume"));
            s.setMusicVolume(data.getFloat("musicVolume"));
            s.setAutoComplete(data.getBoolean("autoComplete"));
            s.setReversedScrollDirection(data.getBoolean("reverse"));
            s.setTutorialEnabled(data.getBoolean("tutorial"));
        }
        return s;
    }
}

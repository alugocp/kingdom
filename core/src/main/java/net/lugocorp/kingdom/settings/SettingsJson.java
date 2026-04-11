package net.lugocorp.kingdom.settings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class handles converting Settings instances to and from JSON
 */
public final class SettingsJson {

    /**
     * Converts a Settings instance to JSON
     */
    public static final String toJson(Settings s) {
        return new JSONObject().put("soundVolume", s.getSoundVolume()).put("musicVolume", s.getMusicVolume())
                .put("autoComplete", s.getAutoComplete()).put("outlineShader", s.getOutlineShader())
                .put("reverse", s.getReversedScrollDirection()).put("tutorial", s.isTutorialEnabled()).toString();
    }

    /**
     * Creates a Settings instance from JSON
     */
    public static final Settings fromJson(String json) {
        final Settings s = new Settings();
        final JSONObject data = new JSONObject(json);
        try {
            s.setSoundVolume(data.getFloat("soundVolume"));
            s.setMusicVolume(data.getFloat("musicVolume"));
            s.setAutoComplete(data.getBoolean("autoComplete"));
            s.setReversedScrollDirection(data.getBoolean("reverse"));
            s.setTutorialEnabled(data.getBoolean("tutorial"));
            if (data.has("outlineShader")) {
                s.setOutlineShader(data.getBoolean("outlineShader"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return s;
    }
}

package net.lugocorp.kingdom.tests.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.lugocorp.kingdom.settings.Settings;
import net.lugocorp.kingdom.settings.SettingsJson;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for Settings serialization
 */
public class SettingsTests {

    @Test
    public void testLoadSettingsBeforeOutlineOption() throws Exception {
        final Settings s = SettingsJson.fromJson(
                "{\"autoComplete\": false,\"soundVolume\": 1,\"tutorial\": false,\"reverse\": false,\"musicVolume\": 1}");
        assertEquals(true, s.getOutlineShader());
    }
}

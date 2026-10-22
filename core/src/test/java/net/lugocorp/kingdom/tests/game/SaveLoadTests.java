package net.lugocorp.kingdom.tests.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.lugocorp.kingdom.serial.SaveLoad;
import net.lugocorp.kingdom.utils.Version;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for game saving/loading (SaveLoad)
 */
public class SaveLoadTests {

    @Test
    public void testFilenameGeneration() throws Exception {
        final SaveLoad s = new SaveLoad();
        assertEquals("2026-01-01T12:00-05:00.game", s.getFilename(new Version(1, 0, 0),
                ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneId.of("EST", ZoneId.SHORT_IDS))));
        assertEquals("2026-01-01T12:00-05:00-alpha.game", s.getFilename(new Version(3, 1, 0, Version.ALPHA),
                ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneId.of("EST", ZoneId.SHORT_IDS))));
        assertEquals("2026-01-01T12:00-05:00-beta.game", s.getFilename(new Version(0, 0, 2, Version.BETA),
                ZonedDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneId.of("EST", ZoneId.SHORT_IDS))));
    }
}

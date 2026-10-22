package net.lugocorp.kingdom.tests.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.lugocorp.kingdom.utils.Version;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for versioning (Version)
 */
public class VersionTests {

    @Test
    public void testWhichVersionIsNewerThanAnother() throws Exception {
        final Version s = new Version(2, 1, 0, Version.BETA);
        assertEquals(true, s.isNewerThan(new Version(1, 10, 0, Version.FULL)));
        assertEquals(true, s.isNewerThan(new Version(1, 1, 10, Version.FULL)));
        assertEquals(false, s.isNewerThan(new Version(2, 1, 0, Version.ALPHA)));
        assertEquals(true, s.isNewerThan(new Version(2, 0, 5, Version.FULL)));
        assertEquals(false, s.isNewerThan(new Version(2, 1, 0, Version.BETA)));
        assertEquals(false, s.isNewerThan(new Version(3, 0, 0, Version.FULL)));
        assertEquals(false, s.isNewerThan(new Version(2, 1, 0, Version.FULL)));
    }
}

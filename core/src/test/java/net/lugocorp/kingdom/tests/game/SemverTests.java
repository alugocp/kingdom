package net.lugocorp.kingdom.tests.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.lugocorp.kingdom.utils.Semver;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for semantic versioning (Semver)
 */
public class SemverTests {

    @Test
    public void testWhichSemverIsNewerThanAnother() throws Exception {
        final Semver s = new Semver(2, 1, 0, Semver.BETA);
        assertEquals(true, s.isNewerThan(new Semver(1, 10, 0, Semver.FULL)));
        assertEquals(true, s.isNewerThan(new Semver(1, 1, 10, Semver.FULL)));
        assertEquals(true, s.isNewerThan(new Semver(2, 1, 0, Semver.ALPHA)));
        assertEquals(true, s.isNewerThan(new Semver(2, 0, 5, Semver.FULL)));
        assertEquals(false, s.isNewerThan(new Semver(2, 1, 0, Semver.BETA)));
        assertEquals(false, s.isNewerThan(new Semver(3, 0, 0, Semver.FULL)));
        assertEquals(false, s.isNewerThan(new Semver(2, 1, 0, Semver.FULL)));
    }
}

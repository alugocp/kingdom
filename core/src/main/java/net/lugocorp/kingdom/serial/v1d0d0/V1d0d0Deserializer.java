package net.lugocorp.kingdom.serial.v1d0d0;
import net.lugocorp.kingdom.serial.Deserializer;
import net.lugocorp.kingdom.utils.Semver;
import java.io.File;
import java.util.Optional;

/**
 * Deserializer implementation for v1.0.0
 */
public class V1d0d0Deserializer extends Deserializer<GameSerial, GameSerial> {

    public V1d0d0Deserializer() {
        super(new Semver(1, 0, 0), Optional.empty());
    }

    /** {@inheritdoc} */
    protected Optional<Semver> getFileVersion(File file) {
        // TODO serial
        return Optional.empty();
    }

    /** {@inheritdoc} */
    protected GameSerial getGameSerial(File file) {
        // TODO serial
        return null;
    }

    /** {@inheritdoc} */
    protected GameSerial migrateSerial(GameSerial prev) {
        // TODO serial
        return null;
    }
}

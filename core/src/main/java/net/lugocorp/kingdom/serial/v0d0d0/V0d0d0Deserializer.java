package net.lugocorp.kingdom.serial.v0d0d0;
import net.lugocorp.kingdom.serial.BaseDeserializer;
import net.lugocorp.kingdom.utils.Semver;
import java.io.File;
import java.util.Optional;

/**
 * Deserializer implementation for v1.0.0
 */
public class V0d0d0Deserializer extends BaseDeserializer<GameSerial> {

    public V0d0d0Deserializer() {
        super(new Semver(1, 0, 0));
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
}

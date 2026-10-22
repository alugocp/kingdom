package net.lugocorp.kingdom.serial.v1d0d0;
import net.lugocorp.kingdom.serial.BaseDeserializer;
import net.lugocorp.kingdom.serial.kryo.SaveFileHandler;
import net.lugocorp.kingdom.utils.Version;
import java.io.File;
import java.util.Optional;

/**
 * Deserializer implementation for v1.0.0
 */
public class V1d0d0Deserializer extends BaseDeserializer<GameSerial> {

    public V1d0d0Deserializer() {
        super(new Version(1, 0, 0));
    }

    /** {@inheritdoc} */
    protected Optional<Version> getFileVersion(File file) {
        return SaveFileHandler.readVersion(file);
    }

    /** {@inheritdoc} */
    protected GameSerial getGameSerial(File file) throws Exception {
        return SaveFileHandler.readFromFile(file, GameSerial.class);
    }
}

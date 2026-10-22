package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Version;
import java.io.File;
import java.util.Optional;

/**
 * Handles the deserialization logic required to rehydrate an entire Game
 * instance
 */
public abstract class BaseDeserializer<V extends Serial<Game>> {
    protected final Version version;

    protected BaseDeserializer(Version version) {
        this.version = version;
    }

    /**
     * Extracts a Version from the given File (if it can be read by this
     * Deserializer)
     */
    protected abstract Optional<Version> getFileVersion(File file);

    /**
     * Extracts a Serial<Game> from the given File
     */
    protected abstract V getGameSerial(File file) throws Exception;

    /**
     * Attempts to deserialize the given File under this version of the Deserializer
     */
    public Optional<V> deserialize(File file) {
        // Check if the loaded file can be handled by this version of the Deserializer
        if (this.getFileVersion(file).map((Version v) -> v.equals(this.version) || v.isNewerThan(this.version))
                .orElse(false)) {
            try {
                final V v = this.getGameSerial(file);
                return Optional.of(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}

package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Semver;
import java.io.File;
import java.util.Optional;

/**
 * Handles the deserialization logic required to rehydrate an entire Game
 * instance
 */
public abstract class BaseDeserializer<V extends Serial<Game>> {
    protected final Semver version;

    protected BaseDeserializer(Semver version) {
        this.version = version;
    }

    /**
     * Extracts a Semver from the given File (if it can be read by this
     * Deserializer)
     */
    protected abstract Optional<Semver> getFileVersion(File file);

    /**
     * Extracts a Serial<Game> from the given File
     */
    protected abstract V getGameSerial(File file);

    /**
     * Attempts to deserialize the given File under this version of the Deserializer
     */
    public Optional<V> deserialize(File file) {
        // Check if the loaded file can be handled by this version of the Deserializer
        if (this.getFileVersion(file).map((Semver v) -> v.isNewerThan(this.version)).orElse(false)) {
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

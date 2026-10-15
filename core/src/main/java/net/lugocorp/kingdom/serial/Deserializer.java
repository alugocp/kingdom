package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Semver;
import java.io.File;
import java.util.Optional;

/**
 * Handles the deserialization logic required to rehydrate an entire Game
 * instance
 */
public abstract class Deserializer<V extends Serial<Game>, P extends Serial<Game>> {
    protected final Optional<Deserializer<P, ?>> prev;
    protected final Semver version;

    protected Deserializer(Semver version, Optional<Deserializer<P, ?>> prev) {
        this.version = version;
        this.prev = prev;
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
     * Migrates a previous version of Serial<Game> to the curent version
     */
    protected abstract V migrateSerial(P prev);

    /**
     * Attempts to deserialize the given File under this version of the Deserializer
     */
    public Optional<Game> deserialize(File file) {
        // The loaded file is handled by this Deserializer
        if (this.getFileVersion(file).map((Semver v) -> v.isNewerThan(this.version)).orElse(false)) {
            try {
                final V v = this.getGameSerial(file);
                final Game game = v.deserialize();
                return Optional.of(game);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        // The loaded file is handled by an older version
        return this.prev.flatMap((Deserializer<P, ?> previous) -> {
            try {
                final P p = previous.getGameSerial(file);
                final V v = this.migrateSerial(p);
                final Game game = v.deserialize();
                return Optional.of(game);
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }
}

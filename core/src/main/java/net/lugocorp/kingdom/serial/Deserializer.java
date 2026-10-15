package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Semver;
import java.io.File;
import java.util.Optional;

/**
 * Handles the deserialization logic required to rehydrate an entire Game
 * instance
 */
public abstract class Deserializer<V extends Serial<Game>, P extends Serial<Game>> extends BaseDeserializer<V> {
    protected final Optional<BaseDeserializer<P>> prev;

    protected Deserializer(Semver version, Optional<BaseDeserializer<P>> prev) {
        super(version);
        this.prev = prev;
    }

    /**
     * Migrates a previous version of Serial<Game> to the curent version
     */
    protected abstract V migrateSerial(P prev);

    /**
     * Attempts to deserialize the given File under this version of the Deserializer
     */
    public Optional<V> deserialize(File file) {
        final Optional<V> v = super.deserialize(file);
        if (v.isPresent()) {
            return v;
        }

        // The loaded file is handled by an older version
        return this.prev.flatMap((BaseDeserializer<P> previous) -> {
            try {
                final Optional<P> p = previous.deserialize(file);
                return p.map((P p1) -> this.migrateSerial(p1));
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }
}

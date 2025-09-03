package net.lugocorp.kingdom.mods;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class handles mapping resource names to their extracted mod assets
 */
public class ModAssetsMap {
    private final Map<String, String> resourceMods = new HashMap<>();

    /**
     * Sets which mod name to associate with the given resource filepath
     */
    public void put(String path, String mod) {
        this.resourceMods.put(path, mod);
    }

    /**
     * Returns which mod (if any) this resource is associated with
     */
    public Optional<String> get(String path) {
        if (this.resourceMods.containsKey(path)) {
            return Optional.of(this.resourceMods.get(path));
        }
        return Optional.empty();
    }
}

package net.lugocorp.kingdom.mods;
import net.lugocorp.kingdom.utils.code.Semver;

/**
 * This class contains all information describing a given mod
 */
public class ModProfile {
    public final String[] credits;
    public final Semver minimumGameVersion;
    public final String description;
    public final String name;
    public final String key;

    public ModProfile(String key, String name, String description, Semver minimumGameVersion, String[] credits) {
        this.minimumGameVersion = minimumGameVersion;
        this.description = description;
        this.credits = credits;
        this.name = name;
        this.key = key;
    }
}

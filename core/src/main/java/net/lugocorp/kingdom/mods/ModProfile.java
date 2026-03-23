package net.lugocorp.kingdom.mods;

/**
 * This class contains all information describing a given mod
 */
public class ModProfile {
    public final String[] credits;
    public final String description;
    public final String name;
    public final String key;

    public ModProfile(String key, String name, String description, String[] credits) {
        this.description = description;
        this.credits = credits;
        this.name = name;
        this.key = key;
    }
}

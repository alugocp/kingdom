package net.lugocorp.kingdom.serial;

/**
 * Represents some Game data that can be serialized into a file
 */
public interface Serializable<S extends Serial> {

    /**
     * Serializes the Game data
     */
    public S serialize();
}

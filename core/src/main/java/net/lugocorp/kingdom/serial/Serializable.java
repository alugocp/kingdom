package net.lugocorp.kingdom.serial;

/**
 * Represents some Game data that can be serialized into a file
 */
public interface Serializable<T> {

    /**
     * Serializes the Game data
     */
    public Serial<T> serialize();
}

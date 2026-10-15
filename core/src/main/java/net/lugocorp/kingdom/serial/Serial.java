package net.lugocorp.kingdom.serial;

/**
 * This is the serialized version of some Game data
 */
public interface Serial<T> {

    /**
     * Returns the original object that this instance represents
     */
    public T deserialize();
}

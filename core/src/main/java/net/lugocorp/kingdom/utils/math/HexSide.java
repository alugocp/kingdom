package net.lugocorp.kingdom.utils.math;

/**
 * Represents each side in a hexagon
 */
public enum HexSide {
    LEFT(1), RIGHT(2), TOP_LEFT(4), TOP_RIGHT(8), BOT_LEFT(16), BOT_RIGHT(32);

    public final int value;

    public HexSide(int value) {
        this.value = value;
    }
}

package net.lugocorp.kingdom.utils;

/**
 * Contains subsystem tags to help filter log output
 */
public enum LogSys {
    AI("AI");

    private final String label;

    private LogSys(String label) {
        this.label = label;
    }
}

package net.lugocorp.kingdom.utils;
import java.io.PrintStream;

/**
 * Utility class for debug logs
 */
public class Log {
    private static final PrintStream out = System.out;
    private static final boolean debug = true;

    /**
     * Logs the given message
     */
    public static void log(String msg) {
        if (!Log.debug) {
            return;
        }
        Log.out.println(msg);
    }

    /**
     * Logs the given formatted message
     */
    public static void log(String format, Object... args) {
        if (!Log.debug) {
            return;
        }
        Log.out.printf(format, args);
        Log.out.print("\n");
    }
}

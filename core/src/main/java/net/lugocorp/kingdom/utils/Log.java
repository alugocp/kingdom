package net.lugocorp.kingdom.utils;
import net.lugocorp.kingdom.FeatureFlags;
import java.io.PrintStream;

/**
 * Utility class for debug logs
 */
public class Log {
    private static final PrintStream out = System.out;
    private static final boolean debug = FeatureFlags.DEBUG;

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

    /**
     * Calls log(msg) but with the given LogSys
     */
    public static void log(LogSys sys, String msg) {
        Log.log(String.format("[%s] %s", sys.label, msg));
    }

    /**
     * Calls log(format, ...args) but with the given LogSys
     */
    public static void log(LogSys sys, String format, Object... args) {
        Log.log(String.format("[%s] %s", sys.label, format), args);
    }
}

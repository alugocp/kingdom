package net.lugocorp.kingdom;
import java.util.Optional;

/**
 * A wrapper class for safe FeatureFlag access
 */
public class DevOptions {

    /**
     * Returns true if we're in debug mode
     */
    public static boolean isDebugMode() {
        return FeatureFlags.DEBUG;
    }

    /**
     * Receives a field only if we're in debug mode
     */
    private static <T> Optional<T> getDebugField(Optional<T> field) {
        return DevOptions.isDebugMode() ? field : Optional.empty();
    }

    /**
     * Accesses FeatureFlags.WORLD_SEED
     */
    public static Optional<Long> getWorldSeed() {
        return DevOptions.getDebugField(FeatureFlags.WORLD_SEED);
    }

    /**
     * Accesses FeatureFlags.UNIT_OPTIONS
     */
    public static Optional<String[]> getUnitOptions() {
        return DevOptions.getDebugField(FeatureFlags.UNIT_OPTIONS);
    }

    /**
     * Accesses FeatureFlags.FIRST_UNIT
     */
    public static Optional<String> getFirstUnit() {
        return DevOptions.getDebugField(FeatureFlags.FIRST_UNIT);
    }

    /**
     * Accesses FeatureFlags.LOG_FILTER
     */
    public static String[] getLogFilter() {
        return FeatureFlags.LOG_FILTER;
    }
}

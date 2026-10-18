package net.lugocorp.kingdom;
import java.util.Optional;

/**
 * This class contains feature flags for developmental use only
 */
class FeatureFlags {
    // Controls debug mode
    static final boolean DEBUG = false;

    // Sets a default world seed for the GameCreationView
    static final Optional<Long> WORLD_SEED = Optional.empty();

    // Sets the initial unit points for a HumanPlayer
    static final int INITIAL_UNIT_POINTS = 0;

    // Sets Unit recruitment options
    static final Optional<String[]> UNIT_OPTIONS = Optional.empty();

    // Sets your first Unit
    static final Optional<String> FIRST_UNIT = Optional.empty();

    // Enables unmodeled Units in the Game
    static final boolean INCLUDE_SECRET_UNITS = false;

    // Sets LogSys labels that should not be printed
    static final String[] LOG_FILTER = {};
}

package net.lugocorp.kingdom.ai.memory;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.player.Player;
import java.util.Optional;

/**
 * This class details what our MemoryMap "remembers" about each Tile
 */
public class MemoryCell {
    Optional<GlyphCategory> glyph = Optional.empty();
    Optional<String> building = Optional.empty();
    Optional<Player> owner = Optional.empty();
    Optional<String> unit = Optional.empty();
    boolean hasBeenSeen = false;
    int vision = 0;

    /**
     * Returns true if this MemoryCell is currently visible in the World
     */
    public boolean isVisible() {
        return this.vision > 0;
    }

    /**
     * Returns true if this MemoryCell has ever been visible in the World
     */
    public boolean wasEverVisible() {
        return this.hasBeenSeen;
    }

    /**
     * Returns this MemoryCell's Unit
     */
    public Optional<String> getUnit() {
        return this.unit;
    }

    /**
     * Returns this MemoryCell's Building
     */
    public Optional<String> getBuilding() {
        return this.building;
    }

    /**
     * Returns this MemoryCell's GlyphCategory
     */
    public Optional<GlyphCategory> getGlyph() {
        return this.glyph;
    }

    /**
     * Returns this MemoryCell's owner Player
     */
    public Optional<Player> getOwner() {
        return this.owner;
    }
}

package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.game.properties.Domain;
import net.lugocorp.kingdom.game.properties.EntityType;
import java.util.function.Supplier;

/**
 * Towers control influence over the map
 */
public class Tower extends Building {
    public final Domain domain = new Domain();

    Tower(int x, int y, Supplier<Tile> getTile) {
        super("Tower", x, y, getTile);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Tower() {
        super(null, 0, 0, null);
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.TOWER;
    }
}

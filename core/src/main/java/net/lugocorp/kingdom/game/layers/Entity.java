package net.lugocorp.kingdom.game.layers;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.properties.Leadership;
import net.lugocorp.kingdom.game.properties.Vision;
import net.lugocorp.kingdom.gameplay.combat.Combat;
import net.lugocorp.kingdom.gameplay.events.EventReceiver;
import java.util.Optional;

/**
 * This class represents any physical object represented on the in-game map
 */
public abstract class Entity extends DynamicModellable implements EventReceiver, Governable {
    public final Leadership leadership = new Leadership(this);
    public final Vision vision = new Vision();
    public final Combat combat;
    public final String name;
    public String desc = "";

    public Entity(String name, int x, int y) {
        super(x, y);
        this.name = name;
        this.combat = new Combat(this);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Entity() {
        super(0, 0);
        this.combat = null;
        this.name = null;
    }

    /**
     * Returns true if this Entity has the given EntityType
     */
    public boolean isEntityType(EntityType type) {
        return this.getEntityType() == type;
    }

    /**
     * Returns true if the given Entity is friendly to this Entity (they belong to
     * the same Player)
     */
    public boolean isFriendly(Entity e) {
        return this.getLeader().equals(e.getLeader());
    }

    /**
     * Returns this Entity'e EntityType
     */
    public abstract EntityType getEntityType();

    /** {@inheritdoc} */
    @Override
    public abstract Optional<Player> getLeader();

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.name;
    }
}

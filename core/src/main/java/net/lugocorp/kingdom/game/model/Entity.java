package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.engine.render.DynamicModellable;
import net.lugocorp.kingdom.game.combat.Combat;
import net.lugocorp.kingdom.game.events.EventReceiver;
import net.lugocorp.kingdom.game.mechanics.Visibility;
import net.lugocorp.kingdom.game.model.fields.EntityType;
import net.lugocorp.kingdom.game.model.fields.Tags;
import net.lugocorp.kingdom.game.player.Player;
import java.util.Optional;

/**
 * This class represents any physical object represented on the in-game map
 */
public abstract class Entity extends DynamicModellable implements EventReceiver {
    public final Visibility visibility = new Visibility();
    public final Tags tags = new Tags();
    public final Combat combat;
    public final String name;
    public String desc = "";

    Entity(String name, int x, int y) {
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
     * Returns this Entity'e EntityType
     */
    public abstract EntityType getEntityType();

    /**
     * Returns the Player that commands this Entity, if there is one
     */
    public abstract Optional<Player> getLeader();
}

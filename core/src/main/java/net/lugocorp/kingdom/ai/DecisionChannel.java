package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;
import java.utils.Optional;

/**
 * DecisionChannels reference a specific Unit or some Menu flow (i.e. recruiting
 * a Unit or Artifact)
 */
public class DecisionChannel {
    private final Optional<Unit> unit;
    private final DecisionClass value;

    private DecisionChannel(DecisionClass value, Optional<Unit> unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * A decision chanel for the given Unit
     */
    public static DecisionChannel unit(Unit u) {
        return new DecisionChannel(DecisionClass.UNIT, Optional.of(unit));
    }

    /**
     * A decision chanel for recruiting a Unit
     */
    public static DecisionChannel recruitUnit() {
        return new DecisionChannel(DecisionClass.RECRUIT_UNIT, Optional.empty());
    }

    /**
     * A decision chanel for recruiting an Artifact
     */
    public static DecisionChannel recruitArtifact() {
        return new DecisionChannel(DecisionClass.RECRUIT_ARTIFACT, Optional.empty());
    }

    /**
     * A decision chanel for deciding on Auction entry
     */
    public static DecisionChannel auctionEntry() {
        return new DecisionChannel(DecisionClass.AUCTION_ENTRY, Optional.empty());
    }

    /**
     * Returns true if this DecisionChannel has the given DecisionClass
     */
    public boolean is(DecisionClass dc) {
        return this.value == dc;
    }

    /**
     * Returns true if this DecisionChannel references a Unit
     */
    public boolean hasUnit() {
        return this.unit.isPresent();
    }

    /**
     * Returns this DecisionChannel's associated Unit (or throws an error if there
     * is none)
     */
    public Unit getUnit() {
        if (!this.isUnit()) {
            throw new RuntimeException("This DecisionChannel does not have an associated Unit");
        }
        return this.unit.get();
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        if (this.hasUnit()) {
            return String.format("%s (%s)", this.value.name(), this.getUnit().name);
        }
        return this.value.name();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DecisionChannel) {
            return ((DecisionChannel) o).value.equals(this.value);
        }
        return false;
    }
}

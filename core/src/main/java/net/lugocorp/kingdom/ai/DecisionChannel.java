package net.lugocorp.kingdom.ai;
import net.lugocorp.kingdom.game.model.Unit;

/**
 * DecisionChannels reference a specific Unit or some Menu flow (i.e. recruiting
 * a Unit or Artifact)
 */
public class DecisionChannel {
    private final String value;

    private DecisionChannel(String value) {
        this.value = value;
    }

    /**
     * A decision chanel for the given Unit
     */
    public static DecisionChannel unit(Unit u) {
        return new DecisionChannel(String.format("UNIT:%s", u.name));
    }

    /**
     * A decision chanel for recruiting a Unit
     */
    public static DecisionChannel recruitUnit() {
        return new DecisionChannel("RECRUIT_UNIT");
    }

    /**
     * A decision chanel for recruiting an Artifact
     */
    public static DecisionChannel recruitArtifact() {
        return new DecisionChannel("RECRUIT_ARTIFACT");
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

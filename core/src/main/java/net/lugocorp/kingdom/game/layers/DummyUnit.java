package net.lugocorp.kingdom.game.layers;
import net.lugocorp.kingdom.game.model.Unit;

/**
 * A subclass of Unit used for non-gameplay-related purposes (should never be
 * spawned)
 */
public class DummyUnit extends Unit {
    private String nameOverride = "";

    public DummyUnit() {
        super("", 0, 0);
    }

    /**
     * This function allows you to control the stratifier that Events use when
     * they're called on this Unit
     */
    public void setNameOverride(String nameOverride) {
        this.nameOverride = nameOverride;
    }

    /** {@inheritdoc} */
    @Override
    public String getStratifier() {
        return this.nameOverride;
    }
}

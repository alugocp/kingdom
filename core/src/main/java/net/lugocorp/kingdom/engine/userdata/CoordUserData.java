package net.lugocorp.kingdom.engine.userdata;
import net.lugocorp.kingdom.math.MutablePoint;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.util.function.Supplier;

/**
 * Data for Unit and Building's userData field
 */
public class CoordUserData {
    public final MutablePoint point = new MutablePoint(0, 0);
    @FieldSerializer.Optional("getLowVisibility")
    private Supplier<Boolean> getLowVisibility;

    public CoordUserData(Supplier<Boolean> getLowVisibility) {
        this.getLowVisibility = getLowVisibility;
    }

    // This is for Kryo purposes only
    public CoordUserData() {
        this.getLowVisibility = null;
    }

    /**
     * Call this when we're reloading this instance from saved game state
     */
    public void rehydrateFromKryo(Supplier<Boolean> getLowVisibility) {
        this.getLowVisibility = getLowVisibility;
    }

    /**
     * Returns true if this CoordUserData falls on a low visibility Tile
     */
    public boolean isLowVisibility() {
        return this.getLowVisibility.get();
    }
}

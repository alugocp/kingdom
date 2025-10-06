package net.lugocorp.kingdom.engine.userdata;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.function.Supplier;

/**
 * Data for Unit and Building's userData field
 */
public class CoordUserData {
    private final Supplier<Boolean> getLowVisibility;
    public Point point = new Point(0, 0);

    public CoordUserData(Supplier<Boolean> getLowVisibility) {
        this.getLowVisibility = getLowVisibility;
    }

    /**
     * Returns true if this CoordUserData falls on a low visibility Tile
     */
    public boolean isLowVisibility() {
        return this.getLowVisibility.get();
    }
}

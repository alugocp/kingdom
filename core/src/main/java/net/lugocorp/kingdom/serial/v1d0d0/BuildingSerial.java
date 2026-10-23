package net.lugocorp.kingdom.serial.v1d0d0;
import net.lugocorp.kingdom.context.BaseContext;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.serial.Serial;
import java.util.List;

/**
 * Serial<Building> implementation for v1.0.0
 */
public class BuildingSerial implements Serial<Building> {
    private final List<String> items;
    private final String label;
    private final Point point;

    public BuildingSerial(String label, Point point, List<String> items) {
        this.label = label;
        this.point = point;
        this.items = items;
    }

    /** {@inheritdoc} */
    public Building deserialize() {
        final BaseContext ctx = Context.getContext();
        final Building b = ctx.generator.building(this.label, this.point.x, this.point.y);
        b.items.ifPresent((Inventory inventory) -> {
            for (String i : this.items) {
                inventory.add(ctx.generator.item(i));
            }
        });
        return null;
    }
}

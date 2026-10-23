package net.lugocorp.kingdom.serial.v1d0d0;
import net.lugocorp.kingdom.context.BaseContext;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.properties.EntityType;
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
    private final String type;
    private final int x;
    private final int y;

    public BuildingSerial(EntityType type, String label, Point point, List<String> items) {
        this.type = type.toString();
        this.label = label;
        this.items = items;
        this.x = point.x;
        this.y = point.y;
    }

    /** {@inheritdoc} */
    public Building deserialize() {
        final BaseContext ctx = Context.getContext();
        if (this.type == EntityType.BUILDING.toString()) {
            final Building b = ctx.generator.building(this.label, this.x, this.y);
            b.items.ifPresent((Inventory inventory) -> {
                for (String i : this.items) {
                    inventory.add(ctx.generator.item(i));
                }
            });
            return b;
        }
        if (this.type == EntityType.BUILDING.toString()) {
            final Tower t = ctx.generator.tower(this.x, this.y);
            t.items.ifPresent((Inventory inventory) -> {
                for (String i : this.items) {
                    inventory.add(ctx.generator.item(i));
                }
            });
            return t;
        }
        if (this.type == EntityType.PATRON.toString()) {
            return ctx.generator.patron(this.label, this.x, this.y);
        }
        throw new RuntimeException(String.format("Could not deserialize buidling %s", this.label));
    }
}

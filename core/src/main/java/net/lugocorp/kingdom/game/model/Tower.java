package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.layers.IndependentGovernable;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Domain;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.serial.v1d0d0.BuildingSerial;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.Lambda;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Towers control influence over the map
 */
public class Tower extends Building implements IndependentGovernable {
    public static final int GOLD_COST = 10;
    private Optional<Player> leader = Optional.empty();
    public final Domain domain = new Domain();

    Tower(int x, int y) {
        super("Tower", x, y);
    }

    /** {@inheritdoc} */
    @Override
    public BuildingSerial serialize() {
        return new BuildingSerial(EntityType.TOWER, this.name, this.getPoint(),
                this.items.map((Inventory inventory) -> Lambda.map((Item i) -> i.name, inventory.getItems()))
                        .orElse(new ArrayList<String>()));
    }

    /** {@inheritdoc} */
    @Override
    public EntityType getEntityType() {
        return EntityType.TOWER;
    }

    /** {@inheritdoc} */
    @Override
    public Optional<Player> getLeader() {
        return this.leader;
    }

    /** {@inheritdoc} */
    @Override
    public void setLeader(Optional<Player> leader) {
        this.leader = leader;
    }

    /** {@inheritdoc} */
    @Override
    public void spawn(GameView view) {
        this.setLoaders(view.av);
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            this.tile = Optional.of(t);
        });
        this.handleEvent(new Events.SpawnEvent(this)).execute();
        this.getMinimapColor().ifPresent((Color c) -> view.hud.bot.minimap.refresh(view.game.world));
    }
}

package net.lugocorp.kingdom.game.model;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.layers.IndependentGovernable;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Domain;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Towers control influence over the map
 */
public class Tower extends Building implements IndependentGovernable {
    private Optional<Player> leader = Optional.empty();
    public final Domain domain = new Domain();

    Tower(int x, int y, Supplier<Tile> getTile) {
        super("Tower", x, y, getTile);
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Tower() {
        super(null, 0, 0, null);
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
        view.game.world.getTile(this.x, this.y).ifPresent((Tile t) -> {
            t.building = Optional.of(this);
            t.setGlyph(Optional.empty());
        });
        this.handleEvent(view, new Events.SpawnEvent<Building>(this)).execute();
        this.getMinimapColor().ifPresent((Color c) -> view.hud.bot.minimap.refresh(view.game.world));
    }
}

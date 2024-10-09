package net.lugocorp.kingdom;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Optional;
import net.lugocorp.kingdom.core.Events;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.Tile;
import net.lugocorp.kingdom.ui.views.LoadingGameView;
import net.lugocorp.kingdom.ui.views.View;

public class Main implements ApplicationListener {
    private Graphics graphics;
    private View view;

    /**
     * Causes the application to swap out its current View
     */
    public void navigate(View v) {
        this.view.dispose();
        this.view = v;
        v.start((View v1) -> this.navigate(v1));
    }

    /** {@inheritdoc} */
    @Override
    public void create() {
        // START MOD TESTING
        EventHandlerBundle events = new EventHandlerBundle();
        events.unit.addEventHandler("Crystal", "GenerateUnitEvent", (Game g, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(g.graphics.loaders.assets, "crystal");

            // Place a free item at this unit's location
            Optional<Tile> tile = g.world.getTile(e.blob.getX(), e.blob.getY());
            tile.ifPresent((Tile t) -> t.items.add(g.generator.item("Potion")));
        });
        events.building.addEventHandler("Mine", "GenerateBuildingEvent", (Game g, Event event) -> {
            Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
            e.blob.setModelInstance(g.graphics.loaders.assets, "mine");
        });
        events.tile.addEventHandler("Grassland", "GenerateTileEvent", (Game g, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(g.graphics.loaders.assets, "tile");
        });
        events.item.addEventHandler("Potion", "GenerateItemEvent", (Game g, Event event) -> {
            Events.GenerateItemEvent e = (Events.GenerateItemEvent) event;
            e.blob.desc = "Consume to restore a unit's health";
            e.blob.icon = Optional.of("potion");
        });
        // END MOD TESTING

        Gdx.graphics.setResizable(true);
        this.graphics = new Graphics();
        this.view = new LoadingGameView(this.graphics, events);
        this.view.start(this::navigate);
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        ScreenUtils.clear(this.view.getBackgroundColor());
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
        this.view.render();
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.view.dispose();
        this.graphics.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
        view.resize(w, h);
    }

    /** {@inheritdoc} */
    @Override
    public void pause() {
    }

    /** {@inheritdoc} */
    @Override
    public void resume() {
    }
}

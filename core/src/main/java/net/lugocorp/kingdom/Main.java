package net.lugocorp.kingdom;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;
import net.lugocorp.kingdom.core.Events;
import net.lugocorp.kingdom.events.Event;
import net.lugocorp.kingdom.events.EventHandlerBundle;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.views.LoadingGameView;
import net.lugocorp.kingdom.views.View;

public class Main implements ApplicationListener {
    private View view;

    public Main() {
        EventHandlerBundle events = new EventHandlerBundle();
        events.unit.addEventHandler("Crystal", "GenerateUnitEvent", (Game g, Event event) -> {
            Events.GenerateUnitEvent e = (Events.GenerateUnitEvent) event;
            e.blob.setModelInstance(g.assets, "crystal");
        });
        events.building.addEventHandler("Mine", "GenerateBuildingEvent", (Game g, Event event) -> {
            Events.GenerateBuildingEvent e = (Events.GenerateBuildingEvent) event;
            e.blob.setModelInstance(g.assets, "mine");
        });
        events.tile.addEventHandler("Grassland", "GenerateTileEvent", (Game g, Event event) -> {
            Events.GenerateTileEvent e = (Events.GenerateTileEvent) event;
            e.blob.setModelInstance(g.assets, "tile");
        });
        this.view = new LoadingGameView(events);
    }

    public Void navigate(View v) {
        this.view.dispose();
        this.view = v;
        v.start(this::navigate);
        return null;
    }

    @Override
    public void create() {
        this.view.start(this::navigate);
    }

    @Override
    public void render() {
        ScreenUtils.clear(this.view.getBackgroundColor());
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
        this.view.render();
    }

    @Override
    public void dispose() {
        this.view.dispose();
    }

    @Override
    public void resize(int w, int h) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}

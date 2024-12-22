package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.VolumeNode;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View allows the player to change some basic settings
 */
class SettingsView implements View {
    private final StartMenuView.Params params;
    private final Menu menu;
    private Consumer<View> navigate;

    SettingsView(StartMenuView.Params params) {
        this.params = params;
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, new ListNode()
                .add(new ButtonNode(params.av, "Back", () -> this.navigate.accept(new StartMenuView(params))))
                .add(new HeaderNode(params.av, "Settings")).add(new SpacerNode())
                .add(new HeaderNode(params.av, "Sound Effect Volume"))
                .add(new VolumeNode(params.av, params.av.settings.getSoundVolume(),
                        (Float v) -> params.av.settings.setSoundVolume(v)))
                .add(new SpacerNode()).add(new HeaderNode(params.av, "Music Volume")).add(new VolumeNode(params.av,
                        params.av.settings.getMusicVolume(), (Float v) -> params.av.settings.setMusicVolume(v))));
    }

    /** {@inheritdoc} */
    @Override
    public Color getBackgroundColor() {
        return new Color(0f, 0f, 0f, 1f);
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;
        MenuController menuController = new MenuController(() -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
    }

    /** {@inheritdoc} */
    @Override
    public void render() {
        this.menu.draw(this.params.av);
    }

    /** {@inheritdoc} */
    @Override
    public void resize(int w, int h) {
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
    }
}

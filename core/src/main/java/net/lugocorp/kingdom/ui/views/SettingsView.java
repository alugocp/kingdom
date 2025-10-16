package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.ui.nodes.ButtonNode;
import net.lugocorp.kingdom.ui.nodes.HeaderNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.nodes.OptionsNode;
import net.lugocorp.kingdom.ui.nodes.SpacerNode;
import net.lugocorp.kingdom.ui.nodes.VolumeNode;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View allows the player to change some basic settings
 */
public class SettingsView implements View {
    private final StartMenuView.Params params;
    private final Menu menu;
    private Consumer<View> navigate;

    SettingsView(StartMenuView.Params params) {
        this.params = params;
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false,
                SettingsView.addSettingsMenuNodes(params.av, new ListNode().add(
                        new ButtonNode(params.av, "Back", () -> this.navigate.accept(new StartMenuView(params))))));
    }

    /**
     * Adds the settings view MenuNodes to the given ListNode
     */
    public static ListNode addSettingsMenuNodes(AudioVideo av, ListNode node) {
        return node.add(new HeaderNode(av, "Settings")).add(new SpacerNode())
                .add(new HeaderNode(av, "Sound Effect Volume"))
                .add(new VolumeNode(av, av.settings.getSoundVolume(), (Float v) -> av.settings.setSoundVolume(v)))
                .add(new SpacerNode()).add(new HeaderNode(av, "Music Volume"))
                .add(new VolumeNode(av, av.settings.getMusicVolume(), (Float v) -> av.settings.setMusicVolume(v)))
                .add(new SpacerNode()).add(new HeaderNode(av, "Scroll Direction"))
                .add(new OptionsNode(av, (Integer index) -> av.settings.setReversedScrollDirection(index == 1))
                        .add("Regular").add("Reversed"));
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;
        MenuController menuController = new MenuController(this.params.av.settings, () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
        menuController.reset();
    }

    /** {@inheritdoc} */
    @Override
    public void render(int dt) {
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

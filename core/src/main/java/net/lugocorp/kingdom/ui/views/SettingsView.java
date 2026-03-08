package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.input.OptionsNode;
import net.lugocorp.kingdom.menu.input.VolumeNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.settings.SettingsIO;
import net.lugocorp.kingdom.ui.View;
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
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, SettingsView.addSettingsMenuNodes(params.av,
                new ListNode().add(new ButtonNode(params.av, "Back", () -> {
                    SettingsIO.write(params.av.settings);
                    this.navigate.accept(new StartMenuView(params));
                }))));
    }

    /**
     * Adds the settings view MenuNodes to the given ListNode
     */
    public static ListNode addSettingsMenuNodes(AudioVideo av, ListNode node) {
        return node.add(new HeaderNode(av, "Settings")).add(new SpacerNode()).add(new HeaderNode(av, "Volume"))
                .add(new RowNode()
                        .add(new ListNode().add(new SubheaderNode(av, "SFX"))
                                .add(new VolumeNode(av, av.settings.getSoundVolume(),
                                        (Float v) -> av.settings.setSoundVolume(v))))
                        .add(new ListNode().add(new SubheaderNode(av, "Music"))
                                .add(new VolumeNode(av, av.settings.getMusicVolume(),
                                        (Float v) -> av.settings.setMusicVolume(v)))))
                .add(new SpacerNode()).add(
                        new HeaderNode(av, "Controls"))
                .add(new RowNode()
                        .add(new ListNode().add(new SubheaderNode(av, "Scroll Direction"))
                                .add(new OptionsNode(av, av.settings.getReversedScrollDirection() ? 1 : 0,
                                        (Integer index) -> av.settings.setReversedScrollDirection(index == 1))
                                        .add("Regular").add("Reversed")))
                        .add(new ListNode().add(new SubheaderNode(av, "Auto Complete Turns"))
                                .add(new OptionsNode(av, av.settings.getAutoComplete() ? 1 : 0,
                                        (Integer index) -> av.settings.setAutoComplete(index == 1)).add("Off")
                                        .add("On"))))
                .add(new RowNode()
                        .add(new ListNode().add(new SubheaderNode(av, "In-Game Tutorial"))
                                .add(new TextNode(av, "Turn this on to see general tips at the start of each game"))
                                .add(new OptionsNode(av, av.settings.isTutorialEnabled() ? 1 : 0,
                                        (Integer index) -> av.settings.setTutorialEnabled(index == 1)).add("Off")
                                        .add("On")))
                        .add(new ListNode().add(new SubheaderNode(av, "Outline Shader"))
                                .add(new TextNode(av, "Turning this off will boost performance but will also disable tile selection via unit and building models"))
                                .add(new OptionsNode(av, av.settings.getOutlineShader() ? 1 : 0,
                                        (Integer index) -> av.settings.setOutlineShader(index == 1)).add("Off")
                                        .add("On"))));
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

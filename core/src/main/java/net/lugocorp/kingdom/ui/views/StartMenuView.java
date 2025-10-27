package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.serial.SaveLoad;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * View to select in-app options
 */
class StartMenuView implements View {
    private final Params params;
    private final Menu menu;
    private Consumer<View> navigate;

    StartMenuView(Params params) {
        this.params = params;
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, new ListNode()
                .add(new HeaderNode(params.av, "Main Menu").center()).add(new SpacerNode(false))
                .add(new ButtonNode(params.av, "New game", () -> this.navigate.accept(new GameCreationView(params))))
                .add(new SpacerNode(false).half())
                // .add(new ButtonNode(params.av, "Load game", () -> this.navigate.accept(new
                // LoadGameView(params))))
                .add(new ButtonNode(params.av, "Settings", () -> this.navigate.accept(new SettingsView(params))))
                .add(new SpacerNode(false).half())
                .add(new ButtonNode(params.av, "Credits", () -> this.navigate.accept(new CreditsView(params))))
                .add(new SpacerNode(false).half())
                .add(new ButtonNode(params.av, "Mods", () -> this.navigate.accept(new ActiveModsView(params)))));
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;
        MenuController menuController = new MenuController(this.params.av.settings, () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
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

    /**
     * This nested class stores all the necessary parameters for StartMenuView and
     * any views that can navigate to it
     */
    static class Params {
        final AllEventHandlers events;
        final List<GameMod> mods;
        final SaveLoad serial;
        final AudioVideo av;

        Params(AudioVideo av, AllEventHandlers events, SaveLoad serial, List<GameMod> mods) {
            this.serial = serial;
            this.events = events;
            this.mods = mods;
            this.av = av;
        }
    }
}

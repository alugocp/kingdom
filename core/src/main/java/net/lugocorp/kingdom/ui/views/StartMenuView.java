package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.Main;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.Background;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.util.Optional;

/**
 * View to select in-app options
 */
class StartMenuView implements View {
    private final Background background;
    private final Menu menu;

    StartMenuView() {
        final AudioVideo av = Context.getContext().av;
        this.background = new Background(av);
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, new ListNode()
                .add(new HeaderNode(av, "Legends of T'ahn").center()).add(new SpacerNode(false))
                .add(new ButtonNode(av, "New game", () -> Context.getContext().navigate.accept(new GameCreationView())))
                .add(new SpacerNode(false).half())
                .add(new ButtonNode(av, "Load game", () -> Context.getContext().navigate.accept(new LoadGameView())))
                .add(new SpacerNode(false).half())
                .add(new ButtonNode(av, "Settings", () -> Context.getContext().navigate.accept(new SettingsView())))
                .add(new SpacerNode(false).half())
                .add(new ButtonNode(av, "Credits", () -> Context.getContext().navigate.accept(new CreditsView())))
                // .add(new SpacerNode(false).half())
                // .add(new ButtonNode(params.av, "Content", () ->
                // Context.getContext().navigate.accept(new
                // ActiveModsView(params))))
                .add(new SpacerNode(false))
                .add(new TextNode(av, String.format("Version %s", Main.VERSION.toString())).center())).noBackground();
    }

    /** {@inheritdoc} */
    @Override
    public void start() {
        final AudioVideo av = Context.getContext().av;
        final MenuController menuController = new MenuController(av.settings, () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
        av.loaders.music.play("sfx/fantasy-adventure-quest");
    }

    /** {@inheritdoc} */
    @Override
    public void render(int dt) {
        this.background.render();
        this.menu.draw(Context.getContext().av);
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

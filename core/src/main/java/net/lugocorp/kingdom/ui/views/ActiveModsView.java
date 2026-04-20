package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.mods.ModProfile;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View walks the player through Game setup and World generation
 */
class ActiveModsView implements View {
    private final StartMenuView.Params params;
    private Consumer<View> navigate;
    private Menu menu;

    ActiveModsView(StartMenuView.Params params) {
        ListNode node = new ListNode()
                .add(new ButtonNode(params.av, "Back", () -> this.navigate.accept(new StartMenuView(params))))
                .add(new HeaderNode(params.av, "Content"));
        for (GameMod mod : params.mods) {
            ModProfile profile = mod.getProfile();
            node.add(new SpacerNode()).add(new HeaderNode(params.av, profile.name))
                    .add(new TextNode(params.av, profile.description));
            if (profile.credits.length > 0) {
                node.add(new TextNode(params.av, "Credits:"));
                for (String name : profile.credits) {
                    node.add(new TextNode(params.av, String.format("  • %s", name)));
                }
            }
        }
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, node);
        this.params = params;
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

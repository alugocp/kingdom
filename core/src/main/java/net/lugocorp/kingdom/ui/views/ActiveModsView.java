package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.context.BaseContext;
import net.lugocorp.kingdom.context.Context;
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
import net.lugocorp.kingdom.ui.Background;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.util.Optional;

/**
 * This View walks the player through Game setup and World generation
 */
class ActiveModsView implements View {
    private final Background background;
    private Menu menu;

    ActiveModsView() {
        final BaseContext ctx = Context.getContext();
        ListNode node = new ListNode()
                .add(new ButtonNode(ctx.av, "Back", () -> Context.getContext().navigate.accept(new StartMenuView())))
                .add(new HeaderNode(ctx.av, "Content"));
        for (GameMod mod : ctx.mods) {
            ModProfile profile = mod.getProfile();
            node.add(new SpacerNode()).add(new HeaderNode(ctx.av, profile.name))
                    .add(new TextNode(ctx.av, profile.description));
            if (profile.credits.length > 0) {
                node.add(new TextNode(ctx.av, "Credits:"));
                for (String name : profile.credits) {
                    node.add(new TextNode(ctx.av, String.format("  • %s", name)));
                }
            }
        }
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, node).noBackground();
        this.background = new Background(ctx.av);
    }

    /** {@inheritdoc} */
    @Override
    public void start() {
        MenuController menuController = new MenuController(Context.getContext().av.settings,
                () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
        menuController.reset();
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

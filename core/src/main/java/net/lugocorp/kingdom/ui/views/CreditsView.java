package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View walks the player through Game setup and World generation
 */
class CreditsView implements View {
    private final StartMenuView.Params params;
    private final Menu menu;
    private Consumer<View> navigate;

    CreditsView(StartMenuView.Params params) {
        this.params = params;
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false,
                new ListNode()
                        .add(new ButtonNode(params.av, "Back", () -> this.navigate.accept(new StartMenuView(params))))
                        .add(new HeaderNode(params.av, "Game Design")).add(new TextNode(params.av, "Alex Lugo"))
                        .add(new SpacerNode()).add(new HeaderNode(params.av, "Programming"))
                        .add(new TextNode(params.av, "Alex Lugo")).add(new SpacerNode())
                        .add(new HeaderNode(params.av, "3D Modelling")).add(new TextNode(params.av, "Alex Lugo"))
                        .add(new SpacerNode()).add(new HeaderNode(params.av, "Character Design"))
                        .add(new TextNode(params.av, "Alex Lugo")));
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

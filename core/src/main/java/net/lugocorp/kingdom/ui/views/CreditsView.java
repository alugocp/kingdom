package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View shows players who contributed to the game
 */
class CreditsView implements View {
    private final StartMenuView.Params params;
    private final Menu menu;
    private Consumer<View> navigate;

    CreditsView(StartMenuView.Params params) {
        this.params = params;
        this.menu = new Menu((Coords.SIZE.x / 2) - 600, 0, 1200, false,
                new ListNode()
                        .add(new RowNode().add(new ButtonNode(params.av, "Back",
                                () -> this.navigate.accept(new StartMenuView(params)))).add(
                                        new HeaderNode(params.av, "Credits").center()))
                        .add(new SpacerNode())
                        .add(new RowNode()
                                .add(new ListNode().add(new SubheaderNode(params.av, "Game Design"))
                                        .add(new TextNode(params.av, "Alex Lugo")))
                                .add(new ListNode().add(new SubheaderNode(params.av, "Programming"))
                                        .add(new TextNode(params.av, "Alex Lugo")))
                                .add(new ListNode().add(new SubheaderNode(params.av, "3D Modelling"))
                                        .add(new TextNode(params.av, "Alex Lugo")))
                                .add(new ListNode().add(new SubheaderNode(params.av, "Character Design"))
                                        .add(new TextNode(params.av, "Alex Lugo"))))
                        .add(new SpacerNode()).add(new SubheaderNode(params.av, "Game Testers"))
                        .add(new RowNode()
                                .add(new ListNode().add(new TextNode(params.av, "IT"))
                                        .add(new TextNode(params.av, "JAG")).add(new TextNode(params.av, "Elliott S"))
                                        .add(new TextNode(params.av, "Ken Possible")))
                                .add(new ListNode().add(new TextNode(params.av, "Swol Stefan"))
                                        .add(new TextNode(params.av, "Rhys"))
                                        .add(new TextNode(params.av, "Alec Lisy")))));
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

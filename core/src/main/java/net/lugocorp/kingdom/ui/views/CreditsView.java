package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.engine.AudioVideo;
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
import net.lugocorp.kingdom.ui.Background;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.util.Optional;

/**
 * This View shows players who contributed to the game
 */
class CreditsView implements View {
    private final Background background;
    private final Menu menu;

    CreditsView() {
        final AudioVideo av = Context.getContext().av;
        this.background = new Background(av);
        this.menu = new Menu(
                (Coords.SIZE.x
                        / 2) - 300,
                0, 600, false,
                new ListNode()
                        .add(new ButtonNode(av, "Back",
                                () -> Context.getContext().navigate.accept(new StartMenuView())))
                        .add(new HeaderNode(av, "Credits").center()).add(new SpacerNode())
                        .add(new RowNode().add(new ListNode().add(new SubheaderNode(av, "Game Design"))
                                .add(new TextNode(av, "Alex Lugo")).add(new TextNode(av, "Mr. X"))).add(
                                        new ListNode().add(new SubheaderNode(av, "Programming"))
                                                .add(new TextNode(av, "Alex Lugo"))))
                        .add(new SpacerNode()).add(
                                new RowNode()
                                        .add(new ListNode().add(new SubheaderNode(av, "3D Modelling"))
                                                .add(new TextNode(av, "Alex Lugo")))
                                        .add(new ListNode()
                                                .add(new SubheaderNode(av, "Character Design")).add(
                                                        new TextNode(av, "Alex Lugo"))))
                        .add(new SpacerNode()).add(new SubheaderNode(av, "Game Testers"))
                        .add(new RowNode()
                                .add(new ListNode().add(new TextNode(av, "IT")).add(new TextNode(av, "JAG"))
                                        .add(new TextNode(av, "Elliott S")).add(new TextNode(av, "Ken Possible"))
                                        .add(new TextNode(av, "Rebecca Oss")).add(new TextNode(av, "Mr. X")))
                                .add(new ListNode().add(new TextNode(av, "Swol Stefan")).add(new TextNode(av, "Rhys"))
                                        .add(new TextNode(av, "Alec Lisy")).add(new TextNode(av, "Sam Unger")))))
                .noBackground();
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

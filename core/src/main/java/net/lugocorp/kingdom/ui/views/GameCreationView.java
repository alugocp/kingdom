package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.game.world.WorldGenerator;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.FateNode;
import net.lugocorp.kingdom.ui.menu.FateViewNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View walks the player through Game setup and World generation
 */
public class GameCreationView implements View {
    private final Game game;
    private final GameView view;
    private final Menu fateSelection;
    private Consumer<View> navigate;
    private Menu menu;

    GameCreationView(Graphics graphics, AllEventHandlers events) {
        // Initialize Game and GameView state for world generation logic
        this.game = new Game(events, new World(10, 5));
        this.view = new GameView(this.game, graphics);
        this.game.generator = new Generator(this.view);
        this.game.mechanics.init(this.game);

        // Initialize GameCreationView UI components
        this.fateSelection = this.getFateSelectionMenu(this.view);
        this.menu = this.fateSelection;
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
        this.menu.draw(this.view.graphics);
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
     * Returns a Menu to that allows the player to view and select a Fate
     */
    private Menu getFateSelectionMenu(GameView view) {
        view.game.human.fate = view.game.mechanics.fates.getFirstFate();
        ListNode options = new ListNode();
        FateViewNode display = new FateViewNode(view.graphics, view.game.mechanics.fates.getFirstFate());
        ListNode root = new ListNode().add(new RowNode()
                .add(new ButtonNode(view.graphics, "Back",
                        () -> this.navigate.accept(new StartMenuView(this.view.graphics, this.game.events))))
                .add(new ButtonNode(view.graphics, "Choose", () -> {
                    new WorldGenerator().generateWorld(this.view);
                    this.navigate.accept(this.view);
                })).add(new TextNode(view.graphics, "Select a fate"))).add(display).add(options);

        // Set up RowNodes of FateNodes
        int a = 0;
        final int columns = (int) Math.floor(Coords.SIZE.x / FateNode.WIDTH) - 1;
        List<Fate> fates = view.game.mechanics.fates.getFates();
        while (a < fates.size()) {
            RowNode row = new RowNode().setColumns(columns);
            for (int b = 0; b < columns && a < fates.size();) {
                final Fate fate = fates.get(a);
                row.add(new FateNode(view.graphics, fate, () -> {
                    view.game.human.fate = fate;
                    display.setFate(view.graphics, fate);
                }));
                a++;
                b++;
            }
            options.add(new SpacerNode());
            options.add(row);
        }
        return new Menu(0, 0, Coords.SIZE.x, true, root);
    }
}

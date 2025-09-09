package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.world.WorldGenOptions;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.FateNode;
import net.lugocorp.kingdom.ui.menu.FateViewNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.OptionsNode;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.TextEntryNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import java.time.OffsetTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This View walks the player through Game setup and World generation
 */
class GameCreationView implements View {
    private static final Random rand = new Random();
    private final WorldGenOptions worldGenOpts = new WorldGenOptions(GameCreationView.getRandomSeed());
    private final StartMenuView.Params params;
    private final Menu worldSelection;
    private final Menu fateSelection;
    private final GameView view;
    private final Game game;
    private Consumer<View> navigate;
    private Menu menu;

    GameCreationView(StartMenuView.Params params) {
        this.params = params;

        // Initialize Game and GameView state for world generation logic
        this.game = new Game(params.events, OffsetTime.now());
        this.view = new GameView(params, this.game);
        this.game.generator = new Generator(this.view);
        this.game.mechanics.init(this.game);

        // Initialize GameCreationView UI components
        this.worldSelection = this.getWorldSelectionMenu(this.view);
        this.fateSelection = this.getFateSelectionMenu(this.view);
        this.menu = this.worldSelection;
    }

    /**
     * Returns a random world seed to place in the UI
     */
    private static long getRandomSeed() {
        return GameCreationView.rand.nextInt(1000000);
    }

    /**
     * Internal syntactic sugar
     */
    private void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Internal syntactic sugar
     */
    private void setWorldSeed(String worldSeed) {
        this.worldGenOpts.seed = Long.parseLong(worldSeed.length() > 0 ? worldSeed : "0");
    }

    /**
     * Internal syntactic sugar
     */
    private void setWorldSize(int worldSize) {
        this.worldGenOpts.size = WorldGenOptions.WorldSize.fromIndex(worldSize);
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
        this.menu.draw(this.view.av);
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
     * Exits this menu and starts loading the new Game
     */
    private void startGame() {
        this.navigate.accept(new GenerateWorldView(this.view, this.worldGenOpts));
    }

    /**
     * Returns a Menu to that allows the player to view and select a World
     * generation algorithm
     */
    private Menu getWorldSelectionMenu(GameView view) {
        final OptionsNode worldSizeOptions = new OptionsNode(view.av, (Integer i) -> this.setWorldSize(i.intValue()));
        for (WorldGenOptions.WorldSize size : WorldGenOptions.WorldSize.values()) {
            worldSizeOptions.add(size.label);
        }
        return new Menu(0, 0, Coords.SIZE.x, true,
                new ListNode()
                        .add(new RowNode()
                                .add(new ButtonNode(view.av, "Back",
                                        () -> this.navigate.accept(new StartMenuView(this.params))).center())
                                .add(new TextNode(view.av, "World Generation").center())
                                .add(new ButtonNode(view.av, "Next", () -> this.setMenu(this.fateSelection)).center()))
                        .add(new SpacerNode())
                        .add(new RowNode().add(new TextNode(view.av, "World Seed"))
                                .add(new TextEntryNode(view.av, Long.toString(this.worldGenOpts.seed),
                                        (String x) -> this.setWorldSeed(x)).setNumbersOnly(true)))
                        .add(new SpacerNode(false))
                        .add(new RowNode().add(new TextNode(view.av, "Map Size")).add(worldSizeOptions)));
    }

    /**
     * Returns a Menu to that allows the player to view and select a Fate
     */
    private Menu getFateSelectionMenu(GameView view) {
        view.game.human.setFate(view.game.mechanics.fates.getFirstFate());
        ListNode options = new ListNode();
        FateViewNode display = new FateViewNode(view.av, view.game.mechanics.fates.getFirstFate());
        ListNode root = new ListNode()
                .add(new RowNode()
                        .add(new ButtonNode(view.av, "Back", () -> this.setMenu(this.worldSelection)).center())
                        .add(new TextNode(view.av, "Select a Fate").center())
                        .add(new ButtonNode(view.av, "Start Game", () -> this.startGame()).center()))
                .add(display).add(options);

        // Set up RowNodes of FateNodes
        int a = 0;
        final int columns = (int) Math.floor(Coords.SIZE.x / FateNode.WIDTH) - 1;
        List<Fate> fates = view.game.mechanics.fates.getFates();
        while (a < fates.size()) {
            RowNode row = new RowNode().setColumns(columns);
            for (int b = 0; b < columns && a < fates.size();) {
                final Fate fate = fates.get(a);
                row.add(new FateNode(view.av, fate, () -> {
                    view.av.loaders.sounds.play("sfx/card-flick");
                    view.game.human.setFate(fate);
                    display.setFate(view.av, fate);
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

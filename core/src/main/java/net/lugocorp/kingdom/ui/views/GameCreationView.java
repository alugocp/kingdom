package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.world.WorldGenOptions;
import net.lugocorp.kingdom.game.world.WorldSize;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.ui.nodes.ButtonNode;
import net.lugocorp.kingdom.ui.nodes.FateNode;
import net.lugocorp.kingdom.ui.nodes.FateViewNode;
import net.lugocorp.kingdom.ui.nodes.HeaderNode;
import net.lugocorp.kingdom.ui.nodes.ListNode;
import net.lugocorp.kingdom.ui.nodes.MenuMenuNode;
import net.lugocorp.kingdom.ui.nodes.RowNode;
import net.lugocorp.kingdom.ui.nodes.SpacerNode;
import net.lugocorp.kingdom.ui.nodes.SubheaderNode;
import net.lugocorp.kingdom.ui.nodes.TextEntryNode;
import net.lugocorp.kingdom.ui.nodes.TextNode;
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
    private MenuController menuController = null;
    private Consumer<View> navigate;
    private Menu menu;

    GameCreationView(StartMenuView.Params params) {
        this.params = params;

        // Initialize Game and GameView state for world generation logic
        this.game = new Game(params.events, OffsetTime.now());
        this.view = new GameView(params, this.game);
        this.game.generator = new Generator(this.view);
        this.game.mechanics.init(this.game);
        this.setWorldSize(0);

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
        this.menuController.reset();
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
        this.worldGenOpts.size = WorldSize.fromIndex(worldSize);
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
        this.menuController = new MenuController(this.params.av.settings, () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
        this.menuController.reset();
    }

    /** {@inheritdoc} */
    @Override
    public void render(int dt) {
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
        // TODO uncomment when we're ready for different WorldSizes
        /*
         * final OptionsNode worldSizeOptions = new OptionsNode(view.av, (Integer i) ->
         * this.setWorldSize(i.intValue())); for (WorldSize size : WorldSize.values()) {
         * worldSizeOptions.add(size.label); }
         */
        return new Menu(0, 0, Coords.SIZE.x, true,
                new ListNode()
                        .add(new RowNode()
                                .add(new ButtonNode(view.av, "Back",
                                        () -> this.navigate.accept(new StartMenuView(this.params))))
                                .add(new HeaderNode(view.av, "World Generation").center())
                                .add(new ButtonNode(view.av, "Next", () -> this.setMenu(this.fateSelection))))
                        .add(new SpacerNode())
                        .add(new RowNode().add(new SubheaderNode(view.av, "World Seed"))
                                .add(new TextEntryNode(view.av, Long.toString(this.worldGenOpts.seed),
                                        (String x) -> this.setWorldSeed(x)).setNumbersOnly(true)))
                        .add(new SpacerNode(false).half()).add(new TextNode(view.av,
                                "The world seed determines random values in world generation. Write down previous world seeds from games you enjoyed to replay on the exact same map."))
        /*
         * .add(new SpacerNode(false)) .add( new RowNode().add(new TextNode(view.av,
         * "Map Size")) .add(worldSizeOptions) )
         */
        );
    }

    /**
     * Returns a Menu to that allows the player to view and select a Fate
     */
    private Menu getFateSelectionMenu(GameView view) {
        view.game.human.setFate(view.game.mechanics.fates.getFirstFate());
        final ListNode options = new ListNode();
        final MenuMenuNode wrapper = new MenuMenuNode(options);
        final FateViewNode display = new FateViewNode(view.av, view.game.mechanics.fates.getFirstFate(), true);
        final ListNode root = new ListNode()
                .add(new RowNode().add(new ButtonNode(view.av, "Back", () -> this.setMenu(this.worldSelection)))
                        .add(new HeaderNode(view.av, "Select a Fate").center())
                        .add(new ButtonNode(view.av, "Start Game", () -> this.startGame())))
                .add(new SpacerNode()).add(
                        new RowNode().add(
                                new ListNode().add(new SubheaderNode(view.av, "Your Selected Fate")).add(display)).add(
                                        new ListNode()
                                                .add(new SubheaderNode(view.av,
                                                        String.format("%d Fates",
                                                                view.game.mechanics.fates.getFates().size())))
                                                .add(wrapper)));

        // Set up RowNodes of FateNodes
        int a = 0;
        final int columns = (int) Math.floor((Coords.SIZE.x / 2) / FateNode.WIDTH) - 1;
        final List<Fate> fates = view.game.mechanics.fates.getFates();
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
            options.add(row);
            options.add(new SpacerNode(false));
        }
        return new Menu(0, 0, Coords.SIZE.x, true, root);
    }
}

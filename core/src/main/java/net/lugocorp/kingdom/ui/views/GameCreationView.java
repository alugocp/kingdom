package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.assets.FontService;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.world.WorldGenOptions;
import net.lugocorp.kingdom.game.world.WorldSize;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.game.FateNode;
import net.lugocorp.kingdom.menu.game.FateViewNode;
import net.lugocorp.kingdom.menu.input.OptionsNode;
import net.lugocorp.kingdom.menu.input.TextEntryNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.MenuMenuNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.utils.code.Tuple;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View walks the player through Game setup and World generation
 */
class GameCreationView implements View {
    private static final int MAX_PLAYERS = 6;
    private final WorldGenOptions worldGenOpts = new WorldGenOptions(GameCreationView.getRandomSeed());
    private final StartMenuView.Params params;
    private final Menu worldSelection;
    private final Menu fateSelection;
    private final Menu playerSelection;
    private final GameView view;
    private MenuController menuController = null;
    private Consumer<View> navigate;
    private Menu menu;

    GameCreationView(StartMenuView.Params params) {
        this.params = params;

        // Initialize Game and GameView state for world generation logic
        final Game game = new Game(params.events, OffsetTime.now());
        this.view = new GameView(params, game);
        game.generator = new Generator(this.view);
        game.mechanics.init(game);
        this.setWorldSize(0);

        // Initialize GameCreationView UI components
        this.worldSelection = this.getWorldSelectionMenu(this.view);
        this.fateSelection = this.getFateSelectionMenu(this.view);
        this.playerSelection = this.getPlayerSelectionMenu(this.view);
        this.menu = this.worldSelection;
    }

    /**
     * Returns a random world seed to place in the UI
     */
    private static long getRandomSeed() {
        return (int) Math.floor(Math.random() * 1000000);
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
        final List<Fate> fates = view.game.mechanics.fates.getFates(view.game);
        final ListNode options = new ListNode();
        final MenuMenuNode wrapper = new MenuMenuNode(options);
        final FateViewNode display = new FateViewNode(view.av, fates.get(0), true);
        final ListNode root = new ListNode()
                .add(new RowNode().add(new ButtonNode(view.av, "Back", () -> this.setMenu(this.worldSelection)))
                        .add(new HeaderNode(view.av, "Select a Fate").center())
                        .add(new ButtonNode(view.av, "Next", () -> this.setMenu(this.playerSelection))))
                .add(new SpacerNode())
                .add(new RowNode()
                        .add(new ListNode().add(new SubheaderNode(view.av, "Your Selected Fate")).add(display))
                        .add(new ListNode().add(new SubheaderNode(view.av, String.format("%d Fates", fates.size())))
                                .add(wrapper)));

        // Set up RowNodes of FateNodes
        int a = 0;
        final int columns = (int) Math.floor((Coords.SIZE.x / 2) / FateNode.WIDTH) - 1;
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
        view.game.human.setFate(fates.get(0));
        return new Menu(0, 0, Coords.SIZE.x, true, root);
    }

    /**
     * Returns a Menu to that allows the Player to customize their opponents
     */
    private Menu getPlayerSelectionMenu(GameView view) {
        final List<CompPlayer> comps = new ArrayList();
        final Tuple<CompPlayer, MenuNode> firstComp = this.addPlayerCustomizationNode(view, 1);
        final ListNode nodes = new ListNode().add(new RowNode().add(new SubheaderNode(view.av, view.game.human.name))
                .add(new TextNode(view.av, view.game.human.getFate().name))).add(firstComp.b);
        final ListNode root = new ListNode();
        final Menu menu = new Menu(0, 0, Coords.SIZE.x, true, root);
        root.add(new RowNode().add(new ButtonNode(view.av, "Back", () -> this.setMenu(this.fateSelection)))
                .add(new HeaderNode(view.av, "Customize Players").center())
                .add(new ButtonNode(view.av, "Start Game", () -> {
                    view.game.comps.addAll(comps);
                    this.startGame();
                }))).add(new SpacerNode()).add(new RowNode().add(new ButtonNode(view.av, "Drop Player", () -> {
                    comps.remove(comps.size() - 1);
                    nodes.pop();
                    menu.pack();
                }).setEnabledCriteria(() -> comps.size() > 1)).add(new ButtonNode(view.av, "Add Player", () -> {
                    final Tuple<CompPlayer, MenuNode> results = this.addPlayerCustomizationNode(view, comps.size() + 1);
                    comps.add(results.a);
                    nodes.add(results.b);
                    menu.pack();
                }).setEnabledCriteria(() -> comps.size() + 1 < GameCreationView.MAX_PLAYERS)))
                .add(new MenuMenuNode(nodes));
        comps.add(firstComp.a);
        menu.pack();
        return menu;
    }

    /**
     * Returns a MenuNode that allows you to customize the given Player
     */
    private Tuple<CompPlayer, MenuNode> addPlayerCustomizationNode(GameView view, int number) {
        final List<Fate> fates = view.game.mechanics.fates.getFates(view.game);
        final CompPlayer comp = new CompPlayer(view, number, view.game.world.getSize(),
                view.game.mechanics.fates.chooseRandomFate(view.game), view.game.colorPool.getFromPool());
        final OptionsNode options = new OptionsNode(view.av,
                (Integer index) -> comp.setFate(
                        index == 0 ? view.game.mechanics.fates.chooseRandomFate(view.game) : fates.get(index - 1)))
                .add("Random");
        final ListNode root = new ListNode().add(new SpacerNode())
                .add(new RowNode().add(new SubheaderNode(view.av, comp.name) {
                    /** {@inheritdoc} */
                    @Override
                    protected BitmapFont getFont() {
                        return this.av.fonts.getFont(FontService.BOLD, 22, comp.color);
                    }
                }).add(options));
        for (Fate fate : fates) {
            options.add(fate.name);
        }
        return new Tuple<CompPlayer, MenuNode>(comp, root);
    }
}

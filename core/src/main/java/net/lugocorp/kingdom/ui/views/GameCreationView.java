package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Fate;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.world.WorldGenOptions;
import net.lugocorp.kingdom.game.world.WorldSize;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.game.FateNode;
import net.lugocorp.kingdom.menu.game.FateViewNode;
import net.lugocorp.kingdom.menu.icon.HelperNode;
import net.lugocorp.kingdom.menu.input.DropdownNode;
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
import net.lugocorp.kingdom.utils.Tuple;
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
    private final List<CompPlayer> comps = new ArrayList();
    private final StartMenuView.Params params;
    private final TextNode humanFateNameNode;
    private final ButtonNode startButton;
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
        game.init(this.view);
        this.setWorldSize(0);

        // Initialize GameCreationView UI components
        this.startButton = new ButtonNode(this.view.av, "Start Game", () -> {
            this.view.game.comps.addAll(this.comps);
            this.disableStartbutton();
            this.startGame();
        });
        this.humanFateNameNode = new TextNode(this.view.av, "");
        this.worldSelection = this.getWorldSelectionMenu();
        this.fateSelection = this.getFateSelectionMenu();
        this.playerSelection = this.getPlayerSelectionMenu();
        this.menu = this.worldSelection;
    }

    /**
     * Disables the start button after clicking it
     */
    private void disableStartbutton() {
        this.startButton.enable(false);
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
        this.menuController = new MenuController(this.view.av.settings, () -> Optional.of(this.menu));
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
        this.view.game.mechanics.turns.getTurn().setPlayers(this.view.game.getAllPlayers());
        this.navigate.accept(new GenerateWorldView(this.view, this.worldGenOpts));
    }

    /**
     * Returns a Menu to that allows the player to view and select a World
     * generation algorithm
     */
    private Menu getWorldSelectionMenu() {
        final OptionsNode worldSizeOptions = new OptionsNode(this.view.av, 0,
                (Integer i) -> this.setWorldSize(i.intValue()));
        for (WorldSize size : WorldSize.values()) {
            worldSizeOptions.add(String.format("%s (%d x %d tiles)", size.label, size.w, size.h));
        }
        return new Menu(0, 0, Coords.SIZE.x, true, new ListNode()
                .add(new RowNode()
                        .addRatio(20,
                                new ButtonNode(this.view.av, "Back",
                                        () -> this.navigate.accept(new StartMenuView(this.params))))
                        .add(new HeaderNode(this.view.av, "World Generation").center())
                        .addRatio(20, new ButtonNode(this.view.av, "Next", () -> this.setMenu(this.fateSelection))))
                .add(new SpacerNode())
                .add(new RowNode().addRatio(25, new SubheaderNode(this.view.av, "World Seed"))
                        .add(new TextEntryNode(this.view.av, Long.toString(this.worldGenOpts.seed),
                                (String x) -> this.setWorldSeed(x)).setNumbersOnly(true)))
                .add(new SpacerNode(false).half())
                .add(new TextNode(this.view.av,
                        "The world seed determines random values in world generation. Write down previous world seeds from games you enjoyed to replay on the exact same map."))
                .add(new SpacerNode(false))
                .add(new RowNode().addRatio(25, new SubheaderNode(this.view.av, "Map Size")).add(worldSizeOptions))
                .add(new SpacerNode(false).half()).add(new TextNode(this.view.av,
                        "There are multiple options for world size that you can generate. These options will determine the number of tiles present in the game world.")));
    }

    /**
     * Returns a Menu to that allows the player to view and select a Fate
     */
    private Menu getFateSelectionMenu() {
        final List<Fate> fates = this.view.game.mechanics.fates.getFates(this.view.game);
        final ListNode options = new ListNode();
        final MenuMenuNode wrapper = new MenuMenuNode(options);
        final FateViewNode display = new FateViewNode(this.view.av, fates.get(0), true);
        final ListNode root = new ListNode()
                .add(new RowNode()
                        .addRatio(20, new ButtonNode(this.view.av, "Back", () -> this.setMenu(this.worldSelection)))
                        .add(new HeaderNode(this.view.av, "Select a Fate").center())
                        .addRatio(20, new ButtonNode(this.view.av, "Next", () -> this.setMenu(this.playerSelection))))
                .add(new SpacerNode()).add(new RowNode()
                        .addRatio(40,
                                new ListNode().add(new SubheaderNode(this.view.av, "Your Selected Fate")).add(display)
                                        .add(new HelperNode(this.view.av,
                                                "Glyphs are categories of related units that have similar abilities")))
                        .addRatio(60,
                                new ListNode()
                                        .add(new SubheaderNode(this.view.av, String.format("%d Fates", fates.size())))
                                        .add(wrapper)));

        // Set up RowNodes of FateNodes
        int a = 0;
        final int columns = (int) Math.floor((Coords.SIZE.x * 0.6f) / FateNode.WIDTH) - 1;
        while (a < fates.size()) {
            RowNode row = new RowNode().setColumns(columns);
            for (int b = 0; b < columns && a < fates.size();) {
                final Fate fate = fates.get(a);
                row.add(new FateNode(this.view.av, fate, () -> {
                    this.humanFateNameNode.setText(fate.name);
                    this.view.av.loaders.sounds.play("sfx/card-flick");
                    this.view.game.human.setFate(fate);
                    display.setFate(this.view.av, fate);
                }));
                a++;
                b++;
            }
            options.add(row);
            options.add(new SpacerNode(false));
        }
        this.humanFateNameNode.setText(fates.get(0).name);
        this.view.game.human.setFate(fates.get(0));
        return new Menu(0, 0, Coords.SIZE.x, true, root);
    }

    /**
     * Returns a Menu to that allows the Player to customize their opponents
     */
    private Menu getPlayerSelectionMenu() {
        this.comps.clear();
        final Tuple<CompPlayer, MenuNode> firstComp = this.addPlayerCustomizationNode(1);
        final DropdownNode colorOptions = new DropdownNode(this.view.av,
                this.view.game.colorPool.getIndex(this.view.game.human.getColor()),
                (Integer index) -> this.view.game.human.setColor(this.view.game.colorPool,
                        this.view.game.colorPool.getFromPool(index)),
                (Integer index) -> this.view.game.colorPool.isAvailable(index));
        final ListNode nodes = new ListNode()
                .add(new RowNode().add(new SubheaderNode(this.view.av, "Player"))
                        .add(new SubheaderNode(this.view.av, "Color")).add(new SubheaderNode(this.view.av, "Fate")))
                .add(new SpacerNode(true).half())
                .add(new RowNode().add(new SubheaderNode(this.view.av, this.view.game.human.name)).add(colorOptions)
                        .add(this.humanFateNameNode))
                .add(firstComp.b);
        final ListNode root = new ListNode();
        final Menu menu = new Menu(0, 0, Coords.SIZE.x, true, root);
        root.add(
                new RowNode().addRatio(20, new ButtonNode(this.view.av, "Back", () -> this.setMenu(this.fateSelection)))
                        .add(new HeaderNode(this.view.av, "Customize Players").center()).addRatio(20, this.startButton))
                .add(new SpacerNode()).add(new RowNode().add(new ButtonNode(this.view.av, "Drop Player", () -> {
                    this.comps.remove(this.comps.size() - 1);
                    nodes.pop();
                    menu.pack();
                }).setEnabledCriteria(() -> this.comps.size() > 1))
                        .add(new ButtonNode(this.view.av, "Add Player", () -> {
                            final Tuple<CompPlayer, MenuNode> results = this
                                    .addPlayerCustomizationNode(this.comps.size() + 1);
                            this.comps.add(results.a);
                            nodes.add(results.b);
                            menu.pack();
                        }).setEnabledCriteria(() -> this.comps.size() + 1 < GameCreationView.MAX_PLAYERS)))
                .add(new MenuMenuNode(nodes));
        this.comps.add(firstComp.a);
        for (String color : this.view.game.colorPool.getColorNames()) {
            colorOptions.add(color);
        }
        menu.pack();
        return menu;
    }

    /**
     * Returns a MenuNode that allows you to customize the given Player
     */
    private Tuple<CompPlayer, MenuNode> addPlayerCustomizationNode(int number) {
        final List<Fate> fates = this.view.game.mechanics.fates.getFates(this.view.game);
        final CompPlayer comp = new CompPlayer(view, number,
                this.view.game.mechanics.fates.chooseRandomFate(this.view.game),
                this.view.game.colorPool.getFromPool());
        final DropdownNode fateOptions = new DropdownNode(this.view.av, 0, (Integer index) -> comp.setFate(
                index == 0 ? this.view.game.mechanics.fates.chooseRandomFate(this.view.game) : fates.get(index - 1)),
                (Integer index) -> true)
                .add("Random", new TextNode(this.view.av, "Chooses a random fate for this computer player"));
        final DropdownNode colorOptions = new DropdownNode(this.view.av,
                this.view.game.colorPool.getIndex(comp.getColor()),
                (Integer index) -> comp.setColor(this.view.game.colorPool, this.view.game.colorPool.getFromPool(index)),
                (Integer index) -> this.view.game.colorPool.isAvailable(index));
        final GameView gameView = this.view;
        final ListNode root = new ListNode().add(new SpacerNode())
                .add(new RowNode().add(new SubheaderNode(this.view.av, comp.name) {
                    /** {@inheritdoc} */
                    @Override
                    protected BitmapFont getFont() {
                        return gameView.av.fonts.getFont(new FontParam().setFont("Fontin-Bold").setSize(22));
                    }
                }).add(colorOptions).add(fateOptions));
        for (Fate fate : fates) {
            fateOptions.add(fate.name, fate.addToListNode(this.view.av, new ListNode()));
        }
        for (String color : this.view.game.colorPool.getColorNames()) {
            colorOptions.add(color);
        }
        return new Tuple<CompPlayer, MenuNode>(comp, root);
    }
}

package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.View;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This View shows players who contributed to the game
 */
class LoadGameView implements View {
    private final StartMenuView.Params params;
    private final ListNode content = new ListNode();
    private final Menu menu;
    private Consumer<View> navigate;

    LoadGameView(StartMenuView.Params params) {
        this.params = params;
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false,
                new ListNode()
                        .add(new ButtonNode(params.av, "Back", () -> this.navigate.accept(new StartMenuView(params))))
                        .add(new HeaderNode(params.av, "Load Games")).add(new SpacerNode())
                        .add(this.content.add(new TextNode(params.av, "Loading..."))));
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;
        MenuController menuController = new MenuController(this.params.av.settings, () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
        menuController.reset();

        // Loads the saved games
        List<Path> savedGames = this.params.serial.getSavedGames();
        this.content.clear();
        if (savedGames.size() == 0) {
            this.content.add(new TextNode(this.params.av, "No saved games"));
        }
        for (Path savedGame : savedGames) {
            this.content.add(new RowNode().add(new TextNode(this.params.av, savedGame.getFileName().toString()))
                    .add(new ButtonNode(this.params.av, "Choose", () -> {
                        try {
                            Game game = this.params.serial.loadGame(savedGame);
                            GameView view = new GameView(this.params, game);
                            game.rehydrateFromKryo(this.params.av, this.params.events, new Generator(view));
                            this.navigate.accept(view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })));
        }
        this.menu.pack();
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

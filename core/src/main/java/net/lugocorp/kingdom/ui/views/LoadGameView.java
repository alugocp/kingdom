package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.ui.menu.RowNode;
import net.lugocorp.kingdom.ui.menu.SpacerNode;
import net.lugocorp.kingdom.ui.menu.TextNode;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
    public Color getBackgroundColor() {
        return new Color(0f, 0f, 0f, 1f);
    }

    /** {@inheritdoc} */
    @Override
    public void start(Consumer<View> navigate) {
        this.navigate = navigate;
        MenuController menuController = new MenuController(() -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);

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
                            this.navigate.accept(new GameView(this.params, game));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })));
        }
        this.menu.pack();
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

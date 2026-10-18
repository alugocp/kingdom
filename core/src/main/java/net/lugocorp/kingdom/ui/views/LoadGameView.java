package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.context.BaseContext;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.context.GameViewContext;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.controllers.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.Background;
import net.lugocorp.kingdom.ui.View;
import com.badlogic.gdx.Gdx;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * This View shows players who contributed to the game
 */
class LoadGameView implements View {
    private final Background background;
    private final ListNode content = new ListNode();
    private final Menu menu;

    LoadGameView() {
        final AudioVideo av = Context.getContext().av;
        this.background = new Background(av);
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, new ListNode()
                .add(new ButtonNode(av, "Back", () -> Context.getContext().navigate.accept(new StartMenuView())))
                .add(new HeaderNode(av, "Load Games")).add(new SpacerNode())
                .add(this.content.add(new TextNode(av, "Loading...")))).noBackground();
    }

    /** {@inheritdoc} */
    @Override
    public void start() {
        final BaseContext ctx = Context.getContext();
        MenuController menuController = new MenuController(ctx.av.settings, () -> Optional.of(this.menu));
        Gdx.input.setInputProcessor(menuController);
        menuController.reset();

        // Loads the saved games
        List<Path> savedGames = ctx.serial.getSavedGames();
        this.content.clear();
        if (savedGames.size() == 0) {
            this.content.add(new TextNode(ctx.av, "No saved games"));
        }
        for (Path savedGame : savedGames) {
            this.content.add(new RowNode().add(new TextNode(ctx.av, savedGame.getFileName().toString()))
                    .add(new ButtonNode(ctx.av, "Choose", () -> {
                        final Optional<Game> game = ctx.serial.loadGame(savedGame);
                        if (game.isPresent()) {
                            final GameView view = new GameView(game.get());
                            Context.setContext(new GameViewContext(ctx, view));
                            ctx.navigate.accept(view);
                        } else {
                            // TODO serial give some error message to the player
                        }
                    })));
        }
        this.menu.pack();
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

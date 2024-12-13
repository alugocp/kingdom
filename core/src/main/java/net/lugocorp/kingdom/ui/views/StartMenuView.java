package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.engine.MenuController;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.events.AllEventHandlers;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.game.world.WorldGenerator;
import net.lugocorp.kingdom.ui.menu.ButtonNode;
import net.lugocorp.kingdom.ui.menu.HeaderNode;
import net.lugocorp.kingdom.ui.menu.ListNode;
import net.lugocorp.kingdom.ui.menu.Menu;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * View to select in-app options
 */
public class StartMenuView implements View {
    private final Graphics graphics;
    private final Menu menu;
    private Consumer<View> navigate;

    public StartMenuView(Graphics graphics, AllEventHandlers events) {
        this.graphics = graphics;
        this.menu = new Menu((Coords.SIZE.x / 2) - 300, 0, 600, false, new ListNode()
                .add(new HeaderNode(graphics, "Main Menu")).add(new ButtonNode(graphics, "New game", () -> {
                    Game game = new Game(this.graphics, events, new World(10, 5));
                    GameView view = new GameView(game, this.graphics);
                    game.generator = new Generator(view);
                    game.mechanics.auction.init(game);
                    game.mechanics.pools.init(game);
                    new WorldGenerator().generateWorld(view);
                    this.navigate.accept(view);
                })));
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
        this.menu.draw(this.graphics);
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

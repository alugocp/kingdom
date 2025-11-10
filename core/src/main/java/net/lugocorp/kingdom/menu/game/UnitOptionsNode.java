package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This is a set of Unit option UI elements for the recruitment Menu
 */
public class UnitOptionsNode extends ListNode {
    private final OrthographicCamera camera = new OrthographicCamera(Coords.SIZE.x, Coords.SIZE.y);
    private final long origin = System.currentTimeMillis();
    private final RowNode previews;
    private final RowNode buttons;
    private final RowNode units;
    private Optional<Menu> menu = Optional.empty();
    private boolean loaded = false;

    public UnitOptionsNode(GameView view, List<Unit> units, int n, Consumer<Unit> click) {
        this.camera.update();
        this.previews = new RowNode().setColumns(n);
        this.buttons = new RowNode().setColumns(n);
        this.units = new RowNode().setColumns(n);
        for (Unit u : units) {
            this.previews
                    .add(new ModelNode(view.av, this.camera, view.getEnvironment(), u.getModelName(), u.getMaterial()));
            this.units.add(((RowNode) u.getMenuContent(view, Optional.empty())).toListNode());
            this.buttons.add(new ButtonNode(view.av, "Choose", () -> click.accept(u)));
        }
        this.add(new SpacerNode(false)).add(new SubheaderNode(view.av, "Loading...").center())
                .add(new SpacerNode(false));
    }

    /**
     * Checks if all the associated mdoels are loaded and if so then displays them
     */
    private void checkModelsLoaded(int width) {
        // Check if any of the models haven't loaded yet
        for (MenuNode n : this.previews.getChildren()) {
            final ModelNode m = (ModelNode) n;
            if (!m.isLoaded() && !m.getModel().isPresent()) {
                return;
            }
        }

        // Wait additional time for the cool loading animation
        if (!this.loaded) {
            if (System.currentTimeMillis() - this.origin < 1500) {
                return;
            }
            this.loaded = true;
        }

        // Swap the content if all models have loaded
        this.clear();
        this.add(this.buttons).add(this.previews).add(this.units);
        this.menu.ifPresent((Menu m) -> this.pack(m, width));
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        super.pack(menu, width);
        this.menu = Optional.of(menu);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        super.draw(av, bounds);
        this.checkModelsLoaded(bounds.w);
    }
}

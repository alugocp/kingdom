package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.overlay.LabelOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This TileSelector allows us to select one Tile from a finite Set
 */
class TileSetSelectMode extends TileSelectMode {
    private final Optional<Function<Tile, Optional<LabelOverlay>>> hover;
    private final Consumer<Point> action;
    private final Set<Point> points;
    private Optional<LabelOverlay> overlay = Optional.empty();

    TileSetSelectMode(Set<Point> points, Consumer<Point> action) {
        this.hover = Optional.empty();
        this.points = points;
        this.action = action;
    }

    TileSetSelectMode(Set<Point> points, Consumer<Point> action, Function<Tile, Optional<LabelOverlay>> hover) {
        this.hover = Optional.of(hover);
        this.points = points;
        this.action = action;
    }

    /**
     * Dispels the LabelOverlay if there is one
     */
    private void dispelOverlay() {
        this.overlay.ifPresent((LabelOverlay o) -> o.dispel());
    }

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        for (Point p : this.points) {
            view.game.world.getTile(p).ifPresent((Tile t) -> t.setOption(true));
        }
    }

    /** {@inheritdoc} */
    @Override
    final boolean isValidTile(GameView view, Point p) {
        return this.points.contains(p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedValidPoint(GameView view, Point p) {
        view.av.loaders.sounds.play("sfx/arrow");
        this.action.accept(p);
        this.dispelOverlay();
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        view.hud.logger.error("Click a glowing tile, or press ESC to cancel");
        this.dispelOverlay();
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        this.dispelOverlay();
        for (Point p : this.points) {
            view.game.world.getTile(p).ifPresent((Tile t) -> t.setOption(false));
        }
    }

    /** {@inheritdoc} */
    @Override
    void hoverTile(GameView view, Point p) {
        this.hover.ifPresent((Function<Tile, Optional<LabelOverlay>> func) -> {
            view.game.world.getTile(p).ifPresent((Tile t) -> {
                final Optional<LabelOverlay> label = func.apply(t);
                label.ifPresent((LabelOverlay o) -> view.overlays.add(o));
                this.dispelOverlay();
                this.overlay = label;
            });
        });
    }
}

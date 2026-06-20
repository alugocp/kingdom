package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.engine.projection.CameraLogic;
import net.lugocorp.kingdom.engine.projection.ViewportLogic;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
import com.badlogic.gdx.Gdx;
import java.util.List;
import java.util.Optional;

/**
 * This TileSelector allows a Unit to move
 */
class TileMoveSelectMode extends TileSelectMode {
    private final Pathfinder pathfinder;
    private final Unit unit;
    private Optional<List<Point>> existingPath = Optional.empty();

    TileMoveSelectMode(Unit unit) {
        this.pathfinder = new Pathfinder(unit);
        this.unit = unit;
    }

    /**
     * Removes all shader decoration from the previously marked path (if one exists)
     */
    private final void removeShaderData(GameView view) {
        this.existingPath.ifPresent((List<Point> path) -> {
            for (Point p : path) {
                if (!view.game.world.getTile(p).isPresent()) {
                    System.out.println(path);
                    for (Point p1 : path) {
                        System.out.println(p1);
                    }
                }
                final Tile t = view.game.world.getTile(p).get();
                t.setMovePath(0, 0);
            }
            this.existingPath = Optional.empty();
        });
    }

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        final int x = Gdx.input.getX();
        final int y = Gdx.input.getY();
        if (view.isHoveringOverGameWorld(x, y)) {
            final Point u = ViewportLogic.unproject(x, y).get();
            final Point p = CameraLogic.getCoordUnderScreenPoint(u.x, u.y);
            this.hoverTile(view, p);
        }
    }

    /** {@inheritdoc} */
    @Override
    final boolean isValidTile(GameView view, Point p) {
        return this.unit.movement.canMoveToPoint(view, p);
    }

    /** {@inheritdoc} */
    @Override
    final void clickedValidPoint(GameView view, Point p) {
        view.av.loaders.sounds.play("sfx/footstep");
        this.unit.movement.move(view, this.existingPath.get(), false).execute();
    }

    /** {@inheritdoc} */
    @Override
    final void clickedInvalidPoint(GameView view) {
        view.hud.logger.error("Unit cannot move there");
    }

    /** {@inheritdoc} */
    @Override
    final void dispel(GameView view) {
        this.removeShaderData(view);
    }

    /** {@inheritdoc} */
    @Override
    final void hoverTile(GameView view, Point p) {
        // Calculate the movement path and remove existing shader flags
        final List<Point> path = this.pathfinder.getPath(view, p.copy());
        this.removeShaderData(view);
        if (path.size() == 0) {
            return;
        }

        // Sets up Tile user data for the render pipeline
        int nextMoveLabel = 1;
        final List<Integer> subpathLengths = this.unit.movement.getSubpathLengths(view, path);
        this.existingPath = Optional.of(path);
        for (int a = 0; a < path.size(); a++) {
            final int a1 = a;
            final Point p1 = path.get(a);
            final Tile t = view.game.world.getTile(p1).get();

            // Set the direction of the movement path texture(s) to render
            final int movePath = Hexagons.getBorderInteger(p1, (Point p2) -> (a1 > 0 && p2.equals(path.get(a1 - 1)))
                    || (a1 < path.size() - 1 && p2.equals(path.get(a1 + 1))));

            // Set the number to render on the path (how many turns this move will take)
            int moveLabel = 0;
            final int reduced = subpathLengths.get(0) - 1;
            if (reduced == 0) {
                moveLabel = nextMoveLabel++;
                subpathLengths.remove(0);
            } else {
                subpathLengths.set(0, reduced);
            }
            t.setMovePath(movePath, moveLabel);
        }
    }
}

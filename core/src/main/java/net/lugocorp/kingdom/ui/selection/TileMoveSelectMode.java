package net.lugocorp.kingdom.ui.selection;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.pathfinding.Pathfinder;
import net.lugocorp.kingdom.ui.views.GameView;
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
                view.game.world.getTile(p).ifPresent((Tile t) -> t.setMovePath(0, 0));
            }
        });
    }

    /** {@inheritdoc} */
    @Override
    final void init(GameView view) {
        // TODO call hover if we're over a Tile
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
        this.unit.movement.move(view, this.existingPath.get()).execute();
        view.hud.bot.minimap.refresh(view.game.world);
        // TODO this needs to refresh on the Unit's updated coordinates
        view.hud.bot.tileMenu.refresh();
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
        // Grab the path and the Unit's distance(s) they can move per turn
        final List<Point> path = this.pathfinder.getPath(view, p);
        final int remainingDistance = view.game.actions.getRemainingMoveDistance(view, this.unit);
        final int maxDistance = this.unit.movement.getMaxDistance(view);

        // Sets up Tile user data for the render pipeline
        this.removeShaderData(view);
        this.existingPath = Optional.of(path);
        for (int a = 0; a < path.size(); a++) {
            final int a1 = a;
            final Point p1 = path.get(a);

            // Set the direction of the movement path texture(s) to render
            final int movePath = Hexagons.getBorderInteger(p1, (Point p2) -> (a1 > 0 && p2.equals(path.get(a1 - 1)))
                    || (a1 < path.size() - 1 && p2.equals(path.get(a1 + 1))));

            // Set the number to render on the path (how many turns this move will take)
            int moveLabel = 0;
            if (a == path.size() - 1) {
                // Label with 1 if last node index < remainingDistance, otherwise we add a turn
                // until max distance closes the gap
                moveLabel = (a < remainingDistance)
                        ? 1
                        : ((int) Math.ceil((a + 1 - remainingDistance) / (float) maxDistance) + 1);
            } else if (a == remainingDistance - 1) {
                // This is the final node you can access this turn
                moveLabel = 1;
            } else if (a >= remainingDistance && (a + 1 - remainingDistance) % maxDistance == 0) {
                // This is the final node you can access on any given future turn
                moveLabel = (int) Math.floor((a + 1 - remainingDistance) / (float) maxDistance) + 1;
            }

            // Set the actual render data
            final int moveLabelForLambda = moveLabel;
            view.game.world.getTile(p1).ifPresent((Tile t) -> t.setMovePath(movePath, moveLabelForLambda));
        }
    }
}

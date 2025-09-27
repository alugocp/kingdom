package net.lugocorp.kingdom.engine.controllers;
import net.lugocorp.kingdom.utils.math.Point;
import java.util.Optional;

/**
 * This class handles useful mouse logic between the controllers
 */
class TouchState {
    private static final int THRESHOLD = 5;
    private boolean pastTheDragThreshold = false;
    private Optional<Point> origin = Optional.empty();
    Optional<Point> prev = Optional.empty();

    /**
     * Returns true if the user if currently pressing the mouse
     */
    boolean isActive() {
        return this.origin.isPresent();
    }

    /**
     * Returns true if the user if currently dragging the mouse
     */
    boolean isDragging() {
        return this.pastTheDragThreshold;
    }

    /**
     * Begins a new transaction with this state
     */
    void start(Point p) {
        this.origin = Optional.of(p);
        this.prev = Optional.of(p);
    }

    /**
     * Updates the state of this instance
     */
    Point update(Point p) {
        final Point result = this.prev.get();
        this.prev = Optional.of(p);
        if (!this.pastTheDragThreshold && (int) Math.sqrt(Math.pow(this.origin.get().x - p.x, 2)
                + Math.pow(this.origin.get().y - p.y, 2)) >= TouchState.THRESHOLD) {
            this.pastTheDragThreshold = true;
        }
        return result;
    }

    /**
     * Resets this instance's state
     */
    void reset() {
        this.pastTheDragThreshold = false;
        this.origin = Optional.empty();
        this.prev = Optional.empty();
    }
}

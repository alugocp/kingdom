package net.lugocorp.kingdom.engine.render;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents any object that can have an associated in-game model which can
 * move about
 */
public abstract class DynamicModellable extends Modellable {
    protected int x;
    protected int y;

    public DynamicModellable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Converts this object's current grid space position into the vector position
     * for its model
     */
    public abstract Vector3 getPositionVector();

    /**
     * Gets the current x position (in grid space)
     */
    public int getX() {
        return this.x;
    }

    /**
     * Gets the current y position (in grid space)
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns a Point representing this object's position in the World
     */
    public Point getPoint() {
        // TODO optimize by storing a mutable Point instead
        return new Point(this.x, this.y);
    }

    /** {@inheritdoc} */
    @Override
    public void resetModelPosition() {
        this.model.ifPresent((ModelInstance model) -> model.transform.setTranslation(this.getPositionVector()));
    }
}

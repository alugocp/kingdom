package net.lugocorp.kingdom.engine.render;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Represents any object that can have an associated in-game model which can
 * move about
 */
public abstract class DynamicModellable extends Modellable {
    private Point point = new Point();
    private float rotation = 0f;
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
     * Sets the current x position (in grid space)
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the current y position (in grid space)
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns a Point representing this object's position in the World
     */
    public Point getPoint() {
        this.point.set(this.x, this.y);
        return this.point;
    }

    /**
     * Rotates the Model by the given amount of radians
     */
    public void setRotation(float radians) {
        if (radians == this.rotation) {
            return;
        }
        this.model.ifPresent((ModelInstance model) -> {
            // We use this.rotation - radians here because we want to think
            // clockwise, but LibGDX rotates counter-clockwise
            model.transform.rotateRad(0f, 1f, 0f, this.rotation - radians);
            this.rotation = radians;
        });
    }

    /**
     * Moves this Modellable to its position plus some arbitrary offset (possibly in
     * between Hexagons)
     */
    public void setModelPositionOffset(float dx, float dy) {
        Vector3 pos = this.getPositionVector().add(Coords.raw.vector(dx, 0f, dy));
        this.model.ifPresent((ModelInstance model) -> model.transform.setTranslation(pos));
    }

    /** {@inheritdoc} */
    @Override
    public void resetModelPosition() {
        this.model.ifPresent((ModelInstance model) -> model.transform.setTranslation(this.getPositionVector()));
    }
}

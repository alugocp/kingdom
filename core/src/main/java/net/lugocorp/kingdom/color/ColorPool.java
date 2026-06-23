package net.lugocorp.kingdom.color;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all the possible Player colors
 */
public class ColorPool {
    private final List<Boolean> available = new ArrayList<>();
    private final List<String> names = new ArrayList<>();
    private final List<Color> pool = new ArrayList<>();

    public ColorPool() {
        this.add(0x00ff00, "Green");
        this.add(0xff0000, "Red");
        this.add(0x0000ff, "Blue");
        this.add(0x880088, "Purple");
        this.add(0xffff00, "Yellow");
        this.add(0xff7d00, "Orange");
        this.add(0x00ffff, "Cyan");
        this.add(0xff00ff, "Pink");
    }

    /**
     * Adds a new Color entry to the ColorPool
     */
    private void add(int hex, String name) {
        this.pool.add(Colors.fromHex(hex));
        this.available.add(true);
        this.names.add(name);
    }

    /**
     * Returns all color names in the pool (whether or not they're available)
     */
    public List<String> getColorNames() {
        return this.names;
    }

    /**
     * Returns true if the Color at the given index is available
     */
    public final boolean isAvailable(int index) {
        return this.available.get(index);
    }

    /**
     * Returns the index of the given Color in the pool
     */
    public int getIndex(Color c) {
        return this.pool.indexOf(c);
    }

    /**
     * Retrieves a Color from the pool
     */
    public final Color getFromPool() {
        for (int a = 0; a < this.available.size(); a++) {
            if (this.available.get(a)) {
                return this.getFromPool(a);
            }
        }
        System.err.println("Warning: The requested color is not available");
        return Color.BLACK;
    }

    /**
     * Retrieves the Color at the given index from the pool
     */
    public final Color getFromPool(int index) {
        if (!this.isAvailable(index)) {
            System.err.println("Warning: The requested color is not available");
            return Color.BLACK;
        }
        this.available.set(index, false);
        return this.pool.get(index);
    }

    /**
     * Returns a Color back to the pool
     */
    public final void releaseToPool(Color c) {
        this.available.set(this.getIndex(c), true);
    }
}

package net.lugocorp.kingdom.engine.fonts;
import com.badlogic.gdx.graphics.Color;
import java.util.Optional;

/**
 * This class contains several information to describe a requested font
 */
public class FontParam {
    private static final int DEFAULT_SIZE = 18;
    private Optional<Color> border = Optional.empty();
    private String font = "Arvo-Regular";
    private Color color = Color.WHITE;
    private int size = 18;

    /**
     * Sets this FontParam's font
     */
    public FontParam setFont(String font) {
        this.font = font;
        return this;
    }

    /**
     * Sets this FontParam's color
     */
    public FontParam setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * Sets this FontParam's border
     */
    public FontParam setBorder(Color border) {
        this.border = Optional.of(border);
        return this;
    }

    /**
     * Sets this FontParam's size
     */
    public FontParam setSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * Gets this FontParam's font
     */
    public String getFont() {
        return this.font;
    }

    /**
     * Gets this FontParam's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Gets this FontParam's border
     */
    public Optional<Color> getBorder() {
        return this.border;
    }

    /**
     * Gets this FontParam's size
     */
    public int getSize() {
        return this.size;
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("(%d, %s, %s)", this.size, this.color.toString(), this.font);
    }

    /** {@inheritdoc} */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof FontParam) {
            FontParam f = (FontParam) o;
            return this.hashCode() == f.hashCode();
        }
        return false;
    }
}

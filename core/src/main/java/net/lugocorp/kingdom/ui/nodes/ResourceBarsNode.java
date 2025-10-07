package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.Menu;
import net.lugocorp.kingdom.ui.MenuNode;
import net.lugocorp.kingdom.utils.logic.Colors;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * This MenuNode displays one or more resource bars
 */
public class ResourceBarsNode implements MenuNode {
    private static final int PADDING = 5;
    private final ResourceBarsNode.Bar[] bars;
    private final BitmapFont font;
    private final AudioVideo av;
    private int textWidth = 0;

    public ResourceBarsNode(AudioVideo av, ResourceBarsNode.Bar... bars) {
        this.font = av.fonts.getFont(20, ColorScheme.TEXT.color);
        this.bars = bars;
        this.av = av;
    }

    /**
     * Updates the current value for the Bar at the given index
     */
    public void setValue(int index, int value) {
        this.bars[index].numbers = String.format("%d / %d", value, this.bars[index].max);
        this.bars[index].value = value;
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return (int) (this.bars.length * (ResourceBarsNode.PADDING + this.font.getLineHeight()));
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        GlyphLayout layout = new GlyphLayout();
        for (ResourceBarsNode.Bar bar : this.bars) {
            layout.setText(this.font, bar.label);
            if (layout.width > this.textWidth) {
                this.textWidth = (int) layout.width + ResourceBarsNode.PADDING;
            }
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int yInitial = Coords.SIZE.y - bounds.y - (int) this.font.getLineHeight();
        final int barWidth = bounds.w - this.textWidth - (ResourceBarsNode.PADDING * 2);
        final int barX = bounds.x + this.textWidth + ResourceBarsNode.PADDING;

        // Draw the resource bars
        av.shapes.begin(ShapeType.Filled);
        for (int a = 0; a < this.bars.length; a++) {
            ResourceBarsNode.Bar bar = this.bars[a];
            final int y = yInitial - (((int) font.getLineHeight() + ResourceBarsNode.PADDING) * a);
            av.shapes.setColor(Colors.fromHex(bar.color));
            av.shapes.rect(barX, y, barWidth * Math.min(1f, bar.value / (float) bar.max), this.font.getLineHeight());
        }
        av.shapes.end();

        // Draw white outline on the resource bars
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(ColorScheme.OUTLINE.color);
        for (int a = 0; a < this.bars.length; a++) {
            final int y = yInitial - (((int) font.getLineHeight() + ResourceBarsNode.PADDING) * a);
            av.shapes.rect(barX, y, barWidth, this.font.getLineHeight());
        }
        av.shapes.end();

        // Draw the text
        av.sprites.begin();
        for (int a = 0; a < this.bars.length; a++) {
            ResourceBarsNode.Bar bar = this.bars[a];
            final int y = yInitial + (int) this.font.getLineHeight() - ResourceBarsNode.PADDING
                    - (((int) font.getLineHeight() + ResourceBarsNode.PADDING) * a);
            this.font.draw(this.av.sprites, bar.label, bounds.x, y);
            this.font.draw(this.av.sprites, bar.numbers, bounds.x + this.textWidth + ResourceBarsNode.PADDING, y);
        }
        av.sprites.end();
    }

    /**
     * This nested class contains all the information for a resource bar
     */
    public static class Bar {
        private final String label;
        private final int color;
        private final int max;
        private String numbers;
        private int value;

        public Bar(String label, int color, int value, int max) {
            this.label = label;
            this.color = color;
            this.value = value;
            this.max = max;
            this.numbers = String.format("%d / %d", value, max);
        }
    }
}

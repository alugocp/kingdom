package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.fonts.FontParam;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.engine.shaders.ElementShader;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * This MenuNode displays one or more resource bars
 */
public class ResourceIconsNode implements MenuNode {
    private static final int PADDING = 5;
    private final GlyphLayout layout = new GlyphLayout();
    private final ResourceIconsNode.Bar[] bars;
    private final BitmapFont font;
    private final AudioVideo av;
    private int textWidth = 0;

    public ResourceIconsNode(AudioVideo av, ResourceIconsNode.Bar... bars) {
        this.font = av.fonts
                .getFont(new FontParam().setFont("Fontin-Bold").setSize(20).setColor(ColorScheme.TEXT.color));
        this.bars = bars;
        this.av = av;
        for (ResourceIconsNode.Bar bar : this.bars) {
            bar.icon = new Drawable(av.loaders.sprites, bar.iconKey);
        }
    }

    /**
     * Updates the current value for the Bar at the given index
     */
    public void setValue(int index, int value) {
        this.bars[index].value = value;
    }

    /**
     * Returns the height of a single ResourceIconsNode.Bar
     */
    private int getBarHeight(ResourceIconsNode.Bar bar) {
        return Math.max((int) (ResourceIconsNode.PADDING + this.font.getLineHeight()), bar.rows * 35);
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        int h = 0;
        for (ResourceIconsNode.Bar bar : this.bars) {
            h += this.getBarHeight(bar);
        }
        return h;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        for (ResourceIconsNode.Bar bar : this.bars) {
            this.layout.setText(this.font, bar.label);
            if (this.layout.width > this.textWidth) {
                this.textWidth = (int) this.layout.width + ResourceIconsNode.PADDING;
            }
        }
        for (ResourceIconsNode.Bar bar : this.bars) {
            bar.cols = (width - this.textWidth) / 35;
            bar.rows = (bar.max / bar.cols) + (bar.max % bar.cols > 0 ? 1 : 0);
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final int iconStartX = bounds.x + this.textWidth + ResourceIconsNode.PADDING;
        int yOffset = 0;

        for (ResourceIconsNode.Bar bar : this.bars) {
            final int barHeight = this.getBarHeight(bar);
            final Rect flip = Coords.screen.flip(bounds.x, bounds.y + yOffset, bounds.w, barHeight);
            yOffset += barHeight;

            // Draw the text
            av.sprites.begin();
            for (int a = 0; a < this.bars.length; a++) {
                this.font.draw(av.sprites, bar.label, flip.x,
                        flip.y + (flip.h / 2) + (int) (this.font.getLineHeight() / 2f));
            }

            // Draw the value icons
            for (int a = 0; a < bar.value; a++) {
                final int x = iconStartX + (35 * (a % bar.cols));
                final int y = flip.y - (35 * (a / bar.cols)) + (35 * (bar.rows - 1));
                bar.icon.render(av.sprites, x, y);
            }
            av.sprites.end();

            // Draw the depleted icons
            av.special.begin();
            av.shaders.element.setMode(ElementShader.GRAY_MODE);
            for (int a = bar.value; a < bar.max; a++) {
                final int x = iconStartX + (35 * (a % bar.cols));
                final int y = flip.y - (35 * (a / bar.cols)) + (35 * (bar.rows - 1));
                bar.icon.render(av.special, x, y);
            }
            av.special.end();
            av.shaders.element.setMode(ElementShader.DEFAULT_MODE);
        }
    }

    /**
     * This nested class contains all the information for a resource bar
     */
    public static class Bar {
        private final String iconKey;
        private final String label;
        private final int max;
        private Drawable icon = null;
        private int cols = 1;
        private int rows = 1;
        private int value;

        public Bar(String label, String iconKey, int value, int max) {
            this.iconKey = iconKey;
            this.label = label;
            this.value = value;
            this.max = max;
        }
    }
}

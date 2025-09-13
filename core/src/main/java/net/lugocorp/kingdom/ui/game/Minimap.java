package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Displays a Minimap of the Game World
 */
public class Minimap {
    private static final int MAX_W = 250;
    private static final int MAX_H = 250;
    private final Point pos = new Point();
    private int tilesPerPixelInOneDimension;
    private Color[][] thumbprint;
    private int scale;
    private int gridw;
    private int gridh;
    private int w;
    private int h;

    /**
     * Initializes values in this object
     */
    public void init(World world) {
        float ratio = world.getHeight() > world.getWidth()
                ? ((float) Minimap.MAX_H / (float) world.getHeight())
                : ((float) Minimap.MAX_W / (float) world.getWidth());
        this.tilesPerPixelInOneDimension = (int) Math.ceil(1f / ratio);
        this.scale = (int) Math.floor(Math.max(ratio, 1f));
        this.gridh = (int) (world.getHeight() / this.tilesPerPixelInOneDimension);
        this.gridw = (int) (world.getWidth() / this.tilesPerPixelInOneDimension);
        this.thumbprint = new Color[this.gridw][this.gridh];
        this.h = (int) (this.gridh * this.scale);
        this.w = (int) (this.gridw * this.scale);
        this.refresh(world);
    }

    /**
     * Returns how many pixels this object takes up on the screen vertically
     */
    public int getHeight() {
        return this.gridh * this.scale;
    }

    /**
     * Sets this Minimap's location
     */
    void setPoint(int x, int y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    /**
     * Handles a click on this Minimap
     */
    boolean click(GameView view, Point p) {
        Rect r = new Rect(this.pos.x, this.pos.y, this.w, this.h);
        if (!r.contains(p)) {
            return false;
        }

        Point clicked = new Point((int) ((p.x - r.x) * this.tilesPerPixelInOneDimension / this.scale),
                (int) ((p.y - r.y) * this.tilesPerPixelInOneDimension / this.scale));
        view.centerOnPoint(clicked, true);
        return true;
    }

    /**
     * Recalculates the Minimap thumbprint
     */
    public void refresh(World world) {
        for (int b = 0; b < this.gridh; b++) {
            for (int a = 0; a < this.gridw; a++) {
                Color c = world.getTile(a * this.tilesPerPixelInOneDimension, b * this.tilesPerPixelInOneDimension)
                        .get().getMinimapColor();
                this.thumbprint[a][b] = c;
            }
        }
    }

    /**
     * Renders the Minimap at the given coordinates
     */
    void draw(AudioVideo av, Point crosshair) {
        // Draw black background
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(Color.BLACK);
        av.shapes.rect(this.pos.x, Coords.SIZE.y - this.pos.y - this.h, this.w, this.h);

        // Draw relevant pixels on the grid
        int y = Coords.SIZE.y - this.pos.y - this.scale;
        for (int b = 0; b < this.gridh; b++) {
            int x = this.pos.x;
            for (int a = 0; a < this.gridw; a++) {
                Color c = this.thumbprint[a][b];
                if (c != Color.BLACK) {
                    av.shapes.setColor(c);
                    av.shapes.rect(x, y, this.scale, this.scale);
                }
                x += this.scale;
            }
            y -= this.scale;
        }

        // Calculate crosshairs center point
        int cx = crosshair.x / this.tilesPerPixelInOneDimension;
        int cy = crosshair.y / this.tilesPerPixelInOneDimension;

        // Draw crosshairs
        final int scale3 = this.scale * 3;
        av.shapes.setColor(Color.WHITE);
        av.shapes.rect(this.pos.x + ((cx - 1) * this.scale),
                Coords.SIZE.y - (this.pos.y + (cy * this.scale)) - this.scale, scale3, this.scale);
        av.shapes.rect(this.pos.x + (cx * this.scale), Coords.SIZE.y - (this.pos.y + ((cy - 1) * this.scale)) - scale3,
                this.scale, scale3);
        av.shapes.end();

        // Draw white borders
        av.shapes.begin(ShapeType.Line);
        av.shapes.setColor(Color.WHITE);
        av.shapes.rect(this.pos.x, Coords.SIZE.y - this.pos.y - this.h, this.w, this.h);
        av.shapes.end();
    }
}

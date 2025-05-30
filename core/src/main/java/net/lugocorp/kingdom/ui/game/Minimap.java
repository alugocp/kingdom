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
        this.h = (int) (this.gridh * this.scale);
        this.w = (int) (this.gridw * this.scale);
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
        view.centerOnPoint(clicked);
        return true;
    }

    /**
     * Renders the Minimap at the given coordinates
     */
    void draw(AudioVideo av, World world, Point crosshair) {
        // TODO optimize this somehow
        // Option 1: store a static image that gets updated when tiles change
        // (tilesPerPixelInOneDimension > 1?)
        // Option 2: break up recalculation amongst frames or once per turn (old-school
        // optimization technique)

        // Draw black background
        int y = this.pos.y;
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(Color.BLACK);
        av.shapes.rect(this.pos.x, Coords.SIZE.y - y - this.h, this.w, this.h);

        // Calculate crosshairs center point
        int cx = crosshair.x / this.tilesPerPixelInOneDimension;
        int cy = crosshair.y / this.tilesPerPixelInOneDimension;

        // Draw relevant pixels on the grid
        y = Coords.SIZE.y - y - this.scale;
        for (int b = 0; b < this.gridh; b++) {
            for (int a = 0; a < this.gridw; a++) {
                boolean isCrosshair = Math.abs(a - cx) + Math.abs(b - cy) <= 1;
                Color c = isCrosshair
                        ? Color.WHITE
                        : world.getTile(a * this.tilesPerPixelInOneDimension, b * this.tilesPerPixelInOneDimension)
                                .get().getMinimapColor();
                if (c != Color.BLACK) {
                    av.shapes.setColor(c);
                    av.shapes.rect(this.pos.x + (a * this.scale), y, this.scale, this.scale);
                }
            }
            y -= this.scale;
        }
        av.shapes.end();
    }
}

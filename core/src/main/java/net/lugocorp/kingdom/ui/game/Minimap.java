package net.lugocorp.kingdom.ui.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.world.World;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Displays a Minimap of the Game World
 */
public class Minimap {
    private static final int MAX_W = 250;
    private static final int MAX_H = 250;
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
     * Renders the Minimap at the given coordinates
     */
    public void draw(AudioVideo av, World world, int x, int y) {
        // TODO show the player view position in the world (box like in Civ or just a
        // point?)
        // TODO optimize this somehow
        // Option 1: store a static image that gets updated when tiles change
        // (tilesPerPixelInOneDimension > 1?)
        // Option 2: break up recalculation amongst frames or once per turn (old-school
        // optimization technique)

        // Draw black background
        av.shapes.begin(ShapeType.Filled);
        av.shapes.setColor(Color.BLACK);
        av.shapes.rect(x, Coords.SIZE.y - y - this.h, this.w, this.h);

        // Draw relevant pixels on the grid
        y = Coords.SIZE.y - y - this.scale;
        for (int b = 0; b < this.gridh; b++) {
            for (int a = 0; a < this.gridw; a++) {
                Color c = world.getTile(a * this.tilesPerPixelInOneDimension, b * this.tilesPerPixelInOneDimension)
                        .get().getMinimapColor();
                if (c != Color.BLACK) {
                    av.shapes.setColor(c);
                    av.shapes.rect(x + (a * this.scale), y, this.scale, this.scale);
                }
            }
            y -= this.scale;
        }
        av.shapes.end();
    }
}

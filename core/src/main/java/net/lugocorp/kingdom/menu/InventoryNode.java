package net.lugocorp.kingdom.menu;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.lugocorp.kingdom.assets.SpritesLoader;
import net.lugocorp.kingdom.engine.Graphics;
import net.lugocorp.kingdom.game.Inventory;
import net.lugocorp.kingdom.game.Item;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;

/**
 * A node that displays some Inventory
 */
public class InventoryNode implements MenuNode {
    private static final int MARGIN = 2;
    public static final int SIDE = 50;
    private final SpritesLoader sprites;
    private final Inventory items;
    private int cols;
    private int rows;

    public InventoryNode(SpritesLoader sprites, Inventory items) {
        this.sprites = sprites;
        this.items = items;
    }

    @Override
    public int getHeight() {
        return this.rows * (InventoryNode.SIDE + InventoryNode.MARGIN);
    }

    @Override
    public void pack(int width) {
        this.cols = (int) (width / (InventoryNode.SIDE + InventoryNode.MARGIN));
        this.rows = this.items.getMax() == 0 ? 0 : (int) Math.ceil(this.items.getMax() / (float) this.cols);
    }

    @Override
    public void draw(Graphics graphics, Rect bounds) {
        if (this.items.getMax() == 0) {
            return;
        }
        graphics.sprites.begin();
        for (int a = 0; a < this.rows; a++) {
            Rect flip = Coords.screen.flip(bounds.x, bounds.y + a * (InventoryNode.SIDE + InventoryNode.MARGIN),
                    bounds.w, InventoryNode.SIDE + InventoryNode.MARGIN);
            for (int b = 0; b < this.cols; b++) {
                int index = (a * this.cols) + b;
                if (index >= this.items.getMax()) {
                    break;
                }
                TextureRegion icon = this.sprites.get("placeholder");
                if (index < this.items.getSize()) {
                    Item item = this.items.get(index);
                    if (item.icon.isPresent()) {
                        icon = this.sprites.get(item.icon.get());
                    }
                }
                graphics.sprites.draw(icon, flip.x + b * (InventoryNode.SIDE + InventoryNode.MARGIN), flip.y);
            }
        }
        graphics.sprites.end();
    }

    @Override
    public void click(Rect bounds, Point p) {
    }
}

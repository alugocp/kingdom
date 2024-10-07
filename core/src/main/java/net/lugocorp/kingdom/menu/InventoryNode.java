package net.lugocorp.kingdom.menu;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.lugocorp.kingdom.engine.GameGraphics;
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
    private final GameGraphics graphics;
    private final Inventory items;
    private int cols;
    private int rows;

    public InventoryNode(GameGraphics graphics, Inventory items) {
        this.graphics = graphics;
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
                TextureRegion icon = this.graphics.loaders.sprites.get("placeholder");
                if (index < this.items.getSize()) {
                    Item item = this.items.get(index);
                    if (item.icon.isPresent()) {
                        icon = this.graphics.loaders.sprites.get(item.icon.get());
                    }
                }
                graphics.sprites.draw(icon, flip.x + b * (InventoryNode.SIDE + InventoryNode.MARGIN), flip.y);
            }
        }
        graphics.sprites.end();
    }

    @Override
    public void click(Menu menu, Rect bounds, Point p) {
        int x = (p.x - bounds.x) / InventoryNode.SIDE;
        int y = (p.y - bounds.y) / InventoryNode.SIDE;
        int i = (cols * y) + x;

        // No item in particular was clicked, nothing to do here
        if (i >= this.items.getSize()) {
            return;
        }

        // Set mini menu for the selected item
        Item item = this.items.get(i);
        ListNode root = new ListNode().add(new HeaderNode(this.graphics, item.name))
                .add(new TextNode(this.graphics, item.desc));
        menu.setMiniMenu(root, p.x, p.y);
    }
}

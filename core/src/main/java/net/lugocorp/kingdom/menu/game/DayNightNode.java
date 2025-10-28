package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.game.mechanics.DayNight;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.MenuPopup;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * An icon that helps us track the day/night cycle
 */
public class DayNightNode implements MenuNode {
    public static int SIDE = 35;
    private final MenuPopup popup = new MenuPopup();
    private final Drawable moonIcon;
    private final Drawable sunIcon;
    private final AudioVideo av;
    private MenuNode desc = new ListNode();
    private Drawable icon;

    public DayNightNode(AudioVideo av) {
        this.moonIcon = new Drawable(av.loaders.sprites, "moon-icon");
        this.sunIcon = new Drawable(av.loaders.sprites, "sun-icon");
        this.icon = this.sunIcon;
        this.av = av;
    }

    /**
     * Updates this icon's state based on our position in the day/night cycle
     */
    public void set(DayNight dayNight) {
        final boolean day = dayNight.isDay();
        final int remaining = dayNight.getRemainingTurns();
        this.icon = day ? this.sunIcon : this.moonIcon;
        this.desc = new ListNode().add(new SubheaderNode(this.av, day ? "Daytime" : "Nighttime"))
                .add(new TextNode(this.av, String.format("%d turn%s until %s", remaining, remaining == 1 ? "" : "s",
                        day ? "nighttime" : "daytime")));
    }

    /** {@inheritdoc} */
    @Override
    public int getHeight() {
        return DayNightNode.SIDE;
    }

    /** {@inheritdoc} */
    @Override
    public void pack(Menu menu, int width) {
        this.popup.setMenu(menu);
    }

    /** {@inheritdoc} */
    @Override
    public void mouseMoved(Rect bounds, Point prev, Point curr) {
        this.popup.update(new Rect(bounds.x, bounds.y, DayNightNode.SIDE, DayNightNode.SIDE), curr, this.desc);
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        final Rect flip = Coords.screen.flip(bounds);
        av.sprites.begin();
        this.icon.render(av.sprites, flip.x, flip.y);
        av.sprites.end();
    }
}

package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.render.Drawable;
import net.lugocorp.kingdom.gameplay.mechanics.DayNight;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.icon.IconNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * An icon that helps us track the day/night cycle
 */
public class DayNightNode extends IconNode {
    private final Drawable moonIcon;
    private final Drawable sunIcon;
    private final AudioVideo av;
    private MenuNode desc = new ListNode();

    public DayNightNode(AudioVideo av) {
        super(av, "sun-icon");
        this.moonIcon = new Drawable(av.loaders.sprites, "moon-icon");
        this.sunIcon = this.getIcon();
        this.av = av;
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.desc;
    }

    /**
     * Updates this icon's state based on our position in the day/night cycle
     */
    public void set(DayNight dayNight) {
        final boolean day = dayNight.isDay();
        final int remaining = dayNight.getRemainingTurns();
        this.setIcon(day ? this.sunIcon : this.moonIcon);
        this.desc = new ListNode().add(new SubheaderNode(this.av, day ? "Daytime" : "Nighttime"))
                .add(new TextNode(this.av, String.format("%d turn%s until %s", remaining, remaining == 1 ? "" : "s",
                        day ? "nighttime" : "daytime")));
    }
}

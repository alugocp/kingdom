package net.lugocorp.kingdom.menu.icon;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.engine.assets.FontParam;
import net.lugocorp.kingdom.engine.controllers.Shortcut;
import net.lugocorp.kingdom.engine.shaders.ElementShader;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.MenuNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.text.SubheaderNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import java.util.Optional;

/**
 * Can be used to represent an Ability or other such action
 */
public class ActionNode extends IconNode {
    public static final int SIDE = 50;
    public static final int MODE_NOTHING = 0;
    public static final int MODE_DISABLED = 1;
    public static final int MODE_ACTIVE = 2;
    private final Optional<Shortcut> shortcut;
    private final Runnable action;
    private final AudioVideo av;
    private final ListNode node;
    private final BitmapFont font;
    private int mode = ActionNode.MODE_ACTIVE;

    public ActionNode(AudioVideo av, String name, String icon, Optional<Shortcut> shortcut, Optional<String> tagline,
            Optional<String> desc, Runnable action) {
        super(av, icon, ActionNode.SIDE);
        this.node = new ListNode().add(new SubheaderNode(av, name));
        tagline.ifPresent((String s) -> this.node.add(new TextNode(av, s) {
            /** {@inheritdoc} */
            @Override
            protected BitmapFont getFont() {
                return this.av.fonts.getFont(new FontParam().setColor(ColorScheme.GOLD.color));
            }
        }));
        desc.ifPresent((String s) -> this.node.add(new TextNode(av, s)));
        this.font = av.fonts
                .getFont(new FontParam().setColor(ColorScheme.TEXT.color).setBorder(ColorScheme.BLACK.color));
        this.shortcut = shortcut;
        this.action = action;
        this.av = av;
    }

    /**
     * Sets this ActionNode's activation mode
     */
    public ActionNode setMode(int mode) {
        this.mode = mode;
        return this;
    }

    /** {@inheritdoc} */
    @Override
    protected MenuNode getPopupNode() {
        return this.node;
    }

    /** {@inheritdoc} */
    @Override
    protected int getElementShaderMode() {
        if (this.mode == ActionNode.MODE_DISABLED) {
            return ElementShader.GRAY_MODE;
        }
        return this.popup.isHovered() ? ElementShader.BRIGHT_MODE : ElementShader.DEFAULT_MODE;
    }

    /**
     * Do something when this ActionNode is clicked or otherwise activated
     */
    private void clickLogic() {
        if (this.mode == ActionNode.MODE_ACTIVE) {
            this.av.loaders.sounds.play("sfx/arrow");
            this.action.run();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void click(Rect bounds, Point p) {
        if (bounds.contains(p)) {
            this.clickLogic();
        }
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        super.draw(av, bounds);
        this.shortcut.ifPresent((Shortcut s) -> {
            final Rect flip = Coords.screen.flip(this.getBounds(bounds));
            av.sprites.begin();
            font.draw(av.sprites, s.label, flip.x, flip.y + flip.h);
            av.sprites.end();
        });
    }

    /** {@inheritdoc} */
    @Override
    public void keyPressed(int keycode) {
        this.shortcut.ifPresent((Shortcut s) -> s.matches(keycode, () -> this.clickLogic()));
    }
}

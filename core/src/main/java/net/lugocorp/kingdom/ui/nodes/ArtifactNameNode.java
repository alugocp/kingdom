package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.ui.ColorScheme;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * TextNode for the name in an ArtifactNode
 */
class ArtifactNameNode extends TextNode {

    ArtifactNameNode(AudioVideo av, String name) {
        super(av, name);
    }

    /** {@inheritdoc} */
    @Override
    protected BitmapFont getFont() {
        return this.av.fonts.getFont(20, ColorScheme.TEXT);
    }
}

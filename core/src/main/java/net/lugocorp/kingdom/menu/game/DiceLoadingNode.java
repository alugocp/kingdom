package net.lugocorp.kingdom.menu.game;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.math.Rect;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import java.util.Optional;

/**
 * Renders a rotating D20 dice model
 */
public class DiceLoadingNode extends ModelNode {
    private final Vector3 axis = new Vector3(0f, 1f, 0f);

    public DiceLoadingNode(AudioVideo av, Camera camera, Environment environment) {
        super(av, camera, environment, "models/d20", Optional.empty());
    }

    /** {@inheritdoc} */
    @Override
    public void draw(AudioVideo av, Rect bounds) {
        super.draw(av, bounds);
        this.getModel().ifPresent((ModelInstance model) -> {
            final float degrees = (float) (Math.random() * 15f) + 10f;
            this.axis.rotate(5f, (float) Math.random(), (float) Math.random(), (float) Math.random());
            model.transform.rotate(this.axis, degrees);
        });
    }
}

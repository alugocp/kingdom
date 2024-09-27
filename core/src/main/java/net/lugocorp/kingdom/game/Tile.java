package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Hexagons;

public class Tile {
    private Optional<ModelInstance> model = Optional.empty();

    public Optional<ModelInstance> getModelInstance() {
        return this.model;
    }

    /**
     * Applies a ModelInstance to this object and sets its position in the render
     * area
     */
    public void setModelInstance(ModelInstance model, int x, int y) {
        final float x_diff = (y % 2 == 0) ? 0f : 0.5f;
        model.transform.setTranslation(
                Coords.getVector((x + x_diff) * Hexagons.WIDTH, y * (Hexagons.HEIGHT - Hexagons.HEIGHT_DIFF)));
        this.model = Optional.of(model);
    }
}

package net.lugocorp.kingdom.game;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import java.util.Optional;

public class Tile {
    private Optional<ModelInstance> model = Optional.empty();

    public Optional<ModelInstance> getModelInstance() {
        return this.model;
    }

    public void setModelInstance(ModelInstance model, int x, int y) {
        model.transform.setTranslation((float)y, 0f, (float)x);
        this.model = Optional.of(model);
    }
}

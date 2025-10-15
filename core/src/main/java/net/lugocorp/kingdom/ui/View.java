package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.utils.math.Coords;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.function.Consumer;

/**
 * Interface for any screen in the application
 */
public interface View {
    public Color getBackgroundColor();
    public void start(Consumer<View> navigate);
    public void render(int dt);
    public void resize(int w, int h);
    public void dispose();

    /**
     * Returns a Viewport for this View
     */
    public default Viewport getViewport() {
        return new FitViewport(Coords.SIZE.x, Coords.SIZE.y);
    }
}

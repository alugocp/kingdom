package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.math.Coords;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.function.Consumer;

/**
 * Interface for any screen in the application
 */
public interface View {
    public void start(Consumer<View> navigate);
    public void render(int dt);
    public void resize(int w, int h);
    public void dispose();

    /**
     * Returns the background color for this View
     */
    public default Color getBackgroundColor() {
        return new Color(0f, 0f, 0f, 1f);
    }

    /**
     * Returns a Viewport for this View
     */
    public default Viewport getViewport() {
        return new FitViewport(Coords.SIZE.x, Coords.SIZE.y);
    }
}

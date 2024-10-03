package net.lugocorp.kingdom.views;
import com.badlogic.gdx.graphics.Color;
import java.util.function.Function;

/**
 * Interface for any screen in the application
 */
public interface View {
    public Color getBackgroundColor();
    public void start(Function<View, Void> navigate);
    public void render();
    public void resize(int w, int h);
    public void dispose();
}

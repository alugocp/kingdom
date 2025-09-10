package net.lugocorp.kingdom.ui.views;
import com.badlogic.gdx.graphics.Color;
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
}

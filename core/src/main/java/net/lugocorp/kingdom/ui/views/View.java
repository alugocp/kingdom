package net.lugocorp.kingdom.ui.views;
import net.lugocorp.kingdom.utils.Consumer;
import com.badlogic.gdx.graphics.Color;

/**
 * Interface for any screen in the application
 */
public interface View {
    public Color getBackgroundColor();
    public void start(Consumer<View> navigate);
    public void render();
    public void resize(int w, int h);
    public void dispose();
}

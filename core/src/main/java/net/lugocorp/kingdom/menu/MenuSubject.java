package net.lugocorp.kingdom.menu;
import net.lugocorp.kingdom.engine.Graphics;

/**
 * Interface for any object that has associated Menu content
 */
public interface MenuSubject {
    public MenuNode getMenuContent(Graphics graphics);
}

package net.lugocorp.kingdom.ui;
import net.lugocorp.kingdom.engine.Graphics;

/**
 * Interface for any object that has associated Menu content
 */
public interface MenuSubject {
    public MenuNode getMenuContent(Graphics graphics);
}

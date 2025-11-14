package net.lugocorp.kingdom.ui.tutorial;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.math.Rect;
import net.lugocorp.kingdom.menu.text.TextNode;

/**
 * Represents a single text box in the Tutorial
 */
class TutorialPopup {
    final TutorialArrow direction;
    final TextNode click;
    final TextNode node;
    final Rect bounds;

    TutorialPopup(TutorialArrow direction, TextNode node, TextNode click, Rect bounds) {
        this.direction = direction;
        this.bounds = bounds;
        this.click = click;
        this.node = node;
    }

    /**
     * Takes in some vertices and orders them to correctly render this TutorialPopup
     */
    float[] getPolylineInput(Point c1, Point c2, Point c3, Point c4, Point p1, Point p2, Point p3) {
        if (this.direction == TutorialArrow.UP) {
            return new float[]{c1.x, c1.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, c2.x, c2.y, c3.x, c3.y, c4.x, c4.y, c1.x,
                    c1.y};
        }
        if (this.direction == TutorialArrow.DOWN) {
            return new float[]{c1.x, c1.y, c2.x, c2.y, c3.x, c3.y, p3.x, p3.y, p2.x, p2.y, p1.x, p1.y, c4.x, c4.y, c1.x,
                    c1.y};
        }
        return new float[]{c1.x, c1.y, c2.x, c2.y, c3.x, c3.y, c4.x, c4.y, c1.x, c1.y};
    }
}

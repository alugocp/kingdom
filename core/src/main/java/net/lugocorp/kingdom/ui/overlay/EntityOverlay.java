package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This Overlay is actually a collection of Overlays above a certain Entity
 */
public class EntityOverlay {
    private static final float LINE_HEIGHT = 0.2f;
    private final List<RisingOverlay> rising = new ArrayList<>();
    private final List<Overlay> overlays = new ArrayList<>();
    private Optional<ActionOverlay> action = Optional.empty();

    /**
     * Adds an Overlay to this EntityOverlay
     */
    public void add(Overlay o) {
        o.getOffset().y += EntityOverlay.LINE_HEIGHT * this.overlays.size();
        this.overlays.add(o);
        this.action.ifPresent((ActionOverlay o1) -> {
            o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
        });
        for (Overlay o1 : this.rising) {
            o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
        }
    }

    /**
     * Sets the ActionOverlay associated with this EntityOverlay
     */
    public void setAction(ActionOverlay o) {
        o.getOffset().y += EntityOverlay.LINE_HEIGHT * this.overlays.size();
        this.action = Optional.of(o);
        for (Overlay o1 : this.rising) {
            o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
        }
    }

    /**
     * Adds a RisingOverlay to this EntityOverlay
     */
    public void addRising(RisingOverlay o) {
        this.rising.add(o);
        for (Overlay o1 : this.rising) {
            o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
        }
    }

    /**
     * Updates this EntityOverlay's progress through its animation
     */
    public void update(int dt) {
        // Process the basic Overlays
        int a = 0;
        while (a < this.overlays.size()) {
            final Overlay o = this.overlays.get(a);
            o.update(dt);
            if (o.isDone()) {
                o.runCallback();
                this.overlays.remove(a);
                for (int b = a; b < this.overlays.size(); b++) {
                    this.overlays.get(b).getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
                this.action.ifPresent((ActionOverlay o1) -> {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                });
                for (Overlay o1 : this.rising) {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
            } else {
                a++;
            }
        }

        // Process the ActionOverlay (if necessary)
        this.action.ifPresent((ActionOverlay o) -> {
            o.update(dt);
            if (o.isDone()) {
                o.runCallback();
                this.action = Optional.empty();
                for (Overlay o1 : this.rising) {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
            }
        });

        // Process the RisingOverlays
        a = 0;
        while (a < this.rising.size()) {
            final Overlay o = this.rising.get(a);
            o.update(dt);
            if (o.isDone()) {
                o.runCallback();
                this.rising.remove(a);
                for (int b = a; b < this.rising.size(); b++) {
                    this.rising.get(b).getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
            } else {
                a++;
            }
        }
    }

    /**
     * Renders this EntityOverlay onto the Game World
     */
    public void render(GameView view) {
        for (Overlay o : this.overlays) {
            o.render(view);
        }
        this.action.ifPresent((ActionOverlay o) -> o.render(view));
        for (RisingOverlay o : this.rising) {
            o.render(view);
        }
    }
}

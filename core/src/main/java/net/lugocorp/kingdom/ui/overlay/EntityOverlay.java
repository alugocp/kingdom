package net.lugocorp.kingdom.ui.overlay;
import net.lugocorp.kingdom.game.layers.Entity;
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
    private Optional<IconsOverlay> icons = Optional.empty();

    /**
     * Adds an Overlay to this EntityOverlay
     */
    public void add(Overlay o) {
        o.getOffset().y += EntityOverlay.LINE_HEIGHT * (this.overlays.size() + (this.icons.isPresent() ? 1 : 0));
        this.overlays.add(o);
        for (Overlay o1 : this.getOverlaysAbove(o)) {
            o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
        }
    }

    /**
     * Sets the ActionOverlay associated with this EntityOverlay
     */
    public void setAction(ActionOverlay o) {
        final boolean replacingOtherAction = this.action.isPresent();
        this.action.ifPresent((ActionOverlay o1) -> o1.runCallback());
        o.getOffset().y += EntityOverlay.LINE_HEIGHT * (this.overlays.size() + (this.icons.isPresent() ? 1 : 0));
        this.action = Optional.of(o);
        if (!replacingOtherAction) {
            for (Overlay o1 : this.getOverlaysAbove(o)) {
                o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
            }
        }
    }

    /**
     * Adds a RisingOverlay to this EntityOverlay
     */
    public void addRising(RisingOverlay o) {
        o.getOffset().y += EntityOverlay.LINE_HEIGHT
                * (this.overlays.size() + (this.icons.isPresent() ? 1 : 0) + (this.action.isPresent() ? 1 : 0));
        this.rising.add(0, o);
        for (Overlay o1 : this.getOverlaysAbove(o)) {
            o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
        }
    }

    /**
     * Sets the IconsOverlay associated with this EntityOverlay
     */
    public void setIcons(GameView view, Entity e) {
        final IconsOverlay o = new IconsOverlay(view, e);
        final boolean replacingOtherIcons = this.icons.isPresent();
        this.icons.ifPresent((IconsOverlay o1) -> o1.runCallback());
        this.icons = Optional.of(o);
        o.getOffset().y += EntityOverlay.LINE_HEIGHT / 2f;
        if (!replacingOtherIcons) {
            for (Overlay o1 : this.getOverlaysAbove(o)) {
                o1.getOffset().y += EntityOverlay.LINE_HEIGHT;
            }
        }
    }

    /**
     * Returns a list of Overlays that are rendered above the given Overlay
     */
    private List<Overlay> getOverlaysAbove(Overlay o) {
        final List<Overlay> above = new ArrayList<>();
        if (this.action.map((ActionOverlay o1) -> o == o1).orElse(false)) {
            above.addAll(this.overlays);
            this.action.ifPresent((ActionOverlay o1) -> above.add(o1));
            above.addAll(this.rising);
        } else if (this.overlays.contains(o)) {
            final int i = this.overlays.indexOf(o);
            for (int a = i + 1; a < this.overlays.size(); a++) {
                above.add(this.overlays.get(a));
            }
            this.action.ifPresent((ActionOverlay o1) -> above.add(o1));
            above.addAll(this.rising);
        } else if (this.action.map((ActionOverlay o1) -> o == o1).orElse(false)) {
            above.addAll(this.rising);
        } else if (this.rising.contains(o)) {
            final int i = this.rising.indexOf(o);
            for (int a = i + 1; a < this.rising.size(); a++) {
                above.add(this.rising.get(a));
            }
        }
        return above;
    }

    /**
     * Updates this EntityOverlay's progress through its animation
     */
    public void update(int dt) {
        // Process the IconsOverlay (if necessary)
        this.icons.ifPresent((IconsOverlay o) -> {
            o.update(dt);
            if (o.isDone()) {
                for (Overlay o1 : this.getOverlaysAbove(o)) {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
                o.runCallback();
                this.icons = Optional.empty();
            }
        });

        // Process the basic Overlays
        int a = 0;
        while (a < this.overlays.size()) {
            final Overlay o = this.overlays.get(a);
            o.update(dt);
            if (o.isDone()) {
                for (Overlay o1 : this.getOverlaysAbove(o)) {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
                o.runCallback();
                this.overlays.remove(a);
            } else {
                a++;
            }
        }

        // Process the ActionOverlay (if necessary)
        this.action.ifPresent((ActionOverlay o) -> {
            o.update(dt);
            if (o.isDone()) {
                for (Overlay o1 : this.getOverlaysAbove(o)) {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
                o.runCallback();
                this.action = Optional.empty();
            }
        });

        // Process the RisingOverlays
        a = 0;
        while (a < this.rising.size()) {
            final Overlay o = this.rising.get(a);
            o.update(dt);
            if (o.isDone()) {
                for (Overlay o1 : this.getOverlaysAbove(o)) {
                    o1.getOffset().y -= EntityOverlay.LINE_HEIGHT;
                }
                o.runCallback();
                this.rising.remove(a);
            } else {
                a++;
            }
        }
    }

    /**
     * Renders this EntityOverlay onto the Game World
     */
    public void render(GameView view) {
        this.icons.ifPresent((IconsOverlay o) -> o.render(view));
        for (Overlay o : this.overlays) {
            o.render(view);
        }
        this.action.ifPresent((ActionOverlay o) -> o.render(view));
        for (RisingOverlay o : this.rising) {
            o.render(view);
        }
    }
}

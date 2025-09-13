package net.lugocorp.kingdom.engine.animation;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class handles Animation
 */
public class AnimationQueue {
    private final List<Animation> animations = new ArrayList<>();
    private final List<Animation> dropList = new ArrayList<>();
    private final List<Animation> addList = new ArrayList<>();

    /**
     * Adds a new Animation
     */
    public AnimationQueue add(Animation a) {
        this.animations.add(a);
        return this;
    }

    /**
     * Progresses the state of all active Animations
     */
    public void update(GameView view, int dt) {
        for (Animation a : this.animations) {
            final boolean remove = a.update(view, dt);
            if (remove) {
                final Optional<Animation> next = a.getFollowup();
                this.dropList.add(a);
                if (next.isPresent()) {
                    this.addList.add(next.get());
                }
            }
        }
        this.animations.removeAll(this.dropList);
        this.animations.addAll(this.addList);
        this.dropList.clear();
        this.addList.clear();
    }
}

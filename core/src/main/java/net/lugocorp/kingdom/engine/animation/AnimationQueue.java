package net.lugocorp.kingdom.engine.animation;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.BatchCounter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class handles Animation
 */
public class AnimationQueue {
    private final BatchCounter<Animation> animations = new BatchCounter(15);
    private final List<Animation> dropList = new ArrayList<>();
    private final List<Animation> addList = new ArrayList<>();

    /**
     * Adds a new Animation
     */
    public AnimationQueue add(Animation a) {
        this.animations.list().add(a);
        return this;
    }

    /**
     * Returns true if there are active Animations
     */
    public boolean inProgress() {
        return this.animations.list().size() > 0;
    }

    /**
     * Progresses the state of all active Animations
     */
    public void update(GameView view, int dt) {
        for (Animation a : this.animations.getBatch()) {
            final boolean remove = a.update(view, dt);
            if (remove) {
                final Optional<Animation> next = a.getFollowup();
                this.dropList.add(a);
                if (next.isPresent()) {
                    this.addList.add(next.get());
                }
            }
        }
        this.animations.removed(this.dropList.size());
        this.animations.list().removeAll(this.dropList);
        this.animations.list().addAll(this.addList);
        this.dropList.clear();
        this.addList.clear();
    }
}

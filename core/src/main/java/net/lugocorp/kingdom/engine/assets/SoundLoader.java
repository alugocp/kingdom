package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.engine.Settings;
import net.lugocorp.kingdom.mods.ModAssetsMap;
import com.badlogic.gdx.audio.Sound;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps the logic for loading sound effects into the game
 */
public class SoundLoader extends AssetsPool<Sound> {
    private final Set<String> queued = new HashSet<>();
    private final Settings settings;

    public SoundLoader(ModAssetsMap modAssetsMap, Settings settings) {
        super(modAssetsMap, Sound.class, "wav");
        this.settings = settings;
    }

    /**
     * Plays the registered Sound with the given name
     */
    public void play(String name) {
        Optional<Sound> s = this.getAsset(name);
        if (s.isPresent()) {
            s.get().play(this.settings.getSoundVolume());
        } else {
            this.queued.add(name);
        }
    }

    /**
     * Run this function every frame so we can play any Sounds that weren't loaded
     * when requested
     */
    public void checkQueuedSounds() {
        for (String name : this.queued) {
            Optional<Sound> s = this.getAsset(name);
            if (s.isPresent()) {
                s.get().play(this.settings.getSoundVolume());
                this.queued.remove(name);
            }
        }
    }
}

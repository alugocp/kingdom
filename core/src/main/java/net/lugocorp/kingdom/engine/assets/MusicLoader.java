package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.mods.ModAssetsMap;
import net.lugocorp.kingdom.settings.Settings;
import com.badlogic.gdx.audio.Music;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps the logic for loading music into the game
 */
public class MusicLoader extends AssetsPool<Music> {
    private final Set<String> queued = new HashSet<>();
    private final Settings settings;

    public MusicLoader(ModAssetsMap modAssetsMap, Settings settings) {
        super(modAssetsMap, Music.class, "mp3");
        this.settings = settings;
    }

    /**
     * Plays the registered Music with the given name
     */
    public void play(String name) {
        Optional<Music> m = this.getAsset(name);
        if (m.isPresent()) {
            m.get().setVolume(this.settings.getMusicVolume());
            m.get().play();
        } else {
            this.queued.add(name);
        }
    }

    /**
     * Run this function every frame so we can play any Music that wasn't loaded
     * when requested
     */
    public void checkQueuedMusic() {
        for (String name : this.queued) {
            Optional<Music> m = this.getAsset(name);
            if (m.isPresent()) {
                m.get().setVolume(this.settings.getMusicVolume());
                m.get().play();
                this.queued.remove(name);
            }
        }
    }
}

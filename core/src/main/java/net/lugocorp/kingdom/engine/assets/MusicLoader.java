package net.lugocorp.kingdom.engine.assets;
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
    private Optional<Music> playing = Optional.empty();

    public MusicLoader(Settings settings) {
        super(Music.class, "mp3");
        this.settings = settings;
    }

    /**
     * Plays the registered Music with the given name
     */
    public void play(String name) {
        final Optional<Music> music = this.getAsset(name);

        // Ignore if this Music is already playing
        if (this.playing.map((Music p) -> music.map((Music m) -> m.equals(p)).orElse(false)).orElse(false)) {
            return;
        }

        // Handle now if the Music is already loaded, otherwise wait for a future frame
        if (music.isPresent()) {
            final Music m = music.get();
            this.playing.ifPresent((Music p) -> {
                p.pause();
                p.setPosition(0f);
            });
            m.setVolume(this.settings.getMusicVolume());
            m.setLooping(true);
            if (m.getVolume() > 0f) {
                m.play();
            }
            this.playing = Optional.of(m);
        } else {
            this.queued.add(name);
        }
    }

    /**
     * Sets the volume for the currently playing Music
     */
    public void setVolume(float volume) {
        this.playing.ifPresent((Music m) -> {
            m.setVolume(volume);
            if (m.isPlaying() && volume == 0f) {
                m.pause();
            } else if (!m.isPlaying() && volume > 0f) {
                m.play();
            }
        });
    }

    /**
     * Run this function every frame so we can play any Music that wasn't loaded
     * when requested
     */
    public void checkQueuedMusic() {
        for (String name : this.queued) {
            Optional<Music> m = this.getAsset(name);
            if (m.isPresent()) {
                this.play(name);
                this.queued.remove(name);
            }
        }
    }
}

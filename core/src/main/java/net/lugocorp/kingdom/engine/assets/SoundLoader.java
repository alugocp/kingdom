package net.lugocorp.kingdom.engine.assets;
import net.lugocorp.kingdom.utils.mods.ModAssetsMap;
import com.badlogic.gdx.audio.Sound;

/**
 * Wraps the logic for loading sound effects into the game
 */
public class SoundLoader extends AssetsPool<Sound> {

    public SoundLoader(ModAssetsMap modAssetsMap) {
        super(modAssetsMap, Sound.class, "wav");
    }

    /**
     * Plays the registered Sound with the given name
     */
    public void play(String name) {
        this.getAsset(name).ifPresent((Sound sound) -> sound.play());
    }
}

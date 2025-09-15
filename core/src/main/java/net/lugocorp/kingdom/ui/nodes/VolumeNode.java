package net.lugocorp.kingdom.ui.nodes;
import net.lugocorp.kingdom.engine.AudioVideo;
import java.util.function.Consumer;

/**
 * Row containing options to set some audio level
 */
public class VolumeNode extends RowNode {
    private static final float VOLUME_OFF = 0f;
    private static final float VOLUME_LOW = 0.3f;
    private static final float VOLUME_MED = 0.6f;
    private static final float VOLUME_HIGH = 1f;
    private final ButtonNode off;
    private final ButtonNode low;
    private final ButtonNode med;
    private final ButtonNode high;

    public VolumeNode(AudioVideo av, float initial, Consumer<Float> onVolumeChange) {
        this.off = new ButtonNode(av, "Off", () -> this.setVolume(onVolumeChange, VolumeNode.VOLUME_OFF));
        this.low = new ButtonNode(av, "Low", () -> this.setVolume(onVolumeChange, VolumeNode.VOLUME_LOW));
        this.med = new ButtonNode(av, "Med", () -> this.setVolume(onVolumeChange, VolumeNode.VOLUME_MED));
        this.high = new ButtonNode(av, "High", () -> this.setVolume(onVolumeChange, VolumeNode.VOLUME_HIGH));
        this.add(this.off).add(this.low).add(this.med).add(this.high);
        this.setVolume(onVolumeChange, initial);
    }

    /**
     * Disables the currently selected volume level button
     */
    private void setVolume(Consumer<Float> onVolumeChange, float volume) {
        this.off.enable(volume != VolumeNode.VOLUME_OFF);
        this.low.enable(volume != VolumeNode.VOLUME_LOW);
        this.med.enable(volume != VolumeNode.VOLUME_MED);
        this.high.enable(volume != VolumeNode.VOLUME_HIGH);
        onVolumeChange.accept(volume);
    }
}

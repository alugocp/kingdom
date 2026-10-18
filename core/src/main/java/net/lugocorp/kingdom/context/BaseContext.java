package net.lugocorp.kingdom.context;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.model.Generator;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.mods.GameMod;
import net.lugocorp.kingdom.serial.SaveLoad;
import net.lugocorp.kingdom.ui.View;
import java.util.List;
import java.util.function.Consumer;

/**
 * This Context exists throughout the entire lifecycle of the application
 */
public class BaseContext extends Context {
    public final Consumer<View> navigate;
    public final AllEventHandlers events;
    public final Generator generator;
    public final List<GameMod> mods;
    public final SaveLoad serial;
    public final AudioVideo av;

    public BaseContext(AllEventHandlers events, Generator generator, Consumer<View> navigate, SaveLoad serial,
            AudioVideo av, List<GameMod> mods) {
        this.generator = generator;
        this.events = events;
        this.navigate = navigate;
        this.serial = serial;
        this.mods = mods;
        this.av = av;
    }
}

package net.lugocorp.kingdom.tests.game;
import static org.junit.jupiter.api.Assertions.assertEquals;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.gameplay.events.AllEventHandlers;
import net.lugocorp.kingdom.serial.KryoProvider;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileOutputStream;
import java.time.OffsetTime;
import org.junit.jupiter.api.Test;

/**
 * This class contains tests for Kryo rehydration
 */
public class KryoTests {

    @Test
    public void testSaveGameObject() {
        final Kryo kryo = KryoProvider.getKryo();
        final Game game = new Game(new AllEventHandlers(), OffsetTime.now());
        final File f = new File("test-save-game");
        boolean passed = true;
        try {
            final Output output = new Output(new FileOutputStream(f));
            kryo.writeObject(output, game);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
            passed = false;
        }
        if (f.exists()) {
            f.delete();
        }
        assertEquals(passed, true);
    }
}

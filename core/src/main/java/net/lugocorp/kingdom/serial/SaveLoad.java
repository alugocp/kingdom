package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Files;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that handles saving and loading games
 */
public class SaveLoad {

    /**
     * Returns a list of paths that have saved games
     */
    public List<Path> getSavedGames() {
        final List<Path> games = new ArrayList<>();
        final File base = Files.getBaseDir().file();
        if (!base.exists()) {
            return games;
        }
        for (File f : base.listFiles()) {
            if (!f.exists() || f.isDirectory()) {
                continue;
            }
            games.add(f.toPath());
        }
        return games;
    }

    /**
     * Loads a Game from the given Path
     */
    public Game loadGame(Path path) throws Exception {
        Files.ensureExistence();
        final Kryo kryo = KryoProvider.getKryo();
        final File file = path.toFile();
        final Input input = new Input(new FileInputStream(file));
        final Game game = kryo.readObject(input, Game.class);
        input.close();
        return game;
    }

    /**
     * Saves a Game
     */
    public void saveGame(Game game) throws Exception {
        Files.ensureExistence();
        final Kryo kryo = KryoProvider.getKryo();
        final String filename = game.startTime.toString();
        final File file = Files.getFile(filename).file();
        try {
            final Output output = new Output(new FileOutputStream(file));
            kryo.writeObject(output, game);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            throw e;
        }
    }
}

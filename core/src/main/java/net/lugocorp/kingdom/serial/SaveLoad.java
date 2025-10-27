package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that handles saving and loading games
 */
public class SaveLoad {
    private static final String LOCATION = "kingdom/games";

    /**
     * Ensures that the save/load directory exists
     */
    private void ensureExistence() throws IOException {
        File f = Gdx.files.external(SaveLoad.LOCATION).file();
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * Returns a list of paths that have saved games
     */
    public List<Path> getSavedGames() {
        List<Path> games = new ArrayList<>();
        File base = Gdx.files.external(SaveLoad.LOCATION).file();
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
        this.ensureExistence();
        Kryo kryo = KryoProvider.getKryo();
        File file = path.toFile();
        Input input = new Input(new FileInputStream(file));
        Game game = kryo.readObject(input, Game.class);
        input.close();
        return game;
    }

    /**
     * Saves a Game
     */
    public void saveGame(Game game) throws Exception {
        this.ensureExistence();
        Kryo kryo = KryoProvider.getKryo();
        String filename = game.startTime.toString();
        File file = Gdx.files.external(String.format("%s/%s", SaveLoad.LOCATION, filename)).file();
        try {
            Output output = new Output(new FileOutputStream(file));
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

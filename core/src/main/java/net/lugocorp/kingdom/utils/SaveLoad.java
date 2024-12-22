package net.lugocorp.kingdom.utils;
import net.lugocorp.kingdom.game.Game;
import com.badlogic.gdx.Gdx;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    public Game loadGame(Path path) throws IOException, ClassNotFoundException {
        this.ensureExistence();
        File file = path.toFile();
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
        Game game = (Game) input.readObject();
        input.close();
        return game;
    }

    /**
     * Saves a Game
     */
    public void saveGame(Game game) throws IOException {
        this.ensureExistence();
        String filename = game.startTime.toString();
        File file = Gdx.files.external(String.format("%s/%s", SaveLoad.LOCATION, filename)).file();
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(game);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
        }
    }
}

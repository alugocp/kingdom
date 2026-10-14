package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Files;
import java.io.File;
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
        final File file = path.toFile();
        // TODO read from file and return
        return null;
    }

    /**
     * Saves a Game
     */
    public void saveGame(Game game) throws Exception {
        Files.ensureExistence();
        final String filename = game.startTime.toString();
        final File file = Files.getFile(filename).file();
        try {
            // TODO write to output file
        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            throw e;
        }
    }
}

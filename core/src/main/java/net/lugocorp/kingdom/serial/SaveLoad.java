package net.lugocorp.kingdom.serial;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.utils.Files;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that handles saving and loading games
 */
public class SaveLoad {
    private static final String EXT = "game";
    private static final Deserializer<Serial<Game>, ?> deserializer = null;

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
            if (f.exists() && !f.isDirectory() && this.isGameFile(f.getName())) {
                games.add(f.toPath());
            }
        }
        return games;
    }

    /**
     * Returns true if the given file name denotes a game file
     */
    private boolean isGameFile(String fileName) {
        final Pattern pattern = Pattern.compile(String.format(".%s$", SaveLoad.EXT), Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(fileName);
        return matcher.find();
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
        final String filename = String.format("%s.%s", game.startTime.toString(), SaveLoad.EXT);
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

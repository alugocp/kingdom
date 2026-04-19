package net.lugocorp.kingdom.utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.IOException;

/**
 * This class contains shared logic for filesystem I/O
 */
public class Files {
    private static final String BASE = "kingdom";

    /**
     * Returns the base kingdom directory as a FileHandle
     */
    public static FileHandle getBaseDir() {
        return Gdx.files.external(Files.BASE);
    }

    /**
     * Returns the given filepath under the base kingdom directory as a FileHandle
     */
    public static FileHandle getFile(String filepath) {
        return Gdx.files.external(String.format("%s/%s", Files.BASE, filepath));
    }

    /**
     * Ensures that the kingdom directory exists
     */
    public static void ensureExistence() throws IOException {
        final File f = Files.getBaseDir().file();
        if (!f.exists()) {
            f.mkdirs();
        }
    }
}

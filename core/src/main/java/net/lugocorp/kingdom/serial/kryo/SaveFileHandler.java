package net.lugocorp.kingdom.serial.kryo;
import net.lugocorp.kingdom.Main;
import net.lugocorp.kingdom.utils.Version;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Optional;

/**
 * This class handles reading and writing save file data with a Kryo-based file
 * format
 */
public class SaveFileHandler {
    public static final Kryo kryo = KryoProvider.getKryo();

    /**
     * Extracts a Version instance from a Kryo-based file format
     */
    public static Optional<Version> readVersion(File file) {
        Input input = null;
        try {
            input = new Input(new FileInputStream(file));
            final int major = input.read();
            final int minor = input.read();
            final int patch = input.read();
            return Optional.of(new Version(major, minor, patch));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * Reads some data from a Kryo-based file format
     */
    public static <T> T readFromFile(File file, Class<T> classInstance) throws Exception {
        final Input input = new Input(new FileInputStream(file));
        try {
            input.read();
            input.read();
            input.read();
            return SaveFileHandler.kryo.readObject(input, classInstance);
        } catch (Exception e) {
            throw e;
        } finally {
            input.close();
        }
    }

    /**
     * Writes some data in a Kryo-based file format
     */
    public static <T> void writeToFile(File file, T data) throws Exception {
        final FileOutputStream fileOutput = new FileOutputStream(file);
        final Output output = new Output(fileOutput);
        try {
            fileOutput.write(Main.VERSION.major);
            fileOutput.write(Main.VERSION.minor);
            fileOutput.write(Main.VERSION.patch);
            SaveFileHandler.kryo.writeObject(output, data);
        } catch (Exception e) {
            throw e;
        } finally {
            output.close();
        }
    }
}

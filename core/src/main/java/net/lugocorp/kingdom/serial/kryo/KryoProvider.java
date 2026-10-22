package net.lugocorp.kingdom.serial.kryo;
import com.esotericsoftware.kryo.Kryo;

/**
 * This class returns a Kryo instance
 */
public class KryoProvider {

    /**
     * Returns a new instance of Kryo
     */
    public static Kryo getKryo() {
        final Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);

        // Register v1.0.0 serial classes
        kryo.register(net.lugocorp.kingdom.serial.v1d0d0.GameSerial.class);
        return kryo;
    }
}

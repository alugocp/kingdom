package net.lugocorp.kingdom.utils.code;

/**
 * Represents a semantic versioning value
 */
public class Semver {
    public final int major;
    public final int minor;
    public final int patch;

    public Semver(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Returns true if this Semver represents a newer version than the given Semver
     */
    public boolean isNewerThan(Semver s) {
        return this.major > s.major
                || (this.major == s.major && (this.minor > s.minor || (this.minor == s.minor && this.patch > s.patch)));
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        return String.format("%d.%d.%d", this.major, this.minor, this.patch);
    }

    /** {@inheritdoc} */
    public int hashCode() {
        return this.toString().hashCode();
    }

    /** {@inheritdoc} */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Semver) {
            Semver s = (Semver) o;
            return this.major == s.major && this.minor == s.minor && this.patch == s.patch;
        }
        return false;
    }
}

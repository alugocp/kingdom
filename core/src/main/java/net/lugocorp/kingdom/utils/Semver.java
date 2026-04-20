package net.lugocorp.kingdom.utils;

/**
 * Represents a semantic versioning value
 */
public class Semver {
    public static final int ALPHA = 0;
    public static final int BETA = 1;
    public static final int FULL = 2;
    public final int major;
    public final int minor;
    public final int patch;
    public final int label;

    public Semver(int major, int minor, int patch, int label) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.label = label;
    }

    public Semver(int major, int minor, int patch) {
        this(major, minor, patch, Semver.FULL);
    }

    /**
     * Returns true if this Semver represents a newer version than the given Semver
     */
    public boolean isNewerThan(Semver s) {
        return this.major > s.major || (this.major == s.major && (this.minor > s.minor || (this.minor == s.minor
                && (this.patch > s.patch || (this.patch == s.patch && this.label > s.label)))));
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        String suffix = "";
        if (this.label == Semver.ALPHA) {
            suffix = " (alpha)";
        }
        if (this.label == Semver.BETA) {
            suffix = " (beta)";
        }
        return String.format("%d.%d.%d%s", this.major, this.minor, this.patch, suffix);
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

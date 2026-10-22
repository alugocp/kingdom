package net.lugocorp.kingdom.utils;

/**
 * Represents a release version
 */
public class Version {
    public static final int ALPHA = 0;
    public static final int BETA = 1;
    public static final int FULL = 2;
    public final int major;
    public final int minor;
    public final int patch;
    public final int label;

    public Version(int major, int minor, int patch, int label) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.label = label;
    }

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, Version.FULL);
    }

    /**
     * Returns true if this Version represents a newer version than the given
     * Version
     */
    public boolean isNewerThan(Version s) {
        return this.major > s.major
                || (this.major == s.major && (this.minor > s.minor || (this.minor == s.minor && this.patch > s.patch)));
    }

    /** {@inheritdoc} */
    @Override
    public String toString() {
        String suffix = "";
        if (this.label == Version.ALPHA) {
            suffix = " (alpha)";
        }
        if (this.label == Version.BETA) {
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
        if (o instanceof Version) {
            Version s = (Version) o;
            return this.major == s.major && this.minor == s.minor && this.patch == s.patch;
        }
        return false;
    }
}

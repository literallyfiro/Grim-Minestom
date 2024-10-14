package ac.grim.grimac.utils;

public final class RelativeFlag {

    public static final RelativeFlag X = new RelativeFlag(1 << 0);
    public static final RelativeFlag Y = new RelativeFlag(1 << 1);
    public static final RelativeFlag Z = new RelativeFlag(1 << 2);
    public static final RelativeFlag YAW = new RelativeFlag(1 << 3);
    public static final RelativeFlag PITCH = new RelativeFlag(1 << 4);

    private final byte mask;

    public RelativeFlag(int mask) {
        this.mask = (byte) mask;
    }

    public RelativeFlag combine(RelativeFlag relativeFlag) { // FIXME: Should this be called append?
        return new RelativeFlag(this.mask | relativeFlag.mask);
    }

    public byte getMask() {
        return mask;
    }

    public boolean isSet(byte flags) {
        return (flags & mask) != 0;
    }

    public byte set(byte flags, boolean relative) {
        if (relative) {
            return (byte) (flags | mask);
        }
        return (byte) (flags & ~mask);
    }

}

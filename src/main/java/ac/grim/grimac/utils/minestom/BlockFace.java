package ac.grim.grimac.utils.minestom;

/**
 * The {@code BlockFace} enum contains constants for the different valid faces in the minecraft protocol.
 *
 * @author retrooper
 * @see <a href="https://wiki.vg/Protocol#Player_Digging">https://wiki.vg/Protocol#Player_Digging</a>
 * @since 1.7.8
 */
public enum BlockFace {
    /**
     * -Y offset
     */
    DOWN(0, -1, 0),

    /**
     * +Y offset
     */
    UP(0, 1, 0),

    /**
     * -Z offset
     */
    NORTH(0, 0, -1),

    /**
     * +Z offset
     */
    SOUTH(0, 0, 1),

    /**
     * -X offset
     */
    WEST(-1, 0, 0),

    /**
     * +X offset
     */
    EAST(1, 0, 0),

    /**
     * Face is set to 255
     */
    OTHER((short) 255, -1, -1, -1);

    private static final BlockFace[] VALUES = values();
    private static final BlockFace[] CARTESIAN_VALUES = new BlockFace[]{DOWN, UP, NORTH, SOUTH, WEST, EAST}; // FIXME: remove this or use this somewhere

    final short faceValue;
    final int modX;
    final int modY;
    final int modZ;

    BlockFace(short faceValue, int modX, int modY, int modZ) {
        this.faceValue = faceValue;
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
    }

    BlockFace(int modX, int modY, int modZ) {
        this.faceValue = (short) ordinal();
        this.modX = modX;
        this.modY = modY;
        this.modZ = modZ;
    }

    public static BlockFace fromMinestom(net.minestom.server.instance.block.BlockFace blockFace) {
        return switch (blockFace) {
            case BOTTOM -> DOWN;
            case TOP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> OTHER;
        };
    }

    public static BlockFace getLegacyBlockFaceByValue(int face) {
        if (face == 255) return OTHER;
        return CARTESIAN_VALUES[face % CARTESIAN_VALUES.length];
    }

    public static BlockFace getBlockFaceByValue(int face) {
        return CARTESIAN_VALUES[face % CARTESIAN_VALUES.length];
    }

    public int getModX() {
        return modX;
    }

    public int getModY() {
        return modY;
    }

    public int getModZ() {
        return modZ;
    }

    public BlockFace getOppositeFace() {
        return switch (this) {
            case DOWN -> UP;
            case UP -> DOWN;
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
            default -> OTHER;
        };
    }

    public BlockFace getCCW() {
        return switch (this) {
            case NORTH -> WEST;
            case SOUTH -> EAST;
            case WEST -> SOUTH;
            case EAST -> NORTH;
            default -> OTHER;
        };
    }

    public BlockFace getCW() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            default -> OTHER;
        };
    }

    public short getFaceValue() {
        return faceValue;
    }

}

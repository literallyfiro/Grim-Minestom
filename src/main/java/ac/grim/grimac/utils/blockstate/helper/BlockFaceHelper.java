package ac.grim.grimac.utils.blockstate.helper;

import ac.grim.grimac.utils.vector.MutableVector;
import net.minestom.server.instance.block.BlockFace;

public class BlockFaceHelper {
    public static boolean isFaceVertical(BlockFace face) {
        return face == BlockFace.TOP || face == BlockFace.BOTTOM;
    }

    public static boolean isFaceHorizontal(BlockFace face) {
        return face == BlockFace.NORTH || face == BlockFace.EAST || face == BlockFace.SOUTH || face == BlockFace.WEST;
    }

    public static BlockFace getClockWise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.EAST;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            default -> BlockFace.SOUTH;
        };
    }

    public static BlockFace getPEClockWise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.EAST;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            default -> BlockFace.SOUTH;
        };
    }

    public static BlockFace getCounterClockwise(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.WEST;
            case SOUTH -> BlockFace.EAST;
            case WEST -> BlockFace.SOUTH;
            default -> BlockFace.NORTH;
        };
    }

    public MutableVector offset(MutableVector toOffset, BlockFace face) {
        toOffset.setX(toOffset.getX() + face.toDirection().normalX());
        toOffset.setY(toOffset.getY() + face.toDirection().normalY());
        toOffset.setZ(toOffset.getZ() + face.toDirection().normalZ());
        return toOffset;
    }
}

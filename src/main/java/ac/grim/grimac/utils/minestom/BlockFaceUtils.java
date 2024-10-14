package ac.grim.grimac.utils.minestom;

import net.minestom.server.instance.block.BlockFace;

public class BlockFaceUtils {

    public static BlockFace getCCW(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.WEST;
            case SOUTH -> BlockFace.EAST;
            case WEST -> BlockFace.SOUTH;
            case EAST -> BlockFace.NORTH;
            default -> null;
        };
    }

    public static BlockFace getCW(BlockFace face) {
        return switch (face) {
            case NORTH -> BlockFace.EAST;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            case EAST -> BlockFace.SOUTH;
            default -> null;
        };
    }

}

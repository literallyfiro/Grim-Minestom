package ac.grim.grimac.utils.minestom;

import net.minestom.server.coordinate.Point;

public class PositionSerializer {

    public static long serializePosition(Point point) {
        return (((long) point.blockX() & 0x3FFFFFF) << 38) |
                (((long) point.blockZ() & 0x3FFFFFF) << 12) |
                ((long) point.blockY() & 0xFFF);
    }

}

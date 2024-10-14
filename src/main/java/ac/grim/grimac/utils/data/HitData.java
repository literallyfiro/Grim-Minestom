package ac.grim.grimac.utils.data;

import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.vector.MutableVector;
import lombok.Getter;
import lombok.ToString;
import ac.grim.grimac.utils.minestom.BlockFace;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

@Getter
@ToString
public class HitData {
    Point position;
    MutableVector blockHitLocation;
    MinestomWrappedBlockState state;
    BlockFace closestDirection;

    public HitData(Point position, MutableVector blockHitLocation, BlockFace closestDirection, MinestomWrappedBlockState state) {
        this.position = position;
        this.blockHitLocation = blockHitLocation;
        this.closestDirection = closestDirection;
        this.state = state;
    }

    public Point getRelativeBlockHitLocation() {
        return new Vec(blockHitLocation.getX() - position.x(), blockHitLocation.getY() - position.y(), blockHitLocation.getZ() - position.z());
    }
}

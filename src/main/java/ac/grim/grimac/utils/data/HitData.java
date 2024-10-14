package ac.grim.grimac.utils.data;

import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import ac.grim.grimac.utils.vector.Vector3i;
import lombok.Getter;
import lombok.ToString;
import net.minestom.server.instance.block.BlockFace;

@Getter
@ToString
public class HitData {
    Vector3i position;
    MutableVector blockHitLocation;
    MinestomWrappedBlockState state;
    BlockFace closestDirection;

    public HitData(Vector3i position, MutableVector blockHitLocation, BlockFace closestDirection, MinestomWrappedBlockState state) {
        this.position = position;
        this.blockHitLocation = blockHitLocation;
        this.closestDirection = closestDirection;
        this.state = state;
    }

    public Vector3d getRelativeBlockHitLocation() {
        return new Vector3d(blockHitLocation.getX() - position.getX(), blockHitLocation.getY() - position.getY(), blockHitLocation.getZ() - position.getZ());
    }
}

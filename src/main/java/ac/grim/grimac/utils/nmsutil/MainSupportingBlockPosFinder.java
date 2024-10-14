package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.MainSupportingBlockData;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@UtilityClass
public class MainSupportingBlockPosFinder {
    public MainSupportingBlockData findMainSupportingBlockPos(GrimPlayer player, MainSupportingBlockData lastSupportingBlock, Point lastMovement, SimpleCollisionBox maxPose, boolean isOnGround) {
        if (!isOnGround) {
            return new MainSupportingBlockData(null, false);
        }

        SimpleCollisionBox slightlyBelowPlayer = new SimpleCollisionBox(maxPose.minX, maxPose.minY - 1.0E-6D, maxPose.minZ, maxPose.maxX, maxPose.minY, maxPose.maxZ);

        Optional<Point> supportingBlock = findSupportingBlock(player, slightlyBelowPlayer);
        if (!supportingBlock.isPresent() && (!lastSupportingBlock.lastOnGroundAndNoBlock())) {
            if (lastMovement != null) {
                SimpleCollisionBox aabb2 = slightlyBelowPlayer.offset(-lastMovement.x(), 0.0D, -lastMovement.z());
                supportingBlock = findSupportingBlock(player, aabb2);
                return new MainSupportingBlockData(supportingBlock.orElse(null), true);
            }
        } else {
            return new MainSupportingBlockData(supportingBlock.orElse(null), true);
        }

        return new MainSupportingBlockData(null, true);
    }

    private Optional<Point> findSupportingBlock(GrimPlayer player, SimpleCollisionBox searchBox) {
        Point playerPos = new Vec(player.x, player.y, player.z);

        AtomicReference<Point> bestBlockPos = new AtomicReference<>();
        AtomicDouble blockPosDistance = new AtomicDouble(Double.MAX_VALUE);

        Collisions.forEachCollisionBox(player, searchBox, (pos) -> {
            Point blockPosAsVector3d = new Vec(pos.blockX() + 0.5, pos.blockY() + 0.5, pos.blockZ() + 0.5);
            double distance = playerPos.distanceSquared(blockPosAsVector3d);

            if (distance < blockPosDistance.get() || distance == blockPosDistance.get() && (bestBlockPos.get() == null || firstHasPriorityOverSecond(pos, bestBlockPos.get()))) {
                bestBlockPos.set(pos);
                blockPosDistance.set(distance);
            }
        });


        return Optional.ofNullable(bestBlockPos.get());
    }

    private boolean firstHasPriorityOverSecond(Point first, Point second) {
        // Order of loop is X, Y, and Z
        // We prioritize lowest Y axis, then lowest X axis, then lowest Z axis
        // Ties among the X and Z positions are broken by the order of looping being X
        //
        // X O O
        // 0 X 0
        // 0 0 X
        // If the three blocks were this, the lowest right would win because of iteration order
        //
        // X 0 0
        // 0 0 X
        // But the upper left would win here because of prioritizing negative X and negative Z
        if (first.blockY() < second.blockY()) return true;

        double sumX = second.blockX() - first.blockX();
        double sumY = second.blockZ() - first.blockZ();

        double horizontalSumTotal = sumX + sumY;
        if (horizontalSumTotal == 0) {
            // If X is farther in the X direction, then it was found later and therefore won't override
            return sumX < 0;
        }

        // Otherwise, lower X and lower Z have priority
        return horizontalSumTotal < 0;
    }
}

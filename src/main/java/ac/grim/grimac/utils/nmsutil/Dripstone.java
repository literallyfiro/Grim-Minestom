package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.Block;

public class Dripstone {
    public static MinestomWrappedBlockState update(GrimPlayer player, MinestomWrappedBlockState toPlace, int x, int y, int z, boolean secondaryUse) {
        VerticalDirection primaryDirection = toPlace.getVerticalDirection();
        VerticalDirection opposite = toPlace.getVerticalDirection() == VerticalDirection.UP ? VerticalDirection.DOWN : VerticalDirection.UP;

        MinestomWrappedBlockState typePlacingOn = player.compensatedWorld.getWrappedBlockStateAt(x, y + (primaryDirection == VerticalDirection.UP ? 1 : -1), z);

        if (isPointedDripstoneWithDirection(typePlacingOn, opposite)) {
            // Use tip if the player is sneaking, or if it already is merged (somehow)
            // secondary use is flipped, for some reason, remember!
            Thickness thick = secondaryUse && typePlacingOn.getThickness() != Thickness.TIP_MERGE ? Thickness.TIP : Thickness.TIP_MERGE;

            toPlace.setThickness(thick);
        } else {
            // Check if the blockstate air does not have the direction of UP already (somehow)
            if (!isPointedDripstoneWithDirection(typePlacingOn, primaryDirection)) {
                toPlace.setThickness(Thickness.TIP);
            } else {
                Thickness dripThick = typePlacingOn.getThickness();
                if (dripThick != Thickness.TIP && dripThick != Thickness.TIP_MERGE) {
                    // Look downwards
                    MinestomWrappedBlockState oppositeData = player.compensatedWorld.getWrappedBlockStateAt(x, y + (opposite == VerticalDirection.UP ? 1 : -1), z);
                    Thickness toSetThick = !isPointedDripstoneWithDirection(oppositeData, primaryDirection)
                            ? Thickness.BASE : Thickness.MIDDLE;
                    toPlace.setThickness(toSetThick);
                } else {
                    toPlace.setThickness(Thickness.FRUSTUM);
                }
            }
        }
        return toPlace;
    }

    private static boolean isPointedDripstoneWithDirection(MinestomWrappedBlockState unknown, VerticalDirection direction) {
        return unknown.getType() == Block.POINTED_DRIPSTONE && unknown.getVerticalDirection() == direction;
    }
}

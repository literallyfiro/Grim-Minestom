package ac.grim.grimac.checks.impl.scaffolding;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.nmsutil.Materials;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

@CheckData(name = "FabricatedPlace")
public class FabricatedPlace extends BlockPlaceCheck {
    public FabricatedPlace(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        Point cursor = place.getCursor();
        if (cursor == null) return;

        double allowed = Materials.isShapeExceedsCube(place.getPlacedAgainstMaterial()) || place.getPlacedAgainstMaterial() == Block.LECTERN ? 1.5 : 1;
        double minAllowed = 1 - allowed;

        if (cursor.x() < minAllowed || cursor.y() < minAllowed || cursor.z() < minAllowed || cursor.x() > allowed || cursor.y() > allowed || cursor.z() > allowed) {
            if (flagAndAlert() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}

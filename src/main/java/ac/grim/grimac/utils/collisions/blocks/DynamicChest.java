package ac.grim.grimac.utils.collisions.blocks;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.BlockFace;

// In 1.12, chests don't have data that say what type of chest they are, other than direction
// In 1.13, chests store whether they are left or right
// With 1.12 clients on 1.13+ servers, the client checks NORTH and WEST for chests before SOUTH and EAST
// With 1.13+ clients on 1.12 servers, ViaVersion checks NORTH and WEST for chests before SOUTH and EAST
public class DynamicChest implements CollisionFactory {
    public CollisionBox fetch(GrimPlayer player, ClientVersion version, MinestomWrappedBlockState chest, int x, int y, int z) {
        // 1.13+ clients on 1.13+ servers
        if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
            if (chest.getTypeData() == Type.SINGLE) {
                return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
            }

            if (chest.getFacing() == BlockFace.SOUTH && chest.getTypeData() == Type.RIGHT || chest.getFacing() == BlockFace.NORTH && chest.getTypeData() == Type.LEFT) {
                return new HexCollisionBox(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D); // Connected to the east face
            } else if (chest.getFacing() == BlockFace.SOUTH && chest.getTypeData() == Type.LEFT || chest.getFacing() == BlockFace.NORTH && chest.getTypeData() == Type.RIGHT) {
                return new HexCollisionBox(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D); // Connected to the west face
            } else if (chest.getFacing() == BlockFace.WEST && chest.getTypeData() == Type.RIGHT || chest.getFacing() == BlockFace.EAST && chest.getTypeData() == Type.LEFT) {
                return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D); // Connected to the south face
            } else {
                return new HexCollisionBox(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D); // Connected to the north face
            }
        }


        // 1.12 clients on 1.12 servers
        // 1.12 clients on 1.12 servers
        // 1.13 clients on 1.12 servers
        if (chest.getFacing() == BlockFace.EAST || chest.getFacing() == BlockFace.WEST) {
            MinestomWrappedBlockState westState = player.compensatedWorld.getWrappedBlockStateAt(x - 1, y, z);

            if (westState.getType() == chest.getType()) {
                return new HexCollisionBox(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D); // Connected to the west face
            }

            MinestomWrappedBlockState eastState = player.compensatedWorld.getWrappedBlockStateAt(x + 1, y, z);
            if (eastState.getType() == chest.getType()) {
                return new HexCollisionBox(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D); // Connected to the east face
            }
        } else {
            MinestomWrappedBlockState northState = player.compensatedWorld.getWrappedBlockStateAt(x, y, z - 1);
            if (northState.getType() == chest.getType()) {
                return new HexCollisionBox(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D); // Connected to the north face
            }

            MinestomWrappedBlockState southState = player.compensatedWorld.getWrappedBlockStateAt(x, y, z + 1);
            if (southState.getType() == chest.getType()) {
                return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D); // Connected to the south face
            }
        }

        // Single chest
        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    }
}

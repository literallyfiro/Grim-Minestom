package ac.grim.grimac.utils.collisions.blocks;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

// 1.13 clients on 1.12 servers have this mostly working, but chorus flowers don’t attach to chorus plants.
// 1.12 clients run their usual calculations on 1.13 servers, same as 1.12 servers
// 1.13 clients on 1.13 servers get everything included in the block data, no world reading required
public class DynamicChorusPlant implements CollisionFactory {
    private static final BlockFace[] directions = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.TOP, BlockFace.BOTTOM};
    private static final CollisionBox[] modernShapes = makeShapes();

    private static CollisionBox[] makeShapes() {
        float f = 0.5F - (float) 0.3125;
        float f1 = 0.5F + (float) 0.3125;
        SimpleCollisionBox baseShape = new SimpleCollisionBox(f, f, f, f1, f1, f1, false);
        CollisionBox[] avoxelshape = new CollisionBox[directions.length];

        for (int i = 0; i < directions.length; ++i) {
            BlockFace direction = directions[i];
            avoxelshape[i] = new SimpleCollisionBox(0.5D + Math.min(-(float) 0.3125, (double) direction.toDirection().normalX() * 0.5D), 0.5D + Math.min(-(float) 0.3125, (double) direction.toDirection().normalY() * 0.5D), 0.5D + Math.min(-(float) 0.3125, (double) direction.toDirection().normalZ() * 0.5D), 0.5D + Math.max((float) 0.3125, (double) direction.toDirection().normalX() * 0.5D), 0.5D + Math.max((float) 0.3125, (double) direction.toDirection().normalY() * 0.5D), 0.5D + Math.max((float) 0.3125, (double) direction.toDirection().normalZ() * 0.5D), false);
        }

        CollisionBox[] avoxelshape1 = new CollisionBox[64];

        for (int k = 0; k < 64; ++k) {
            ComplexCollisionBox directionalShape = new ComplexCollisionBox(baseShape);

            for (int j = 0; j < directions.length; ++j) {
                if ((k & 1 << j) != 0) {
                    directionalShape.add(avoxelshape[j]);
                }
            }

            avoxelshape1[k] = directionalShape;
        }

        return avoxelshape1;
    }

    @Override
    public CollisionBox fetch(GrimPlayer player, ClientVersion version, MinestomWrappedBlockState block, int x, int y, int z) {
        // ViaVersion replacement block (Purple wool)
        if (version.isOlderThanOrEquals(ClientVersion.V_1_8))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        // Player is 1.12- on 1.13 server
        // Player is 1.12 on 1.12 server
        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
            return getLegacyBoundingBox(player, version, x, y, z);
        }

        Set<BlockFace> directions;

        directions = new HashSet<>();
        if (block.getWest() == West.TRUE) directions.add(BlockFace.WEST);
        if (block.getEast() == East.TRUE) directions.add(BlockFace.EAST);
        if (block.getNorth() == North.TRUE) directions.add(BlockFace.NORTH);
        if (block.getSouth() == South.TRUE) directions.add(BlockFace.SOUTH);
        if (block.isUp()) directions.add(BlockFace.TOP);
        if (block.isDown()) directions.add(BlockFace.BOTTOM);

        // Player is 1.13+ on 1.13+ server
        return modernShapes[getAABBIndex(directions)].copy();
    }

    public CollisionBox getLegacyBoundingBox(GrimPlayer player, ClientVersion version, int x, int y, int z) {
        Set<BlockFace> faces = getLegacyStates(player, version, x, y, z);

        float f1 = faces.contains(BlockFace.WEST) ? 0.0F : 0.1875F;
        float f2 = faces.contains(BlockFace.BOTTOM) ? 0.0F : 0.1875F;
        float f3 = faces.contains(BlockFace.NORTH) ? 0.0F : 0.1875F;
        float f4 = faces.contains(BlockFace.EAST) ? 1.0F : 0.8125F;
        float f5 = faces.contains(BlockFace.TOP) ? 1.0F : 0.8125F;
        float f6 = faces.contains(BlockFace.SOUTH) ? 1.0F : 0.8125F;

        return new SimpleCollisionBox(f1, f2, f3, f4, f5, f6);
    }

    public Set<BlockFace> getLegacyStates(GrimPlayer player, ClientVersion version, int x, int y, int z) {
        Set<BlockFace> faces = new HashSet<>();

        // 1.13 clients on 1.12 servers don't see chorus flowers attached to chorus because of a ViaVersion bug
        Block versionFlower = version.isOlderThanOrEquals(ClientVersion.V_1_12_2) ? Block.CHORUS_FLOWER : null;

        Block downBlock = player.compensatedWorld.getStateTypeAt(x, y - 1, z);
        Block upBlock = player.compensatedWorld.getStateTypeAt(x, y + 1, z);
        Block northBlock = player.compensatedWorld.getStateTypeAt(x, y, z - 1);
        Block eastBlock = player.compensatedWorld.getStateTypeAt(x + 1, y, z);
        Block southBlock = player.compensatedWorld.getStateTypeAt(x, y, z + 1);
        Block westBlock = player.compensatedWorld.getStateTypeAt(x - 1, y, z);

        if (downBlock == Block.CHORUS_PLANT || downBlock == versionFlower || downBlock == Block.END_STONE) {
            faces.add(BlockFace.BOTTOM);
        }

        if (upBlock == Block.CHORUS_PLANT || upBlock == versionFlower) {
            faces.add(BlockFace.TOP);
        }
        if (northBlock == Block.CHORUS_PLANT || northBlock == versionFlower) {
            faces.add(BlockFace.EAST);
        }
        if (eastBlock == Block.CHORUS_PLANT || eastBlock == versionFlower) {
            faces.add(BlockFace.EAST);
        }
        if (southBlock == Block.CHORUS_PLANT || southBlock == versionFlower) {
            faces.add(BlockFace.NORTH);
        }
        if (westBlock == Block.CHORUS_PLANT || westBlock == versionFlower) {
            faces.add(BlockFace.NORTH);
        }

        return faces;
    }

    protected int getAABBIndex(Set<BlockFace> p_196486_1_) {
        int i = 0;

        for (int j = 0; j < directions.length; ++j) {
            if (p_196486_1_.contains(directions[j])) {
                i |= 1 << j;
            }
        }

        return i;
    }
}

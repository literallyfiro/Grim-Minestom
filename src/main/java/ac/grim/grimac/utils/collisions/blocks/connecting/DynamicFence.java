package ac.grim.grimac.utils.collisions.blocks.connecting;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.CollisionData;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

public class DynamicFence extends DynamicConnecting implements CollisionFactory {
    private static final CollisionBox[] COLLISION_BOXES = makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F, true);

    public static SimpleCollisionBox[] LEGACY_BOUNDING_BOXES = new SimpleCollisionBox[] {new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new SimpleCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    @Override
    public CollisionBox fetch(GrimPlayer player, ClientVersion version, MinestomWrappedBlockState block, int x, int y, int z) {
        boolean east;
        boolean north;
        boolean south;
        boolean west;

        // 1.13+ servers on 1.13+ clients send the full fence data
        if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
            east = block.getEast() != East.FALSE;
            north = block.getNorth() != North.FALSE;
            south = block.getSouth() != South.FALSE;
            west = block.getWest() != West.FALSE;
        } else {
            east = connectsTo(player, version, x, y, z, BlockFace.EAST);
            north = connectsTo(player, version, x, y, z, BlockFace.NORTH);
            south = connectsTo(player, version, x, y, z, BlockFace.SOUTH);
            west = connectsTo(player, version, x, y, z, BlockFace.WEST);
        }

        return COLLISION_BOXES[getAABBIndex(north, east, south, west)].copy();
    }

    @Override
    public boolean checkCanConnect(GrimPlayer player, MinestomWrappedBlockState state, Block one, Block two, BlockFace direction) {
        if (BlockTags.FENCES.contains(one))
            return !(one == Block.NETHER_BRICK_FENCE) && !(two == Block.NETHER_BRICK_FENCE);
        else
            return BlockTags.FENCES.contains(one) || CollisionData.getData(one).getMovementCollisionBox(player, player.getClientVersion(), state, 0, 0, 0).isSideFullBlock(direction);
    }
}

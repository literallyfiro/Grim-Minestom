package ac.grim.grimac.utils.collisions;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.blocks.connecting.DynamicFence;
import ac.grim.grimac.utils.collisions.blocks.connecting.DynamicWall;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.HitBoxFactory;
import ac.grim.grimac.utils.collisions.datatypes.NoCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Expansion to the CollisionData class, which is different than regular ray tracing hitboxes
public enum HitboxData {
    SCAFFOLDING((player, item, version, data, x, y, z) -> {
        // If is holding scaffolding
        if (item == Block.SCAFFOLDING) {
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);
        }

        // STABLE_SHAPE for the scaffolding
        ComplexCollisionBox box = new ComplexCollisionBox(
                new HexCollisionBox(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D),
                new HexCollisionBox(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D),
                new HexCollisionBox(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
                new HexCollisionBox(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D),
                new HexCollisionBox(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D));

        if (data.getHalf() == Half.LOWER) { // Add the unstable shape to the collision boxes
            box.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D));
            box.add(new HexCollisionBox(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D));
            box.add(new HexCollisionBox(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D));
            box.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D));
        }

        return box;
    }, Block.SCAFFOLDING),

    DRIPLEAF((player, item, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_16_4))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        ComplexCollisionBox box = new ComplexCollisionBox();

        if (data.getFacing() == BlockFace.NORTH) { // Stem
            box.add(new HexCollisionBox(5.0D, 0.0D, 9.0D, 11.0D, 15.0D, 15.0D));
        } else if (data.getFacing() == BlockFace.SOUTH) {
            box.add(new HexCollisionBox(5.0D, 0.0D, 1.0D, 11.0D, 15.0D, 7.0D));
        } else if (data.getFacing() == BlockFace.EAST) {
            box.add(new HexCollisionBox(1.0D, 0.0D, 5.0D, 7.0D, 15.0D, 11.0D));
        } else {
            box.add(new HexCollisionBox(9.0D, 0.0D, 5.0D, 15.0D, 15.0D, 11.0D));
        }

        if (data.getTilt() == Tilt.NONE || data.getTilt() == Tilt.UNSTABLE) {
            box.add(new HexCollisionBox(0.0, 11.0, 0.0, 16.0, 15.0, 16.0));
        } else if (data.getTilt() == Tilt.PARTIAL) {
            box.add(new HexCollisionBox(0.0, 11.0, 0.0, 16.0, 13.0, 16.0));
        }

        return box;

    }, Block.BIG_DRIPLEAF),

    FENCE_GATE((player, item, version, data, x, y, z) -> {
        // This technically should be taken from the block data/made multi-version/run block updates... but that's too far even for me
        // This way is so much easier and works unless the magic stick wand is used
        boolean isInWall;
        boolean isXAxis = data.getFacing() == BlockFace.WEST || data.getFacing() == BlockFace.EAST;
        if (isXAxis) {
            boolean zPosWall = Materials.isWall(player.compensatedWorld.getStateTypeAt(x, y, z + 1));
            boolean zNegWall = Materials.isWall(player.compensatedWorld.getStateTypeAt(x, y, z - 1));
            isInWall = zPosWall || zNegWall;
        } else {
            boolean xPosWall = Materials.isWall(player.compensatedWorld.getStateTypeAt(x + 1, y, z));
            boolean xNegWall = Materials.isWall(player.compensatedWorld.getStateTypeAt(x - 1, y, z));
            isInWall = xPosWall || xNegWall;
        }

        if (isInWall) {
            return isXAxis ? new HexCollisionBox(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D) : new HexCollisionBox(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
        }

        return isXAxis ? new HexCollisionBox(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D) : new HexCollisionBox(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    }, BlockTags.FENCE_GATES.getStates().toArray(new Block[0])),


    FENCE((player, item, version, data, x, y, z) -> {
        MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(x, y, z);

        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
            int i = 0;
            if (data.getSouth() == South.TRUE) {
                i |= 0b1;
            }
            if (data.getWest() == West.TRUE) {
                i |= 0b10;
            }
            if (data.getNorth() == North.TRUE) {
                i |= 0b100;
            }
            if (data.getEast() == East.TRUE) {
                i |= 0b1000;
            }

            return DynamicFence.LEGACY_BOUNDING_BOXES[i].copy();
        }

        List<SimpleCollisionBox> boxes = new ArrayList<>();
        CollisionData.getData(state.getType()).getMovementCollisionBox(player, version, state).downCast(boxes);

        for (SimpleCollisionBox box : boxes) {
            box.maxY = 1;
        }

        return new ComplexCollisionBox(boxes.toArray(new SimpleCollisionBox[0]));
    }, BlockTags.FENCES.getStates().toArray(new Block[0])),

    WALL((player, item, version, data, x, y, z) -> {
        MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(x, y, z);
        return new DynamicWall().fetchRegularBox(player, state, version, x, y, z);
    }, BlockTags.WALLS.getStates().toArray(new Block[0])),

    HONEY_BLOCK(new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true), Block.HONEY_BLOCK),

    POWDER_SNOW(new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true), Block.POWDER_SNOW),

    SOUL_SAND(new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true), Block.SOUL_SAND),

    CACTUS(new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D), Block.CACTUS),

    SNOW((player, item, version, data, x, y, z) -> {
        return new SimpleCollisionBox(0, 0, 0, 1, data.getLayers() * 0.125, 1);
    }, Block.SNOW),

    LECTERN_BLOCK((player, item, version, data, x, y, z) -> {
        ComplexCollisionBox common = new ComplexCollisionBox(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
                new HexCollisionBox(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));

        if (data.getFacing() == BlockFace.WEST) {
            common.add(new HexCollisionBox(1.0D, 10.0D, 0.0D, 5.333333D, 14.0D, 16.0D));
            common.add(new HexCollisionBox(5.333333D, 12.0D, 0.0D, 9.666667D, 16.0D, 16.0D));
            common.add(new HexCollisionBox(9.666667D, 14.0D, 0.0D, 14.0D, 18.0D, 16.0D));
        } else if (data.getFacing() == BlockFace.NORTH) {
            common.add(new HexCollisionBox(0.0D, 10.0D, 1.0D, 16.0D, 14.0D, 5.333333D));
            common.add(new HexCollisionBox(0.0D, 12.0D, 5.333333D, 16.0D, 16.0D, 9.666667D));
            common.add(new HexCollisionBox(0.0D, 14.0D, 9.666667D, 16.0D, 18.0D, 14.0D));
        } else if (data.getFacing() == BlockFace.EAST) {
            common.add(new HexCollisionBox(10.666667D, 10.0D, 0.0D, 15.0D, 14.0D, 16.0D));
            common.add(new HexCollisionBox(6.333333D, 12.0D, 0.0D, 10.666667D, 16.0D, 16.0D));
            common.add(new HexCollisionBox(2.0D, 14.0D, 0.0D, 6.333333D, 18.0D, 16.0D));
        } else { // SOUTH
            common.add(new HexCollisionBox(0.0D, 10.0D, 10.666667D, 16.0D, 14.0D, 15.0D));
            common.add(new HexCollisionBox(0.0D, 12.0D, 6.333333D, 16.0D, 16.0D, 10.666667D));
            common.add(new HexCollisionBox(0.0D, 14.0D, 2.0D, 16.0D, 18.0D, 6.333333D));
        }

        return common;
    }, Block.LECTERN),

    WALL_HANGING_SIGNS((player, item, version, data, x, y, z) -> {
        return switch (data.getFacing()) {
            case NORTH, SOUTH -> new ComplexCollisionBox(new HexCollisionBox(0.0D, 14.0D, 6.0D, 16.0D, 16.0D, 10.0D),
                    new HexCollisionBox(1.0D, 0.0D, 7.0D, 15.0D, 10.0D, 9.0D));
            case WEST, EAST -> new ComplexCollisionBox(new HexCollisionBox(6.0D, 14.0D, 0.0D, 10.0D, 16.0D, 16.0D),
                    new HexCollisionBox(7.0D, 0.0D, 1.0D, 9.0D, 10.0D, 15.0D));
            default -> NoCollisionBox.INSTANCE;
        };
    }, BlockTags.WALL_HANGING_SIGNS.getStates().toArray(new Block[0])),

    PITCHER_CROP((player, item, version, data, x, y, z) -> {
        final SimpleCollisionBox FULL_UPPER_SHAPE = new HexCollisionBox(3.0D, 0.0D, 3.0D, 13.0D, 15.0D, 13.0D);
        final SimpleCollisionBox FULL_LOWER_SHAPE = new HexCollisionBox(3.0D, -1.0D, 3.0D, 13.0D, 16.0D, 13.0D);
        final SimpleCollisionBox COLLISION_SHAPE_BULB = new HexCollisionBox(5.0D, -1.0D, 5.0D, 11.0D, 3.0D, 11.0D);
        final SimpleCollisionBox COLLISION_SHAPE_CROP = new HexCollisionBox(3.0D, -1.0D, 3.0D, 13.0D, 5.0D, 13.0D);
        final SimpleCollisionBox[] UPPER_SHAPE_BY_AGE = new SimpleCollisionBox[]{new HexCollisionBox(3.0D, 0.0D, 3.0D, 13.0D, 11.0D, 13.0D), FULL_UPPER_SHAPE};
        final SimpleCollisionBox[] LOWER_SHAPE_BY_AGE = new SimpleCollisionBox[]{COLLISION_SHAPE_BULB, new HexCollisionBox(3.0D, -1.0D, 3.0D, 13.0D, 14.0D, 13.0D), FULL_LOWER_SHAPE, FULL_LOWER_SHAPE, FULL_LOWER_SHAPE};

        return data.getHalf() == Half.UPPER ? UPPER_SHAPE_BY_AGE[Math.min(Math.abs(4 - (data.getAge() + 1)), UPPER_SHAPE_BY_AGE.length - 1)] : LOWER_SHAPE_BY_AGE[data.getAge()];
    }, Block.PITCHER_CROP),

    BUTTON((player, item, version, data, x, y, z) -> {
        final BlockFace facing = data.getFacing();
        final boolean powered = data.isPowered();
        switch (data.getFace()) {
            case FLOOR:
                // x axis
                if (facing == BlockFace.EAST || facing == BlockFace.WEST) {
                    return powered ? new HexCollisionBox(6.0, 0.0, 5.0, 10.0, 1.0, 11.0) : new HexCollisionBox(6.0, 0.0, 5.0, 10.0, 2.0, 11.0);
                }

                return powered ? new HexCollisionBox(5.0, 0.0, 6.0, 11.0, 1.0, 10.0) : new HexCollisionBox(5.0, 0.0, 6.0, 11.0, 2.0, 10.0);
            case WALL:
                CollisionBox shape = switch (facing) {
                    case EAST ->
                            powered ? new HexCollisionBox(0.0, 6.0, 5.0, 1.0, 10.0, 11.0) : new HexCollisionBox(0.0, 6.0, 5.0, 2.0, 10.0, 11.0);
                    case WEST ->
                            powered ? new HexCollisionBox(15.0, 6.0, 5.0, 16.0, 10.0, 11.0) : new HexCollisionBox(14.0, 6.0, 5.0, 16.0, 10.0, 11.0);
                    case SOUTH ->
                            powered ? new HexCollisionBox(5.0, 6.0, 0.0, 11.0, 10.0, 1.0) : new HexCollisionBox(5.0, 6.0, 0.0, 11.0, 10.0, 2.0);
                    case NORTH, TOP, BOTTOM ->
                            powered ? new HexCollisionBox(5.0, 6.0, 15.0, 11.0, 10.0, 16.0) : new HexCollisionBox(5.0, 6.0, 14.0, 11.0, 10.0, 16.0);
                    default -> NoCollisionBox.INSTANCE;
                };

                return shape;
            case CEILING:
            default:
                // x axis
                if (facing == BlockFace.EAST || facing == BlockFace.WEST) {
                    return powered ? new HexCollisionBox(6.0, 15.0, 5.0, 10.0, 16.0, 11.0) : new HexCollisionBox(6.0, 14.0, 5.0, 10.0, 16.0, 11.0);
                } else {
                    return powered ? new HexCollisionBox(5.0, 15.0, 6.0, 11.0, 16.0, 10.0) : new HexCollisionBox(5.0, 14.0, 6.0, 11.0, 16.0, 10.0);
                }
        }
    }, BlockTags.BUTTONS.getStates().toArray(new Block[0])),

    WALL_SIGN((player, item, version, data, x, y, z) -> {
        return switch (data.getFacing()) {
            case NORTH -> new HexCollisionBox(0.0, 4.5, 14.0, 16.0, 12.5, 16.0);
            case SOUTH -> new HexCollisionBox(0.0, 4.5, 0.0, 16.0, 12.5, 2.0);
            case EAST -> new HexCollisionBox(0.0, 4.5, 0.0, 2.0, 12.5, 16.0);
            case WEST -> new HexCollisionBox(14.0, 4.5, 0.0, 16.0, 12.5, 16.0);
            default -> NoCollisionBox.INSTANCE;
        };
    }, BlockTags.WALL_SIGNS.getStates().toArray(new Block[0])),

    WALL_HANGING_SIGN((player, item, version, data, x, y, z) -> {
        return switch (data.getFacing()) {
            case NORTH, SOUTH -> new ComplexCollisionBox(new HexCollisionBox(0.0D, 14.0D, 6.0D, 16.0D, 16.0D, 10.0D),
                    new HexCollisionBox(1.0D, 0.0D, 7.0D, 15.0D, 10.0D, 9.0D));
            default -> new ComplexCollisionBox(new HexCollisionBox(6.0D, 14.0D, 0.0D, 10.0D, 16.0D, 16.0D),
                    new HexCollisionBox(7.0D, 0.0D, 1.0D, 9.0D, 10.0D, 15.0D));
        };
    }, BlockTags.WALL_HANGING_SIGNS.getStates().toArray(new Block[0])),

    STANDING_SIGN((player, item, version, data, x, y, z) ->
            new HexCollisionBox(4.0, 0.0, 4.0, 12.0, 16.0, 12.0),
            BlockTags.STANDING_SIGNS.getStates().toArray(new Block[0])),

    REDSTONE_WIRE((player, item, version, data, x, y, z) ->
            // Easier to just use no collision box
            // Redstone wire is very complex with its collision shapes and has many de-syncs
            NoCollisionBox.INSTANCE,
            Block.REDSTONE_WIRE),

    FIRE((player, item, version, data, x, y, z) -> {
        // Since 1.16 fire has a small hitbox
        if (version.isNewerThanOrEquals(ClientVersion.V_1_16)) {
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
        }

        return NoCollisionBox.INSTANCE;
    }, BlockTags.FIRE.getStates().toArray(new Block[0])),

    BANNER(((player, item, version, data, x, y, z) ->
            new SimpleCollisionBox(4.0, 0.0, 4.0, 12.0, 16.0, 12.0)),
            BlockTags.BANNERS.getStates().toArray(new Block[0]));


    private static final Map<Block, HitboxData> lookup = new HashMap<>();

    static {
        for (HitboxData data : HitboxData.values()) {
            for (Block type : data.materials) {
                lookup.put(type, data);
            }
        }
    }

    private final Block[] materials;
    private CollisionBox box;
    private HitBoxFactory dynamic;

    HitboxData(CollisionBox box, Block... materials) {
        this.box = box;
        Set<Block> mList = new HashSet<>(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Block[0]);
    }

    HitboxData(HitBoxFactory dynamic, Block... materials) {
        this.dynamic = dynamic;
        Set<Block> mList = new HashSet<>(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Block[0]);
    }

    public static HitboxData getData(Block material) {
        return lookup.get(material);
    }

    public static CollisionBox getBlockHitbox(GrimPlayer player, Block heldItem, ClientVersion version, MinestomWrappedBlockState block, int x, int y, int z) {
        HitboxData data = getData(block.getType());

        if (data == null) {
            // Fall back to collision boxes
            return CollisionData.getRawData(block.getType()).getMovementCollisionBox(player, version, block, x, y, z);
        }

        // Simple collision box to override
        if (data.box != null)
            return data.box.copy().offset(x, y, z);

        // Allow this class to override collision boxes when they aren't the same as regular boxes
        return HitboxData.getData(block.getType()).dynamic.fetch(player, heldItem, version, block, x, y, z).offset(x, y, z);
    }
}

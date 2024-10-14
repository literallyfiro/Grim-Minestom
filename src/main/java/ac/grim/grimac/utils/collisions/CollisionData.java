package ac.grim.grimac.utils.collisions;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.predictionengine.movementtick.MovementTickerStrider;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.blocks.DoorHandler;
import ac.grim.grimac.utils.collisions.blocks.DynamicChest;
import ac.grim.grimac.utils.collisions.blocks.DynamicChorusPlant;
import ac.grim.grimac.utils.collisions.blocks.DynamicStair;
import ac.grim.grimac.utils.collisions.blocks.PistonBaseCollision;
import ac.grim.grimac.utils.collisions.blocks.PistonHeadCollision;
import ac.grim.grimac.utils.collisions.blocks.TrapDoorHandler;
import ac.grim.grimac.utils.collisions.blocks.connecting.DynamicFence;
import ac.grim.grimac.utils.collisions.blocks.connecting.DynamicPane;
import ac.grim.grimac.utils.collisions.blocks.connecting.DynamicWall;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.CollisionFactory;
import ac.grim.grimac.utils.collisions.datatypes.ComplexCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.DynamicCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.HexCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.NoCollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

// Warning for major game updates!
// Do not use an enum for stuff like Axis and other data types not in 1.7
// Meaning only stuff like getDirection() should have enums
//
// An enum will break support for all previous versions which is very bad
// An if statement for new data types is perfectly safe and should be used instead
//
// This is actually mean to be put into PacketEvents, but I don't like proprietary plugins stealing my code...
public enum CollisionData {
    VINE((player, version, block, x, y, z) -> {
        ComplexCollisionBox boxes = new ComplexCollisionBox();

        if (block.isUp())
            boxes.add(new HexCollisionBox(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D));

        if (block.getWest() == West.TRUE)
            boxes.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D));

        if (block.getEast() == East.TRUE)
            boxes.add(new HexCollisionBox(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));

        if (block.getNorth() == North.TRUE)
            boxes.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D));

        if (block.getSouth() == South.TRUE)
            boxes.add(new HexCollisionBox(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D));

        // This is where fire differs from vine with its hitbox
        if (block.getBlock() == Block.FIRE.registry().material().block() && boxes.isNull())
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

        return boxes;

    }, Block.VINE, Block.FIRE),

    LAVA((player, version, block, x, y, z) -> {
        if (MovementTickerStrider.isAbove(player) && player.compensatedEntities.getSelf().getRiding() instanceof PacketEntityStrider) {
            if (block.getLevel() == 0) {
                return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
            }
        }

        return NoCollisionBox.INSTANCE;
    }, Block.LAVA),

    WATER(NoCollisionBox.INSTANCE, Block.WATER),

    BREWINGSTAND((player, version, block, x, y, z) -> {
        int base = 0;

        if (version.isNewerThanOrEquals(ClientVersion.V_1_13))
            base = 1;

        return new ComplexCollisionBox(
                new HexCollisionBox(base, 0, base, 16 - base, 2, 16 - base),
                new SimpleCollisionBox(0.4375, 0.0, 0.4375, 0.5625, 0.875, 0.5625, false));

    }, Block.BREWING_STAND),

    BAMBOO((player, version, block, x, y, z) -> {
        // ViaVersion replacement block - sugarcane
        if (version.isOlderThan(ClientVersion.V_1_13_2))
            return NoCollisionBox.INSTANCE;

        // Offset taken from NMS
        long i = (x * 3129871L) ^ (long) z * 116129781L ^ (long) 0;
        i = i * i * 42317861L + i * 11L;
        i = i >> 16;

        return new HexCollisionBox(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D).offset((((i & 15L) / 15.0F) - 0.5D) * 0.5D, 0, (((i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
    }, Block.BAMBOO),


    BAMBOO_SAPLING((player, version, block, x, y, z) -> {
        long i = (x * 3129871L) ^ (long) z * 116129781L ^ (long) 0;
        i = i * i * 42317861L + i * 11L;
        i = i >> 16;

        return new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D).offset((((i & 15L) / 15.0F) - 0.5D) * 0.5D, 0, (((i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
    }, Block.BAMBOO_SAPLING),

    COMPOSTER((player, version, block, x, y, z) -> {
        double height = 0.125;

        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2))
            height = 0.25;

        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2))
            height = 0.3125;

        return new ComplexCollisionBox(
                new SimpleCollisionBox(0, 0, 0, 1, height, 1, false),
                new SimpleCollisionBox(0, height, 0, 0.125, 1, 1, false),
                new SimpleCollisionBox(1 - 0.125, height, 0, 1, 1, 1, false),
                new SimpleCollisionBox(0, height, 0, 1, 1, 0.125, false),
                new SimpleCollisionBox(0, height, 1 - 0.125, 1, 1, 1, false));
    }, Block.COMPOSTER),

    RAIL(new SimpleCollisionBox(0, 0, 0, 1, 0.125, 0, false),
            Block.RAIL, Block.ACTIVATOR_RAIL,
            Block.DETECTOR_RAIL, Block.POWERED_RAIL),

    ANVIL((player, version, data, x, y, z) -> {
        BlockFace face = data.getFacing();
        // Anvil collision box was changed in 1.13 to be more accurate
        // https://www.mcpk.wiki/wiki/Version_Differences
        // The base is 0.75×0.75, and its floor is 0.25b high.
        // The top is 1×0.625, and its ceiling is 0.375b low.
        if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
            ComplexCollisionBox complexAnvil = new ComplexCollisionBox();
            // Base of the anvil
            complexAnvil.add(new HexCollisionBox(2, 0, 2, 14, 4, 14));
            if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                complexAnvil.add(new HexCollisionBox(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D));
                complexAnvil.add(new HexCollisionBox(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D));
                complexAnvil.add(new HexCollisionBox(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));
            } else {
                complexAnvil.add(new HexCollisionBox(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D));
                complexAnvil.add(new HexCollisionBox(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D));
                complexAnvil.add(new HexCollisionBox(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));
            }

            return complexAnvil;
        } else {
            // Just a single solid collision box with 1.12
            if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                return new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F, false);
            } else {
                return new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F, false);
            }
        }
    }, BlockTags.ANVIL.getStates().toArray(new Block[0])),

    WALL(new DynamicWall(), BlockTags.WALLS.getStates().toArray(new Block[0])),

    SLAB((player, version, data, x, y, z) -> {
        Type slabType = data.getTypeData();
        if (slabType == Type.DOUBLE) {
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);
        } else if (slabType == Type.BOTTOM) {
            return new SimpleCollisionBox(0, 0, 0, 1, 0.5, 1, false);
        }

        return new SimpleCollisionBox(0, 0.5, 0, 1, 1, 1, false);
        // 1.13 can handle double slabs as it's in the block data
        // 1.12 has double slabs as a separate block, no block data to differentiate it
    }, BlockTags.SLABS.getStates().toArray(new Block[0])),

    SKULL(new SimpleCollisionBox(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F, false),
            Block.CREEPER_HEAD, Block.ZOMBIE_HEAD, Block.DRAGON_HEAD, Block.PLAYER_HEAD,
            Block.SKELETON_SKULL, Block.WITHER_SKELETON_SKULL, Block.HEAVY_CORE),
    
    PIGLIN_HEAD(new HexCollisionBox(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D), Block.PIGLIN_HEAD),

    // Overwrite previous SKULL enum for legacy, where head and wall skull isn't separate
    WALL_SKULL((player, version, data, x, y, z) -> {
        switch (data.getFacing()) {
            default:
            case NORTH:
                return new SimpleCollisionBox(0.25F, 0.25F, 0.5F, 0.75F, 0.75F, 1.0F, false);
            case SOUTH:
                return new SimpleCollisionBox(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 0.5F, false);
            case WEST:
                return new SimpleCollisionBox(0.5F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F, false);
            case EAST:
                return new SimpleCollisionBox(0.0F, 0.25F, 0.25F, 0.5F, 0.75F, 0.75F, false);
        }
    }, Block.CREEPER_WALL_HEAD, Block.DRAGON_WALL_HEAD, Block.PLAYER_WALL_HEAD, Block.ZOMBIE_WALL_HEAD,
            Block.SKELETON_WALL_SKULL, Block.WITHER_SKELETON_WALL_SKULL),

    PIGLIN_WALL_HEAD((player, version, data, x, y, z) -> {
        switch (data.getFacing()) {
            default:
            case NORTH:
                return new HexCollisionBox(3.0D, 4.0D, 8.0D, 13.0D, 12.0D, 16.0D);
            case SOUTH:
                return new HexCollisionBox(3.0D, 4.0D, 0.0D, 13.0D, 12.0D, 8.0D);
            case EAST:
                return new HexCollisionBox(0.0D, 4.0D, 3.0D, 8.0D, 12.0D, 13.0D);
            case WEST:
                return new HexCollisionBox(8.0D, 4.0D, 3.0D, 16.0D, 12.0D, 13.0D);
        }
    }, Block.PIGLIN_WALL_HEAD),

    BANNER(new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D),
            BlockTags.BANNERS.getStates().toArray(new Block[0])),

    CORAL_FAN((player, version, data, x, y, z) -> {
        return new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
    }, BlockTags.CORALS.getStates().toArray(new Block[0])),

    DOOR(new DoorHandler(), BlockTags.DOORS.getStates().toArray(new Block[0])),

    HOPPER((player, version, data, x, y, z) -> {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
            ComplexCollisionBox hopperBox = new ComplexCollisionBox();

            switch (data.getFacing()) {
                case BOTTOM:
                    hopperBox.add(new HexCollisionBox(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
                    break;
                case EAST:
                    hopperBox.add(new HexCollisionBox(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
                    break;
                case NORTH:
                    hopperBox.add(new HexCollisionBox(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
                    break;
                case SOUTH:
                    hopperBox.add(new HexCollisionBox(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
                    break;
                case WEST:
                    hopperBox.add(new HexCollisionBox(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
                    break;
            }

            hopperBox.add(new SimpleCollisionBox(0, 0.625, 0, 1.0, 0.6875, 1.0, false));
            hopperBox.add(new SimpleCollisionBox(0, 0.6875, 0, 0.125, 1, 1, false));
            hopperBox.add(new SimpleCollisionBox(0.125, 0.6875, 0, 1, 1, 0.125, false));
            hopperBox.add(new SimpleCollisionBox(0.125, 0.6875, 0.875, 1, 1, 1, false));
            hopperBox.add(new SimpleCollisionBox(0.25, 0.25, 0.25, 0.75, 0.625, 0.75, false));
            hopperBox.add(new SimpleCollisionBox(0.875, 0.6875, 0.125, 1, 1, 0.875, false));

            return hopperBox;
        } else {
            double height = 0.125 * 5;

            return new ComplexCollisionBox(
                    new SimpleCollisionBox(0, 0, 0, 1, height, 1, false),
                    new SimpleCollisionBox(0, height, 0, 0.125, 1, 1, false),
                    new SimpleCollisionBox(1 - 0.125, height, 0, 1, 1, 1, false),
                    new SimpleCollisionBox(0, height, 0, 1, 1, 0.125, false),
                    new SimpleCollisionBox(0, height, 1 - 0.125, 1, 1, 1, false));
        }

    }, Block.HOPPER),

    CAKE((player, version, data, x, y, z) -> {
        double height = 0.5;
        if (version.isOlderThan(ClientVersion.V_1_8))
            height = 0.4375;
        double eatenPosition = (1 + (data.getBites()) * 2) / 16D;
        return new SimpleCollisionBox(eatenPosition, 0, 0.0625, 1 - 0.0625, height, 1 - 0.0625, false);
    }, Block.CAKE),

    COCOA_BEANS((player, version, data, x, y, z) -> {
        return getCocoa(version, data.getAge(), data.getFacing());
    }, Block.COCOA),

    STONE_CUTTER((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    }, Block.STONECUTTER),

    SWEET_BERRY((player, version, data, x, y, z) -> {
        if (data.getAge() == 0) {
            return new HexCollisionBox(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
        } else if (data.getAge() < 3) {
            return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
        }
        return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);
    }, Block.SWEET_BERRY_BUSH),

    SAPLING(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
            BlockTags.SAPLINGS.getStates().toArray(new Block[0])),

    ROOTS(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D),
            Block.WARPED_ROOTS, Block.CRIMSON_ROOTS),

    FLOWER(new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D),
            BlockTags.SMALL_FLOWERS.getStates().toArray(new Block[0])),

    DEAD_BUSH(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D), Block.DEAD_BUSH),

    SUGARCANE(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), Block.SUGAR_CANE),

    NETHER_SPROUTS(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), Block.NETHER_SPROUTS),

    GRASS_FERN(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D),
            Block.GRASS_BLOCK, Block.FERN),

    TALL_GRASS(new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true), Block.TALL_GRASS),

    SEA_GRASS(new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
            Block.SEAGRASS),

    CAVE_VINES(new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D), Block.CAVE_VINES, Block.CAVE_VINES_PLANT),

    TWISTING_VINES_BLOCK(new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D), Block.TWISTING_VINES, Block.WEEPING_VINES),

    TWISTING_VINES(new HexCollisionBox(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), Block.TWISTING_VINES_PLANT, Block.WEEPING_VINES_PLANT),

    KELP(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D), Block.KELP),
    // Kelp block is a full block, so it by default is correct

    BELL((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        BlockFace direction = data.getFacing();

        if (data.getAttachment() == Attachment.FLOOR) {
            return direction != BlockFace.NORTH && direction != BlockFace.SOUTH ?
                    new HexCollisionBox(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D) :
                    new HexCollisionBox(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);

        }

        ComplexCollisionBox complex = new ComplexCollisionBox(
                new HexCollisionBox(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D),
                new HexCollisionBox(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D));

        if (data.getAttachment() == Attachment.CEILING) {
            complex.add(new HexCollisionBox(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));
        } else if (data.getAttachment() == Attachment.DOUBLE_WALL) {
            if (direction != BlockFace.NORTH && direction != BlockFace.SOUTH) {
                complex.add(new HexCollisionBox(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
            } else {
                complex.add(new HexCollisionBox(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
            }
        } else if (direction == BlockFace.NORTH) {
            complex.add(new HexCollisionBox(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
        } else if (direction == BlockFace.SOUTH) {
            complex.add(new HexCollisionBox(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
        } else {
            if (direction == BlockFace.EAST) {
                complex.add(new HexCollisionBox(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
            } else {
                complex.add(new HexCollisionBox(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
            }
        }

        return complex;

    }, Block.BELL),

    SCAFFOLDING((player, version, data, x, y, z) -> {
        // ViaVersion replacement block - hay block
        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        if (player.lastY > y + 1 - 1e-5 && !player.isSneaking) {
            return new ComplexCollisionBox(new HexCollisionBox(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D),
                    new HexCollisionBox(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D),
                    new HexCollisionBox(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D),
                    new HexCollisionBox(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0),
                    new HexCollisionBox(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D));
        }

        return data.getDistance() != 0 && data.isBottom() && player.lastY > y - 1e-5 ?
                new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D) :
                NoCollisionBox.INSTANCE;
    }, Block.SCAFFOLDING),

    LADDER((player, version, data, x, y, z) -> {
        int width = 3;
        if (version.isOlderThanOrEquals(ClientVersion.V_1_8))
            width = 2;

        switch (data.getFacing()) {
            case NORTH:
                return new HexCollisionBox(0.0D, 0.0D, 16.0D - width, 16.0D, 16.0D, 16.0D);
            case SOUTH:
                return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, width);
            case WEST:
                return new HexCollisionBox(16.0D - width, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
            default:
            case EAST:
                return new HexCollisionBox(0.0D, 0.0D, 0.0D, width, 16.0D, 16.0D);
        }
    }, Block.LADDER),

    CAMPFIRE((player, version, data, x, y, z) -> {
        // ViaVersion replacement block - slab if not lit or fire if lit
        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2)) {

            if (data.isLit()) {
                return NoCollisionBox.INSTANCE;
            }

            return new HexCollisionBox(0, 0, 0, 16, 8, 16);
        }

        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D);
    }, Block.CAMPFIRE, Block.SOUL_CAMPFIRE),

    LANTERN((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        if (data.isHanging()) {
            return new ComplexCollisionBox(new HexCollisionBox(5.0D, 1.0D, 5.0D, 11.0D, 8.0D, 11.0D),
                    new HexCollisionBox(6.0D, 8.0D, 6.0D, 10.0D, 10.0D, 10.0D));
        }

        return new ComplexCollisionBox(new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 7.0D, 11.0D),
                new HexCollisionBox(6.0D, 7.0D, 6.0D, 10.0D, 9.0D, 10.0D));

    }, Block.LANTERN, Block.SOUL_LANTERN),


    LECTERN((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        return new ComplexCollisionBox(
                new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), // base
                new HexCollisionBox(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D)); // post
    }, Block.LECTERN),


    HONEY_BLOCK((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_14_4))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D); // post
    }, Block.HONEY_BLOCK),

    SPORE_BLOSSOM(new HexCollisionBox(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D), Block.SPORE_BLOSSOM),

    GLOW_LICHEN((player, version, data, x, y, z) -> {
        ComplexCollisionBox box = new ComplexCollisionBox();

        if (data.isUp()) {
            box.add(new HexCollisionBox(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D));
        }
        if (data.isDown()) {
            box.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D));
        }
        if (data.getWest() == West.TRUE) {
            box.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D));
        }
        if (data.getEast() == East.TRUE) {
            box.add(new HexCollisionBox(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D));
        }
        if (data.getNorth() == North.TRUE) {
            box.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D));
        }
        if (data.getSouth() == South.TRUE) {
            box.add(new HexCollisionBox(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D));
        }

        return box;
    }, Block.GLOW_LICHEN),

    DRAGON_EGG_BLOCK(new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D), Block.DRAGON_EGG),

    GRINDSTONE((player, version, data, x, y, z) -> {
        BlockFace facing = data.getFacing();

        // ViaVersion replacement block - Anvil
        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
            // Just a single solid collision box with 1.12
            if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
                return new SimpleCollisionBox(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F, false);
            } else {
                return new SimpleCollisionBox(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F, false);
            }
        }

        if (version.isOlderThanOrEquals(ClientVersion.V_1_13_2)) {
            ComplexCollisionBox complexAnvil = new ComplexCollisionBox();
            // Base of the anvil
            complexAnvil.add(new HexCollisionBox(2, 0, 2, 14, 4, 14));

            if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
                complexAnvil.add(new HexCollisionBox(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D));
                complexAnvil.add(new HexCollisionBox(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D));
                complexAnvil.add(new HexCollisionBox(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D));
            } else {
                complexAnvil.add(new HexCollisionBox(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D));
                complexAnvil.add(new HexCollisionBox(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D));
                complexAnvil.add(new HexCollisionBox(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D));
            }

            return complexAnvil;
        }

        Face attachment = data.getFace();
        if (attachment == Face.FLOOR) {
            if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
                return new ComplexCollisionBox(new HexCollisionBox(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D),
                        new HexCollisionBox(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D),
                        new HexCollisionBox(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D),
                        new HexCollisionBox(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D),
                        new HexCollisionBox(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
            } else {
                return new ComplexCollisionBox(new HexCollisionBox(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D),
                        new HexCollisionBox(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D),
                        new HexCollisionBox(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D),
                        new HexCollisionBox(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D),
                        new HexCollisionBox(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
            }
        } else if (attachment == Face.WALL) {
            switch (facing) {
                case NORTH:
                    return new ComplexCollisionBox(new HexCollisionBox(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D),
                            new HexCollisionBox(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D),
                            new HexCollisionBox(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D),
                            new HexCollisionBox(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D),
                            new HexCollisionBox(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
                case WEST:
                    return new ComplexCollisionBox(new HexCollisionBox(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D),
                            new HexCollisionBox(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D),
                            new HexCollisionBox(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D),
                            new HexCollisionBox(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D),
                            new HexCollisionBox(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
                case SOUTH:
                    return new ComplexCollisionBox(new HexCollisionBox(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D),
                            new HexCollisionBox(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D),
                            new HexCollisionBox(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D),
                            new HexCollisionBox(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D),
                            new HexCollisionBox(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
                case EAST:
                    return new ComplexCollisionBox(new HexCollisionBox(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D),
                            new HexCollisionBox(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D),
                            new HexCollisionBox(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D),
                            new HexCollisionBox(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D),
                            new HexCollisionBox(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
            }
        } else {
            if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
                return new ComplexCollisionBox(new HexCollisionBox(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D),
                        new HexCollisionBox(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D),
                        new HexCollisionBox(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D),
                        new HexCollisionBox(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D),
                        new HexCollisionBox(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
            } else {
                return new ComplexCollisionBox(new HexCollisionBox(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D),
                        new HexCollisionBox(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D),
                        new HexCollisionBox(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D),
                        new HexCollisionBox(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D),
                        new HexCollisionBox(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
            }
        }

        return NoCollisionBox.INSTANCE;

    }, Block.GRINDSTONE),

    PANE(new DynamicPane(), Materials.getPanes().toArray(new Block[0])),

    CHAIN_BLOCK((player, version, data, x, y, z) -> {
        if (version.isOlderThan(ClientVersion.V_1_16)) {
            // viaversion replacement - iron bars
            return CollisionData.PANE.dynamic.fetch(player, version, data, x, y, z);
        }

        if (data.getAxis() == Axis.X) {
            return new HexCollisionBox(0.0D, 6.5D, 6.5D, 16.0D, 9.5D, 9.5D);
        } else if (data.getAxis() == Axis.Y) {
            return new HexCollisionBox(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
        }

        return new HexCollisionBox(6.5D, 6.5D, 0.0D, 9.5D, 9.5D, 16.0D);
    }, Block.CHAIN),

    CHORUS_PLANT(new DynamicChorusPlant(), Block.CHORUS_PLANT),

    FENCE_GATE((player, version, data, x, y, z) -> {
        if (data.isOpen())
            return NoCollisionBox.INSTANCE;

        return switch (data.getFacing()) {
            case NORTH, SOUTH -> new SimpleCollisionBox(0.0F, 0.0F, 0.375F, 1.0F, 1.5F, 0.625F, false);
            case WEST, EAST -> new SimpleCollisionBox(0.375F, 0.0F, 0.0F, 0.625F, 1.5F, 1.0F, false);
            default ->
                // This code is unreachable but the compiler does not know this
                NoCollisionBox.INSTANCE;
        };

    }, BlockTags.FENCE_GATES.getStates().toArray(new Block[0])),

    FENCE(new DynamicFence(), BlockTags.FENCES.getStates().toArray(new Block[0])),

    SNOW((player, version, data, x, y, z) -> {
        if (data.getLayers() == 1 && version.isNewerThanOrEquals(ClientVersion.V_1_13)) {
            // Via doesn't touch this
            return NoCollisionBox.INSTANCE;
//            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
//                return NoCollisionBox.INSTANCE;
//            }
//            // Handle viaversion mapping
//            if (ViaVersionUtil.isAvailable() && Via.getConfig().isSnowCollisionFix()) {
//                data = data.clone();
//                data.setLayers(2);
//            }
        }

        return new SimpleCollisionBox(0, 0, 0, 1, (data.getLayers() - 1) * 0.125, 1);
    }, Block.SNOW),

    STAIR(new DynamicStair(), BlockTags.STAIRS.getStates().toArray(new Block[0])),

    CHEST(new DynamicChest(), Block.CHEST, Block.TRAPPED_CHEST),

    ENDER_CHEST(new SimpleCollisionBox(0.0625F, 0.0F, 0.0625F,
            0.9375F, 0.875F, 0.9375F, false),
            Block.ENDER_CHEST),

    ENCHANTING_TABLE(new SimpleCollisionBox(0, 0, 0, 1, 1 - 0.25, 1, false),
            Block.ENCHANTING_TABLE),

    FRAME((player, version, data, x, y, z) -> {
        ComplexCollisionBox complexCollisionBox = new ComplexCollisionBox(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D));

        if (data.isEye()) {
            if (version.isNewerThanOrEquals(ClientVersion.V_1_13)) {  // 1.13 players have a 0.5x0.5 eye
                complexCollisionBox.add(new HexCollisionBox(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D));
            } else { // 1.12 and below players have a 0.375x0.375 eye
                complexCollisionBox.add(new HexCollisionBox(5.0D, 13.0D, 5.0D, 11.0D, 16.0D, 11.0D));
            }
        }

        return complexCollisionBox;

    }, Block.END_PORTAL_FRAME),

    CARPET((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_7_10))
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, false);

        return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F, false);
    }, BlockTags.WOOL_CARPETS.getStates().toArray(new Block[0])),

    MOSS_CARPET((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_7_10))
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, false);

        return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F, false);
    }, Block.MOSS_CARPET),

    DAYLIGHT(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.375, 1.0F, false),
            Block.DAYLIGHT_DETECTOR),

    FARMLAND((player, version, data, x, y, z) -> {
        // Thanks Mojang for changing block collisions without changing protocol version!
        // Anyways, let a 1.10/1.10.1/1.10.2 client decide what farmland collision box it uses
        if (version == ClientVersion.V_1_10) {
            if (Math.abs(player.y % 1.0) < 0.001) {
                return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);
            }
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
        }

        if (version.isNewerThanOrEquals(ClientVersion.V_1_10))
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

        return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

    }, Block.FARMLAND),

    HANGING_ROOTS(new HexCollisionBox(2.0D, 10.0D, 2.0D, 14.0D, 16.0D, 14.0D), Block.HANGING_ROOTS),

    GRASS_PATH((player, version, data, x, y, z) -> {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_9))
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

        return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

    }, Block.DIRT_PATH),

    LILYPAD((player, version, data, x, y, z) -> {
        // Boats break lilypads client sided on 1.12- clients.
        if (player.compensatedEntities.getSelf().getRiding() != null && player.compensatedEntities.getSelf().getRiding().isBoat() && version.isOlderThanOrEquals(ClientVersion.V_1_12_2))
            return NoCollisionBox.INSTANCE;

        if (version.isOlderThan(ClientVersion.V_1_9))
            return new SimpleCollisionBox(0.0f, 0.0F, 0.0f, 1.0f, 0.015625F, 1.0f, false);
        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);
    }, Block.LILY_PAD),

    BED((player, version, data, x, y, z) -> {
        // It's all the same box on 1.14 clients
        if (version.isOlderThan(ClientVersion.V_1_14))
            return new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.5625, 1.0F, false);

        ComplexCollisionBox baseBox = new ComplexCollisionBox(new HexCollisionBox(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D));

        switch (data.getFacing()) {
            case NORTH:
                baseBox.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D));
                baseBox.add(new HexCollisionBox(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D));
                break;
            case SOUTH:
                baseBox.add(new HexCollisionBox(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D));
                baseBox.add(new HexCollisionBox(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D));
                break;
            case WEST:
                baseBox.add(new HexCollisionBox(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D));
                baseBox.add(new HexCollisionBox(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D));
                break;
            case EAST:
                baseBox.add(new HexCollisionBox(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D));
                baseBox.add(new HexCollisionBox(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D));
                break;
        }

        return baseBox;
    }, BlockTags.BEDS.getStates().toArray(new Block[0])),

    TRAPDOOR(new TrapDoorHandler(), BlockTags.TRAPDOORS.getStates().toArray(new Block[0])),


    DIODES(new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F, false),
            Block.REPEATER, Block.COMPARATOR),

    STRUCTURE_VOID(new SimpleCollisionBox(0.375, 0.375, 0.375,
            0.625, 0.625, 0.625, false),
            Block.STRUCTURE_VOID),

    END_ROD((player, version, data, x, y, z) -> {
        return getEndRod(version, data.getFacing());
    }, Block.END_ROD, Block.LIGHTNING_ROD),

    CAULDRON((player, version, data, x, y, z) -> {
        double height = 0.25;

        if (version.isOlderThan(ClientVersion.V_1_13))
            height = 0.3125;

        return new ComplexCollisionBox(
                new SimpleCollisionBox(0, 0, 0, 1, height, 1, false),
                new SimpleCollisionBox(0, height, 0, 0.125, 1, 1, false),
                new SimpleCollisionBox(1 - 0.125, height, 0, 1, 1, 1, false),
                new SimpleCollisionBox(0, height, 0, 1, 1, 0.125, false),
                new SimpleCollisionBox(0, height, 1 - 0.125, 1, 1, 1, false));
    }, BlockTags.CAULDRONS.getStates().toArray(new Block[0])),

    CACTUS(new SimpleCollisionBox(0.0625, 0, 0.0625,
            1 - 0.0625, 1 - 0.0625, 1 - 0.0625, false), Block.CACTUS),


    PISTON_BASE(new PistonBaseCollision(), Block.PISTON, Block.STICKY_PISTON),

    PISTON_HEAD(new PistonHeadCollision(), Block.PISTON_HEAD),

    SOULSAND(new SimpleCollisionBox(0, 0, 0, 1, 0.875, 1, false),
            Block.SOUL_SAND),

    PICKLE((player, version, data, x, y, z) -> {
        return getPicklesBox(version, data.getPickles());
    }, Block.SEA_PICKLE),

    TURTLEEGG((player, version, data, x, y, z) -> {
        // ViaVersion replacement block (West facing cocoa beans)
        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
            return getCocoa(version, data.getEggs(), BlockFace.WEST);
        }

        if (data.getEggs() == 1) {
            return new HexCollisionBox(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
        }

        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
    }, Block.TURTLE_EGG),

    CONDUIT((player, version, data, x, y, z) -> {
        // ViaVersion replacement block - Beacon
        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        return new HexCollisionBox(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    }, Block.CONDUIT),

    POT(new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D),
            BlockTags.FLOWER_POTS.getStates().toArray(new Block[0])),

    WALL_SIGN((player, version, data, x, y, z) -> {
        switch (data.getFacing()) {
            case NORTH:
                return new HexCollisionBox(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D);
            case SOUTH:
                return new HexCollisionBox(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D);
            case WEST:
                return new HexCollisionBox(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D);
            case EAST:
                return new HexCollisionBox(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D);
            default:
                return NoCollisionBox.INSTANCE;
        }
    }, BlockTags.WALL_SIGNS.getStates().toArray(new Block[0])),

    WALL_FAN((player, version, data, x, y, z) -> {
        switch (data.getFacing()) {
            case NORTH:
                return new HexCollisionBox(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D);
            case SOUTH:
                return new HexCollisionBox(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D);
            case WEST:
                return new HexCollisionBox(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);
            case EAST:
            default:
                return new HexCollisionBox(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D);
        }
    }, BlockTags.WALL_CORALS.getStates().toArray(new Block[0])),

    CORAL_PLANT((player, version, data, x, y, z) -> {
        return new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);
    }, BlockTags.CORAL_PLANTS.getStates().toArray(new Block[0])),

    SIGN(new SimpleCollisionBox(0.25, 0.0, 0.25, 0.75, 1.0, 0.75, false),
            BlockTags.STANDING_SIGNS.getStates().toArray(new Block[0])),

    BEETROOT((player, version, data, x, y, z) -> {
        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, (data.getAge() + 1) * 2, 1.0D);
    }, Block.BEETROOTS),

    WHEAT((player, version, data, x, y, z) -> {
        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, (data.getAge() + 1) * 2, 1.0D);
    }, Block.WHEAT),

    CARROT_NETHERWART((player, version, data, x, y, z) -> {
        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, data.getAge() + 2, 1.0D);
    }, Block.CARROTS, Block.NETHER_WART),

    NETHER_WART((player, version, data, x, y, z) -> {
        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 1.0D, 5 + (data.getAge() * 3), 1.0D);
    }, Block.NETHER_WART),

    BUTTON((player, version, data, x, y, z) -> {
        double f2 = (float) (data.isPowered() ? 1 : 2) / 16.0;

        return switch (data.getFacing()) {
            case WEST -> new SimpleCollisionBox(0.0, 0.375, 0.3125, f2, 0.625, 0.6875, false);
            case EAST -> new SimpleCollisionBox(1.0 - f2, 0.375, 0.3125, 1.0, 0.625, 0.6875, false);
            case NORTH -> new SimpleCollisionBox(0.3125, 0.375, 0.0, 0.6875, 0.625, f2, false);
            case SOUTH -> new SimpleCollisionBox(0.3125, 0.375, 1.0 - f2, 0.6875, 0.625, 1.0, false);
            case BOTTOM -> new SimpleCollisionBox(0.3125, 0.0, 0.375, 0.6875, 0.0 + f2, 0.625, false);
            case TOP -> new SimpleCollisionBox(0.3125, 1.0 - f2, 0.375, 0.6875, 1.0, 0.625, false);
        };

    }, BlockTags.BUTTONS.getStates().toArray(new Block[0])),

    LEVER((player, version, data, x, y, z) -> {
        double f = 0.1875;

        return switch (data.getFacing()) {
            case WEST -> new SimpleCollisionBox(1.0 - f * 2.0, 0.2, 0.5 - f, 1.0, 0.8, 0.5 + f, false);
            case EAST -> new SimpleCollisionBox(0.0, 0.2, 0.5 - f, f * 2.0, 0.8, 0.5 + f, false);
            case NORTH -> new SimpleCollisionBox(0.5 - f, 0.2, 1.0 - f * 2.0, 0.5 + f, 0.8, 1.0, false);
            case SOUTH -> new SimpleCollisionBox(0.5 - f, 0.2, 0.0, 0.5 + f, 0.8, f * 2.0, false);
            case BOTTOM -> new SimpleCollisionBox(0.25, 0.4, 0.25, 0.75, 1.0, 0.75, false);
            case TOP -> new SimpleCollisionBox(0.25, 0.0, 0.25, 0.75, 0.6, 0.75, false);
            default -> NoCollisionBox.INSTANCE;
        };

    }, Block.LEVER),

    STONE_PRESSURE_PLATE((player, version, data, x, y, z) -> {
        if (data.isPowered()) { // Pressed
            return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
        }

        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    }, BlockTags.STONE_PRESSURE_PLATES.getStates().toArray(new Block[0])),

    WOOD_PRESSURE_PLATE((player, version, data, x, y, z) -> {
        if (data.isPowered()) { // Pressed
            return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
        }

        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    }, BlockTags.WOODEN_PRESSURE_PLATES.getStates().toArray(new Block[0])),

    OTHER_PRESSURE_PLATE((player, version, data, x, y, z) -> {
        if (data.getPower() > 0) { // Pressed
            return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
        }

        return new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    }, Block.LIGHT_WEIGHTED_PRESSURE_PLATE, Block.HEAVY_WEIGHTED_PRESSURE_PLATE),

    TRIPWIRE((player, version, data, x, y, z) -> {
        if (data.isAttached()) {
            return new HexCollisionBox(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
        }
        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    }, Block.TRIPWIRE),

    ATTACHED_PUMPKIN_STEM((player, version, data, x, y, z) -> {
        if (version.isOlderThan(ClientVersion.V_1_13))
            return new HexCollisionBox(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);

        switch (data.getFacing()) {
            case SOUTH:
                return new HexCollisionBox(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D);
            case WEST:
                return new HexCollisionBox(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
            case NORTH:
                return new HexCollisionBox(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D);
            case EAST:
            default:
                return new HexCollisionBox(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D);
        }
    }, Block.ATTACHED_MELON_STEM, Block.ATTACHED_PUMPKIN_STEM),

    PUMPKIN_STEM((player, version, data, x, y, z) -> {
        return new HexCollisionBox(7, 0, 7, 9, 2 * (data.getAge() + 1), 9);
    }, Block.PUMPKIN_STEM, Block.MELON_STEM),

    TRIPWIRE_HOOK((player, version, data, x, y, z) -> {
        switch (data.getFacing()) {
            case NORTH:
                return new HexCollisionBox(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
            case SOUTH:
                return new HexCollisionBox(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
            case WEST:
                return new HexCollisionBox(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
            case EAST:
            default:
                return new HexCollisionBox(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);
        }
    }, Block.TRIPWIRE_HOOK),

    TORCH(new HexCollisionBox(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D),
            Block.TORCH, Block.REDSTONE_TORCH),

    WALL_TORCH((player, version, data, x, y, z) -> {
        return switch (data.getFacing()) {
            case NORTH -> new HexCollisionBox(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D);
            case SOUTH -> new HexCollisionBox(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D);
            case WEST -> new HexCollisionBox(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D);
            case EAST ->
                    new HexCollisionBox(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D); // 1.13 separates wall and normal torches, 1.12 does not
            default -> new HexCollisionBox(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
        };

    }, Block.WALL_TORCH, Block.REDSTONE_WALL_TORCH),

    RAILS((player, version, data, x, y, z) -> {
        Shape shape = data.getShape();
        if (shape == Shape.ASCENDING_EAST || shape == Shape.ASCENDING_WEST ||
                shape == Shape.ASCENDING_NORTH || shape == Shape.ASCENDING_SOUTH) {
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        }

        return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    }, BlockTags.RAILS.getStates().toArray(new Block[0])),

    // Known as block 36 - has no collision box
    TECHNICAL_MOVING_PISTON(NoCollisionBox.INSTANCE, Block.MOVING_PISTON),

    // 1.17 blocks
    CANDLE((player, version, data, x, y, z) -> {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17)) {
            return switch (data.getCandles()) {
                case 1 -> new HexCollisionBox(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
                case 2 -> new HexCollisionBox(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
                case 3 -> new HexCollisionBox(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
                default -> new HexCollisionBox(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);
            };
        }

        return getPicklesBox(version, data.getCandles());
    }, BlockTags.CANDLES.getStates().toArray(new Block[0])),

    CANDLE_CAKE((player, version, data, x, y, z) -> {
        ComplexCollisionBox cake = new ComplexCollisionBox(new HexCollisionBox(1.0, 0.0, 1.0, 15.0, 8.0, 15.0));
        if (version.isNewerThanOrEquals(ClientVersion.V_1_17))
            cake.add(new HexCollisionBox(7.0, 8.0, 7.0, 9.0, 14.0, 9.0));
        return cake;
    }, BlockTags.CANDLE_CAKES.getStates().toArray(new Block[0])),

    SCULK_SENSOR(new HexCollisionBox(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.SCULK_SENSOR, Block.CALIBRATED_SCULK_SENSOR),

    DECORATED_POT(new HexCollisionBox(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0), Block.DECORATED_POT),

    BIG_DRIPLEAF((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_16_4))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        if (data.getTilt() == Tilt.NONE || data.getTilt() == Tilt.UNSTABLE) {
            return new HexCollisionBox(0.0, 11.0, 0.0, 16.0, 15.0, 16.0);
        } else if (data.getTilt() == Tilt.PARTIAL) {
            return new HexCollisionBox(0.0, 11.0, 0.0, 16.0, 13.0, 16.0);
        }

        return NoCollisionBox.INSTANCE;

    }, Block.BIG_DRIPLEAF),

    DRIPSTONE((player, version, data, x, y, z) -> {
        if (version.isOlderThan(ClientVersion.V_1_17))
            return getEndRod(version, BlockFace.TOP);

        HexCollisionBox box;

        if (data.getThickness() == Thickness.TIP_MERGE) {
            box = new HexCollisionBox(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
        } else if (data.getThickness() == Thickness.TIP) {
            if (data.getVerticalDirection() == VerticalDirection.DOWN) {
                box = new HexCollisionBox(5.0, 5.0, 5.0, 11.0, 16.0, 11.0);
            } else {
                box = new HexCollisionBox(5.0, 0.0, 5.0, 11.0, 11.0, 11.0);
            }
        } else if (data.getThickness() == Thickness.FRUSTUM) {
            box = new HexCollisionBox(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
        } else if (data.getThickness() == Thickness.MIDDLE) {
            box = new HexCollisionBox(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
        } else {
            box = new HexCollisionBox(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
        }

        // Copied from NMS and it works!  That's all you need to know.
        long i = (x * 3129871L) ^ (long) z * 116129781L ^ (long) 0;
        i = i * i * 42317861L + i * 11L;
        i = i >> 16;

        return box.offset(GrimMath.clamp((((i & 15L) / 15.0F) - 0.5D) * 0.5D, -0.125f, 0.125f), 0, GrimMath.clamp((((i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D, -0.125f, 0.125f));
    }, Block.POINTED_DRIPSTONE),

    POWDER_SNOW((player, version, data, x, y, z) -> {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_16_4))
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        // If fall distance greater than 2.5, 0.899999 box
        if (player.fallDistance > 2.5) {
            return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, 0.8999999761581421, 1.0, false);
        }

        ModifiableItemStack boots = player.getInventory().getBoots();
        if (player.lastY > y + 1 - 1e-5 && boots != null && boots.getType() == Material.LEATHER_BOOTS && !player.isSneaking && !player.compensatedEntities.getSelf().inVehicle())
            return new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true);

        return NoCollisionBox.INSTANCE;

    }, Block.POWDER_SNOW),

    NETHER_PORTAL((player, version, data, x, y, z) -> {
        if (data.getAxis() == Axis.X) {
            return new HexCollisionBox(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
        }
        return new HexCollisionBox(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    }, Block.NETHER_PORTAL),

    END_PORTAL(new HexCollisionBox(0.0D, 6.0D, 0.0D, 16.0D, 12.0D, 16.0D), Block.END_PORTAL),

    AZALEA((player, version, data, x, y, z) -> {
        return new ComplexCollisionBox(new HexCollisionBox(0.0, 8.0, 0.0, 16.0, 16.0, 16.0),
                new HexCollisionBox(6.0, 0.0, 6.0, 10.0, 8.0, 10.0));
    }, Block.AZALEA, Block.FLOWERING_AZALEA),

    AMETHYST_CLUSTER((player, version, data, x, y, z) -> {
        return getAmethystBox(version, data.getFacing(), 7, 3);
    }, Block.AMETHYST_CLUSTER),

    SMALL_AMETHYST_BUD((player, version, data, x, y, z) -> {
        return getAmethystBox(version, data.getFacing(), 3, 4);
    }, Block.SMALL_AMETHYST_BUD),

    MEDIUM_AMETHYST_BUD((player, version, data, x, y, z) -> {
        return getAmethystBox(version, data.getFacing(), 4, 3);
    }, Block.MEDIUM_AMETHYST_BUD),

    LARGE_AMETHYST_BUD((player, version, data, x, y, z) -> {
        return getAmethystBox(version, data.getFacing(), 5, 3);
    }, Block.LARGE_AMETHYST_BUD),

    MUD_BLOCK((player, version, data, x, y, z) -> {
        if (version.isNewerThanOrEquals(ClientVersion.V_1_19)) {
            return new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);
        }
        return new SimpleCollisionBox(0, 0, 0, 1, 1, 1);
    }, Block.MUD),

    MANGROVE_PROPAGULE_BLOCK((player, version, data, x, y, z) -> {
        if (!data.isHanging()) {
            return new HexCollisionBox(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
        }
        switch (data.getAge()) {
            case 0:
                return new HexCollisionBox(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            case 1:
                return new HexCollisionBox(7.0D, 10.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            case 2:
                return new HexCollisionBox(7.0D, 7.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            case 3:
                return new HexCollisionBox(7.0D, 3.0D, 7.0D, 9.0D, 16.0D, 9.0D);
            case 4:
            default:
                return new HexCollisionBox(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
        }
    }, Block.MANGROVE_PROPAGULE),

    SCULK_SHRIKER(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.SCULK_SHRIEKER),

    FROGSPAWN(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 1.5D, 16.0D), Block.FROGSPAWN),

    SNIFFER_EGG(new HexCollisionBox(1.0D, 0.0D, 2.0D, 15.0D, 16.0D, 14.0D), Block.SNIFFER_EGG),

    PINK_PETALS_BLOCK(new HexCollisionBox(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.PINK_PETALS),

    TORCHFLOWER_CROP((player, version, data, x, y, z) -> {
        if (data.getAge() == 0) {
            return new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);
        }
        // age is 1
        return new HexCollisionBox(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    }, Block.TORCHFLOWER_CROP),

    PITCHER_CROP((player, version, data, x, y, z) -> {
        final SimpleCollisionBox COLLISION_SHAPE_BULB = new HexCollisionBox(5.0D, -1.0D, 5.0D, 11.0D, 3.0D, 11.0D);
        final SimpleCollisionBox COLLISION_SHAPE_CROP = new HexCollisionBox(3.0D, -1.0D, 3.0D, 13.0D, 5.0D, 13.0D);

        if (data.getAge() == 0) {
            return COLLISION_SHAPE_BULB;
        } else {
            return data.getHalf() == Half.LOWER ? COLLISION_SHAPE_CROP : NoCollisionBox.INSTANCE;
        }
    }, Block.PITCHER_CROP),

    WALL_HANGING_SIGNS((player, version, data, x, y, z) -> {
        return switch (data.getFacing()) {
            case NORTH, SOUTH -> new HexCollisionBox(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
            case WEST, EAST -> new HexCollisionBox(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);
            default -> NoCollisionBox.INSTANCE;
        };
    }, BlockTags.WALL_HANGING_SIGNS.getStates().toArray(new Block[0])),

    NONE(NoCollisionBox.INSTANCE, Block.AIR, Block.CAVE_AIR, Block.VOID_AIR, Block.LIGHT),

    DEFAULT(new SimpleCollisionBox(0, 0, 0, 1, 1, 1, true), Block.STONE);

    // This should be an array... but a hashmap will do for now...
    private static final Map<Block, CollisionData> rawLookupMap = new IdentityHashMap<>();

    static {
        for (CollisionData data : values()) {
            for (Block type : data.materials) {
                rawLookupMap.put(type, data);
            }
        }
    }

    public final Block[] materials;
    public CollisionBox box;
    public CollisionFactory dynamic;

    CollisionData(CollisionBox box, Block... states) {
        this.box = box;
        Set<Block> mList = new HashSet<>(Arrays.asList(states));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Block[0]);
    }

    CollisionData(CollisionFactory dynamic, Block... states) {
        this.dynamic = dynamic;
        Set<Block> mList = new HashSet<>(Arrays.asList(states));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Block[0]);
    }

    private static CollisionBox getAmethystBox(ClientVersion version, BlockFace facing, int param_0, int param_1) {
        if (version.isOlderThanOrEquals(ClientVersion.V_1_16_4))
            return NoCollisionBox.INSTANCE;

        return switch (facing) {
            case BOTTOM -> new HexCollisionBox(param_1, 16 - param_0, param_1, 16 - param_1, 16.0, 16 - param_1);
            case NORTH -> new HexCollisionBox(param_1, param_1, 16 - param_0, 16 - param_1, 16 - param_1, 16.0);
            case SOUTH -> new HexCollisionBox(param_1, param_1, 0.0, 16 - param_1, 16 - param_1, param_0);
            case EAST -> new HexCollisionBox(0.0, param_1, param_1, param_0, 16 - param_1, 16 - param_1);
            case WEST -> new HexCollisionBox(16 - param_0, param_1, param_1, 16.0, 16 - param_1, 16 - param_1);
            default -> new HexCollisionBox(param_1, 0.0, param_1, 16 - param_1, param_0, 16 - param_1);
        };
    }

    private static CollisionBox getPicklesBox(ClientVersion version, int pickles) {
        // ViaVersion replacement block (West facing cocoa beans)
        if (version.isOlderThanOrEquals(ClientVersion.V_1_12_2)) {
            return getCocoa(version, pickles, BlockFace.WEST);
        }

        return switch (pickles) {
            case 1 -> new HexCollisionBox(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
            case 2 -> new HexCollisionBox(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
            case 3 -> new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
            case 4 -> new HexCollisionBox(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);
            default -> NoCollisionBox.INSTANCE;
        };
    }

    private static CollisionBox getCocoa(ClientVersion version, int age, BlockFace direction) {
        // From 1.9 - 1.10, the large cocoa block is the same as the medium one
        // https://bugs.mojang.com/browse/MC-94274
        if (version.isNewerThanOrEquals(ClientVersion.V_1_9_1) && version.isOlderThan(ClientVersion.V_1_11))
            age = Math.min(age, 1);

        switch (direction) {
            case EAST:
                switch (age) {
                    case 0:
                        return new HexCollisionBox(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D);
                    case 1:
                        return new HexCollisionBox(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D);
                    case 2:
                        return new HexCollisionBox(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D);
                }
            case WEST:
                switch (age) {
                    case 0:
                        return new HexCollisionBox(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D);
                    case 1:
                        return new HexCollisionBox(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D);
                    case 2:
                        return new HexCollisionBox(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D);
                }
            case NORTH:
                switch (age) {
                    case 0:
                        return new HexCollisionBox(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D);
                    case 1:
                        return new HexCollisionBox(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D);
                    case 2:
                        return new HexCollisionBox(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D);
                }
            case SOUTH:
                switch (age) {
                    case 0:
                        return new HexCollisionBox(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D);
                    case 1:
                        return new HexCollisionBox(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D);
                    case 2:
                        return new HexCollisionBox(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D);
                }
        }
        return NoCollisionBox.INSTANCE;
    }

    private static CollisionBox getEndRod(ClientVersion version, BlockFace face) {
        // ViaVersion replacement block - torch
        if (version.isOlderThan(ClientVersion.V_1_9))
            return NoCollisionBox.INSTANCE;

        return switch (face) {
            default -> new HexCollisionBox(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0);
            case NORTH, SOUTH -> new HexCollisionBox(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
            case EAST, WEST -> new HexCollisionBox(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
        };
    }

    // Would pre-computing all states be worth the memory cost? I doubt it
    public static CollisionData getData(Block state) { // TODO: Find a better hack for lava and scaffolding
        // What the fuck mojang, why put noCollision() and then give PITCHER_CROP collision?
        return state.isSolid() || state == Block.LAVA || state == Block.SCAFFOLDING || state == Block.PITCHER_CROP || state == Block.HEAVY_CORE || BlockTags.WALL_HANGING_SIGNS.contains(state) ? rawLookupMap.getOrDefault(state, DEFAULT) : NONE;
    }

    // TODO: This is wrong if a block doesn't have any hitbox and isn't specified, light block?
    public static CollisionData getRawData(Block state) {
        return rawLookupMap.getOrDefault(state, DEFAULT);
    }

    public CollisionBox getMovementCollisionBox(GrimPlayer player, ClientVersion version, MinestomWrappedBlockState block, int x, int y, int z) {
        if (this.box != null)
            return this.box.copy().offset(x, y, z);

        return new DynamicCollisionBox(player, version, dynamic, block).offset(x, y, z);
    }

    public CollisionBox getMovementCollisionBox(GrimPlayer player, ClientVersion version, MinestomWrappedBlockState block) {
        if (this.box != null)
            return this.box.copy();

        return new DynamicCollisionBox(player, version, dynamic, block);
    }
}

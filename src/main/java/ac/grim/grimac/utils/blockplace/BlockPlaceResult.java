package ac.grim.grimac.utils.blockplace;

import ac.grim.grimac.events.packets.CheckManagerListener;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.blockstate.helper.BlockFaceHelper;
import ac.grim.grimac.utils.collisions.CollisionData;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.ItemTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.StateValue;
import ac.grim.grimac.utils.nmsutil.Dripstone;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3i;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum BlockPlaceResult {

    // If the block only has directional data
    ANVIL((player, place) -> {
        MinestomWrappedBlockState data = new MinestomWrappedBlockState(place.getMaterial());
        data.setFacing(BlockFaceHelper.getClockWise(place.getPlayerFacing()));
        place.set(data);
    }, ItemTags.ANVIL),

    // The client only predicts one of the individual bed blocks, interestingly
    BED((player, place) -> {
        // 1.12- players don't predict bed places for some reason
        if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2)) return;

        BlockFace facing = place.getPlayerFacing();
        if (place.isBlockFaceOpen(facing)) {
            place.set(place.getMaterial());
        }
    }, ItemTags.BEDS),

    SNOW((player, place) -> {
        Vector3i against = place.getPlacedAgainstBlockLocation();
        MinestomWrappedBlockState blockState = place.getExistingBlockData();
        int layers = 0;
        if (blockState.getType() == Block.SNOW) {
            layers = blockState.getLayers(); // Indexed at 1
        }

        MinestomWrappedBlockState below = place.getBelowState();

        if (!BlockTags.ICE.contains(below.getType()) && below.getType() != Block.BARRIER) {
            boolean set = false;
            if (below.getType() != Block.HONEY_BLOCK && below.getType() != Block.SOUL_SAND) {
                if (place.isFullFace(BlockFace.BOTTOM)) { // Vanilla also checks for 8 layers of snow but that's redundant...
                    set = true;
                }
            } else { // Honey and soul sand are exempt from this full face check
                set = true;
            }

            if (set) {
                if (blockState.getType() == Block.SNOW) {
                    MinestomWrappedBlockState snow = new MinestomWrappedBlockState(Block.SNOW);
                    snow.setLayers(Math.min(8, layers + 1));
                    place.set(against, snow);
                } else {
                    place.set();
                }
            }
        }

    }, Material.SNOW),

    SLAB((player, place) -> {
        MutableVector clickedPos = place.getClickedLocation();
        MinestomWrappedBlockState slabData = new MinestomWrappedBlockState(place.getMaterial());
        MinestomWrappedBlockState existing = place.getExistingBlockData();

        if (BlockTags.SLABS.contains(existing.getType())) {
            slabData.setTypeData(Type.DOUBLE);
            place.set(place.getPlacedAgainstBlockLocation(), slabData);
        } else {
            BlockFace direction = place.getDirection();
            boolean clickedTop = direction != BlockFace.BOTTOM && (direction == BlockFace.TOP || !(clickedPos.getY() > 0.5D));
            slabData.setTypeData(clickedTop ? Type.BOTTOM : Type.TOP);
            place.set(slabData);
        }

    }, ItemTags.SLABS),

    STAIRS((player, place) -> {
        BlockFace direction = place.getDirection();
        MinestomWrappedBlockState stair = new MinestomWrappedBlockState(place.getMaterial());
        stair.setFacing(place.getPlayerFacing());

        Half half = (direction != BlockFace.BOTTOM && (direction == BlockFace.TOP || place.getClickedLocation().getY() < 0.5D)) ? Half.BOTTOM : Half.TOP;
        stair.setHalf(half);
        place.set(stair);
    }, ItemTags.STAIRS),

    END_ROD((player, place) -> {
        MinestomWrappedBlockState endRod = new MinestomWrappedBlockState(place.getMaterial());
        endRod.setFacing(place.getDirection());
        place.set(endRod);
    }, Material.END_ROD, Material.LIGHTNING_ROD),

    LADDER((player, place) -> {
        //  No placing a ladder against another ladder
        if (!place.isReplaceClicked()) {
            MinestomWrappedBlockState existing = player.compensatedWorld.getWrappedBlockStateAt(place.getPlacedAgainstBlockLocation());
            if (existing.getType() == Block.LADDER && existing.getFacing() == place.getDirection()) {
                return;
            }
        }

        for (BlockFace face : place.getNearestPlacingDirections()) {
            // Torches need solid faces
            // Heads have no special preferences - place them anywhere
            // Signs need solid - exempts chorus flowers and a few other strange cases
            if (BlockFaceHelper.isFaceHorizontal(face) && place.isFullFace(face)) {
                MinestomWrappedBlockState ladder = new MinestomWrappedBlockState(place.getMaterial());
                ladder.setFacing(face.getOppositeFace());
                place.set(ladder);
                return;
            }
        }
    }, Material.LADDER),

    FARM_BLOCK((player, place) -> {
        // What we also need to check:
        MinestomWrappedBlockState above = place.getAboveState();
        if (!above.isBlocking() && !BlockTags.FENCE_GATES.contains(above.getType()) && above.getType() != Block.MOVING_PISTON) {
            place.set(place.getMaterial());
        }
    }, Material.FARMLAND),

    // 1.13+ only blocks from here below!  No need to write everything twice
    AMETHYST_CLUSTER((player, place) -> {
        MinestomWrappedBlockState amethyst = new MinestomWrappedBlockState(place.getMaterial());
        amethyst.setFacing(place.getDirection());
        if (place.isFullFace(place.getDirection().getOppositeFace())) place.set(amethyst);
    }, Material.AMETHYST_CLUSTER, Material.SMALL_AMETHYST_BUD, Material.MEDIUM_AMETHYST_BUD, Material.LARGE_AMETHYST_BUD),

    BAMBOO((player, place) -> {
        Vector3i clicked = place.getPlacedAgainstBlockLocation();
        if (player.compensatedWorld.getFluidLevelAt(clicked.getX(), clicked.getY(), clicked.getZ()) > 0) return;

        MinestomWrappedBlockState below = place.getBelowState();
        if (BlockTags.BAMBOO_PLANTABLE_ON.contains(below.getType())) {
            if (below.getType() == Block.BAMBOO_SAPLING || below.getType() == Block.BAMBOO) {
                place.set(Block.BAMBOO);
            } else {
                MinestomWrappedBlockState above = place.getBelowState();
                if (above.getType() == Block.BAMBOO_SAPLING || above.getType() == Block.BAMBOO) {
                    place.set(Block.BAMBOO);
                } else {
                    place.set(Block.BAMBOO_SAPLING);
                }
            }
        }
    }, Material.BAMBOO),

    BELL((player, place) -> {
        BlockFace direction = place.getDirection();
        MinestomWrappedBlockState bell = new MinestomWrappedBlockState(place.getMaterial());

        boolean canSurvive = !BlockTags.FENCE_GATES.contains(place.getPlacedAgainstMaterial());
        // This is exempt from being able to place on
        if (!canSurvive) return;

        if (place.isFaceVertical()) {
            if (direction == BlockFace.BOTTOM) {
                bell.setAttachment(Attachment.CEILING);
                canSurvive = place.isFaceFullCenter(BlockFace.TOP);
            }
            if (direction == BlockFace.TOP) {
                bell.setAttachment(Attachment.FLOOR);
                canSurvive = place.isFullFace(BlockFace.BOTTOM);
            }
            bell.setFacing(place.getPlayerFacing());
        } else {
            boolean flag = place.isXAxis()
                    && place.isFullFace(BlockFace.EAST)
                    && place.isFullFace(BlockFace.WEST)

                    || place.isZAxis()
                    && place.isFullFace(BlockFace.SOUTH)
                    && place.isFullFace(BlockFace.NORTH);

            bell.setFacing(place.getDirection().getOppositeFace());
            bell.setAttachment(flag ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
            canSurvive = place.isFullFace(place.getDirection().getOppositeFace());

            if (canSurvive) {
                place.set(bell);
                return;
            }

            boolean flag1 = place.isFullFace(BlockFace.BOTTOM);
            bell.setAttachment(flag1 ? Attachment.FLOOR : Attachment.CEILING);
            canSurvive = place.isFullFace(flag1 ? BlockFace.BOTTOM : BlockFace.TOP);
        }
        if (canSurvive) place.set(bell);
    }, Material.BELL),

    CANDLE((player, place) -> {
        MinestomWrappedBlockState existing = place.getExistingBlockData();
        MinestomWrappedBlockState candle = new MinestomWrappedBlockState(place.getMaterial());

        if (BlockTags.CANDLES.contains(existing.getType())) {
            // Max candles already exists
            if (existing.getCandles() == 4) return;
            candle.setCandles(existing.getCandles() + 1);
        }

        if (place.isFaceFullCenter(BlockFace.BOTTOM)) {
            place.set(candle);
        }
    }, ItemTags.CANDLES),

    // Sea pickles refuse to overwrite any collision... but... that's already checked.  Unsure what Mojang is doing.
    SEA_PICKLE((player, place) -> {
        MinestomWrappedBlockState existing = place.getExistingBlockData();

        if (!place.isFullFace(BlockFace.BOTTOM) && !place.isFaceEmpty(BlockFace.BOTTOM)) return;

        if (existing.getType() == Block.SEA_PICKLE) {
            // Max pickels already exist
            if (existing.getPickles() == 4) return;
            existing.setPickles(existing.getPickles() + 1);
        } else {
            existing = new MinestomWrappedBlockState(Block.SEA_PICKLE);
        }

        place.set(existing);
    }, Material.SEA_PICKLE),

    CHAIN((player, place) -> {
        MinestomWrappedBlockState chain = new MinestomWrappedBlockState(place.getMaterial());
        BlockFace face = place.getDirection();

        switch (face) {
            case EAST:
            case WEST:
                chain.setAxis(Axis.X);
                break;
            case NORTH:
            case SOUTH:
                chain.setAxis(Axis.Z);
                break;
            case TOP:
            case BOTTOM:
                chain.setAxis(Axis.Y);
                break;
        }

        place.set(chain);
    }, Material.CHAIN),

    COCOA((player, place) -> {
        for (BlockFace face : place.getNearestPlacingDirections()) {
            if (BlockFaceHelper.isFaceVertical(face)) continue;
            Block mat = place.getDirectionalState(face).getType();
            if (mat == Block.JUNGLE_LOG || mat == Block.STRIPPED_JUNGLE_LOG || mat == Block.JUNGLE_WOOD) {
                MinestomWrappedBlockState data = new MinestomWrappedBlockState(place.getMaterial());
                data.setFacing(face);
                place.set(face, data);
                break;
            }
        }
    }, Material.COCOA_BEANS),

    DIRT_PATH((player, place) -> {
        MinestomWrappedBlockState state = place.getDirectionalState(BlockFace.TOP);
        // If there is a solid block above the dirt path, it turns to air.  This does not include fence gates
        if (!state.isBlocking() || BlockTags.FENCE_GATES.contains(state.getType())) {
            place.set(place.getMaterial());
        } else {
            place.set(Block.DIRT);
        }
    }, Material.DIRT_PATH),

    HOPPER((player, place) -> {
        BlockFace opposite = place.getDirection().getOppositeFace();
        MinestomWrappedBlockState hopper = new MinestomWrappedBlockState(place.getMaterial());
        hopper.setFacing(place.isFaceVertical() ? BlockFace.BOTTOM : opposite);
        place.set(hopper);
    }, Material.HOPPER),

    LANTERN((player, place) -> {
        for (BlockFace face : place.getNearestPlacingDirections()) {
            if (BlockFaceHelper.isFaceHorizontal(face)) continue;
            MinestomWrappedBlockState lantern = new MinestomWrappedBlockState(place.getMaterial());

            boolean isHanging = face == BlockFace.TOP;
            lantern.setHanging(isHanging);

            boolean canSurvive = place.isFaceFullCenter(isHanging ? BlockFace.TOP : BlockFace.BOTTOM) && !BlockTags.FENCE_GATES.contains(place.getPlacedAgainstMaterial());
            if (!canSurvive) continue;

            place.set(lantern);
            return;
        }
    }, Material.LANTERN, Material.SOUL_LANTERN),

    POINTED_DRIPSTONE((player, place) -> {
        // To explain what Mojang is doing, take the example of placing on top face
        BlockFace primaryDir = place.getNearestVerticalDirection().getOppositeFace(); // The player clicked downwards, so use upwards
        MinestomWrappedBlockState typePlacingOn = place.getDirectionalState(primaryDir.getOppositeFace()); // Block we are placing on

        // Check to see if we can place on the block or there is dripstone on the block that we are placing on also pointing upwards
        boolean primarySameType = typePlacingOn.getInternalData().containsKey(StateValue.VERTICAL_DIRECTION) && typePlacingOn.getVerticalDirection().name().equals(primaryDir.name());
        boolean primaryValid = place.isFullFace(primaryDir.getOppositeFace()) || primarySameType;

        // Try to use the opposite direction, just to see if switching directions makes it valid.
        if (!primaryValid) {
            BlockFace secondaryDirection = primaryDir.getOppositeFace(); // See if placing it DOWNWARDS is valid
            MinestomWrappedBlockState secondaryType = place.getDirectionalState(secondaryDirection.getOppositeFace()); // Get the block above us
            // Check if the dripstone above us is also facing downwards
            boolean secondarySameType = secondaryType.getInternalData().containsKey(StateValue.VERTICAL_DIRECTION) && secondaryType.getVerticalDirection().name().equals(primaryDir.name());

            primaryDir = secondaryDirection;
            // Update block survivability
            primaryValid = place.isFullFace(secondaryDirection.getOppositeFace()) || secondarySameType;
        }

        // No valid locations
        if (!primaryValid) return;

        MinestomWrappedBlockState toPlace = new MinestomWrappedBlockState(Block.POINTED_DRIPSTONE);
        toPlace.setVerticalDirection(VerticalDirection.valueOf(primaryDir.name())); // This block is facing UPWARDS as placed on the top face

        // We then have to calculate the thickness of the dripstone
        //
        // PrimaryDirection should be the direction that the current dripstone being placed will face
        // oppositeType should be the opposite to the direction the dripstone is facing, what it is pointing into
        //
        // If the dripstone is -> <- pointed at one another

        // If check the blockstate that is above now with the direction of BOTTOM
        Vector3i placedPos = place.getPlacedBlockPos();
        Dripstone.update(player, toPlace, placedPos.getX(), placedPos.getY(), placedPos.getZ(), place.isSecondaryUse());

        place.set(toPlace);
    }, Material.POINTED_DRIPSTONE),

    CACTUS((player, place) -> {
        for (BlockFace face : place.getHorizontalFaces()) {
            if (place.isSolidBlocking(face) || place.isLava(face)) {
                return;
            }
        }

        if (place.isOn(Block.CACTUS, Block.SAND, Block.RED_SAND) && !place.isLava(BlockFace.TOP)) {
            place.set();
        }
    }, Material.CACTUS),

    CAKE((player, place) -> {
        if (place.isSolidBlocking(BlockFace.BOTTOM)) {
            place.set();
        }
    }, Material.CAKE),

    CANDLE_CAKE((player, place) -> {
        if (place.isSolidBlocking(BlockFace.BOTTOM)) {
            place.set();
        }
    }, Material.values().stream().filter(mat -> mat.namespace().key().asString().contains("candle_cake"))
            .toList().toArray(new Material[0])),

    PISTON_BASE((player, place) -> {
        MinestomWrappedBlockState piston = new MinestomWrappedBlockState(place.getMaterial());
        piston.setFacing(place.getNearestVerticalDirection().getOppositeFace());
        place.set(piston);
    }, Material.PISTON, Material.STICKY_PISTON),

    AZALEA((player, place) -> {
        MinestomWrappedBlockState below = place.getBelowState();
        if (place.isOnDirt() || below.getType() == Block.FARMLAND || below.getType() == Block.CLAY) {
            place.set(place.getMaterial());
        }
    }, Material.AZALEA, Material.FLOWERING_AZALEA),

    CROP((player, place) -> {
        MinestomWrappedBlockState below = place.getBelowState();
        if (below.getType() == Block.FARMLAND) {
            // This is wrong and depends on lighting, but the server resync's anyways plus this isn't a solid block so I don't care.
            place.set();
        }
    }, Material.CARROT, Material.BEETROOT, Material.POTATO,
            Material.PUMPKIN_SEEDS, Material.MELON_SEEDS, Material.WHEAT_SEEDS, Material.TORCHFLOWER_SEEDS),

    SUGARCANE((player, place) -> {
        if (place.isOn(Block.SUGAR_CANE)) {
            place.set();
            return;
        }

        if (place.isOnDirt() || place.isOn(Block.SAND, Block.RED_SAND)) {
            Vector3i pos = place.getPlacedBlockPos();
            pos = pos.withY(pos.getY() - 1);

            for (BlockFace direction : place.getHorizontalFaces()) {
                Vector3i toSearchPos = pos;
                toSearchPos = toSearchPos.withX(toSearchPos.getX() + direction.toDirection().normalX());
                toSearchPos = toSearchPos.withZ(toSearchPos.getZ() + direction.toDirection().normalZ());

                MinestomWrappedBlockState directional = player.compensatedWorld.getWrappedBlockStateAt(toSearchPos);
                if (Materials.isWater(player.getClientVersion(), directional) || directional.getType() == Block.FROSTED_ICE) {
                    place.set();
                    return;
                }
            }
        }
    }, Material.SUGAR_CANE),

    // Moss carpet is a carpet not under the carpets tag
    MOSS_CARPET((player, place) -> {
        if (!place.getBelowMaterial().isAir()) {
            place.set();
        }
    }, Material.MOSS_CARPET),

    CARPET((player, place) -> {
        if (!place.getBelowMaterial().isAir()) {
            place.set();
        }
    }, ItemTags.WOOL_CARPETS),

    CHORUS_FLOWER((player, place) -> {
        MinestomWrappedBlockState blockstate = place.getBelowState();
        if (blockstate.getType() != Block.CHORUS_PLANT && blockstate.getType() != Block.END_STONE) {
            if (blockstate.getType().isAir()) {
                boolean flag = false;

                for (BlockFace direction : place.getHorizontalFaces()) {
                    MinestomWrappedBlockState blockstate1 = place.getDirectionalState(direction);
                    if (blockstate1.getType() == Block.CHORUS_PLANT) {
                        if (flag) {
                            return;
                        }

                        flag = true;
                    } else if (!blockstate.getType().isAir()) {
                        return;
                    }
                }

                if (flag) {
                    place.set();
                }
            }
        } else {
            place.set();
        }
    }, Material.CHORUS_FLOWER),

    CHORUS_PLANT((player, place) -> {
        MinestomWrappedBlockState blockstate = place.getBelowState();
        boolean flag = !place.getAboveState().getType().isAir() && !blockstate.getType().isAir();

        for (BlockFace direction : place.getHorizontalFaces()) {
            MinestomWrappedBlockState blockstate1 = place.getDirectionalState(direction);
            if (blockstate1.getType() == Block.CHORUS_PLANT) {
                if (flag) {
                    return;
                }

                Vector3i placedPos = place.getPlacedBlockPos();
                placedPos = placedPos.add(direction.toDirection().normalX(), -1, direction.toDirection().normalZ());

                MinestomWrappedBlockState blockstate2 = player.compensatedWorld.getWrappedBlockStateAt(placedPos);
                if (blockstate2.getType() == Block.CHORUS_PLANT || blockstate2.getType() == Block.END_STONE) {
                    place.set();
                }
            }
        }

        if (blockstate.getType() == Block.CHORUS_PLANT || blockstate.getType() == Block.END_STONE) {
            place.set();
        }
    }, Material.CHORUS_PLANT),

    DEAD_BUSH((player, place) -> {
        MinestomWrappedBlockState below = place.getBelowState();
        if (below.getType() == Block.SAND || below.getType() == Block.RED_SAND ||
                BlockTags.TERRACOTTA.contains(below.getType()) || place.isOnDirt()) {
            place.set(place.getMaterial());
        }
    }, Material.DEAD_BUSH),

    DIODE((player, place) -> {
        if (place.isFaceRigid(BlockFace.BOTTOM)) {
            place.set();
        }
    }, Material.REPEATER, Material.COMPARATOR, Material.REDSTONE),

    FUNGUS((player, place) -> {
        if (place.isOn(Block.CRIMSON_NYLIUM, Block.WARPED_NYLIUM, Block.MYCELIUM, Block.SOUL_SOIL, Block.FARMLAND) || place.isOnDirt()) {
            place.set();
        }
    }, Material.CRIMSON_FUNGUS, Material.WARPED_FUNGUS),

    SPROUTS((player, place) -> {
        if (place.isOn(Block.CRIMSON_NYLIUM, Block.WARPED_NYLIUM, Block.SOUL_SOIL, Block.FARMLAND) || place.isOnDirt()) {
            place.set();
        }
    }, Material.NETHER_SPROUTS, Material.WARPED_ROOTS, Material.CRIMSON_ROOTS),

    NETHER_WART((player, place) -> {
        if (place.isOn(Block.SOUL_SAND)) {
            place.set();
        }
    }, Material.NETHER_WART),

    WATERLILY((player, place) -> {
        MinestomWrappedBlockState below = place.getDirectionalState(BlockFace.BOTTOM);
        if (!place.isInLiquid() && (Materials.isWater(player.getClientVersion(), below) || place.isOn(Block.ICE, Block.FROSTED_ICE))) {
            place.set();
        }
    }, Material.LILY_PAD),

    WITHER_ROSE((player, place) -> {
        if (place.isOn(Block.NETHERRACK, Block.SOUL_SAND, Block.SOUL_SOIL, Block.FARMLAND) || place.isOnDirt()) {
            place.set();
        }
    }, Material.WITHER_ROSE),

    // Blocks that have both wall and standing states
    TORCH_OR_HEAD((player, place) -> {
        // type doesn't matter to grim, same hitbox.
        // If it's a torch, create a wall torch
        // Otherwise, it's going to be a head.  The type of this head also doesn't matter
        MinestomWrappedBlockState dir;
        boolean isTorch = place.getMaterial().namespace().key().asString().contains("torch");
        boolean isHead = place.getMaterial().namespace().key().asString().contains("head") || place.getMaterial().namespace().key().asString().contains("skull");
        boolean isWallSign = !isTorch && !isHead;

        if (isHead && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2))
            return; // 1.12- players don't predict head places

        if (isTorch) {
            dir = new MinestomWrappedBlockState(Block.WALL_TORCH);
        } else if (place.getMaterial().namespace().key().asString().contains("head") || place.getMaterial().namespace().key().asString().contains("skull")) {
            dir = new MinestomWrappedBlockState(Block.PLAYER_WALL_HEAD);
        } else {
            dir = new MinestomWrappedBlockState(Block.OAK_WALL_SIGN);
        }

        for (BlockFace face : place.getNearestPlacingDirections()) {
            // Torches need solid faces
            // Heads have no special preferences - place them anywhere
            // Signs need solid - exempts chorus flowers and a few other strange cases
            if (face != BlockFace.TOP) {
                if (BlockFaceHelper.isFaceHorizontal(face)) {
                    boolean canPlace = isHead || ((isWallSign || place.isFullFace(face)) && (isTorch || place.isSolidBlocking(face)));
                    if (canPlace && face != BlockFace.TOP) { // center requires nothing (head), full face (torch), or solid (sign)
                        dir.setFacing(face.getOppositeFace());
                        place.set(dir);
                        return;
                    }
                } else {
                    boolean canPlace = isHead || ((isWallSign || place.isFaceFullCenter(face)) && (isTorch || place.isSolidBlocking(face)));
                    if (canPlace) {
                        place.set(place.getMaterial());
                        return;
                    }
                }
            }
        }
    }, Material.values().stream().filter(mat ->
                    mat.namespace().key().asString().contains("torch") // Find all torches
                            || (mat.namespace().key().asString().contains("head") || mat.namespace().key().asString().contains("skull")) && !mat.namespace().key().asString().contains("piston") // Skulls
                            || mat.namespace().key().asString().contains("sign")) // And signs
            .toArray(Material[]::new)),

    MULTI_FACE_BLOCK((player, place) -> {
        Block placedType = place.getMaterial();

        MinestomWrappedBlockState multiFace = place.getExistingBlockData();
        if (multiFace.getType() != placedType) {
            multiFace = new MinestomWrappedBlockState(placedType);
        }

        for (BlockFace face : place.getNearestPlacingDirections()) {
            switch (face) {
                case TOP:
                    if (multiFace.isUp()) continue;
                    if (place.isFullFace(face)) {
                        multiFace.setUp(true);
                        break;
                    }
                    continue;
                case BOTTOM:
                    if (multiFace.isDown()) continue;
                    if (place.isFullFace(face)) {
                        multiFace.setDown(true);
                        break;
                    }
                    continue;
                case NORTH:
                    if (multiFace.getNorth() == North.TRUE) continue;
                    if (place.isFullFace(face)) {
                        multiFace.setNorth(North.TRUE);
                        break;
                    }
                    continue;
                case SOUTH:
                    if (multiFace.getSouth() == South.TRUE) continue;
                    if (place.isFullFace(face)) {
                        multiFace.setSouth(South.TRUE);
                        break;
                    }
                    continue;
                case EAST:
                    if (multiFace.getEast() == East.TRUE) continue;
                    if (place.isFullFace(face)) {
                        multiFace.setEast(East.TRUE);
                        return;
                    }
                    continue;
                case WEST:
                    if (multiFace.getWest() == West.TRUE) continue;
                    if (place.isFullFace(face)) {
                        multiFace.setWest(West.TRUE);
                        break;
                    }
                    continue;
            }
        }

        place.set(multiFace);
    }, Material.GLOW_LICHEN, Material.SCULK_VEIN),

    FACE_ATTACHED_HORIZONTAL_DIRECTIONAL((player, place) -> {
        for (BlockFace face : place.getNearestPlacingDirections()) {
            if (place.isFullFace(face)) {
                place.set(place.getMaterial());
                return;
            }
        }
    }, Material.values().stream().filter(mat -> mat.namespace().key().asString().contains("button") // Find all buttons
                    || mat.namespace().key().asString().contains("lever")) // And levers
            .toArray(Material[]::new)),

    GRINDSTONE((player, place) -> { // Grindstones do not have special survivability requirements
        MinestomWrappedBlockState stone = new MinestomWrappedBlockState(place.getMaterial());
        if (place.isFaceVertical()) {
            stone.setFace(place.getPlayerFacing() == BlockFace.TOP ? Face.CEILING : Face.FLOOR);
        } else {
            stone.setFace(Face.WALL);
        }
        stone.setFacing(place.getPlayerFacing());
        place.set(stone);
    }, Material.GRINDSTONE),

    // Blocks that have both wall and standing states
    // Banners
    BANNER((player, place) -> {
        for (BlockFace face : place.getNearestPlacingDirections()) {
            if (place.isSolidBlocking(face) && face != BlockFace.TOP) {
                if (BlockFaceHelper.isFaceHorizontal(face)) {
                    // type doesn't matter to grim, same hitbox.
                    // If it's a torch, create a wall torch
                    // Otherwise, it's going to be a head.  The type of this head also doesn't matter.
                    MinestomWrappedBlockState dir = new MinestomWrappedBlockState(Block.BLACK_WALL_BANNER);
                    dir.setFacing(face.getOppositeFace());
                    place.set(dir);
                } else {
                    place.set(place.getMaterial());
                }
                break;
            }
        }
    }, ItemTags.BANNERS),

    BIG_DRIPLEAF((player, place) -> {
        MinestomWrappedBlockState existing = place.getDirectionalState(BlockFace.BOTTOM);
        if (place.isFullFace(BlockFace.BOTTOM) || existing.getType() == Block.BIG_DRIPLEAF || existing.getType() == Block.BIG_DRIPLEAF_STEM) {
            place.set(place.getMaterial());
        }
    }, Material.BIG_DRIPLEAF),

    SMALL_DRIPLEAF((player, place) -> {
        MinestomWrappedBlockState existing = place.getDirectionalState(BlockFace.BOTTOM);
        if (place.isBlockFaceOpen(BlockFace.TOP) && BlockTags.SMALL_DRIPLEAF_PLACEABLE.contains(existing.getType()) || (place.isInWater() && (place.isOnDirt() || existing.getType() == Block.FARMLAND))) {
            place.set(place.getMaterial());
        }
    }, Material.SMALL_DRIPLEAF),

    SEAGRASS((player, place) -> {
        MinestomWrappedBlockState existing = place.getDirectionalState(BlockFace.BOTTOM);
        if (place.isInWater() && place.isFullFace(BlockFace.BOTTOM) && existing.getType() != Block.MAGMA_BLOCK) {
            place.set(place.getMaterial());
        }
    }, Material.SEAGRASS),

    HANGING_ROOT((player, place) -> {
        if (place.isFullFace(BlockFace.TOP)) {
            place.set(place.getMaterial());
        }
    }, Material.HANGING_ROOTS),

    SPORE_BLOSSOM((player, place) -> {
        if (place.isFullFace(BlockFace.TOP) && !place.isInWater()) {
            place.set();
        }
    }, Material.SPORE_BLOSSOM),

    FIRE((player, place) -> {
        boolean byFlammable = false;
        for (BlockFace face : BlockFace.values()) {
            // Do we care about this enuogh to fix? // TODO: Check flmmable
            byFlammable = true;
        }
        if (byFlammable || place.isFullFace(BlockFace.BOTTOM)) {
            place.set(place.getMaterial());
        }
    }, Material.FLINT_AND_STEEL, Material.FIRE_CHARGE), // soul fire isn't directly placeable

    TRIPWIRE_HOOK((player, place) -> {
        if (place.isFaceHorizontal() && place.isFullFace(place.getDirection().getOppositeFace())) {
            place.set(place.getMaterial());
        }
    }, Material.TRIPWIRE_HOOK),

    CORAL_PLANT((player, place) -> {
        if (place.isFullFace(BlockFace.BOTTOM)) {
            place.set(place.getMaterial());
        }
    }, Material.values().stream().filter(mat -> (mat.namespace().key().asString().contains("coral")
                    && !mat.namespace().key().asString().contains("block") && !mat.namespace().key().asString().contains("fan")))
            .toArray(Material[]::new)),

    CORAL_FAN((player, place) -> {
        for (BlockFace face : place.getNearestPlacingDirections()) {
            // Torches need solid faces
            // Heads have no special preferences - place them anywhere
            // Signs need solid - exempts chorus flowers and a few other strange cases
            if (face != BlockFace.TOP) {
                boolean canPlace = place.isFullFace(face);
                if (BlockFaceHelper.isFaceHorizontal(face)) {
                    if (canPlace) { // center requires nothing (head), full face (torch), or solid (sign)
                        MinestomWrappedBlockState coralFan = new MinestomWrappedBlockState(Block.FIRE_CORAL_WALL_FAN);
                        coralFan.setFacing(face);
                        place.set(coralFan);
                        return;
                    }
                } else if (place.isFaceFullCenter(BlockFace.BOTTOM) && canPlace) {
                    place.set(place.getMaterial());
                    return;
                }
            }
        }
    }, Material.values().stream().filter(mat -> (mat.namespace().key().asString().contains("coral")
                    && !mat.namespace().key().asString().contains("block") && mat.namespace().key().asString().contains("fan")))
            .toArray(Material[]::new)),

    PRESSURE_PLATE((player, place) -> {
        if (place.isFullFace(BlockFace.BOTTOM) || place.isFaceFullCenter(BlockFace.BOTTOM)) {
            place.set();
        }
    }, Material.values().stream().filter(mat -> (mat.namespace().key().asString().contains("plate")))
            .toArray(Material[]::new)),

    RAIL((player, place) -> {
        if (place.isFaceRigid(BlockFace.BOTTOM)) {
            place.set(place.getMaterial());
        }
    }, ItemTags.RAILS),

    KELP((player, place) -> {
        Block below = place.getDirectionalState(BlockFace.BOTTOM).getType();
        MinestomWrappedBlockState existing = place.getExistingBlockData();

        double fluidLevel = 0;
        if (Materials.isWater(player.getClientVersion(), existing)) {
            if (existing.getType() == Block.WATER) {
                int level = existing.getLevel();
                // Falling water has a level of 8
                fluidLevel = ((level & 0x8) == 8) ? (8.0 / 9.0f) : (8 - level) / 9.0f;
            } else { // Water source block such as bubble columns
                fluidLevel = 1.0;
            }
        }

        if (below != Block.MAGMA_BLOCK && (place.isFullFace(BlockFace.BOTTOM) || below == Block.KELP || below == Block.KELP_PLANT) && fluidLevel >= 8 / 9d) {
            place.set(place.getMaterial());
        }
    }, Material.KELP),

    CAVE_VINE((player, place) -> {
        Block below = place.getDirectionalState(BlockFace.TOP).getType();
        if (place.isFullFace(BlockFace.BOTTOM) || below == Block.CAVE_VINES || below == Block.CAVE_VINES_PLANT) {
            place.set(place.getMaterial());
        }
    }, Material.GLOW_BERRIES),

    WEEPING_VINE((player, place) -> {
        Block below = place.getDirectionalState(BlockFace.TOP).getType();
        if (place.isFullFace(BlockFace.TOP) || below == Block.WEEPING_VINES || below == Block.WEEPING_VINES_PLANT) {
            place.set(place.getMaterial());
        }
    }, Material.WEEPING_VINES),

    TWISTED_VINE((player, place) -> {
        Block below = place.getDirectionalState(BlockFace.BOTTOM).getType();
        if (place.isFullFace(BlockFace.BOTTOM) || below == Block.TWISTING_VINES || below == Block.TWISTING_VINES_PLANT) {
            place.set(place.getMaterial());
        }
    }, Material.TWISTING_VINES),

    // Vine logic
    // If facing up, then there is a face facing up.
    // Checks for solid faces in the direction that it is in
    // Also checks for vines with the same directional above itself
    // However, as all vines have the same hitbox (to collisions and climbing)
    // As long as one of these properties is met, it is good enough for grim!
    VINE((player, place) -> {
        if (place.getAboveState().getType() == Block.VINE) {
            place.set();
            return;
        }

        for (BlockFace face : place.getHorizontalFaces()) {
            if (place.isSolidBlocking(face)) {
                place.set();
                return;
            }
        }
    }, Material.VINE),

    LECTERN((player, place) -> {
        MinestomWrappedBlockState lectern = new MinestomWrappedBlockState(place.getMaterial());
        lectern.setFacing(place.getPlayerFacing().getOppositeFace());
        place.set(lectern);
    }, Material.LECTERN),

    FENCE_GATE((player, place) -> {
        MinestomWrappedBlockState gate = new MinestomWrappedBlockState(place.getMaterial());
        gate.setFacing(place.getPlayerFacing());

        // Check for redstone signal!
        if (place.isBlockPlacedPowered()) {
            gate.setOpen(true);
        }

        place.set(gate);
    }, BlockTags.FENCE_GATES),

    TRAPDOOR((player, place) -> {
        MinestomWrappedBlockState door = new MinestomWrappedBlockState(place.getMaterial());

        BlockFace direction = place.getDirection();
        if (!place.isReplaceClicked() && BlockFaceHelper.isFaceHorizontal(direction)) {
            door.setFacing(direction);
            boolean clickedTop = place.getClickedLocation().getY() > 0.5;
            Half half = clickedTop ? Half.TOP : Half.BOTTOM;
            door.setHalf(half);
        } else if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) { // 1.9 logic only
            door.setFacing(place.getPlayerFacing().getOppositeFace());
            Half half = direction == BlockFace.TOP ? Half.BOTTOM : Half.TOP;
            door.setHalf(half);
        }

        // Check for redstone signal!
        if (place.isBlockPlacedPowered()) {
            door.setOpen(true);
        }

        // 1.8 has special placing requirements
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            MinestomWrappedBlockState dirState = place.getDirectionalState(door.getFacing().getOppositeFace());
            boolean fullFace = CollisionData.getData(dirState.getType()).getMovementCollisionBox(player, player.getClientVersion(), dirState).isFullBlock();
            boolean blacklisted = BlockTags.ICE.contains(dirState.getType()) || BlockTags.GLASS_BLOCKS.contains(dirState.getType()) ||
                    dirState.getType() == Block.TNT || BlockTags.LEAVES.contains(dirState.getType()) ||
                    dirState.getType() == Block.SNOW || dirState.getType() == Block.CACTUS;
            boolean whitelisted = dirState.getType() == Block.GLOWSTONE || BlockTags.SLABS.contains(dirState.getType()) ||
                    BlockTags.STAIRS.contains(dirState.getType());

            // Need a solid block to place a trapdoor on
            if (!((dirState.isBlocking() && !blacklisted && fullFace) || whitelisted)) {
                return;
            }
        }


        place.set(door);
    }, ItemTags.TRAPDOORS),

    DOOR((player, place) -> {
        if (place.isFullFace(BlockFace.BOTTOM) && place.isBlockFaceOpen(BlockFace.TOP)) {
            MinestomWrappedBlockState door = new MinestomWrappedBlockState(place.getMaterial());
            door.setFacing(place.getPlayerFacing());

            // Get the hinge
            BlockFace playerFacing = place.getPlayerFacing();

            BlockFace ccw = BlockFaceHelper.getCounterClockwise(playerFacing);
            MinestomWrappedBlockState ccwState = place.getDirectionalState(ccw);
            CollisionBox ccwBox = CollisionData.getData(ccwState.getType()).getMovementCollisionBox(player, player.getClientVersion(), ccwState);

            MutableVector aboveCCWPos = place.getClickedLocation().add(new MutableVector(ccw.toDirection().normalX(), ccw.toDirection().normalY(), ccw.toDirection().normalZ())).add(new MutableVector(0, 1, 0));
            MinestomWrappedBlockState aboveCCWState = player.compensatedWorld.getWrappedBlockStateAt(aboveCCWPos);
            CollisionBox aboveCCWBox = CollisionData.getData(aboveCCWState.getType()).getMovementCollisionBox(player, player.getClientVersion(), aboveCCWState);

            BlockFace cw = BlockFaceHelper.getPEClockWise(playerFacing);
            MinestomWrappedBlockState cwState = place.getDirectionalState(cw);
            CollisionBox cwBox = CollisionData.getData(cwState.getType()).getMovementCollisionBox(player, player.getClientVersion(), cwState);

            MutableVector aboveCWPos = place.getClickedLocation().add(new MutableVector(cw.toDirection().normalX(), cw.toDirection().normalY(), cw.toDirection().normalZ())).add(new MutableVector(0, 1, 0));
            MinestomWrappedBlockState aboveCWState = player.compensatedWorld.getWrappedBlockStateAt(aboveCWPos);
            CollisionBox aboveCWBox = CollisionData.getData(aboveCWState.getType()).getMovementCollisionBox(player, player.getClientVersion(), aboveCWState);

            int i = (ccwBox.isFullBlock() ? -1 : 0) + (aboveCCWBox.isFullBlock() ? -1 : 0) + (cwBox.isFullBlock() ? 1 : 0) + (aboveCWBox.isFullBlock() ? 1 : 0);

            boolean isCCWLower = false;
            if (BlockTags.DOORS.contains(ccwState.getType())) isCCWLower = ccwState.getHalf() == Half.LOWER;

            boolean isCWLower = false;
            if (BlockTags.DOORS.contains(cwState.getType())) isCWLower = ccwState.getHalf() == Half.LOWER;

            Hinge hinge;
            if ((!isCCWLower || isCWLower) && i <= 0) {
                if ((!isCWLower || isCCWLower) && i >= 0) {
                    int j = playerFacing.toDirection().normalX();
                    int k = playerFacing.toDirection().normalZ();
                    MutableVector vec3 = place.getClickedLocation();
                    double d0 = vec3.getX();
                    double d1 = vec3.getY();
                    hinge = (j >= 0 || d1 >= 0.5D) && (j <= 0 || d1 <= 0.5D) && (k >= 0 || d0 <= 0.5D) && (k <= 0 || d0 >= 0.5D) ? Hinge.LEFT : Hinge.RIGHT;
                } else {
                    hinge = Hinge.LEFT;
                }
            } else {
                hinge = Hinge.RIGHT;
            }

            // Check for redstone signal!
            if (place.isBlockPlacedPowered()) {
                door.setOpen(true);
            }

//            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) { // Only works on 1.13+
                door.setHinge(hinge);
//            }

            door.setHalf(Half.LOWER);
            place.set(door);

//            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) { // Only works on 1.13+
                door.setHalf(Half.UPPER);
                place.setAbove(door);
//            } else {
//                // We have to create a new door just for upper... due to neither door having complete info
//                // Lol, I have to use strings as PacketEvents wasn't designed around one material having two sets of data
//                // This is 1.12 only, but the server is also 1.12
//                MinestomWrappedBlockState above = MinestomWrappedBlockState.getByString(, "minecraft:" + place.getMaterial().namespace().key().asString().toLowerCase(Locale.ROOT) + "[half=upper,hinge=" + hinge.toString().toLowerCase(Locale.ROOT) + "]");
//                place.setAbove(above);
//            }
        }
    }, ItemTags.DOORS),

    SCAFFOLDING((player, place) -> {
        place.setReplaceClicked(false); // scaffolding is sometimes replace clicked

        // The client lies about block place location and face to not false vanilla ac
        // However, this causes TWO desync's!
        if (place.getPlacedAgainstMaterial() == Block.SCAFFOLDING) {
            // This can desync due to look being a tick behind, pls fix mojang
            // Convert the packet to the real direction
            BlockFace direction;
            if (place.isSecondaryUse()) {
                direction = place.isInside() ? place.getDirection().getOppositeFace() : place.getDirection();
            } else {
                direction = place.getDirection() == BlockFace.TOP ? place.getPlayerFacing() : BlockFace.TOP;
            }

            place.setFace(direction);
            // Mojang also lies about the location causing another GOD DAMN DESYNC
            // Jesus christ, two desync's in a single block... should I be disappointed or concerned?
            // Ghost blocks won't be fixed because of how it depends on the world state
            int i = 0;
            Vector3i starting = new Vector3i(place.getPlacedAgainstBlockLocation().getX() + direction.toDirection().normalX(), place.getPlacedAgainstBlockLocation().getY() + direction.toDirection().normalY(), place.getPlacedAgainstBlockLocation().getZ() + direction.toDirection().normalZ());
            while (i < 7) {
                if (player.compensatedWorld.getWrappedBlockStateAt(starting).getType() != Block.SCAFFOLDING) {
                    if (player.compensatedWorld.getWrappedBlockStateAt(starting).isReplaceable()) {
                        place.setBlockPosition(starting);
                        place.setReplaceClicked(true);
                        break; // We found it!
                    }
                    return; // Cancel block place
                }

                starting = new Vector3i(starting.getX() + direction.toDirection().normalX(), starting.getY() + direction.toDirection().normalY(), starting.getZ() + direction.toDirection().normalZ());
                if (BlockFaceHelper.isFaceHorizontal(direction)) {
                    i++;
                }
            }
            if (i == 7) return; // Cancel block place
        } // else, cancel if the scaffolding is exactly 7 away, grim doesn't handle this edge case yet.


        // A scaffolding has a distance of 0 IFF it is placed above a sturdy face
        // Else it has a distance greater than 0
        boolean sturdyBelow = place.isFullFace(BlockFace.BOTTOM);
        boolean isBelowScaffolding = place.getBelowMaterial() == Block.SCAFFOLDING;
        boolean isBottom = !sturdyBelow && !isBelowScaffolding;

        MinestomWrappedBlockState scaffolding = new MinestomWrappedBlockState(Block.SCAFFOLDING);
        scaffolding.setBottom(isBottom);

        place.set(scaffolding);
    }, Material.SCAFFOLDING),

    DOUBLE_PLANT((player, place) -> {
        if (place.isBlockFaceOpen(BlockFace.TOP) && place.isOnDirt() || place.isOn(Block.FARMLAND)) {
            place.set();
            place.setAbove(); // Client predicts block above
        }
    }, Material.TALL_GRASS, Material.LARGE_FERN, Material.SUNFLOWER,
            Material.LILAC, Material.ROSE_BUSH, Material.PEONY),

    MUSHROOM((player, place) -> {
        if (BlockTags.MUSHROOM_GROW_BLOCK.contains(place.getBelowMaterial())) {
            place.set();
        } else if (place.isFullFace(BlockFace.BOTTOM)) { // TODO: Check occluding
            Vector3i placedPos = place.getPlacedBlockPos();
            // This is wrong and depends on lighting, but the server resync's anyways plus this isn't a solid block. so I don't care.
            place.set();
        }
    }, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM),

    MANGROVE_PROPAGULE((player, place) -> {
        // Must be hanging below mangrove leaves
        if (place.getAboveState().getType() != Block.MANGROVE_LEAVES) return;
        // Fall back to BUSH_BLOCK_TYPE
        if (place.isOnDirt() || place.isOn(Block.FARMLAND)) {
            place.set();
        }
    }, Material.MANGROVE_PROPAGULE),

    FROGSPAWN((player, place) -> {
        if (Materials.isWater(player.getClientVersion(), place.getExistingBlockData()) && Materials.isWater(player.getClientVersion(), place.getAboveState())) {
            place.set();
        }
    }, Material.FROGSPAWN),

    BUSH_BLOCK_TYPE((player, place) -> {
        if (place.isOnDirt() || place.isOn(Block.FARMLAND)) {
            place.set();
        }
    }, Material.SPRUCE_SAPLING, Material.ACACIA_SAPLING,
            Material.BIRCH_SAPLING, Material.DARK_OAK_SAPLING,
            Material.OAK_SAPLING, Material.JUNGLE_SAPLING,
            Material.SWEET_BERRIES, Material.DANDELION,
            Material.POPPY, Material.BLUE_ORCHID,
            Material.ALLIUM, Material.AZURE_BLUET,
            Material.RED_TULIP, Material.ORANGE_TULIP,
            Material.WHITE_TULIP, Material.PINK_TULIP,
            Material.OXEYE_DAISY, Material.CORNFLOWER,
            Material.LILY_OF_THE_VALLEY, Material.PINK_PETALS,
            Material.GRASS_BLOCK),

    POWDER_SNOW_BUCKET((player, place) -> {
        place.set();
        CheckManagerListener.setPlayerItem(player, place.getHand(), Material.BUCKET);
    }, Material.POWDER_SNOW_BUCKET),

    GAME_MASTER((player, place) -> {
        if (player.canUseGameMasterBlocks()) {
            place.set();
        }
    }, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
            Material.JIGSAW, Material.STRUCTURE_BLOCK),

    NO_DATA((player, place) -> {
        place.set(place.getMaterial());
    }, Material.AIR);

    // This should be an array... but a hashmap will do for now...
    private static final Map<Material, BlockPlaceResult> lookupMap = new HashMap<>();

    static {
        for (BlockPlaceResult data : values()) {
            for (Material type : data.materials) {
                lookupMap.put(type, data);
            }
        }
    }

    private final BlockPlaceFactory data;
    private final Material[] materials;

    BlockPlaceResult(BlockPlaceFactory data, Material... materials) {
        this.data = data;
        Set<Material> mList = new HashSet<>(Arrays.asList(materials));
        mList.remove(null); // Sets can contain one null
        this.materials = mList.toArray(new Material[0]);
    }

    BlockPlaceResult(BlockPlaceFactory data, ItemTags tags) {
        this(data, tags.getStates().toArray(new Material[0]));
    }

    BlockPlaceResult(BlockPlaceFactory data, BlockTags tag) {
        List<Material> types = new ArrayList<>(tag.getStates().size());
        for (Block state : tag.getStates()) {
            // todo minestom this sucks
            types.add(state.defaultState().registry().material());
        }

        this.data = data;
        this.materials = types.toArray(new Material[0]);
    }

    public static BlockPlaceFactory getMaterialData(Material placed) {
        return lookupMap.getOrDefault(placed, NO_DATA).data;
    }
}

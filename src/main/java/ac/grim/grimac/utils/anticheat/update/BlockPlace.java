package ac.grim.grimac.utils.anticheat.update;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.collisions.AxisSelect;
import ac.grim.grimac.utils.collisions.AxisUtil;
import ac.grim.grimac.utils.collisions.CollisionData;
import ac.grim.grimac.utils.collisions.blocks.DoorHandler;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.HitData;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.StateValue;
import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.nmsutil.ReachUtils;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import ac.grim.grimac.utils.vector.Vector3f;
import ac.grim.grimac.utils.vector.Vector3i;
import ac.grim.grimac.utils.minestom.enums.*;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockPlace {
    protected static final BlockFace[] UPDATE_SHAPE_ORDER = new BlockFace[]{BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.BOTTOM, BlockFace.TOP};
    private static final BlockFace[] BY_2D = new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};
    static final BlockFace[] BY_3D = new BlockFace[]{BlockFace.BOTTOM, BlockFace.TOP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
    @Setter
    Vector3i blockPosition;
    @Getter
    Player.Hand hand;
    @Getter
    @Setter
    boolean replaceClicked;
    boolean isCancelled = false;
    GrimPlayer player;
    @Getter
    ModifiableItemStack itemStack;
    @Getter
    Block material;
    @Getter
    @Nullable HitData hitData;
    @Getter
    int faceId;
    BlockFace face;
    @Getter
    @Setter
    boolean isInside;
    @Getter
    @Setter
    Vector3f cursor;

    @Getter private final boolean block;

    public BlockPlace(GrimPlayer player, Player.Hand hand, Vector3i blockPosition, int faceId, BlockFace face, ModifiableItemStack itemStack, HitData hitData) {
        this.player = player;
        this.hand = hand;
        this.blockPosition = blockPosition;
        this.faceId = faceId;
        this.face = face;
        this.itemStack = itemStack;

        // todo minestom placed type
        if (itemStack.getItemStack().material().block()== null) {
            this.material = Block.FIRE;
            this.block = false;
        } else {
            this.material = itemStack.getItemStack().material().block();
            this.block = true;
        }
        this.hitData = hitData;

        MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(getPlacedAgainstBlockLocation());
        this.replaceClicked = canBeReplaced(this.material, state, face);
    }

    public Vector3i getPlacedAgainstBlockLocation() {
        return blockPosition;
    }

    public MinestomWrappedBlockState getExistingBlockData() {
        return player.compensatedWorld.getWrappedBlockStateAt(getPlacedBlockPos());
    }

    public Block getPlacedAgainstMaterial() {
        return player.compensatedWorld.getWrappedBlockStateAt(getPlacedAgainstBlockLocation()).getType();
    }

    public MinestomWrappedBlockState getBelowState() {
        Vector3i pos = getPlacedBlockPos();
        pos = pos.withY(pos.getY() - 1);
        return player.compensatedWorld.getWrappedBlockStateAt(pos);
    }

    public MinestomWrappedBlockState getAboveState() {
        Vector3i pos = getPlacedBlockPos();
        pos = pos.withY(pos.getY() + 1);
        return player.compensatedWorld.getWrappedBlockStateAt(pos);
    }

    public MinestomWrappedBlockState getDirectionalState(BlockFace facing) {
        Vector3i pos = getPlacedBlockPos();
        pos = pos.add(facing.toDirection().normalX(), facing.toDirection().normalY(), facing.toDirection().normalZ());
        return player.compensatedWorld.getWrappedBlockStateAt(pos);
    }

    public boolean isSolidBlocking(BlockFace relative) {
        MinestomWrappedBlockState state = getDirectionalState(relative);
        return state.isBlocking();
    }


    private boolean canBeReplaced(Block heldItem, MinestomWrappedBlockState state, BlockFace face) {
        // Cave vines and weeping vines have a special case... that always returns false (just like the base case for it!)
        boolean baseReplaceable = state.getType() != heldItem && state.isReplaceable();

        if (BlockTags.CANDLES.contains(state.getType())) {
            return heldItem == state.getType() && state.getCandles() < 4 && !isSecondaryUse();
        }
        if (state.getType() == Block.SEA_PICKLE) {
            return heldItem == state.getType() && state.getPickles() < 4 && !isSecondaryUse();
        }
        if (state.getType() == Block.TURTLE_EGG) {
            return heldItem == state.getType() && state.getEggs() < 4 && !isSecondaryUse();
        }
        // Glow lichen can be replaced if it has an open face, or the player is placing something
        if (state.getType() == Block.GLOW_LICHEN) {
            if (heldItem != Block.GLOW_LICHEN) {
                return true;
            }
            if (!state.isUp()) return true;
            if (!state.isDown()) return true;
            if (state.getNorth() == North.FALSE) return true;
            if (state.getSouth() == South.FALSE) return true;
            if (state.getEast() == East.FALSE) return true;
            return state.getWest() == West.FALSE;
        }
        if (state.getType() == Block.SCAFFOLDING) {
            return heldItem == Block.SCAFFOLDING;
        }
        if (BlockTags.SLABS.contains(state.getType())) {
            if (state.getTypeData() == Type.DOUBLE || state.getType() != heldItem) return false;

            // Here vanilla refers from
            // Set check can replace -> get block -> call block canBeReplaced -> check can replace boolean (default true)
            // uh... what?  I'm unsure what Mojang is doing here.  I think they just made a stupid mistake.
            // as this code is quite old.
            boolean flag = getClickedLocation().getY() > 0.5D;
            BlockFace clickedFace = getDirection();
            if (state.getTypeData() == Type.BOTTOM) {
                return clickedFace == BlockFace.TOP || flag && isFaceHorizontal();
            } else {
                return clickedFace == BlockFace.BOTTOM || !flag && isFaceHorizontal();
            }
        }
        if (state.getType() == Block.SNOW) {
            int layers = state.getLayers();
            if (heldItem == state.getType() && layers < 8) { // We index at 1 (less than 8 layers)
                return face == BlockFace.TOP;
            } else {
                return layers == 1; // index at 1, (1 layer)
            }
        }
        if (state.getType() == Block.VINE) {
            if (baseReplaceable) return true;
            if (heldItem != state.getType()) return false;
            if (!state.isUp()) return true;
            if (state.getNorth() == North.FALSE) return true;
            if (state.getSouth() == South.FALSE) return true;
            if (state.getEast() == East.FALSE) return true;
            return state.getWest() == West.FALSE;
        }

        return baseReplaceable;
    }

    public boolean isFaceFullCenter(BlockFace facing) {
        MinestomWrappedBlockState data = getDirectionalState(facing);
        CollisionBox box = CollisionData.getData(data.getType()).getMovementCollisionBox(player, player.getClientVersion(), data);

        if (box.isNull()) return false;
        if (isFullFace(facing)) return true;
        if (BlockTags.LEAVES.contains(data.getType())) return false;
        if (BlockTags.FENCE_GATES.contains(data.getType())) return false;

        List<SimpleCollisionBox> collisions = new ArrayList<>();
        box.downCast(collisions);

        AxisSelect axis = AxisUtil.getAxis(facing.getOppositeFace());

        for (SimpleCollisionBox simpleBox : collisions) {
            simpleBox = axis.modify(simpleBox);
            if (simpleBox.minX <= 7 / 16d && simpleBox.maxX >= 7 / 16d
                    && simpleBox.minY <= 0 && simpleBox.maxY >= 10 / 16d
                    && simpleBox.minZ <= 7 / 16d && simpleBox.maxZ >= 9 / 16d) {
                return true;
            }
        }

        return false;
    }

    public boolean isFaceRigid(BlockFace facing) {
        MinestomWrappedBlockState data = getDirectionalState(facing);
        CollisionBox box = CollisionData.getData(data.getType()).getMovementCollisionBox(player, player.getClientVersion(), data);

        if (box.isNull()) return false;
        if (isFullFace(facing)) return true;
        if (BlockTags.LEAVES.contains(data.getType())) return false;

        List<SimpleCollisionBox> collisions = new ArrayList<>();
        box.downCast(collisions);

        AxisSelect axis = AxisUtil.getAxis(facing.getOppositeFace());

        for (SimpleCollisionBox simpleBox : collisions) {
            simpleBox = axis.modify(simpleBox);
            if (simpleBox.minX <= 2 / 16d && simpleBox.maxX >= 14 / 16d
                    && simpleBox.minY <= 0 && simpleBox.maxY >= 1
                    && simpleBox.minZ <= 2 / 16d && simpleBox.maxZ >= 14 / 16d) {
                return true;
            }
        }

        return false;
    }

    public boolean isFullFace(BlockFace relative) {
        MinestomWrappedBlockState state = getDirectionalState(relative);
        BlockFace face = relative.getOppositeFace();
        BlockFace bukkitFace = BlockFace.valueOf(face.name());

        AxisSelect axis = AxisUtil.getAxis(face);

        CollisionBox box = CollisionData.getData(state.getType()).getMovementCollisionBox(player, player.getClientVersion(), state);

        Block blockMaterial = state.getType();

        if (BlockTags.LEAVES.contains(blockMaterial)) {
            // Leaves can't support blocks
            return false;
        } else if (blockMaterial == Block.SNOW) {
            return state.getLayers() == 8 || face == BlockFace.BOTTOM;
        } else if (BlockTags.STAIRS.contains(blockMaterial)) {
            if (face == BlockFace.TOP) {
                return state.getHalf() == Half.TOP;
            }
            if (face == BlockFace.BOTTOM) {
                return state.getHalf() == Half.BOTTOM;
            }

            return state.getFacing() == bukkitFace;
        } else if (blockMaterial == Block.COMPOSTER) { // Composters have solid faces except for on the top
            return face != BlockFace.TOP;
        } else if (blockMaterial == Block.SOUL_SAND) { // Soul sand is considered to be a full block when placing things
            return true;
        } else if (blockMaterial == Block.LADDER) { // Yes, although it breaks immediately, you can place blocks on ladders
            return state.getFacing().getOppositeFace() == bukkitFace;
        } else if (BlockTags.TRAPDOORS.contains(blockMaterial)) { // You can place blocks that need solid faces on trapdoors
            return (state.getFacing().getOppositeFace() == bukkitFace && state.isOpen()) ||
                    (state.getHalf() == Half.TOP && !state.isOpen() && bukkitFace == BlockFace.TOP) ||
                    (state.getHalf() == Half.BOTTOM && !state.isOpen() && bukkitFace == BlockFace.BOTTOM);
        } else if (BlockTags.DOORS.contains(blockMaterial)) { // You can place blocks that need solid faces on doors
            CollisionData data = CollisionData.getData(blockMaterial);

            if (data.dynamic instanceof DoorHandler) {
                int x = getPlacedAgainstBlockLocation().getX();
                int y = getPlacedAgainstBlockLocation().getY();
                int z = getPlacedAgainstBlockLocation().getZ();
                BlockFace dir = ((DoorHandler) data.dynamic).fetchDirection(player, player.getClientVersion(), state, x, y, z);
                return dir.getOppositeFace() == bukkitFace;
            }
        }

        List<SimpleCollisionBox> collisions = new ArrayList<>();
        box.downCast(collisions);

        for (SimpleCollisionBox simpleBox : collisions) {
            if (axis.modify(simpleBox).isFullBlockNoCache()) return true;
        }

        // Not an explicit edge case and is complicated, so isn't a full face
        return false;
    }

    public boolean isBlockFaceOpen(BlockFace facing) {
        Vector3i pos = getPlacedBlockPos();
        pos = pos.add(facing.toDirection().normalX(), facing.toDirection().normalY(), facing.toDirection().normalZ());
        // You can't build above height limit.
        if (pos.getY() >= player.compensatedWorld.getMaxHeight()) return false;

        return player.compensatedWorld.getWrappedBlockStateAt(pos).isReplaceable();
    }


    public boolean isFaceEmpty(BlockFace facing) {
        MinestomWrappedBlockState data = getDirectionalState(facing);
        CollisionBox box = CollisionData.getData(data.getType()).getMovementCollisionBox(player, player.getClientVersion(), data);

        if (box.isNull()) return false;
        if (isFullFace(facing)) return true;
        if (BlockTags.LEAVES.contains(data.getType())) return false;

        List<SimpleCollisionBox> collisions = new ArrayList<>();
        box.downCast(collisions);

        AxisSelect axis = AxisUtil.getAxis(facing.getOppositeFace());

        for (SimpleCollisionBox simpleBox : collisions) {
            simpleBox = axis.modify(simpleBox);
            // If all sides to the box have width, there is collision.
            switch (facing) {
                case NORTH:
                    if (simpleBox.minZ == 0) return false;
                    break;
                case SOUTH:
                    if (simpleBox.maxZ == 1) return false;
                    break;
                case EAST:
                    if (simpleBox.maxX == 1) return false;
                    break;
                case WEST:
                    if (simpleBox.minX == 0) return false;
                    break;
                case TOP:
                    if (simpleBox.maxY == 1) return false;
                    break;
                case BOTTOM:
                    if (simpleBox.minY == 0) return false;
                    break;
            }
        }

        return true;
    }

    public boolean isLava(BlockFace facing) {
        Vector3i pos = getPlacedBlockPos();
        pos = pos.add(facing.toDirection().normalX(), facing.toDirection().normalY(), facing.toDirection().normalZ());
        return player.compensatedWorld.getWrappedBlockStateAt(pos).getType() == Block.LAVA;
    }

    // I believe this is correct, although I'm using a method here just in case it's a tick off... I don't trust Mojang
    public boolean isSecondaryUse() {
        return player.isSneaking;
    }

    public boolean isInWater() {
        Vector3i pos = getPlacedBlockPos();
        return player.compensatedWorld.isWaterSourceBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isInLiquid() {
        Vector3i pos = getPlacedBlockPos();
        MinestomWrappedBlockState data = player.compensatedWorld.getWrappedBlockStateAt(pos);
        return Materials.isWater(player.getClientVersion(), data) || data.getType() == Block.LAVA;
    }

    public Block getBelowMaterial() {
        return getBelowState().getType();
    }

    public boolean isOn(Block... mat) {
        Block lookingFor = getBelowMaterial();
        return Arrays.stream(mat).anyMatch(m -> m == lookingFor);
    }

    public boolean isOnDirt() {
        return isOn(Block.DIRT, Block.GRASS_BLOCK, Block.PODZOL, Block.COARSE_DIRT, Block.MYCELIUM, Block.ROOTED_DIRT, Block.MOSS_BLOCK);
    }

    // I have to be the first anticheat to actually account for this... wish me luck
    // It's interested that redstone code is actually really simple, but has so many quirks
    // we don't need to account for these quirks though as they are more related to block updates.
    public boolean isBlockPlacedPowered() {
        Vector3i placed = getPlacedBlockPos();

        for (BlockFace face : BY_3D) {
            Vector3i modified = placed.add(face.toDirection().normalX(), face.toDirection().normalY(), face.toDirection().normalZ());

            // A block next to the player is providing power.  Therefore the block is powered
            if (player.compensatedWorld.getRawPowerAtState(face, modified.getX(), modified.getY(), modified.getZ()) > 0) {
                return true;
            }

            // Check if a block can even provide power... bukkit doesn't have a method for this?
            MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(modified);

            boolean isByDefaultConductive = !Materials.isSolidBlockingBlacklist(state.getType(), player.getClientVersion()) &&
                    CollisionData.getData(state.getType()).getMovementCollisionBox(player, player.getClientVersion(), state).isFullBlock();

            // Soul sand is exempt from this check.
            // Glass, moving pistons, beacons, redstone blocks (for some reason) and observers are not conductive
            // Otherwise, if something is solid blocking and a full block, then it is conductive
            if (state.getType() != Block.SOUL_SAND &&
                    BlockTags.GLASS_BLOCKS.contains(state.getType()) || state.getType() == Block.MOVING_PISTON
                    || state.getType() == Block.BEACON || state.getType() ==
                    Block.REDSTONE_BLOCK || state.getType() == Block.OBSERVER || !isByDefaultConductive) {
                continue;
            }

            // There's a better way to do this, but this is "good enough"
            // Mojang probably does it in a worse way than this.
            for (BlockFace recursive : BY_3D) {
                Vector3i poweredRecursive = placed.add(recursive.toDirection().normalX(), recursive.toDirection().normalY(), recursive.toDirection().normalZ());

                // A block next to the player is directly powered.  Therefore, the block is powered
                if (player.compensatedWorld.getDirectSignalAtState(recursive, poweredRecursive.getX(), poweredRecursive.getY(), poweredRecursive.getZ()) > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public BlockFace[] getHorizontalFaces() {
        return BY_2D;
    }

    public BlockFace getDirection() {
        return face;
    }

    public void setFace(BlockFace face) {
        this.face = face;
        this.faceId = face.ordinal();
    }

    private final BlockFace[] CARTESIAN_VALUES = new BlockFace[]{BlockFace.BOTTOM, BlockFace.TOP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
    public void setFaceId(int face) {
        this.faceId = face;
        this.face = getBlockFaceByValue(face);
    }

    private BlockFace getBlockFaceByValue(int face) {
        return CARTESIAN_VALUES[face % CARTESIAN_VALUES.length];
    }

    private List<BlockFace> getNearestLookingDirections() {
        float f = player.yRot * ((float) Math.PI / 180F);
        float f1 = -player.xRot * ((float) Math.PI / 180F);
        float f2 = player.trigHandler.sin(f);
        float f3 = player.trigHandler.cos(f);
        float f4 = player.trigHandler.sin(f1);
        float f5 = player.trigHandler.cos(f1);
        boolean flag = f4 > 0.0F;
        boolean flag1 = f2 < 0.0F;
        boolean flag2 = f5 > 0.0F;
        float f6 = flag ? f4 : -f4;
        float f7 = flag1 ? -f2 : f2;
        float f8 = flag2 ? f5 : -f5;
        float f9 = f6 * f3;
        float f10 = f8 * f3;
        BlockFace direction = flag ? BlockFace.EAST : BlockFace.WEST;
        BlockFace direction1 = flag1 ? BlockFace.TOP : BlockFace.BOTTOM;
        BlockFace direction2 = flag2 ? BlockFace.SOUTH : BlockFace.NORTH;
        if (f6 > f8) {
            if (f7 > f9) {
                return makeDirList(direction1, direction, direction2);
            } else {
                return f10 > f7 ? makeDirList(direction, direction2, direction1) : makeDirList(direction, direction1, direction2);
            }
        } else if (f7 > f10) {
            return makeDirList(direction1, direction2, direction);
        } else {
            return f9 > f7 ? makeDirList(direction2, direction, direction1) : makeDirList(direction2, direction1, direction);
        }
    }

    private List<BlockFace> makeDirList(BlockFace one, BlockFace two, BlockFace three) {
        return Arrays.asList(one, two, three, three.getOppositeFace(), two.getOppositeFace(), one.getOppositeFace());
    }

    public BlockFace getNearestVerticalDirection() {
        return player.yRot < 0.0F ? BlockFace.TOP : BlockFace.BOTTOM;
    }

    // Copied from vanilla nms
    public List<BlockFace> getNearestPlacingDirections() {
        BlockFace[] faces = getNearestLookingDirections().toArray(new BlockFace[0]);

        if (!isReplaceClicked()) {
            BlockFace direction = getDirection();

            // Blame mojang for this code, not me
            int i;
            for (i = 0; i < faces.length && faces[i] != direction.getOppositeFace(); ++i) {
            }

            if (i > 0) {
                System.arraycopy(faces, 0, faces, 1, i);
                faces[0] = direction.getOppositeFace();
            }
        }

        return Arrays.asList(faces);
    }

    public boolean isFaceVertical() {
        return !isFaceHorizontal();
    }

    public boolean isFaceHorizontal() {
        BlockFace face = getDirection();
        return face == BlockFace.NORTH || face == BlockFace.EAST || face == BlockFace.SOUTH || face == BlockFace.WEST;
    }

    public boolean isXAxis() {
        BlockFace face = getDirection();
        return face == BlockFace.WEST || face == BlockFace.EAST;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Vector3i getPlacedBlockPos() {
        if (replaceClicked) return blockPosition;

        int x = blockPosition.getX() + getNormalBlockFace().getX();
        int y = blockPosition.getY() + getNormalBlockFace().getY();
        int z = blockPosition.getZ() + getNormalBlockFace().getZ();
        return new Vector3i(x, y, z);
    }

    public Vector3i getNormalBlockFace() {
        return switch (face) {
            case BOTTOM -> new Vector3i(0, -1, 0);
            case SOUTH -> new Vector3i(0, 0, 1);
            case NORTH -> new Vector3i(0, 0, -1);
            case WEST -> new Vector3i(-1, 0, 0);
            case EAST -> new Vector3i(1, 0, 0);
            default -> new Vector3i(0, 1, 0);
        };
    }

    public void set(Block material) {
        set(new MinestomWrappedBlockState(material));
    }

    public void set(BlockFace face, MinestomWrappedBlockState state) {
        Vector3i blockPos = getPlacedBlockPos().add(face.toDirection().normalX(), face.toDirection().normalY(), face.toDirection().normalZ());
        set(blockPos, state);
    }

    public void set(Vector3i position, MinestomWrappedBlockState state) {
        // Hack for scaffolding to be the correct bounding box
        CollisionBox box = CollisionData.getData(state.getType()).getMovementCollisionBox(player, player.getClientVersion(), state, position.getX(), position.getY(), position.getZ());


        // Note scaffolding is a special case because it can never intersect with the player's bounding box,
        // and we fetch it with lastY instead of y which is wrong, so it is easier to just ignore scaffolding here
        if (state.getType() != Block.SCAFFOLDING) {
            // A player cannot place a block in themselves.
            // 0.03 can desync quite easily
            // 0.002 desync must be done with teleports, it is very difficult to do with slightly moving.
            if (box.isIntersected(player.boundingBox)) {
                return;
            }

            // Other entities can also block block-placing
            // This sucks and desyncs constantly, but what can you do?
            //
            // 1.9+ introduced the mechanic where both the client and server must agree upon a block place
            // 1.8 clients will simply not send the place when it fails, thanks mojang.
            if (player.getClientVersion().isNewerThan(ClientVersion.V_1_8)) {
                for (PacketEntity entity : player.compensatedEntities.entityMap.values()) {
                    SimpleCollisionBox interpBox = entity.getPossibleCollisionBoxes();

                    final double scale = entity.getAttributeValue(Attribute.GENERIC_SCALE);
                    double width = BoundingBoxSize.getWidth(player, entity) * scale;
                    double height = BoundingBoxSize.getHeight(player, entity) * scale;
                    double interpWidth = Math.max(interpBox.maxX - interpBox.minX, interpBox.maxZ - interpBox.minZ);
                    double interpHeight = interpBox.maxY - interpBox.minY;

                    // If not accurate, fall back to desync pos
                    // This happens due to the lack of an idle packet on 1.9+ clients
                    // On 1.8 clients this should practically never happen
                    if (interpWidth - width > 0.05 || interpHeight - height > 0.05) {
                        Vector3d entityPos = entity.trackedServerPosition.getPos();
                        interpBox = GetBoundingBox.getPacketEntityBoundingBox(player, entityPos.getX(), entityPos.getY(), entityPos.getZ(), entity);
                    }

                    if (box.isIntersected(interpBox)) {
                        return; // Blocking the block placement
                    }
                }
            }
        }

        // If a block already exists here, then we can't override it.
        MinestomWrappedBlockState existingState = player.compensatedWorld.getWrappedBlockStateAt(position);
        if (!replaceClicked && !canBeReplaced(material, existingState, face)) {
            return;
        }

        // Check for min and max bounds of world
        if (player.compensatedWorld.getMaxHeight() <= position.getY() || position.getY() < player.compensatedWorld.getMinHeight()) {
            return;
        }

        // Check for waterlogged
        if (state.getInternalData().containsKey(StateValue.WATERLOGGED)) { // waterloggable
            state.setWaterlogged(existingState.getType() == Block.WATER && existingState.getLevel() == 0);
        }

        player.getInventory().onBlockPlace(this);
        player.compensatedWorld.updateBlock(position.getX(), position.getY(), position.getZ(), state.getGlobalId());
    }

    public boolean isZAxis() {
        BlockFace face = getDirection();
        return face == BlockFace.NORTH || face == BlockFace.SOUTH;
    }

    // We need to now run block
    public void tryCascadeBlockUpdates(Vector3i pos) {
        if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2)) return;

        cascadeBlockUpdates(pos);
    }

    private void cascadeBlockUpdates(Vector3i pos) {

    }

    public void set(MinestomWrappedBlockState state) {
        set(getPlacedBlockPos(), state);
    }

    public void resync() {
        isCancelled = true;
    }

    // All method with rants about mojang must go below this line

    // MOJANG??? Why did you remove this from the damn packet.  YOU DON'T DO BLOCK PLACING RIGHT!
    // You use last tick vector on the server and current tick on the client...
    // You also have 0.03 for FIVE YEARS which will mess this up.  nice one mojang
    // * 0.0004 as of 2/24/2022
    // Fix your damn netcode
    //
    // You also have the desync caused by eye height as apparently tracking the player's ticks wasn't important to you
    // No mojang, you really do need to track client ticks to get their accurate eye height.
    // another damn desync added... maybe next decade it will get fixed and double the amount of issues.
    public MutableVector getClickedLocation() {
        SimpleCollisionBox box = new SimpleCollisionBox(getPlacedAgainstBlockLocation());
        MutableVector look = ReachUtils.getLook(player, player.xRot, player.yRot);

        final double distance = player.compensatedEntities.getSelf().getAttributeValue(Attribute.PLAYER_BLOCK_INTERACTION_RANGE) + 3;
        MutableVector eyePos = new MutableVector(player.x, player.y + player.getEyeHeight(), player.z);
        MutableVector endReachPos = eyePos.clone().add(new MutableVector(look.getX() * distance, look.getY() * distance, look.getZ() * distance));
        MutableVector intercept = ReachUtils.calculateIntercept(box, eyePos, endReachPos).getFirst();

        // Bring this back to relative to the block
        // The player didn't even click the block... (we should force resync BEFORE we get here!)
        if (intercept == null) return new MutableVector();

        intercept.setX(intercept.getX() - box.minX);
        intercept.setY(intercept.getY() - box.minY);
        intercept.setZ(intercept.getZ() - box.minZ);

        return intercept;
    }

    // Remember to use the next tick's look, which we handle elsewhere
    public BlockFace getPlayerFacing() {
        return BY_2D[GrimMath.floor(player.xRot / 90.0D + 0.5D) & 3];
    }

    public void set() {
        if (material == null) {
            LogUtil.warn("Material " + null + " has no placed type!");
            return;
        }
        set(material);
    }

    public void setAbove() {
        Vector3i placed = getPlacedBlockPos();
        placed = placed.add(0, 1, 0);
        set(placed, new MinestomWrappedBlockState(material));
    }

    public void setAbove(MinestomWrappedBlockState toReplaceWith) {
        Vector3i placed = getPlacedBlockPos();
        placed = placed.add(0, 1, 0);
        set(placed, toReplaceWith);
    }
}

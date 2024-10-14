package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.CollisionData;
import ac.grim.grimac.utils.collisions.blocks.DoorHandler;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.vector.MutableVector;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

public class FluidTypeFlowing {
    public static MutableVector getFlow(GrimPlayer player, int originalX, int originalY, int originalZ) {
        float fluidLevel = (float) Math.min(player.compensatedWorld.getFluidLevelAt(originalX, originalY, originalZ), 8 / 9D);
        ClientVersion version = player.getClientVersion();

        if (fluidLevel == 0) return new MutableVector();

        double d0 = 0.0D;
        double d1 = 0.0D;
        for (BlockFace enumdirection : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            int modifiedX = originalX + enumdirection.toDirection().normalX();
            int modifiedZ = originalZ + enumdirection.toDirection().normalZ();

            if (affectsFlow(player, originalX, originalY, originalZ, modifiedX, originalY, modifiedZ)) {
                float f = (float) Math.min(player.compensatedWorld.getFluidLevelAt(modifiedX, originalY, modifiedZ), 8 / 9D);
                float f1 = 0.0F;
                if (f == 0.0F) {
                    Block mat = player.compensatedWorld.getStateTypeAt(modifiedX, originalY, modifiedZ);

                    // Grim's definition of solid is whether the block has a hitbox
                    // Minecraft is... it's whatever Mojang was feeling like, but it's very consistent
                    // Use method call to support 1.13-1.15 clients and banner oddity
                    if (Materials.isSolidBlockingBlacklist(mat, version)) {
                        if (affectsFlow(player, originalX, originalY, originalZ, modifiedX, originalY - 1, modifiedZ)) {
                            f = (float) Math.min(player.compensatedWorld.getFluidLevelAt(modifiedX, originalY - 1, modifiedZ), 8 / 9D);
                            if (f > 0.0F) {
                                f1 = fluidLevel - (f - 0.8888889F);
                            }
                        }
                    }

                } else if (f > 0.0F) {
                    f1 = fluidLevel - f;
                }

                if (f1 != 0.0F) {
                    d0 += (float) enumdirection.toDirection().normalX() * f1;
                    d1 += (float) enumdirection.toDirection().normalZ() * f1;
                }
            }
        }

        MutableVector vec3d = new MutableVector(d0, 0.0D, d1);

        // Fluid level 1-7 is for regular fluid heights
        // Fluid level 8-15 is for falling fluids
        MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(originalX, originalY, originalZ);
        if ((state.getType() == Block.WATER || state.getType() == Block.LAVA) && state.getLevel() >= 8) {
            for (BlockFace enumdirection : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                if (isSolidFace(player, originalX, originalY, originalZ, enumdirection) || isSolidFace(player, originalX, originalY + 1, originalZ, enumdirection)) {
                    vec3d = normalizeVectorWithoutNaN(vec3d).add(new MutableVector(0.0D, -6.0D, 0.0D));
                    break;
                }
            }
        }
        return normalizeVectorWithoutNaN(vec3d);
    }

    private static boolean affectsFlow(GrimPlayer player, int originalX, int originalY, int originalZ, int x2, int y2, int z2) {
        return isEmpty(player, x2, y2, z2) || isSame(player, originalX, originalY, originalZ, x2, y2, z2);
    }

    protected static boolean isSolidFace(GrimPlayer player, int originalX, int y, int originalZ, BlockFace direction) {
        int x = originalX + direction.toDirection().normalX();
        int z = originalZ + direction.toDirection().normalZ();

        MinestomWrappedBlockState data = player.compensatedWorld.getWrappedBlockStateAt(x, y, z);
        Block type = data.getType();

        if (isSame(player, x, y, z, originalX, y, originalZ)) return false;
        if (type == Block.ICE) return false;

        // 1.11 and below clients use a different method to determine solid faces
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_12)) {
            if (type == Block.PISTON || type == Block.STICKY_PISTON) {
                return data.getFacing().getOppositeFace() == direction ||
                        CollisionData.getData(type).getMovementCollisionBox(player, player.getClientVersion(), data, 0, 0, 0).isFullBlock();
            } else if (type == Block.PISTON_HEAD) {
                return data.getFacing() == direction;
            }
        }

        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_12)) {
            // No bush, cocoa, wart, reed
            // No double grass, tall grass, or vine
            // No button, flower pot, ladder, lever, rail, redstone, redstone wire, skull, torch, trip wire, or trip wire hook
            // No carpet
            // No snow
            // Otherwise, solid
            return !Materials.isSolidBlockingBlacklist(type, player.getClientVersion());
        } else if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_12) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_13_2)) {
            // 1.12/1.13 exempts stairs, pistons, sticky pistons, and piston heads.
            // It also exempts shulker boxes, leaves, trapdoors, stained glass, beacons, cauldrons, glass, glowstone, ice, sea lanterns, and conduits.
            //
            // Everything is hardcoded, and I have attempted by best at figuring out things, although it's not perfect
            // Report bugs on GitHub, as always.  1.13 is an odd version and issues could be lurking here.
            if (Materials.isStairs(type) || Materials.isLeaves(type)
                    || Materials.isShulker(type) || Materials.isGlassBlock(type)
                    || BlockTags.TRAPDOORS.contains(type))
                return false;

            if (type == Block.BEACON || BlockTags.CAULDRONS.contains(type)
                    || type == Block.GLOWSTONE || type == Block.SEA_LANTERN || type == Block.CONDUIT)
                return false;

            if (type == Block.PISTON || type == Block.STICKY_PISTON || type == Block.PISTON_HEAD)
                return false;

            return type == Block.SOUL_SAND || (CollisionData.getData(type).getMovementCollisionBox(player, player.getClientVersion(), data, x, y, z).isFullBlock());
        } else {
            if (Materials.isLeaves(type)) {
                // Leaves don't have solid faces in 1.13, they do in 1.14 and 1.15, and they don't in 1.16 and beyond
                return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2);
            } else if (type == Block.SNOW) {
                return data.getLayers() == 8;
            } else if (Materials.isStairs(type)) {
                return data.getFacing() == direction;
            } else if (type == Block.COMPOSTER) {
                return true;
            } else if (type == Block.SOUL_SAND) {
                return player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2) || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16);
            } else if (type == Block.LADDER) {
                return data.getFacing().getOppositeFace() == direction;
            } else if (BlockTags.TRAPDOORS.contains(type)) {
                return data.getFacing().getOppositeFace() == direction && data.isOpen();
            } else if (BlockTags.DOORS.contains(type)) {
                CollisionData collisionData = CollisionData.getData(type);

                if (collisionData.dynamic instanceof DoorHandler) {
                    BlockFace dir = ((DoorHandler) collisionData.dynamic).fetchDirection(player, player.getClientVersion(), data, x, y, z);
                    return dir.getOppositeFace() == direction;
                }
            }

            // Explicitly a full block, therefore it has a full face
            return (CollisionData.getData(type).getMovementCollisionBox(player, player.getClientVersion(), data, x, y, z).isFullBlock());
        }
    }

    private static MutableVector normalizeVectorWithoutNaN(MutableVector vector) {
        double var0 = vector.length();
        return var0 < 1.0E-4 ? new MutableVector() : vector.multiply(1 / var0);
    }

    public static boolean isEmpty(GrimPlayer player, int x, int y, int z) {
        return player.compensatedWorld.getFluidLevelAt(x, y, z) == 0;
    }

    // Check if both are a type of water or both are a type of lava
    // This is a bit slow... but I don't see a better way to do it with the bukkit api and no nms
    public static boolean isSame(GrimPlayer player, int x1, int y1, int z1, int x2, int y2, int z2) {
        return player.compensatedWorld.getWaterFluidLevelAt(x1, y1, z1) > 0 &&
                player.compensatedWorld.getWaterFluidLevelAt(x2, y2, z2) > 0 ||
                player.compensatedWorld.getLavaFluidLevelAt(x1, y1, z1) > 0 &&
                        player.compensatedWorld.getLavaFluidLevelAt(x2, y2, z2) > 0;
    }
}

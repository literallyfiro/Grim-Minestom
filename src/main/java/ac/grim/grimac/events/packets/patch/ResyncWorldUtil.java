package ac.grim.grimac.events.packets.patch;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.WrapperPlayServerMultiBlockChange;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.Map;

public class ResyncWorldUtil {
    static HashMap<Map<String, String>, Integer> blockDataToId = new HashMap<>();

    public static void resyncPosition(GrimPlayer player, Point pos) {
        resyncPositions(player, pos.blockX(), pos.blockY(), pos.blockZ(), pos.blockX(), pos.blockY(), pos.blockZ());
    }

    public static void resyncPositions(GrimPlayer player, SimpleCollisionBox box) {
        resyncPositions(player, GrimMath.floor(box.minX), GrimMath.floor(box.minY), GrimMath.floor(box.minZ),
                GrimMath.ceil(box.maxX), GrimMath.ceil(box.maxY), GrimMath.ceil(box.maxZ));
    }

    public static void resyncPositions(GrimPlayer player, int minBlockX, int mY, int minBlockZ, int maxBlockX, int mxY, int maxBlockZ) {
        // Check the 4 corners of the player world for loaded chunks before calling event
        if (!player.compensatedWorld.isChunkLoaded(minBlockX >> 4, minBlockZ >> 4) || !player.compensatedWorld.isChunkLoaded(minBlockX >> 4, maxBlockZ >> 4)
                || !player.compensatedWorld.isChunkLoaded(maxBlockX >> 4, minBlockZ >> 4) || !player.compensatedWorld.isChunkLoaded(maxBlockX >> 4, maxBlockZ >> 4))
            return;

        // Takes 0.15ms or so to complete. Not bad IMO. Unsure how I could improve this other than sending packets async.
        // But that's on PacketEvents.

        // Player hasn't spawned, don't spam packets
        if (!player.getSetbackTeleportUtil().hasAcceptedSpawnTeleport) return;

        // Check the 4 corners of the BB for loaded chunks, don't freeze main thread to load chunks.
        if (!player.bukkitPlayer.getInstance().isChunkLoaded(minBlockX >> 4, minBlockZ >> 4) || !player.bukkitPlayer.getInstance().isChunkLoaded(minBlockX >> 4, maxBlockZ >> 4)
                || !player.bukkitPlayer.getInstance().isChunkLoaded(maxBlockX >> 4, minBlockZ >> 4) || !player.bukkitPlayer.getInstance().isChunkLoaded(maxBlockX >> 4, maxBlockZ >> 4))
            return;

        // This is based on Tuinity's code, thanks leaf. Now merged into paper.
        // I have no idea how I could possibly get this more efficient...
        final int minSection = player.compensatedWorld.getMinHeight() >> 4;
        final int minBlock = minSection;
        final int maxBlock = player.compensatedWorld.getMaxHeight() - 1;

        int minBlockY = Math.max(minBlock, mY);
        int maxBlockY = Math.min(maxBlock, mxY);

        for (int currChunkZ = minBlockZ; currChunkZ <= maxBlockZ; ++currChunkZ) {
            int minZ = currChunkZ == minBlockZ ? minBlockZ & 15 : 0; // coordinate in chunk
            int maxZ = currChunkZ == maxBlockZ ? maxBlockZ & 15 : 15; // coordinate in chunk

            for (int currChunkX = minBlockX; currChunkX <= maxBlockX; ++currChunkX) {
                int minX = currChunkX == minBlockX ? minBlockX & 15 : 0; // coordinate in chunk
                int maxX = currChunkX == maxBlockX ? maxBlockX & 15 : 15; // coordinate in chunk

                Chunk chunk = player.bukkitPlayer.getInstance().getChunkAt(currChunkX, currChunkZ);

                for (int currChunkY = minBlockY; currChunkY <= maxBlockY; ++currChunkY) {
                    int minY = currChunkY == minBlockY ? minBlockY & 15 : 0; // coordinate in chunk
                    int maxY = currChunkY == maxBlockY ? maxBlockY & 15 : 15; // coordinate in chunk

                    int totalBlocks = (maxX - minX + 1) * (maxZ - minZ + 1) * (maxY - minY + 1);
                    WrapperPlayServerMultiBlockChange.EncodedBlock[] encodedBlocks = new WrapperPlayServerMultiBlockChange.EncodedBlock[totalBlocks];

                    int blockIndex = 0;
                    // Alright, we are now in a chunk section
                    // This can be used to construct and send a multi block change
                    for (int currZ = minZ; currZ <= maxZ; ++currZ) {
                        for (int currX = minX; currX <= maxX; ++currX) {
                            for (int currY = minY; currY <= maxY; ++currY) {
                                Block block = chunk.getBlock(currX, currY | (currChunkY << 4), currZ);

                                int blockId = blockDataToId.computeIfAbsent(block.properties(), data -> MinestomWrappedBlockState.getByString(MinestomWrappedBlockState.propertiesToString(block)).getGlobalId());
                                encodedBlocks[blockIndex++] = new WrapperPlayServerMultiBlockChange.EncodedBlock(blockId, currX, currY | (currChunkY << 4), currZ);
                            }
                        }
                    }

                    WrapperPlayServerMultiBlockChange packet = new WrapperPlayServerMultiBlockChange(new Vec(currChunkX, currChunkY, currChunkZ), encodedBlocks);
                    player.bukkitPlayer.sendPacket(packet.toMinestomPacket());
//                    ChannelHelper.runInEventLoop(player.user.getChannel(), () -> player.user.sendPacket(packet));
                }
            }
        }
    }
}

package ac.grim.grimac.events.packets.patch;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.MultiBlockChangePacket;

import static ac.grim.grimac.GrimAPI.EXECUTOR_SERVICE;

public class ResyncWorldUtil {
//    static HashMap<BlockData, Integer> blockDataToId = new HashMap<>();

    public static void resyncPosition(GrimPlayer player, Vector3i pos) {
        resyncPositions(player, pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
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
        final int minBlock = minSection << 4;
        final int maxBlock = player.compensatedWorld.getMaxHeight() - 1;

        int minBlockY = Math.max(minBlock, mY);
        int maxBlockY = Math.min(maxBlock, mxY);

        int minChunkX = minBlockX >> 4;
        int maxChunkX = maxBlockX >> 4;

        int minChunkY = minBlockY >> 4;
        int maxChunkY = maxBlockY >> 4;

        int minChunkZ = minBlockZ >> 4;
        int maxChunkZ = maxBlockZ >> 4;

        for (int currChunkZ = minChunkZ; currChunkZ <= maxChunkZ; ++currChunkZ) {
            int minZ = currChunkZ == minChunkZ ? minBlockZ & 15 : 0; // coordinate in chunk
            int maxZ = currChunkZ == maxChunkZ ? maxBlockZ & 15 : 15; // coordinate in chunk

            for (int currChunkX = minChunkX; currChunkX <= maxChunkX; ++currChunkX) {
                int minX = currChunkX == minChunkX ? minBlockX & 15 : 0; // coordinate in chunk
                int maxX = currChunkX == maxChunkX ? maxBlockX & 15 : 15; // coordinate in chunk

                Chunk chunk = player.bukkitPlayer.getInstance().getChunkAt(currChunkX, currChunkZ);
                if (chunk == null) continue;


                for (int currChunkY = minChunkY; currChunkY <= maxChunkY; ++currChunkY) {
                    int minY = currChunkY == minChunkY ? minBlockY & 15 : 0; // coordinate in chunk
                    int maxY = currChunkY == maxChunkY ? maxBlockY & 15 : 15; // coordinate in chunk

                    int totalBlocks = (maxX - minX + 1) * (maxZ - minZ + 1) * (maxY - minY + 1);
                    long[] blocks = new long[totalBlocks];

                    int blockIndex = 0;
                    // Alright, we are now in a chunk section
                    // This can be used to construct and send a multi block change
                    for (int currZ = minZ; currZ <= maxZ; ++currZ) {
                        for (int currX = minX; currX <= maxX; ++currX) {
                            for (int currY = minY; currY <= maxY; ++currY) {
                                Block block = chunk.getBlock(currX, currY | (currChunkY << 4), currZ);
                                int blockId = block.id();

                                blocks[blockIndex++] = blockDataToLong(blockId, currX, currY | (currChunkY << 4), currZ);
                            }
                        }
                    }


                    System.out.println("Sending multi block change packet");

                    long encodedPos = 0;
                    encodedPos |= (currChunkX & 0x3FFFFFL) << 42;
                    encodedPos |= (currChunkZ & 0x3FFFFFL) << 20;
                    encodedPos |= (currChunkY & 0xFFFFFL);
                    MultiBlockChangePacket packet = new MultiBlockChangePacket(encodedPos, blocks);
                    EXECUTOR_SERVICE.submit(() -> player.bukkitPlayer.sendPacket(packet));
//                    ChannelHelper.runInEventLoop(player.user.getChannel(), () -> player.user.sendPacket(packet));
                }
            }
        }
    }

    private static long blockDataToLong(int blockID, int x, int y, int z) {
        return (long) blockID << 12 | (x & 0xF) << 8 | (z & 0xF) << 4 | (y & 0xF);
    }
}

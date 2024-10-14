package ac.grim.grimac.events.packets.worldreader;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.chunks.Column;
import ac.grim.grimac.utils.data.TeleportData;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.network.packet.server.play.AcknowledgeBlockChangePacket;
import net.minestom.server.network.packet.server.play.BlockChangePacket;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;
import net.minestom.server.network.packet.server.play.ChunkDataPacket;
import net.minestom.server.network.packet.server.play.MultiBlockChangePacket;
import net.minestom.server.network.packet.server.play.UnloadChunkPacket;

public class BasePacketWorldReader {

//    public BasePacketWorldReader(EventNode<Event> eventNode) {
//        super(PacketListenerPriority.HIGH);
//    }

    public BasePacketWorldReader(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("base-packet-world-reader");
        node.setPriority(EventPriority.HIGH.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof UnloadChunkPacket unloadChunk) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            unloadChunk(player, unloadChunk.chunkX(), unloadChunk.chunkZ());
        }

        if (event.getPacket() instanceof ChunkDataPacket packet) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            handleMapChunk(player, packet, event);
        }

        if (event.getPacket() instanceof BlockChangePacket packet) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            handleBlockChange(player, packet);
        }

        if (event.getPacket() instanceof MultiBlockChangePacket packet) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            handleMultiBlockChange(player, packet);
        }

        if (event.getPacket() instanceof AcknowledgeBlockChangePacket changes) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            player.compensatedWorld.handlePredictionConfirmation(changes.sequence());
        }

//        if (event.getPacket() instanceof ACKNOWLEDGE_PLAYER_DIGGINGPacket) {
//            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
//            if (player == null) return;
//
//            WrapperPlayServerAcknowledgePlayerDigging ack = new WrapperPlayServerAcknowledgePlayerDigging(event);
//            player.compensatedWorld.handleBlockBreakAck(ack.getBlockPosition(), ack.getBlockId(), ack.getAction(), ack.isSuccessful());
//        }

        if (event.getPacket() instanceof ChangeGameStatePacket newState) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                if (newState.reason() == ChangeGameStatePacket.Reason.BEGIN_RAINING) {
                    player.compensatedWorld.isRaining = true;
                } else if (newState.reason() == ChangeGameStatePacket.Reason.END_RAINING) {
                    player.compensatedWorld.isRaining = false;
                } else if (newState.reason() == ChangeGameStatePacket.Reason.RAIN_LEVEL_CHANGE) {
                    player.compensatedWorld.isRaining = newState.value() > 0.2f;
                }
            });
        }
    }

    public Chunk[] read(GrimPlayer player, int chunkSize, int x, int z) {
        Chunk[] chunks = new Chunk[chunkSize];

        for (int index = 0; index < chunkSize; ++index) {
            chunks[index] = player.bukkitPlayer.getInstance().getChunk(x, z);
        }

        return chunks;
    }

    public void handleMapChunk(GrimPlayer player, ChunkDataPacket chunkData, PlayerPacketOutEvent event) {
        int x = chunkData.chunkX();
        int z = chunkData.chunkZ();

        int height = player.bukkitPlayer.getDimensionType().height();

        Chunk[] chunks = read(player, height, x, z);

        addChunkToCache(event, player, chunks, true, x, z);
    }

    public void addChunkToCache(PlayerPacketOutEvent event, GrimPlayer player, Chunk[] chunks, boolean isGroundUp, int chunkX, int chunkZ) {
        double chunkCenterX = (chunkX << 4) + 8;
        double chunkCenterZ = (chunkZ << 4) + 8;
        boolean shouldPostTrans = Math.abs(player.x - chunkCenterX) < 16 && Math.abs(player.z - chunkCenterZ) < 16;

        for (TeleportData teleports : player.getSetbackTeleportUtil().pendingTeleports) {
            if (teleports.getFlags().getMask() != 0) {
                continue; // Worse that will happen is people will get an extra setback...
            }
            shouldPostTrans = shouldPostTrans || (Math.abs(teleports.getLocation().getX() - chunkCenterX) < 16 && Math.abs(teleports.getLocation().getZ() - chunkCenterZ) < 16);
        }

        if (shouldPostTrans) {
            event.getTasksAfterSend().add(player::sendTransaction); // Player is in this unloaded chunk
        }
        if (isGroundUp) {
            Column column = new Column(chunkX, chunkZ, chunks, player.lastTransactionSent.get());
            player.compensatedWorld.addToCache(column, chunkX, chunkZ);
        } else {
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                Column existingColumn = player.compensatedWorld.getChunk(chunkX, chunkZ);
                if (existingColumn == null) {
                    // Corrupting the player's empty chunk is actually quite meaningless
                    // You are able to set blocks inside it, and they do apply, it just always returns air despite what its data says
                    // So go ahead, corrupt the player's empty chunk and make it no longer all air, it doesn't matter
                    //
                    // LogUtil.warn("Invalid non-ground up continuous sent for empty chunk " + chunkX + " " + chunkZ + " for " + player.user.getProfile().getName() + "! This corrupts the player's empty chunk!");
                    return;
                }
                existingColumn.mergeChunks(chunks);
            });
        }
    }

    public void unloadChunk(GrimPlayer player, int x, int z) {
        if (player == null) return;
        player.compensatedWorld.removeChunkLater(x, z);
    }

    public void handleBlockChange(GrimPlayer player, BlockChangePacket blockChange) {
        int range = 16;
        Vector3i blockPosition = new Vector3i(blockChange.blockPosition());
        // Don't spam transactions (block changes are sent in batches)
        if (Math.abs(blockPosition.getX() - player.x) < range && Math.abs(blockPosition.getY() - player.y) < range && Math.abs(blockPosition.getZ() - player.z) < range &&
                player.lastTransSent + 2 < System.currentTimeMillis())
            player.sendTransaction();

        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedWorld.updateBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), blockChange.blockStateId()));
    }

    public void handleMultiBlockChange(GrimPlayer player, MultiBlockChangePacket multiBlockChange) {
        boolean didSend = false;
        int range = 16;

        for (long data : multiBlockChange.blocks()) {
            short position = (short) (data & 0xFFFL);

            int sectionX = (int) (multiBlockChange.chunkSectionPosition() >> 42);
            int sectionY = (int) (multiBlockChange.chunkSectionPosition() << 44 >> 44);
            int sectionZ = (int) (multiBlockChange.chunkSectionPosition() << 22 >> 42);

            int x = (sectionX<< 4) + (position >>> 8 & 0xF);
            int y = (sectionY << 4) + (position & 0xF);
            int z = (sectionZ << 4) + (position >>> 4 & 0xF);

            int blockID = (int) (data >>> 12);

            // Don't send a transaction unless it's within 16 blocks of the player
            if (!didSend && Math.abs(x - player.x) < range && Math.abs(y - player.y) < range && Math.abs(z - player.z) < range &&
                    player.lastTransSent + 2 < System.currentTimeMillis()) {
                didSend = true;
                player.sendTransaction();
            }

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedWorld.updateBlock(x, y, z, blockID));
        }
    }
}

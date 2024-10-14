package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.Pair;
import ac.grim.grimac.utils.minestom.EventPriority;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.common.ClientPongPacket;
import net.minestom.server.network.packet.server.common.PingPacket;

public class PacketPingListener {

    // Must listen on LOWEST (or maybe low) to stop Tuinity packet limiter from kicking players for transaction/pong spam
    public PacketPingListener(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-ping-listener");
        node.setPriority(EventPriority.LOWEST.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);
        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);

        globalNode.addChild(node);
    }

    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPongPacket pong) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;
            player.packetStateData.lastTransactionPacketWasValid = false;

            int id = pong.id();
            // If it wasn't below 0, it wasn't us
            // If it wasn't in short range, it wasn't us either
            if (id == (short) id) {
                short shortID = ((short) id);
                if (player.addTransactionResponse(shortID)) {
                    player.packetStateData.lastTransactionPacketWasValid = true;
                    // Not needed for vanilla as vanilla ignores this packet, needed for packet limiters
                    event.setCancelled(true);
                }
            }
        }
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof PingPacket pong) {
            int id = pong.id();
            //
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;
            player.packetStateData.lastServerTransWasValid = false;
            // Check if in the short range, we only use short range
            if (id == (short) id) {
                // Cast ID twice so we can use the list
                Short shortID = ((short) id);
                if (player.didWeSendThatTrans.remove(shortID)) {
                    player.packetStateData.lastServerTransWasValid = true;
                    player.transactionsSent.add(new Pair<>(shortID, System.nanoTime()));
                    player.lastTransactionSent.getAndIncrement();
                }
            }
        }
    }


}

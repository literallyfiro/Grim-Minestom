package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.minestom.EventPriority;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.server.common.TagsPacket;

public class PacketServerTags {

    public PacketServerTags(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-server-tags");
        node.setPriority(EventPriority.NORMAL.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof TagsPacket tags) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            final boolean isPlay = event.getPacket().getId(ConnectionState.PLAY) == tags.playId();
            if (isPlay) {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.tagManager.handleTagSync(tags));
            } else {
                // This is during configuration stage, player isn't even in the game yet so no need to lag compensate.
                player.tagManager.handleTagSync(tags);
            }
        }
    }
}

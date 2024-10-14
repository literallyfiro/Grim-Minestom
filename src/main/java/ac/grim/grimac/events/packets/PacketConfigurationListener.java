package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.impl.misc.ClientBrand;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.minestom.EventPriority;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;

public class PacketConfigurationListener {

    public PacketConfigurationListener(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-configuration-listener");
        node.setPriority(EventPriority.LOW.ordinal());
        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);
        globalNode.addChild(node);
    }

    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPluginMessagePacket wrapper) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            String channelName = wrapper.channel();
            byte[] data = wrapper.data();
            if (channelName.equalsIgnoreCase("minecraft:brand") || channelName.equals("MC|Brand")) {
                player.checkManager.getPacketCheck(ClientBrand.class).handle(channelName, data);
            }
        }
    }

}

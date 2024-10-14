package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.minestom.EventPriority;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;

public class PacketPlayerCooldown {

//    public PacketPlayerCooldown() {
//        super(PacketListenerPriority.HIGH);
//    }

    public PacketPlayerCooldown(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-player-cooldown");
        node.setPriority(EventPriority.HIGH.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof SetCooldownPacket cooldown) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            int lastTransactionSent = player.lastTransactionSent.get();

            if (cooldown.cooldownTicks() == 0) { // for removing the cooldown
                player.latencyUtils.addRealTimeTask(lastTransactionSent + 1, () -> {
                    player.checkManager.getCompensatedCooldown().removeCooldown(Material.fromId(cooldown.itemId()));
                });
            } else { // Not for removing the cooldown
                player.latencyUtils.addRealTimeTask(lastTransactionSent, () -> {
                    player.checkManager.getCompensatedCooldown().addCooldown(Material.fromId(cooldown.itemId()),
                            cooldown.cooldownTicks(), lastTransactionSent);
                });
            }
        }
    }
}

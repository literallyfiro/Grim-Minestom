package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.ShulkerData;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.BlockActionPacket;

// If a player doesn't get this packet, then they don't know the shulker box is currently opened
// Meaning if a player enters a chunk with an opened shulker box, they see the shulker box as closed.
//
// Exempting the player on shulker boxes is an option... but then you have people creating PvP arenas
// on shulker boxes to get high lenience.
//
public class PacketBlockAction {
//    public PacketBlockAction() {
//        super(PacketListenerPriority.HIGH);
//    }

    public PacketBlockAction(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-block-action");
        node.setPriority(EventPriority.HIGH.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof BlockActionPacket blockAction) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            Vector3i blockPos = new Vector3i(blockAction.blockPosition());

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                // The client ignores the state sent to the client.
                MinestomWrappedBlockState existing = player.compensatedWorld.getWrappedBlockStateAt(blockPos);
                if (Materials.isShulker(existing.getType())) {
                    // Param is the number of viewers of the shulker box.
                    // Hashset with .equals() set to be position
                    if (blockAction.actionParam() >= 1) {
                        ShulkerData data = new ShulkerData(blockPos, player.lastTransactionSent.get(), false);
                        player.compensatedWorld.openShulkerBoxes.remove(data);
                        player.compensatedWorld.openShulkerBoxes.add(data);
                    } else {
                        // The shulker box is closing
                        ShulkerData data = new ShulkerData(blockPos, player.lastTransactionSent.get(), true);
                        player.compensatedWorld.openShulkerBoxes.remove(data);
                        player.compensatedWorld.openShulkerBoxes.add(data);
                    }
                }
            });
        }
    }
}

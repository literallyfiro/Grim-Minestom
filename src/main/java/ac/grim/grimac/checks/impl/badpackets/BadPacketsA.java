package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;

@CheckData(name = "BadPacketsA")
public class BadPacketsA extends Check implements PacketCheck {
    int lastSlot = -1;

    public BadPacketsA(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientHeldItemChangePacket packet) {
            final int slot = packet.slot();

            if (slot == lastSlot && flagAndAlert("slot=" + slot) && shouldModifyPackets()) {
                event.setCancelled(true);
                player.onPacketCancel();
            }

            lastSlot = slot;
        }
    }
}

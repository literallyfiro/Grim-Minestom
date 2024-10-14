package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;

@CheckData(name = "BadPacketsB")
public class BadPacketsB extends Check implements PacketCheck {
    public BadPacketsB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientSteerVehiclePacket packet) {
            if (Math.abs(packet.forward()) > 0.98f || Math.abs(packet.sideways()) > 0.98f) {
                if (flagAndAlert("forwards=" + packet.forward() + ", sideways=" + packet.sideways()) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}

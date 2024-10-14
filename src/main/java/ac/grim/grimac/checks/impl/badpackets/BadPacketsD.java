package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;

@CheckData(name = "BadPacketsD")
public class BadPacketsD extends Check implements PacketCheck {
    public BadPacketsD(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (player.packetStateData.lastPacketWasTeleport) return;

        if (event.getPacket() instanceof ClientPlayerRotationPacket || event.getPacket() instanceof ClientPlayerPositionAndRotationPacket) {
            final float pitch = new WrapperPlayClientPlayerFlying(event).getLocation().pitch();
            if (pitch > 90 || pitch < -90) {
                // Ban.
                if (flagAndAlert("pitch=" + pitch)) {
                    if (shouldModifyPackets()) {
                        // prevent other checks from using an invalid pitch
                        if (player.yRot > 90) player.yRot = 90;
                        if (player.yRot < -90) player.yRot = -90;

                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                }
            }
        }
    }
}

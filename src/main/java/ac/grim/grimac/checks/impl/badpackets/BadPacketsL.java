package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;

import java.util.Locale;

// checks for impossible dig packets
@CheckData(name = "BadPacketsL")
public class BadPacketsL extends Check implements PacketCheck {

    public BadPacketsL(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPlayerDiggingPacket packet) {
            if (packet.status() == ClientPlayerDiggingPacket.Status.STARTED_DIGGING || packet.status() == ClientPlayerDiggingPacket.Status.FINISHED_DIGGING || packet.status() == ClientPlayerDiggingPacket.Status.CANCELLED_DIGGING) return;

            // 1.8 and above clients always send digging packets that aren't used for digging at 0, 0, 0, facing DOWN
            // 1.7 and below clients do the same, except use SOUTH for RELEASE_USE_ITEM
            final BlockFace expectedFace = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10) && packet.status() == ClientPlayerDiggingPacket.Status.DROP_ITEM
                    ? BlockFace.SOUTH : BlockFace.BOTTOM;

            if (packet.blockFace() != expectedFace
                    || packet.blockPosition().x() != 0
                    || packet.blockPosition().y() != 0
                    || packet.blockPosition().z() != 0
                    || packet.sequence() != 0
            ) {
                if (flagAndAlert("xyzF="
                        + packet.blockPosition().x() + ", " + packet.blockPosition().y() + ", " + packet.blockPosition().z() + ", " + packet.blockFace()
                        + ", sequence=" + packet.sequence()
                        + ", action=" + packet.status().toString().toLowerCase(Locale.ROOT).replace("_", " ") + " v" + player.getVersionName()
                ) && shouldModifyPackets() && packet.status() != ClientPlayerDiggingPacket.Status.DROP_ITEM) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}

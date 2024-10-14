package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;

@CheckData(name = "CrashG")
public class CrashG extends Check implements PacketCheck {

    public CrashG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (!isSupportedVersion()) return;

        if (event.getPacket() instanceof ClientPlayerBlockPlacementPacket place) {
            if (place.sequence() < 0) {
                flagAndAlert();
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }

        if (event.getPacket() instanceof ClientPlayerDiggingPacket dig) {
            if (dig.sequence() < 0) {
                flagAndAlert();
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }

        if (event.getPacket() instanceof ClientUseItemPacket use) {
            if (use.sequence() < 0) {
                flagAndAlert();
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }

    }

    private boolean isSupportedVersion() {
        return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19);
//        return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19) && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_19);
    }

}

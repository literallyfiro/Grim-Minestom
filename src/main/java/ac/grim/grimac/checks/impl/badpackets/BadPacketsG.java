package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;

@CheckData(name = "BadPacketsG")
public class BadPacketsG extends Check implements PacketCheck {
    boolean wasTeleport;
    boolean lastSneaking;

    public BadPacketsG(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        wasTeleport = player.packetStateData.lastPacketWasTeleport || wasTeleport;

        if (event.getPacket() instanceof ClientEntityActionPacket packet) {
            if (packet.action() == ClientEntityActionPacket.Action.START_SNEAKING) {
                if (lastSneaking && !wasTeleport) {
                    if (flagAndAlert("state=true") && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                } else {
                    lastSneaking = true;
                }
            } else if (packet.action() == ClientEntityActionPacket.Action.STOP_SNEAKING) {
                if (!lastSneaking && !wasTeleport) {
                    if (flagAndAlert("state=false") && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                } else {
                    lastSneaking = false;
                }
            }
        }
    }
}

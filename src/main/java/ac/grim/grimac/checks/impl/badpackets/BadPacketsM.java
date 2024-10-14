package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;

@CheckData(name = "BadPacketsM", experimental = true)
public class BadPacketsM extends Check implements PacketCheck {
    public BadPacketsM(final GrimPlayer player) {
        super(player);
    }

    // 1.7 players do not send INTERACT_AT, so we cannot check them
    private final boolean exempt = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_7_10);
    private boolean sentInteractAt = false;

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientInteractEntityPacket wrapper && !exempt) {
            final PacketEntity entity = player.compensatedEntities.entityMap.get(wrapper.targetId());

            // For armor stands, vanilla clients send:
            //  - when renaming the armor stand or in spectator mode: INTERACT_AT + INTERACT
            //  - in all other cases: only INTERACT
            // Just exempt armor stands to be safe
            if (entity != null && entity.getType() == EntityType.ARMOR_STAND) return;

            // INTERACT_AT then INTERACT
            if (wrapper.type() instanceof ClientInteractEntityPacket.Interact) {
                if (!sentInteractAt) {
                    if (flagAndAlert("Missed Interact-At") && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                }
                sentInteractAt = false;
            } else if (wrapper.type() instanceof ClientInteractEntityPacket.InteractAt) {
                if (sentInteractAt) {
                    if (flagAndAlert("Missed Interact") && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                }
                sentInteractAt = true;
            }
        }
    }
}

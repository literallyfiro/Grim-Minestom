package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;

import static ac.grim.grimac.events.packets.patch.ResyncWorldUtil.resyncPosition;
import static ac.grim.grimac.utils.nmsutil.BlockBreakSpeed.getBlockDamage;

@CheckData(name = "BadPacketsZ", experimental = true)
public class BadPacketsZ extends Check implements PacketCheck {
    public BadPacketsZ(final GrimPlayer player) {
        super(player);
    }

    private boolean lastBlockWasInstantBreak = false;
    private Vector3i lastBlock, lastCancelledBlock, lastLastBlock = null;
    private final int exemptedY = player.getClientVersion().isOlderThan(ClientVersion.V_1_8) ? 255 : -1;

    // The client sometimes sends a wierd cancel packet
    private boolean shouldExempt(final Vector3i pos) {
        // lastLastBlock is always null when this happens, and lastBlock isn't
        if (lastLastBlock != null || lastBlock == null)
            return false;

        // on pre 1.14.4 clients, the YPos of this packet is always the same
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4) && pos.y != exemptedY)
            return false;

        // and if this block is not an instant break
        return player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4) || getBlockDamage(player, pos) < 1;
    }

    public void handle(PlayerPacketEvent event, ClientPlayerDiggingPacket dig) {
        if (dig.status() == ClientPlayerDiggingPacket.Status.STARTED_DIGGING) {
            final Vector3i pos = new Vector3i(dig.blockPosition().blockX(), dig.blockPosition().blockY(), dig.blockPosition().blockZ());

            lastBlockWasInstantBreak = getBlockDamage(player, pos) >= 1;
            lastCancelledBlock = null;
            lastLastBlock = lastBlock;
            lastBlock = pos;
        }

        if (dig.status() == ClientPlayerDiggingPacket.Status.CANCELLED_DIGGING) {
            final Vector3i pos = new Vector3i(dig.blockPosition().blockX(), dig.blockPosition().blockY(), dig.blockPosition().blockZ());

            if (shouldExempt(pos)) {
                lastCancelledBlock = pos;
                lastLastBlock = null;
                lastBlock = null;
                return;
            }

            if (!pos.equals(lastBlock)) {
                // https://github.com/GrimAnticheat/Grim/issues/1512
                if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4) || (!lastBlockWasInstantBreak && pos.equals(lastCancelledBlock))) {
                    if (flagAndAlert("action=CANCELLED_DIGGING" + ", last=" + MessageUtil.toUnlabledString(lastBlock) + ", pos=" + MessageUtil.toUnlabledString(pos))) {
                        if (shouldModifyPackets()) {
                            event.setCancelled(true);
                            player.onPacketCancel();
                            resyncPosition(player, pos);
                        }
                    }
                }
            }

            lastCancelledBlock = pos;
            lastLastBlock = null;
            lastBlock = null;
            return;
        }

        if (dig.status() == ClientPlayerDiggingPacket.Status.FINISHED_DIGGING) {
            final Vector3i pos = new Vector3i(dig.blockPosition().blockX(), dig.blockPosition().blockY(), dig.blockPosition().blockZ());

            // when a player looks away from the mined block, they send a cancel, and if they look at it again, they don't send another start. (thanks mojang!)
            if (!pos.equals(lastCancelledBlock) && (!lastBlockWasInstantBreak || player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4)) && !pos.equals(lastBlock)) {
                if (flagAndAlert("action=FINISHED_DIGGING" + ", last=" + MessageUtil.toUnlabledString(lastBlock) + ", pos=" + MessageUtil.toUnlabledString(pos))) {
                    if (shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                        resyncPosition(player, pos);
                    }
                }
            }

            lastCancelledBlock = null;

            // 1.14.4+ clients don't send another start break in protected regions
            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14_4)) {
                lastLastBlock = null;
                lastBlock = null;
            }
        }
    }
}

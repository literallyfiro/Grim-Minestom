package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;

@CheckData(name = "BadPacketsU", experimental = true)
public class BadPacketsU extends Check implements PacketCheck {
    public BadPacketsU(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPlayerBlockPlacementPacket packet) {
            // todo minestom is this useless
            // BlockFace.OTHER is USE_ITEM for pre 1.9
//            if (packet.blockFace() == BlockFace.OTHER) {
//                // This packet is always sent at (-1, -1, -1) at (0, 0, 0) on the block
//                // except y gets wrapped?
//                final int expectedY = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8) ? 4095 : 255;
//
//                final boolean failedItemCheck = packet.getItemStack().isPresent() && isEmpty(packet.getItemStack().get())
//                        // ViaVersion can sometimes cause this part of the check to false
//                        && player.getClientVersion().isOlderThan(ClientVersion.V_1_9);
//
//                final Vector3i pos = new Vector3i(packet.blockPosition());
//                final Vector3f cursor = new Vector3f(packet.cursorPositionX(), packet.cursorPositionY(), packet.cursorPositionZ());
//
//                if (failedItemCheck
//                        || pos.x != -1
//                        || pos.y != expectedY
//                        || pos.z != -1
//                        || cursor.x != 0
//                        || cursor.y != 0
//                        || cursor.z != 0
//                        || packet.sequence() != 0
//                ) {
//                    final String verbose = String.format(
//                            "xyz=%s, %s, %s, cursor=%s, %s, %s, item=%s, sequence=%s",
//                            pos.x, pos.y, pos.z, cursor.x, cursor.y, cursor.z, !failedItemCheck, packet.sequence()
//                    );
//                    if (flagAndAlert(verbose) && shouldModifyPackets()) {
//                        player.onPacketCancel();
//                        event.setCancelled(true);
//                    }
//                }
//            }
        }
    }

    private boolean isEmpty(ItemStack itemStack) {
        return itemStack.material() == Material.AIR;
    }
}

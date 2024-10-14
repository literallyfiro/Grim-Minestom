package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;

@CheckData(name = "BadPacketsX", experimental = true)
public class BadPacketsX extends Check implements PacketCheck {
    public BadPacketsX(GrimPlayer player) {
        super(player);
    }

    public final boolean noFireHitbox = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2);

    public final void handle(PlayerPacketEvent event, ClientPlayerDiggingPacket dig, Block block) {
        if (dig.status() != ClientPlayerDiggingPacket.Status.STARTED_DIGGING && dig.status() != ClientPlayerDiggingPacket.Status.FINISHED_DIGGING)
            return;

        // the block does not have a hitbox
        boolean invalid = (block == Block.LIGHT && !(player.getInventory().getHeldItem().getType() == Material.LIGHT || player.getInventory().getOffHand().getType() == Material.LIGHT))
                || block.isAir()
                || block.isLiquid()
                || block == Block.BUBBLE_COLUMN
                || block == Block.MOVING_PISTON
                || (block == Block.FIRE && noFireHitbox)
                // or the client claims to have broken an unbreakable block
                || block.registry().hardness() == -1.0f && dig.status() == ClientPlayerDiggingPacket.Status.FINISHED_DIGGING;

        if (invalid && flagAndAlert("block=" + block.name() + ", type=" + dig.status()) && shouldModifyPackets()) {
            event.setCancelled(true);
            player.onPacketCancel();
        }
    }
}

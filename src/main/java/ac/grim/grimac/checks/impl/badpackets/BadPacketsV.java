package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;

@CheckData(name = "BadPacketsV", experimental = true)
public class BadPacketsV extends Check implements PacketCheck {
    public BadPacketsV(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientInteractEntityPacket interactEntity) {
            if (!(interactEntity.type() instanceof ClientInteractEntityPacket.Attack)) return;
            if (!player.packetStateData.isSlowedByUsingItem()) return;
            ModifiableItemStack itemInUse = player.getInventory().getItemInHand(player.packetStateData.eatingHand);
            if (flagAndAlert("UseItem=" + itemInUse.getType().namespace().key().asString()) && shouldModifyPackets()) {
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }
    }
}

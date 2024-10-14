package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;

@CheckData(name = "BadPacketsQ")
public class BadPacketsQ extends Check implements PacketCheck {
    public BadPacketsQ(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientEntityActionPacket wrapper) {
            if (wrapper.horseJumpBoost() < 0 || wrapper.horseJumpBoost() > 100 || wrapper.playerId() != player.entityID || (wrapper.action() != ClientEntityActionPacket.Action.START_JUMP_HORSE && wrapper.horseJumpBoost() != 0)) {
                if (flagAndAlert("boost=" + wrapper.horseJumpBoost() + ", action=" + wrapper.action() + ", entity=" + wrapper.playerId()) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}

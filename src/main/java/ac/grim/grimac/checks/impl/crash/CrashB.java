package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientCreativeInventoryActionPacket;

@CheckData(name = "CrashB")
public class CrashB extends Check implements PacketCheck {
    public CrashB(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientCreativeInventoryActionPacket) {
            if (player.gamemode != GameMode.CREATIVE) {
                player.getSetbackTeleportUtil().executeViolationSetback();
                event.setCancelled(true);
                player.onPacketCancel();
                flagAndAlert(); // Could be transaction split, no need to setback though
            }
        }
    }
}

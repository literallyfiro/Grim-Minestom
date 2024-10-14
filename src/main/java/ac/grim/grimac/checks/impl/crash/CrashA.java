package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import net.minestom.server.event.player.PlayerPacketEvent;

@CheckData(name = "CrashA")
public class CrashA extends Check implements PacketCheck {
    private static final double HARD_CODED_BORDER = 2.9999999E7D;

    public CrashA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (player.packetStateData.lastPacketWasTeleport) return;
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            WrapperPlayClientPlayerFlying packet = new WrapperPlayClientPlayerFlying(event);

            if (!packet.hasPositionChanged()) return;
            // Y technically is uncapped, but no player will reach these values legit
            if (Math.abs(packet.getLocation().x()) > HARD_CODED_BORDER || Math.abs(packet.getLocation().z()) > HARD_CODED_BORDER || Math.abs(packet.getLocation().y()) > Integer.MAX_VALUE) {
                flagAndAlert(); // Ban
                player.getSetbackTeleportUtil().executeViolationSetback();
                event.setCancelled(true);
                player.onPacketCancel();
            }
        }
    }
}

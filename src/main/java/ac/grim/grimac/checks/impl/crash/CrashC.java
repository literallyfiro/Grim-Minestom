package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerPacketEvent;

@CheckData(name = "CrashC")
public class CrashC extends Check implements PacketCheck {
    public CrashC(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            if (flying.hasPositionChanged()) {
                Pos pos = flying.getLocation();
                if (Double.isNaN(pos.x()) || Double.isNaN(pos.y()) || Double.isNaN(pos.z())
                        || Double.isInfinite(pos.x()) || Double.isInfinite(pos.y()) || Double.isInfinite(pos.z()) ||
                        Float.isNaN(pos.yaw()) || Float.isNaN(pos.pitch()) ||
                        Float.isInfinite(pos.yaw()) || Float.isInfinite(pos.pitch())) {
                    flagAndAlert("xyzYP: " + pos.x() + ", " + pos.y() + ", " + pos.z() + ", " + pos.yaw() + ", " + pos.pitch());
                    player.getSetbackTeleportUtil().executeViolationSetback();
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}

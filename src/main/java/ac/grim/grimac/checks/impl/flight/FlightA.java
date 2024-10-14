package ac.grim.grimac.checks.impl.flight;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import net.minestom.server.event.player.PlayerPacketEvent;

// This check catches 100% of cheaters.
public class FlightA extends Check implements PacketCheck {
    public FlightA(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        // If the player sends a flying packet, but they aren't flying, then they are cheating.
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket()) && !player.isFlying) {
            flag();
        }
    }
}

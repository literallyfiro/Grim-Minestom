package ac.grim.grimac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;

public interface PacketCheck extends AbstractCheck {

    default void onPacketReceive(final PlayerPacketEvent event) {
    }

    default void onPacketSend(final PlayerPacketOutEvent event) {
    }

    default void onPositionUpdate(final PositionUpdate positionUpdate) {
    }
}

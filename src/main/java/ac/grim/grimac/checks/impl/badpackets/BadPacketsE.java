package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;

@CheckData(name = "BadPacketsE")
public class BadPacketsE extends Check implements PacketCheck {
    private int noReminderTicks;

    public BadPacketsE(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPlayerPositionAndRotationPacket ||
                event.getPacket() instanceof ClientPlayerPositionPacket) {
            noReminderTicks = 0;
        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            noReminderTicks++;
        } else if (event.getPacket() instanceof ClientSteerVehiclePacket) {
            noReminderTicks = 0; // Exempt vehicles
        }

        if (noReminderTicks > 20) {
            flagAndAlert("ticks=" + noReminderTicks); // ban?  I don't know how this would false
        }
    }

    public void handleRespawn() {
        noReminderTicks = 0;
    }
}

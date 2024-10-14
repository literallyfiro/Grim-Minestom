package ac.grim.grimac.checks.impl.movement;

import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;
import net.minestom.server.network.packet.client.play.ClientVehicleMovePacket;

@CheckData(name = "Timer - Vehicle", configName = "TimerVehicle", setback = 10)
public class VehicleTimer extends TimerCheck {
    boolean isDummy = false;

    public VehicleTimer(GrimPlayer player) {
        super(player);
    }

    @Override
    public boolean shouldCountPacketForTimer(ClientPacket packetType) {
        // Ignore teleports
        if (player.packetStateData.lastPacketWasTeleport) return false;

        if (packetType instanceof ClientVehicleMovePacket) {
            isDummy = false;
            return true; // Client controlling vehicle
        }

        if (packetType instanceof ClientSteerVehiclePacket) {
            if (isDummy) { // Server is controlling vehicle
                return true;
            }
            isDummy = true; // Client is controlling vehicle
        }

        return false;
    }
}

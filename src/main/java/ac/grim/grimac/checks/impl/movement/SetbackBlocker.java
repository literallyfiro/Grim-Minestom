package ac.grim.grimac.checks.impl.movement;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.vector.Vector3d;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.network.packet.client.play.ClientVehicleMovePacket;

public class SetbackBlocker extends Check implements PacketCheck {
    public SetbackBlocker(GrimPlayer playerData) {
        super(playerData);
    }

    public void onPacketReceive(final PlayerPacketEvent event) {
        if (player.disableGrim) return; // Let's avoid letting people disable grim with grim.nomodifypackets

        if (event.getPacket() instanceof ClientInteractEntityPacket) {
            if (player.getSetbackTeleportUtil().cheatVehicleInterpolationDelay > 0) {
                event.setCancelled(true); // Player is in the vehicle
            }
        }

        // Don't block teleport packets
        if (player.packetStateData.lastPacketWasTeleport) return;

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            // The player must obey setbacks
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
            }

            // Look is the only valid packet to send while in a vehicle
            if (player.compensatedEntities.getSelf().inVehicle() && !(event.getPacket() instanceof ClientPlayerRotationPacket) && !player.packetStateData.lastPacketWasTeleport) {
                event.setCancelled(true);
            }

            // The player is sleeping, should be safe to block position packets
            if (player.isInBed && new Vector3d(player.x, player.y, player.z).distanceSquared(player.bedPosition) > 1) {
                event.setCancelled(true);
            }

            // Player is dead
            if (player.compensatedEntities.getSelf().isDead) {
                event.setCancelled(true);
            }
        }

        if (event.getPacket() instanceof ClientVehicleMovePacket) {
            if (player.getSetbackTeleportUtil().shouldBlockMovement()) {
                event.setCancelled(true);
            }

            // Don't let a player move a vehicle when not in a vehicle
            if (!player.compensatedEntities.getSelf().inVehicle()) {
                event.setCancelled(true);
            }

            // A player is sleeping while in a vehicle
            if (player.isInBed) {
                event.setCancelled(true);
            }

            // Player is dead
            if (player.compensatedEntities.getSelf().isDead) {
                event.setCancelled(true);
            }
        }
    }
}

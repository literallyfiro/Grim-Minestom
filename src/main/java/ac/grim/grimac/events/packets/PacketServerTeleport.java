package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.RelativeFlag;
import ac.grim.grimac.utils.data.Pair;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.vector.Vector3d;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.PlayerPositionAndLookPacket;
import net.minestom.server.network.packet.server.play.VehicleMovePacket;

public class PacketServerTeleport {

//    public PacketServerTeleport() {
//        super(PacketListenerPriority.LOW);
//    }

    public PacketServerTeleport(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-server-teleport");
        node.setPriority(EventPriority.LOW.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof PlayerPositionAndLookPacket teleport) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());

            Vector3d pos = new Vector3d(teleport.position().x(), teleport.position().y(), teleport.position().z());

            if (player == null) return;

            // This is the first packet sent to the client which we need to track
            if (player.getSetbackTeleportUtil().getRequiredSetBack() == null) {
                // Player teleport event gets called AFTER player join event
                player.x = teleport.position().x();
                player.y = teleport.position().y();
                player.z = teleport.position().z();
                player.xRot = teleport.position().yaw();
                player.yRot = teleport.position().pitch();

                player.lastX = teleport.position().x();
                player.lastY = teleport.position().y();
                player.lastZ = teleport.position().z();
                player.lastXRot = teleport.position().yaw();
                player.lastYRot = teleport.position().pitch();
                player.pollData();
            }

            // Convert relative teleports to normal teleports
            // We have to do this because 1.8 players on 1.9+ get teleports changed by ViaVersion
            // Additionally, velocity is kept after relative teleports making predictions difficult
            // The added complexity isn't worth a feature that I have never seen used
            //
            // If you do actually need this make an issue on GitHub with an explanation for why
            // todo minestom here
//            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) {
//                if (RelativeFlag.X.isSet(teleport.flags())) {
//                    pos = pos.add(new Vector3d(player.x, 0, 0));
//                }
//
//                if (RelativeFlag.Y.isSet(teleport.flags())) {
//                    pos = pos.add(new Vector3d(0, player.y, 0));
//                }
//
//                if (RelativeFlag.Z.isSet(teleport.flags())) {
//                    pos = pos.add(new Vector3d(0, 0, player.z));
//                }
//
//                teleport.setX(pos.getX());
//                teleport.setY(pos.getY());
//                teleport.setZ(pos.getZ());
//                teleport.setRelativeMask((byte) (teleport.getRelativeFlags().getMask() & 0b11000));
//            }

            RelativeFlag flag = new RelativeFlag(teleport.flags());

            player.sendTransaction();
            final int lastTransactionSent = player.lastTransactionSent.get();
            event.getTasksAfterSend().add(player::sendTransaction);

            // todo minestom
//            if (teleport.isDismountVehicle()) {
//                // Remove player from vehicle
//                event.getTasksAfterSend().add(() -> {
//                    player.compensatedEntities.getSelf().eject();
//                });
//            }

            Pos target = new Pos(pos.getX(), pos.getY(), pos.getZ());
            player.getSetbackTeleportUtil().addSentTeleport(target, lastTransactionSent, flag, true, teleport.teleportId());
        }

        if (event.getPacket() instanceof VehicleMovePacket vehicleMove) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            player.sendTransaction();
            int lastTransactionSent = player.lastTransactionSent.get();
            Vector3d finalPos = new Vector3d(vehicleMove.position());

            event.getTasksAfterSend().add(player::sendTransaction);
            // todo minestom here
            player.vehicleData.vehicleTeleports.add(new Pair<>(lastTransactionSent, finalPos));
        }
    }
}

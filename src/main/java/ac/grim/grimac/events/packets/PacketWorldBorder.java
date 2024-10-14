package ac.grim.grimac.events.packets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.math.GrimMath;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.InitializeWorldBorderPacket;
import net.minestom.server.network.packet.server.play.WorldBorderCenterPacket;
import net.minestom.server.network.packet.server.play.WorldBorderLerpSizePacket;
import net.minestom.server.network.packet.server.play.WorldBorderSizePacket;

public class PacketWorldBorder extends Check implements PacketCheck {
    double centerX;
    double centerZ;
    double oldDiameter;
    double newDiameter;
    double absoluteMaxSize;
    long startTime = 1;
    long endTime = 1;

    public PacketWorldBorder(GrimPlayer playerData) {
        super(playerData);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public double getCurrentDiameter() {
        double d0 = (double) (System.currentTimeMillis() - this.startTime) / ((double) this.endTime - this.startTime);
        return d0 < 1.0D ? GrimMath.lerp(d0, oldDiameter, newDiameter) : newDiameter;
    }

    @Override
    public void onPacketSend(PlayerPacketOutEvent event) {
//        if (event.getPacket() instanceof WORLD_BORDERPacket) {
//            WrapperPlayServerWorldBorder packet = new WrapperPlayServerWorldBorder(event);
//
//            player.sendTransaction();
//            // Names are misleading, it's diameter not radius.
//            if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.SET_SIZE) {
//                setSize(packet.getRadius());
//            } else if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.LERP_SIZE) {
//                setLerp(packet.getOldRadius(), packet.getNewRadius(), packet.getSpeed());
//            } else if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.SET_CENTER) {
//                setCenter(packet.getCenterX(), packet.getCenterZ());
//            } else if (packet.getAction() == WrapperPlayServerWorldBorder.WorldBorderAction.INITIALIZE) {
//                setCenter(packet.getCenterX(), packet.getCenterZ());
//                setLerp(packet.getOldRadius(), packet.getNewRadius(), packet.getSpeed());
//                setAbsoluteMaxSize(packet.getPortalTeleportBoundary());
//            }
//        }
        if (event.getPacket() instanceof InitializeWorldBorderPacket border) {
            player.sendTransaction();
            setCenter(border.x(), border.z());
            setLerp(border.oldDiameter(), border.newDiameter(), border.speed());
            setAbsoluteMaxSize(border.portalTeleportBoundary());
        }

        if (event.getPacket() instanceof WorldBorderCenterPacket center) {
            player.sendTransaction();
            setCenter(center.x(), center.z());
        }

        if (event.getPacket() instanceof WorldBorderSizePacket size) {
            player.sendTransaction();
            setSize(size.diameter());
        }

        if (event.getPacket() instanceof WorldBorderLerpSizePacket size) {
            player.sendTransaction();
            setLerp(size.oldDiameter(), size.newDiameter(), size.speed());
        }
    }

    private void setCenter(double x, double z) {
        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
            centerX = x;
            centerZ = z;
        });
    }

    private void setSize(double size) {
        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
            oldDiameter = size;
            newDiameter = size;
        });
    }

    private void setLerp(double oldDiameter, double newDiameter, long length) {
        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
            this.oldDiameter = oldDiameter;
            this.newDiameter = newDiameter;
            this.startTime = System.currentTimeMillis();
            this.endTime = this.startTime + length;
        });
    }

    private void setAbsoluteMaxSize(double absoluteMaxSize) {
        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
            this.absoluteMaxSize = absoluteMaxSize;
        });
    }

    public double getAbsoluteMaxSize() {
        return absoluteMaxSize;
    }
}

package ac.grim.grimac.utils;

import ac.grim.grimac.utils.vector.Vector3d;
import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;

@Getter
public class WrapperPlayClientPlayerFlying {

    private boolean positionChanged;
    private boolean rotationChanged;
    private Pos location;
    private boolean onGround;

    public WrapperPlayClientPlayerFlying(PlayerPacketEvent event) {
        positionChanged = event.getPacket() instanceof ClientPlayerPositionPacket ||
                event.getPacket() instanceof ClientPlayerPositionAndRotationPacket;
        rotationChanged = event.getPacket() instanceof ClientPlayerRotationPacket ||
                event.getPacket() instanceof ClientPlayerPositionAndRotationPacket;
        readEvent(event);
    }

    private void readEvent(PlayerPacketEvent event) {
        Vector3d position = new Vector3d();
        float yaw = 0.0f;
        float pitch = 0.0f;
        boolean onGroundLocal = false;

        if (event.getPacket() instanceof ClientPlayerPositionPacket packet) {
            if (positionChanged) {
                double x = packet.position().x();
                double y = packet.position().y();
                double z = packet.position().z();
                position = new Vector3d(x, y, z);
            }
            onGroundLocal = packet.onGround();
        } else if (event.getPacket() instanceof ClientPlayerRotationPacket packet) {
            if (rotationChanged) {
                yaw = packet.yaw();
                pitch = packet.pitch();
            }
            onGroundLocal = packet.onGround();
        } else if (event.getPacket() instanceof ClientPlayerPositionAndRotationPacket packet) {
            if (positionChanged) {
                double x = packet.position().x();
                double y = packet.position().y();
                double z = packet.position().z();
                position = new Vector3d(x, y, z);
            }
            if (rotationChanged) {
                yaw = packet.position().yaw();
                pitch = packet.position().pitch();
            }
            onGroundLocal = packet.onGround();
        }

        location = new Pos(position.x, position.y, position.z, yaw, pitch);
        onGround = onGroundLocal;
    }

    public boolean hasPositionChanged() {
        return positionChanged;
    }

    public boolean hasRotationChanged() {
        return rotationChanged;
    }

    public static boolean isFlying(ClientPacket type) {
        return type instanceof ClientPlayerPositionPacket
                || type instanceof ClientPlayerRotationPacket
                || type instanceof ClientPlayerPositionAndRotationPacket;
    }

}

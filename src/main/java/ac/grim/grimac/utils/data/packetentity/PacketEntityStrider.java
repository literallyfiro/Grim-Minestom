package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.Entity;

import java.util.UUID;

public class PacketEntityStrider extends PacketEntityRideable {
    public boolean isShaking = false;

    public PacketEntityStrider(GrimPlayer player, UUID uuid, Entity type, double x, double y, double z) {
        super(player, uuid, type, x, y, z);
    }
}

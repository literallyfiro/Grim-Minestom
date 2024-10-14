package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.Entity;

import java.util.UUID;

public class PacketEntityHook extends PacketEntity{
    public int owner;
    public int attached = -1;

    public PacketEntityHook(GrimPlayer player, UUID uuid, Entity type, double x, double y, double z, int owner) {
        super(player, uuid, type, x, y, z);
        this.owner = owner;
    }
}

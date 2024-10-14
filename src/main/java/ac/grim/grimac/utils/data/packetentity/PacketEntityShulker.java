package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.block.BlockFace;

import java.util.UUID;

public class PacketEntityShulker extends PacketEntity {
    public BlockFace facing = BlockFace.BOTTOM;

    public PacketEntityShulker(GrimPlayer player, UUID uuid, Entity type, double x, double y, double z) {
        super(player, uuid, type, x, y, z);
    }
}

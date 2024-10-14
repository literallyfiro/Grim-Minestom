package ac.grim.grimac.utils.data.packetentity.dragon;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import net.minestom.server.entity.Entity;

public final class PacketEntityEnderDragonPart extends PacketEntity {

    private final DragonPart part;
    private final float width, height;

    public PacketEntityEnderDragonPart(GrimPlayer player, Entity entity, DragonPart part, double x, double y, double z, float width, float height) {
        super(player, null, entity, x, y, z);
        this.part = part;
        this.width = width;
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public DragonPart getPart() {
        return part;
    }
}

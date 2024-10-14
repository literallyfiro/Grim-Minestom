package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.attribute.Attribute;

import java.util.UUID;

public class PacketEntityRideable extends PacketEntity {

    public boolean hasSaddle = false;
    public int boostTimeMax = 0;
    public int currentBoostTime = 0;

    public PacketEntityRideable(GrimPlayer player, UUID uuid, Entity type, double x, double y, double z) {
        super(player, uuid, type, x, y, z);
        setAttribute(Attribute.GENERIC_STEP_HEIGHT, 1.0f);
        trackAttribute(ValuedAttribute.ranged(Attribute.GENERIC_MOVEMENT_SPEED, 0.1f, 0, 1024));
    }
}

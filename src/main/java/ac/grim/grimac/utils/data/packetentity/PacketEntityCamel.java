package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.attribute.Attribute;

import java.util.UUID;

public class PacketEntityCamel extends PacketEntityHorse {

    public boolean dashing = false; //TODO: handle camel dashing

    public PacketEntityCamel(GrimPlayer player, UUID uuid, Entity type, double x, double y, double z, float xRot) {
        super(player, uuid, type, x, y, z, xRot);

        setAttribute(Attribute.GENERIC_JUMP_STRENGTH, 0.42f);
        setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.09f);
        setAttribute(Attribute.GENERIC_STEP_HEIGHT, 1.5f);
    }
}

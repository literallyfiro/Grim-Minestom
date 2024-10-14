package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.animal.ChestedHorseMeta;

import java.util.UUID;

public class PacketEntityHorse extends PacketEntityTrackXRot {

    public boolean isRearing = false;
    public boolean hasSaddle = false;
    public boolean isTame = false;

    public PacketEntityHorse(GrimPlayer player, UUID uuid, Entity type, double x, double y, double z, float xRot) {
        super(player, uuid, type, x, y, z, xRot);
        setAttribute(Attribute.GENERIC_STEP_HEIGHT, 1.0f);

        final boolean preAttribute = player.getClientVersion().isOlderThan(ClientVersion.V_1_20_5);
        // This was horse.jump_strength pre-attribute
        trackAttribute(ValuedAttribute.ranged(Attribute.GENERIC_JUMP_STRENGTH, 0.7, 0, preAttribute ? 2 : 32)
                .withSetRewriter((oldValue, newValue) -> {
                    // Seems viabackwards doesn't rewrite this (?)
                    if (preAttribute) {
                        return oldValue;
                    }
                    // Modern player OR an old server setting legacy horse.jump_strength attribute
                    return newValue;
                }));
        trackAttribute(ValuedAttribute.ranged(Attribute.GENERIC_MOVEMENT_SPEED, 0.225f, 0, 1024));

        if (type.getEntityMeta() instanceof ChestedHorseMeta) {
            setAttribute(Attribute.GENERIC_JUMP_STRENGTH, 0.5);
            setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.175f);
        }

        if (type.getEntityType() == EntityType.ZOMBIE_HORSE || type.getEntityType() == EntityType.SKELETON_HORSE) {
            setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, 0.2f);
        }
    }

}

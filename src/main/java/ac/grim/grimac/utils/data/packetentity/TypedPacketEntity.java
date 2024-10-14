package ac.grim.grimac.utils.data.packetentity;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.AgeableMobMeta;
import net.minestom.server.entity.metadata.animal.AbstractHorseMeta;
import net.minestom.server.entity.metadata.animal.AnimalMeta;
import net.minestom.server.entity.metadata.animal.HorseMeta;
import net.minestom.server.entity.metadata.minecart.AbstractMinecartMeta;
import net.minestom.server.entity.metadata.minecart.MinecartMeta;
import net.minestom.server.entity.metadata.other.BoatMeta;

public abstract class TypedPacketEntity {

    private final EntityType type;
    private final Entity entity;
    private final boolean isLiving, isSize, isMinecart, isHorse, isAgeable, isAnimal, isBoat;

    public TypedPacketEntity(Entity type) {
        this.entity = type;
        this.type = type.getEntityType();
        this.isLiving = type instanceof LivingEntity;
        this.isSize = type.getEntityType() == EntityType.PHANTOM || type.getEntityType() == EntityType.SLIME || type.getEntityType() == EntityType.MAGMA_CUBE;
        this.isMinecart = type.getEntityMeta() instanceof AbstractMinecartMeta;
        this.isHorse = type.getEntityMeta() instanceof AbstractHorseMeta;
        this.isAgeable = type.getEntityMeta() instanceof AgeableMobMeta;
        this.isAnimal = type.getEntityMeta() instanceof AnimalMeta;
        this.isBoat = type.getEntityMeta() instanceof BoatMeta;
    }

    public boolean isLivingEntity() {
        return isLiving;
    }

    public boolean isSize() {
        return isSize;
    }

    public boolean isMinecart() {
        return isMinecart;
    }

    public boolean isHorse() {
        return isHorse;
    }

    public boolean isAgeable() {
        return isAgeable;
    }

    public boolean isAnimal() {
        return isAnimal;
    }

    public boolean isBoat() {
        return isBoat;
    }

    public boolean isPushable() {
        // Players can only push living entities
        // Minecarts and boats are the only non-living that can push
        // Bats, parrots, and armor stands cannot
        if (type == EntityType.ARMOR_STAND || type == EntityType.BAT || type == EntityType.PARROT) return false;
        return isLiving || isBoat || isMinecart;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntityType getType() {
        return type;
    }
}

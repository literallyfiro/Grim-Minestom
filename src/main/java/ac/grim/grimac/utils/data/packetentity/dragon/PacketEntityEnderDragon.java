package ac.grim.grimac.utils.data.packetentity.dragon;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minestom.server.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PacketEntityEnderDragon extends PacketEntity {

    private final List<PacketEntityEnderDragonPart> parts = new ArrayList<>();

    public PacketEntityEnderDragon(GrimPlayer player, UUID uuid, Entity entity, double x, double y, double z) {
        super(player, uuid, entity, x, y, z);
        final Int2ObjectOpenHashMap<PacketEntity> entityMap = player.compensatedEntities.entityMap;
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.HEAD, x, y, z, 1.0F, 1.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.NECK, x, y, z, 3.0F, 3.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.BODY, x, y, z, 5.0F, 3.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.TAIL, x, y, z, 2.0F, 2.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.TAIL, x, y, z, 2.0F, 2.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.TAIL, x, y, z, 2.0F, 2.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.WING, x, y, z, 4.0F, 2.0F));
        parts.add(new PacketEntityEnderDragonPart(player, entity, DragonPart.WING, x, y, z, 4.0F, 2.0F));
        for (int i = 1; i < parts.size() + 1; i++) {
            entityMap.put(entity.getEntityId() + i, parts.get(i - 1));
        }
    }

    public List<PacketEntityEnderDragonPart> getParts() {
        return parts;
    }
}

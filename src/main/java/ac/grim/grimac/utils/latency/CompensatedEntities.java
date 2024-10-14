package ac.grim.grimac.utils.latency;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.data.ShulkerData;
import ac.grim.grimac.utils.data.TrackerData;
import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.data.packetentity.PacketEntityCamel;
import ac.grim.grimac.utils.data.packetentity.PacketEntityHook;
import ac.grim.grimac.utils.data.packetentity.PacketEntityHorse;
import ac.grim.grimac.utils.data.packetentity.PacketEntityRideable;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySelf;
import ac.grim.grimac.utils.data.packetentity.PacketEntityShulker;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySizeable;
import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
import ac.grim.grimac.utils.data.packetentity.PacketEntityTrackXRot;
import ac.grim.grimac.utils.data.packetentity.dragon.PacketEntityEnderDragon;
import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
import ac.grim.grimac.utils.nmsutil.WatchableIndexUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.metadata.animal.AbstractHorseMeta;
import net.minestom.server.entity.metadata.other.BoatMeta;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.utils.NamespaceID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public class CompensatedEntities {

    public static final UUID SPRINTING_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    public final Int2ObjectOpenHashMap<PacketEntity> entityMap = new Int2ObjectOpenHashMap<>(40, 0.7f);
    public final Int2ObjectOpenHashMap<TrackerData> serverPositionsMap = new Int2ObjectOpenHashMap<>(40, 0.7f);
    public final Object2ObjectOpenHashMap<UUID, Player> profiles = new Object2ObjectOpenHashMap<>();
    public Integer serverPlayerVehicle = null;
    public boolean hasSprintingAttributeEnabled = false;

    GrimPlayer player;

    public TrackerData selfTrackedEntity;
    public PacketEntitySelf playerEntity;

    public CompensatedEntities(GrimPlayer player) {
        this.player = player;
        this.playerEntity = new PacketEntitySelf(player);
        this.selfTrackedEntity = new TrackerData(0, 0, 0, 0, 0, EntityType.PLAYER, player.lastTransactionSent.get());
    }

    public int getPacketEntityID(PacketEntity entity) {
        for (Map.Entry<Integer, PacketEntity> entry : entityMap.int2ObjectEntrySet()) {
            if (entry.getValue() == entity) {
                return entry.getKey();
            }
        }
        return Integer.MIN_VALUE;
    }

    public void tick() {
        this.playerEntity.setPositionRaw(player.boundingBox);
        for (PacketEntity vehicle : entityMap.values()) {
            for (PacketEntity passenger : vehicle.passengers) {
                tickPassenger(vehicle, passenger);
            }
        }
    }

    public void removeEntity(int entityID) {
        PacketEntity entity = entityMap.remove(entityID);
        if (entity == null) return;

        if (entity instanceof PacketEntityEnderDragon dragon) {
            for (int i = 1; i < dragon.getParts().size() + 1; i++) {
                entityMap.remove(entityID + i);
            }
        }

        for (PacketEntity passenger : new ArrayList<>(entity.passengers)) {
            passenger.eject();
        }
    }

    public OptionalInt getSlowFallingAmplifier() {
        return player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_12_2) ? OptionalInt.empty() : getPotionLevelForPlayer(PotionEffect.SLOW_FALLING);
    }

    public OptionalInt getPotionLevelForPlayer(PotionEffect type) {
        return getEntityInControl().getPotionEffectLevel(type);
    }

    public boolean hasPotionEffect(PotionEffect type) {
        return getEntityInControl().hasPotionEffect(type);
    }

    public PacketEntity getEntityInControl() {
        return playerEntity.getRiding() != null ? playerEntity.getRiding() : playerEntity;
    }

    public void updateAttributes(int entityID, List<EntityAttributesPacket.Property> objects) {
        if (entityID == player.entityID) {
            // Check for sprinting attribute. Note that this value can desync: https://bugs.mojang.com/browse/MC-69459
            for (EntityAttributesPacket.Property snapshotWrapper : objects) {
                final Attribute attribute = snapshotWrapper.attribute();
                if (attribute != Attribute.GENERIC_MOVEMENT_SPEED) continue;

                boolean found = false;
                for (AttributeModifier modifier : snapshotWrapper.modifiers()) {
                    final NamespaceID name = modifier.id();
                    NamespaceID sprinting = NamespaceID.from("minecraft:sprinting");
                    if (name.key().asString().equals(SPRINTING_MODIFIER_UUID.toString()) || name.key().equals(sprinting)) {
                        found = true;
                        break;
                    }
                }

                // The server can set the player's sprinting attribute
                hasSprintingAttributeEnabled = found;
                break;
            }
        }

        PacketEntity entity = player.compensatedEntities.getEntity(entityID);
        if (entity == null) return;

        for (EntityAttributesPacket.Property snapshotWrapper : objects) {
            Attribute attribute = snapshotWrapper.attribute();
            if (attribute == null) continue; // TODO: Warn if this happens? Either modded server or bug in packetevents.

            final Optional<ValuedAttribute> valuedAttribute = entity.getAttribute(attribute);
            if (!valuedAttribute.isPresent()) {
                // Not an attribute we want to track
                continue;
            }

            valuedAttribute.get().with(snapshotWrapper);
        }
    }

    private void tickPassenger(PacketEntity riding, PacketEntity passenger) {
        if (riding == null || passenger == null) {
            return;
        }

        passenger.setPositionRaw(riding.getPossibleCollisionBoxes().offset(0, BoundingBoxSize.getMyRidingOffset(riding) + BoundingBoxSize.getPassengerRidingOffset(player, passenger), 0));

        for (PacketEntity passengerPassenger : riding.passengers) {
            tickPassenger(passenger, passengerPassenger);
        }
    }

    public void addEntity(int entityID, UUID uuid, Entity entity, Point position, float xRot, int data) {
        // Dropped items are all server sided and players can't interact with them (except create them!), save the performance
        EntityType entityType = entity.getEntityType();
        if (entityType == EntityType.ITEM) return;

        PacketEntity packetEntity;

        if (EntityType.CAMEL.equals(entityType)) {
            packetEntity = new PacketEntityCamel(player, uuid, entity, position.x(), position.y(), position.z(), xRot);
        } else if (entity.getEntityMeta() instanceof AbstractHorseMeta) {
            packetEntity = new PacketEntityHorse(player, uuid, entity, position.x(), position.y(), position.z(), xRot);
        } else if (entityType == EntityType.SLIME || entityType == EntityType.MAGMA_CUBE || entityType == EntityType.PHANTOM) {
            packetEntity = new PacketEntitySizeable(player, uuid, entity, position.x(), position.y(), position.z());
        } else {
            if (EntityType.PIG.equals(entityType)) {
                packetEntity = new PacketEntityRideable(player, uuid, entity, position.x(), position.y(), position.z());
            } else if (EntityType.SHULKER.equals(entityType)) {
                packetEntity = new PacketEntityShulker(player, uuid, entity, position.x(), position.y(), position.z());
            } else if (EntityType.STRIDER.equals(entityType)) {
                packetEntity = new PacketEntityStrider(player, uuid, entity, position.x(), position.y(), position.z());
            } else if (entity.getEntityMeta() instanceof BoatMeta || EntityType.CHICKEN.equals(entityType)) {
                packetEntity = new PacketEntityTrackXRot(player, uuid, entity, position.x(), position.y(), position.z(), xRot);
            } else if (EntityType.FISHING_BOBBER.equals(entityType)) {
                packetEntity = new PacketEntityHook(player, uuid, entity, position.x(), position.y(), position.z(), data);
            } else if (EntityType.ENDER_DRAGON.equals(entityType)) {
                packetEntity = new PacketEntityEnderDragon(player, uuid, entity, position.x(), position.y(), position.z());
            } else {
                packetEntity = new PacketEntity(player, uuid, entity, position.x(), position.y(), position.z());
            }
        }

        entityMap.put(entityID, packetEntity);
    }

    public PacketEntity getEntity(int entityID) {
        if (entityID == player.entityID) {
            return playerEntity;
        }
        return entityMap.get(entityID);
    }

    public PacketEntitySelf getSelf() {
        return playerEntity;
    }

    public TrackerData getTrackedEntity(int id) {
        if (id == player.entityID) {
            return selfTrackedEntity;
        }
        return serverPositionsMap.get(id);
    }

    public void updateEntityMetadata(int entityID, Map<Integer, Metadata.Entry<?>> watchableObjects) {
        PacketEntity entity = player.compensatedEntities.getEntity(entityID);
        if (entity == null) return;

        if (entity.isAgeable()) {
            int id = 16;

            // 1.14 good
            Metadata.Entry<?> ageableObject = WatchableIndexUtil.getIndex(watchableObjects, id);
            if (ageableObject != null) {
                Object value = ageableObject.value();
                // Required because bukkit Ageable doesn't align with minecraft's ageable
                if (value instanceof Boolean) {
                    entity.isBaby = (boolean) value;
                } else if (value instanceof Byte) {
                    entity.isBaby = ((Byte) value) < 0;
                }
            }
        }

        if (entity.isSize()) {
            int id = 16;

            Metadata.Entry<?> sizeObject = WatchableIndexUtil.getIndex(watchableObjects, id);
            if (sizeObject != null) {
                Object value = sizeObject.value();
                if (value instanceof Integer) {
                    ((PacketEntitySizeable) entity).size = Math.max((int) value, 1);
                } else if (value instanceof Byte) {
                    ((PacketEntitySizeable) entity).size = Math.max((byte) value, 1);
                }
            }
        }

        if (entity instanceof PacketEntityShulker) {
            int id = 16;

            Metadata.Entry<?> shulkerAttached = WatchableIndexUtil.getIndex(watchableObjects, id);

            if (shulkerAttached != null) {
                // This NMS -> Bukkit conversion is great and works in all 11 versions.
                ((PacketEntityShulker) entity).facing = BlockFace.valueOf(shulkerAttached.value().toString().toUpperCase());
            }

            Metadata.Entry<?> height = WatchableIndexUtil.getIndex(watchableObjects, id + 2);
            if (height != null) {
                if ((byte) height.value() == 0) {
                    ShulkerData data = new ShulkerData(entity, player.lastTransactionSent.get(), true);
                    player.compensatedWorld.openShulkerBoxes.remove(data);
                    player.compensatedWorld.openShulkerBoxes.add(data);
                } else {
                    ShulkerData data = new ShulkerData(entity, player.lastTransactionSent.get(), false);
                    player.compensatedWorld.openShulkerBoxes.remove(data);
                    player.compensatedWorld.openShulkerBoxes.add(data);
                }
            }
        }

        if (entity instanceof PacketEntityRideable) {
            int offset = 0;

            if (entity.getType() == EntityType.PIG) {
                Metadata.Entry<?> pigSaddle = WatchableIndexUtil.getIndex(watchableObjects, 17 - offset);
                if (pigSaddle != null) {
                    ((PacketEntityRideable) entity).hasSaddle = (boolean) pigSaddle.value();
                }

                Metadata.Entry<?> pigBoost = WatchableIndexUtil.getIndex(watchableObjects, 18 - offset);
                if (pigBoost != null) { // What does 1.9-1.10 do here? Is this feature even here?
                    ((PacketEntityRideable) entity).boostTimeMax = (int) pigBoost.value();
                    ((PacketEntityRideable) entity).currentBoostTime = 0;
                }
            } else if (entity instanceof PacketEntityStrider) {
                Metadata.Entry<?> striderBoost = WatchableIndexUtil.getIndex(watchableObjects, 17 - offset);
                if (striderBoost != null) {
                    ((PacketEntityRideable) entity).boostTimeMax = (int) striderBoost.value();
                    ((PacketEntityRideable) entity).currentBoostTime = 0;
                }

                Metadata.Entry<?> striderSaddle = WatchableIndexUtil.getIndex(watchableObjects, 19 - offset);
                if (striderSaddle != null) {
                    ((PacketEntityRideable) entity).hasSaddle = (boolean) striderSaddle.value();
                }
            }
        }

        if (entity instanceof PacketEntityHorse) {
            int offset = 0;

//            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9_4)) {
//                offset = 5;
//            } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_13_2)) {
//                offset = 4;
//            } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
//                offset = 2;
//            } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
//                offset = 1;
//            }

            Metadata.Entry<?> horseByte = WatchableIndexUtil.getIndex(watchableObjects, 17 - offset);
            if (horseByte != null) {
                byte info = (byte) horseByte.value();

                ((PacketEntityHorse) entity).isTame = (info & 0x02) != 0;
                ((PacketEntityHorse) entity).hasSaddle = (info & 0x04) != 0;
                ((PacketEntityHorse) entity).isRearing = (info & 0x20) != 0;
            }

            // track camel dashing
            if (entity instanceof PacketEntityCamel) {
                PacketEntityCamel camel = (PacketEntityCamel) entity;
                Metadata.Entry<?> entityData = WatchableIndexUtil.getIndex(watchableObjects, 18);
                if (entityData != null) {
                    camel.dashing = (boolean) entityData.value();
                }
            }
        }

        Metadata.Entry<?> gravity = WatchableIndexUtil.getIndex(watchableObjects, 5);

        if (gravity != null) {
            Object gravityObject = gravity.value();

            if (gravityObject instanceof Boolean) {
                // Vanilla uses hasNoGravity, which is a bad name IMO
                // hasGravity > hasNoGravity
                entity.hasGravity = !((Boolean) gravityObject);
            }
        }

        if (entity.getType() == EntityType.FIREWORK_ROCKET) {
            int offset = 0;
//            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_12_2)) {
//                offset = 2;
//            } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
//                offset = 1;
//            }

            Metadata.Entry<?> fireworkWatchableObject = WatchableIndexUtil.getIndex(watchableObjects, 9 - offset);
            if (fireworkWatchableObject == null) return;

            if (fireworkWatchableObject.value() instanceof Integer) { // Pre 1.14
                int attachedEntityID = (Integer) fireworkWatchableObject.value();
                if (attachedEntityID == player.entityID) {
                    player.compensatedFireworks.addNewFirework(entityID);
                }
            } else { // 1.14+
                Optional<Integer> attachedEntityID = (Optional<Integer>) fireworkWatchableObject.value();

                if (attachedEntityID.isPresent() && attachedEntityID.get().equals(player.entityID)) {
                    player.compensatedFireworks.addNewFirework(entityID);
                }
            }
        }

        if (entity instanceof PacketEntityHook) {
            int index = 8;

            Metadata.Entry<?> hookWatchableObject = WatchableIndexUtil.getIndex(watchableObjects, index);
            if (hookWatchableObject == null) return;

            Integer attachedEntityID = (Integer) hookWatchableObject.value();
            ((PacketEntityHook) entity).attached = attachedEntityID - 1; // the server adds 1 to the ID
        }
    }
}

package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.data.TrackerData;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.data.packetentity.PacketEntityHook;
import ac.grim.grimac.utils.data.packetentity.PacketEntityTrackXRot;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.common.PingPacket;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.network.packet.server.play.EntityEffectPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionAndRotationPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.EntityRotationPacket;
import net.minestom.server.network.packet.server.play.EntityStatusPacket;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import net.minestom.server.network.packet.server.play.OpenHorseWindowPacket;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.RemoveEntityEffectPacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;
import net.minestom.server.potion.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PacketEntityReplication extends Check implements PacketCheck {

    private boolean hasSentPreWavePacket = true;

    // Let's imagine the player is on a boat.
    // The player breaks this boat
    // If we were to despawn the boat without an extra transaction, then the boat would disappear before
    // it disappeared on the client side, creating a ghost boat to flag checks with
    //
    // If we were to despawn the tick after, spawning must occur the transaction before to stop the same exact
    // problem with ghost boats in reverse.
    //
    // Therefore, we despawn the transaction after, and spawn the tick before.
    //
    // If we despawn then spawn an entity in the same transaction, then this solution would despawn the new entity
    // instead of the old entity, so we wouldn't see the boat at all
    //
    // Therefore, if the server sends a despawn and then a spawn in the same transaction for the same entity,
    // We should simply add a transaction (which will clear this list!)
    //
    // Another valid solution is to simply spam more transactions, but let's not waste bandwidth.
    private final List<Integer> despawnedEntitiesThisTransaction = new ArrayList<>();

    // Maximum ping when a firework boost is removed from the player.
    private final int maxFireworkBoostPing = GrimAPI.INSTANCE.getConfigManager().getConfig().getIntElse("max-ping-firework-boost", 1000);

    public PacketEntityReplication(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            // Teleports don't interpolate, duplicate 1.17 packets don't interpolate
            if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate)
                return;

            boolean isTickingReliably = player.isTickingReliablyFor(3);

            PacketEntity playerVehicle = player.compensatedEntities.getSelf().getRiding();
            for (PacketEntity entity : player.compensatedEntities.entityMap.values()) {
                if (entity == playerVehicle && !player.vehicleData.lastDummy) {
                    // The player has this as their vehicle, so they aren't interpolating it.
                    // And it isn't a dummy position
                    entity.setPositionRaw(entity.getPossibleCollisionBoxes());
                } else {
                    entity.onMovement(isTickingReliably);
                }
            }
        }
    }

    @Override
    public void onPacketSend(PlayerPacketOutEvent event) {
        // ensure grim is the one that sent the transaction
        if ((event.getPacket() instanceof PingPacket) && player.packetStateData.lastServerTransWasValid) {
            despawnedEntitiesThisTransaction.clear();
        }
//        if (event.getPacket() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
//            WrapperPlayServerSpawnLivingEntity packetOutEntity = new WrapperPlayServerSpawnLivingEntity(event);
//            addEntity(packetOutEntity.getEntityId(), packetOutEntity.getEntityUUID(), packetOutEntity.getEntityType(), packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), packetOutEntity.getEntityMetadata(), 0);
//        }
        if (event.getPacket() instanceof SpawnEntityPacket packetOutEntity) {
            // todo minestom
            Entity entity = new Entity(Objects.requireNonNull(EntityType.fromId(packetOutEntity.type())), packetOutEntity.uuid());
            addEntity(packetOutEntity.entityId(),
                    packetOutEntity.uuid(),
                    entity,
                    packetOutEntity.position(),
                    null,
                    packetOutEntity.data());
        }
//        if (event.getPacket() instanceof JoinGamePacket joinGamePacket) {
//            System.out.println("JoinGamePacket");
//            System.out.println("Entity ID: " + joinGamePacket.entityId() + " (" + EntityType.fromId(joinGamePacket.entityId()) + ")");
//            addEntity(joinGamePacket.entityId(),
//                    joinGamePacket.uuid(),
//                    EntityType.PLAYER, joinGamePacket.uuid(),
//                    joinGamePacket.position(),
//                    null, 0);
//        }
//        if (event.getPacket() == PacketType.Play.Server.SPAWN_PLAYER) {
//            WrapperPlayServerSpawnPlayer packetOutEntity = new WrapperPlayServerSpawnPlayer(event);
//            addEntity(packetOutEntity.getEntityId(), packetOutEntity.getUUID(), EntityTypes.PLAYER, packetOutEntity.getPosition(), packetOutEntity.getYaw(), packetOutEntity.getPitch(), packetOutEntity.getEntityMetadata(), 0);
//        }

        if (event.getPacket() instanceof EntityPositionPacket move) {
            System.out.println("EntityPositionPacket");
            handleMoveEntity(event, move.entityId(), move.deltaX(), move.deltaY(), move.deltaZ(), null, null, true, true);
        }
        if (event.getPacket() instanceof EntityPositionAndRotationPacket move) {
            handleMoveEntity(event, move.entityId(), move.deltaX(), move.deltaY(), move.deltaZ(), move.yaw() * 0.7111111F, move.pitch() * 0.7111111F, true, true);
        }
        if (event.getPacket() instanceof EntityTeleportPacket move) {
            Pos pos = move.position();
            handleMoveEntity(event, move.entityId(), pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch(), false, true);
        }
        if (event.getPacket() instanceof EntityRotationPacket move) { // Affects interpolation
            handleMoveEntity(event, move.entityId(), 0, 0, 0, move.yaw() * 0.7111111F, move.pitch() * 0.7111111F, true, false);
        }

        if (event.getPacket() instanceof EntityMetaDataPacket entityMetadata) {
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedEntities.updateEntityMetadata(entityMetadata.entityId(), entityMetadata.entries()));
        }

        // 1.19.3+
        if (event.getPacket() instanceof PlayerInfoUpdatePacket info) {
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                for (PlayerInfoUpdatePacket.Entry entry : info.entries()) {
                    Player minestomPlayer = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(entry.uuid());
                    player.compensatedEntities.profiles.put(entry.uuid(), minestomPlayer);
                }
            });
        } else if (event.getPacket() instanceof PlayerInfoRemovePacket remove) {
            System.out.println("PlayerInfoRemovePacket");
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(),
                    () -> remove.uuids().forEach(player.compensatedEntities.profiles::remove));
        }
//        else if (event.getPacket() == PacketType.Play.Server.PLAYER_INFO) {
//            WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event);
//            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
//                if (info.getAction() == WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) {
//                    for (WrapperPlayServerPlayerInfo.PlayerData entry : info.getPlayerDataList()) {
//                        final UserProfile gameProfile = entry.getUserProfile();
//                        final UUID uuid = gameProfile.getUUID();
//                        player.compensatedEntities.profiles.put(uuid, gameProfile);
//                    }
//                } else if (info.getAction() == WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER) {
//                    info.getPlayerDataList().forEach(profile -> player.compensatedEntities.profiles.remove(profile.getUserProfile().getUUID()));
//                }
//            });
//        }

        if (event.getPacket() instanceof EntityEffectPacket effect) {
            Potion type = effect.potion();

            // ViaVersion tries faking levitation effects and fails badly lol, flagging the anticheat
            // Block other effects just in case ViaVersion gets any ideas
            //
            // Set to 24 so ViaVersion blocks it
            // 24 is the levitation effect
//            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9) && ViaVersionUtil.isAvailable()
//                    && type.getId(player.getClientVersion()) > 23) {
//                event.setCancelled(true);
//                return;
//            }

            // ViaVersion dolphin's grace also messes us up, set it to a potion effect that doesn't exist on 1.12
            // Effect 31 is bad omen
//            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_13) && ViaVersionUtil.isAvailable()
//                    && type.getId(player.getClientVersion()) == 30) {
//                event.setCancelled(true);
//                return;
//            }

            if (isDirectlyAffectingPlayer(player, effect.entityId())) player.sendTransaction();

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                PacketEntity entity = player.compensatedEntities.getEntity(effect.entityId());
                if (entity == null) return;

                entity.addPotionEffect(type.effect(), type.amplifier());
            });
        }

        if (event.getPacket() instanceof RemoveEntityEffectPacket effect) {
            if (isDirectlyAffectingPlayer(player, effect.entityId())) player.sendTransaction();

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                PacketEntity entity = player.compensatedEntities.getEntity(effect.entityId());
                if (entity == null) return;

                entity.removePotionEffect(effect.potionEffect());
            });
        }

        if (event.getPacket() instanceof EntityAttributesPacket attributes) {
            int entityID = attributes.entityId();

            // The attributes for this entity is active, currently
            if (isDirectlyAffectingPlayer(player, entityID)) player.sendTransaction();

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(),
                    () -> player.compensatedEntities.updateAttributes(entityID, attributes.properties()));
        }

        if (event.getPacket() instanceof EntityStatusPacket status) {
            // This hasn't changed from 1.7.2 to 1.17
            // Needed to exempt players on dead vehicles, as dead entities have strange physics.
            if (status.status() == 3) {
                PacketEntity entity = player.compensatedEntities.getEntity(status.entityId());

                if (entity == null) return;
                entity.isDead = true;
            }

            if (status.status() == 9) {
                if (status.entityId() != player.entityID) return;

                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.packetStateData.setSlowedByUsingItem(false));
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.packetStateData.setSlowedByUsingItem(false));
            }

            if (status.status() == 31) {
                PacketEntity hook = player.compensatedEntities.getEntity(status.entityId());
                if (!(hook instanceof PacketEntityHook)) return;

                PacketEntityHook hookEntity = (PacketEntityHook) hook;
                if (hookEntity.attached == player.entityID) {
                    player.sendTransaction();
                    // We don't transaction sandwich this, it's too rare to be a real problem.
                    player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.uncertaintyHandler.fishingRodPulls.add(hookEntity.owner));
                }
            }

            if (status.status() >= 24 && status.status() <= 28 && status.entityId() == player.entityID) {
                player.compensatedEntities.getSelf().setOpLevel(status.status() - 24);
            }
        }

        if (event.getPacket() instanceof SetSlotPacket slot) {
            if (slot.windowId() == 0) {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                    if (slot.slot() - 36 == player.packetStateData.lastSlotSelected) {
                        player.packetStateData.setSlowedByUsingItem(false);
                    }
                });

                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> {
                    if (slot.slot() - 36 == player.packetStateData.lastSlotSelected) {
                        player.packetStateData.setSlowedByUsingItem(false);
                    }
                });
            }
        }

        if (event.getPacket() instanceof WindowItemsPacket items) {
            if (items.windowId() == 0) { // Player inventory
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.packetStateData.setSlowedByUsingItem(false));
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.packetStateData.setSlowedByUsingItem(false));
            }
        }

        // 1.8 clients fail to send the RELEASE_USE_ITEM packet when a window is opened client sided while using an item
        if (event.getPacket() instanceof OpenWindowPacket) {
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.packetStateData.setSlowedByUsingItem(false));
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.packetStateData.setSlowedByUsingItem(false));
        }
        if (event.getPacket() instanceof OpenHorseWindowPacket) {
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.packetStateData.setSlowedByUsingItem(false));
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.packetStateData.setSlowedByUsingItem(false));
        }

        if (event.getPacket() instanceof SetPassengersPacket mount) {
            int vehicleID = mount.vehicleEntityId();
            List<Integer> passengers = mount.passengersId();

            handleMountVehicle(event, vehicleID, passengers);
        }

//        if (event.getPacket() instanceof AttachEntityPacket attach) {
//            // This packet was replaced by the mount packet on 1.9+ servers - to support multiple passengers on one vehicle
//            if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) return;
//
//            // If this is mounting rather than leashing
//            if (!attach.isLeash()) {
//                // Alright, let's convert this to the 1.9+ format to make it easier for grim
//                int vehicleID = attach.getHoldingId();
//                int attachID = attach.getAttachedId();
//                TrackerData trackerData = player.compensatedEntities.getTrackedEntity(attachID);
//
//                if (trackerData != null) {
//                    // 1.8 sends a vehicle ID of -1 to dismount the entity from its vehicle
//                    // This is opposite of the 1.9+ format, which sends the vehicle ID and then an empty array.
//                    if (vehicleID == -1) { // Dismounting
//                        vehicleID = trackerData.getLegacyPointEightMountedUpon();
//                        handleMountVehicle(event, vehicleID, new int[]{}); // The vehicle is empty
//                        return;
//                    } else { // Mounting
//                        trackerData.setLegacyPointEightMountedUpon(vehicleID);
//                        handleMountVehicle(event, vehicleID, new int[]{attachID});
//                    }
//                } else {
//                    // I don't think we can recover from this... warn and move on as this shouldn't happen.
//                    LogUtil.warn("Server sent an invalid attach entity packet for entity " + attach.getHoldingId() + " with passenger " + attach.getAttachedId() + "! The client ignores this.");
//                }
//            }
//        }

        if (event.getPacket() instanceof DestroyEntitiesPacket destroy) {
            List<Integer> destroyEntityIds = destroy.entityIds();

            for (int entityID : destroyEntityIds) {
                despawnedEntitiesThisTransaction.add(entityID);
                player.compensatedEntities.serverPositionsMap.remove(entityID);
                // Remove the tracked vehicle (handling tracking knockback) if despawned
                if (player.compensatedEntities.serverPlayerVehicle != null && player.compensatedEntities.serverPlayerVehicle == entityID) {
                    player.compensatedEntities.serverPlayerVehicle = null;
                }
            }

            final int destroyTransaction = player.lastTransactionSent.get() + 1;
            player.latencyUtils.addRealTimeTask(destroyTransaction, () -> {
                for (int integer : destroyEntityIds) {
                    player.compensatedEntities.removeEntity(integer);
                    player.compensatedFireworks.removeFirework(integer);
                }
            });

            // Don't let the player freeze transactions to keep the firework boost velocity + uncertainty
            // Also generally prevents people with high ping gaining too high an advantage in firework use
            if (maxFireworkBoostPing > 0) {
                player.runNettyTaskInMs(() -> {
                    if (player.lastTransactionReceived.get() >= destroyTransaction) return;
                    for (int entityID : destroyEntityIds) {
                        // If the player has a firework boosting them, setback
                        if (player.compensatedFireworks.hasFirework(entityID)) {
                            player.getSetbackTeleportUtil().executeViolationSetback();
                            break;
                        }
                    }
                }, maxFireworkBoostPing);
            }
        }
    }

    private void handleMountVehicle(PlayerPacketOutEvent event, int vehicleID, List<Integer> passengers) {
        boolean wasInVehicle = player.getRidingVehicleId() == vehicleID;
        boolean inThisVehicle = false;

        for (int passenger : passengers) {
            inThisVehicle = passenger == player.entityID;
            if (inThisVehicle) break;
        }

        if (inThisVehicle && !wasInVehicle) {
            player.handleMountVehicle(vehicleID);
        }

        if (!inThisVehicle && wasInVehicle) {
            player.handleDismountVehicle(event);
        }
        // Better lag compensation if we were affected by this
        if (wasInVehicle || inThisVehicle) {
            player.sendTransaction();
        }
        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
            PacketEntity vehicle = player.compensatedEntities.getEntity(vehicleID);

            // Vanilla likes sending null vehicles, so we must ignore those like the client ignores them
            if (vehicle == null) return;

            // Eject existing passengers for this vehicle
            for (PacketEntity passenger : new ArrayList<>(vehicle.passengers)) {
                passenger.eject();
            }

            // Add the entities as vehicles
            for (int entityID : passengers) {
                PacketEntity passenger = player.compensatedEntities.getEntity(entityID);
                if (passenger == null) continue;
                passenger.mount(vehicle);
            }
        });
    }

    private void handleMoveEntity(PlayerPacketOutEvent event, int entityId, double deltaX, double deltaY, double deltaZ, Float yaw, Float pitch, boolean isRelative, boolean hasPos) {
        TrackerData data = player.compensatedEntities.getTrackedEntity(entityId);

        if (!hasSentPreWavePacket) {
            hasSentPreWavePacket = true;
            player.sendTransaction();
        }

        if (data != null) {
            // Update the tracked server's entity position
            if (isRelative) {
                // There is a bug where vehicles may start flying due to mojang setting packet position on the client
                // (Works at 0 ping but causes funny bugs at any higher ping)
                // As we don't want vehicles to fly, we need to replace it with a teleport if it is player vehicle
                //
                // Don't bother with client controlled vehicles though
                boolean vanillaVehicleFlight = player.compensatedEntities.serverPlayerVehicle != null
                        && player.compensatedEntities.serverPlayerVehicle == entityId
                        && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9);

                // ViaVersion sends two relative packets when moving more than 4 blocks
                // This is broken and causes the client to interpolate like (0, 4) and (1, 3) instead of (1, 7)
                // This causes impossible hits, so grim must replace this with a teleport entity packet
                // Not ideal, but neither is 1.8 players on a 1.9+ server.
                if (vanillaVehicleFlight ||
                        ((Math.abs(deltaX) >= 3.9375 || Math.abs(deltaY) >= 3.9375 || Math.abs(deltaZ) >= 3.9375) && player.getClientVersion().isOlderThan(ClientVersion.V_1_9))) {
                    player.bukkitPlayer.sendPacket(new EntityTeleportPacket(entityId, new Pos(data.getX() + deltaX, data.getY() + deltaY, data.getZ() + deltaZ, yaw == null ? data.getXRot() : yaw, pitch == null ? data.getYRot() : pitch), false));
                    event.setCancelled(true);
                    return;
                }

                data.setX(data.getX() + deltaX);
                data.setY(data.getY() + deltaY);
                data.setZ(data.getZ() + deltaZ);
            } else {
                data.setX(deltaX);
                data.setY(deltaY);
                data.setZ(deltaZ);
            }
            if (yaw != null) {
                data.setXRot(yaw);
                data.setYRot(pitch);
            }

            // We can't hang two relative moves on one transaction
            if (data.getLastTransactionHung() == player.lastTransactionSent.get()) {
                player.sendTransaction();
            }
            data.setLastTransactionHung(player.lastTransactionSent.get());
        }

        int lastTrans = player.lastTransactionSent.get();

        player.latencyUtils.addRealTimeTask(lastTrans, () -> {
            PacketEntity entity = player.compensatedEntities.getEntity(entityId);
            if (entity == null) return;
            if (entity instanceof PacketEntityTrackXRot && yaw != null) {
                PacketEntityTrackXRot xRotEntity = (PacketEntityTrackXRot) entity;
                xRotEntity.packetYaw = yaw;
                xRotEntity.steps = entity.isBoat() ? 10 : 3;
            }

            entity.onFirstTransaction(isRelative, hasPos, deltaX, deltaY, deltaZ, player);
        });

        player.latencyUtils.addRealTimeTask(lastTrans + 1, () -> {
            PacketEntity entity = player.compensatedEntities.getEntity(entityId);
            if (entity == null) return;
            entity.onSecondTransaction();
        });
    }

    public void addEntity(int entityID, UUID uuid, Entity entity, Pos position, Map<Integer, Metadata.Entry<?>> entityMetadata, int extraData) {
        if (despawnedEntitiesThisTransaction.contains(entityID)) {
            player.sendTransaction();
        }

        player.compensatedEntities.serverPositionsMap.put(entityID,
                new TrackerData(position.x(), position.y(), position.z(), position.yaw(), position.pitch(), entity.getEntityType(), player.lastTransactionSent.get()));

        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
            player.compensatedEntities.addEntity(entityID, uuid, entity, position, position.yaw(), extraData);
            if (entityMetadata != null) {
                player.compensatedEntities.updateEntityMetadata(entityID, entityMetadata);
            }
        });
    }

    private boolean isDirectlyAffectingPlayer(GrimPlayer player, int entityID) {
        // The attributes for this entity is active, currently
        return (player.compensatedEntities.serverPlayerVehicle == null && entityID == player.entityID) ||
                (player.compensatedEntities.serverPlayerVehicle != null && entityID == player.compensatedEntities.serverPlayerVehicle);
    }

    public void onEndOfTickEvent() {
        // Only send a transaction at the end of the tick if we are tracking players
        player.sendTransaction(true); // We injected before vanilla flushes :) we don't need to flush
    }

    public void tickStartTick() {
        hasSentPreWavePacket = false;
    }
}

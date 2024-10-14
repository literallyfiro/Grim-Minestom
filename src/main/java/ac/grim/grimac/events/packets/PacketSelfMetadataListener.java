package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.impl.movement.NoSlowD;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.nmsutil.WatchableIndexUtil;
import ac.grim.grimac.utils.vector.Vector3d;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;

import java.util.HashMap;
import java.util.Map;

public class PacketSelfMetadataListener {

    public PacketSelfMetadataListener(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-self-metadata");
        node.setPriority(EventPriority.HIGH.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof EntityMetaDataPacket entityMetadata) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null)
                return;

            if (entityMetadata.entityId() == player.entityID) {
                // If we send multiple transactions, we are very likely to split them
                boolean hasSendTransaction = false;

                // 1.14+ poses:
                // - Client: I am sneaking
                // - Client: I am no longer sneaking
                // - Server: You are now sneaking
                // - Client: Okay, I am now sneaking.
                // - Server: You are no longer sneaking
                // - Client: Okay, I am no longer sneaking
                //
                // 1.13- poses:
                // - Client: I am sneaking
                // - Client: I am no longer sneaking
                // - Server: Okay, got it.
                //
                // Why mojang, why.  Why are you so incompetent at netcode.
                //
                // Also, mojang.  This system makes movement ping dependent!
                // A player using or exiting an elytra, or using or exiting sneaking will have differnet movement
                // to a player because of sending poses!  ViaVersion works fine without sending these poses
                // to the player on old servers... because the player just overrides this pose the very next tick
                //
                // It makes no sense to me why mojang is doing this, it has to be a bug.
                Map<Integer, Metadata.Entry<?>> metadataStuff = entityMetadata.entries();
                Map<Integer, Metadata.Entry<?>> metadataStuffCopy = new HashMap<>(metadataStuff);

                // Remove the pose metadata from the list
//                metadataStuff.removeIf(element -> element.getIndex() == 6);
//                entityMetadata.setEntityMetadata(metadataStuff);
                metadataStuffCopy.remove(6);

                Metadata. Entry<?> watchable = WatchableIndexUtil.getIndex(metadataStuffCopy, 0);

                if (watchable != null) {
                    Object zeroBitField = watchable.value();

                    if (zeroBitField instanceof Byte) {
                        byte field = (byte) zeroBitField;
                        boolean isGliding = (field & 0x80) == 0x80 && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9);
                        boolean isSwimming = (field & 0x10) == 0x10;
                        boolean isSprinting = (field & 0x8) == 0x8;

                        if (!hasSendTransaction) player.sendTransaction();
                        hasSendTransaction = true;

                        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                            player.isSwimming = isSwimming;
                            player.lastSprinting = isSprinting;
                            // Protect this due to players being able to get the server to spam this packet a lot
                            if (player.isGliding != isGliding) {
                                player.pointThreeEstimator.updatePlayerGliding();
                            }
                            player.isGliding = isGliding;
                        });
                    }
                }

//                if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
                Metadata. Entry<?> gravity = WatchableIndexUtil.getIndex(entityMetadata.entries(), 5);

                if (gravity != null) {
                    Object gravityObject = gravity.value();

                    if (gravityObject instanceof Boolean) {
                        if (!hasSendTransaction) player.sendTransaction();
                        hasSendTransaction = true;

                        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                            // Vanilla uses hasNoGravity, which is a bad name IMO
                            // hasGravity > hasNoGravity
                            player.playerEntityHasGravity = !((Boolean) gravityObject);
                        });
                    }
                }
//                }

//                if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
                Metadata. Entry<?> frozen = WatchableIndexUtil.getIndex(entityMetadata.entries(), 7);

                if (frozen != null) {
                    if (!hasSendTransaction) player.sendTransaction();
                    hasSendTransaction = true;
                    player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                        player.powderSnowFrozenTicks = (int) frozen.value();
                    });
                }
//                }

//                if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14)) {
                int id = 14;

//                if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_14_4)) {
//                    id = 12; // Added in 1.14 with an initial ID of 12
//                } else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_16_5)) {
//                    id = 13; // 1.15 changed this to 13
//                } else {
//                    id = 14; // 1.17 changed this to 14
//                }

                Metadata. Entry<?> bedObject = WatchableIndexUtil.getIndex(entityMetadata.entries(), id);
                if (bedObject != null) {
                    if (!hasSendTransaction) player.sendTransaction();
                    hasSendTransaction = true;

                    player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                        Pos bed = (Pos) bedObject.value();
                        if (bed != null) {
                            player.isInBed = true;
                            player.bedPosition = new Vector3d(bed.x() + 0.5, bed.y(), bed.z() + 0.5);
                        } else { // Run when we know the player is not in bed 100%
                            player.isInBed = false;
                        }
                    });
                }
//                }

                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                    Metadata. Entry<?> riptide = WatchableIndexUtil.getIndex(entityMetadata.entries(), 8);

                    // This one only present if it changed
                    if (riptide != null && riptide.value() instanceof Byte) {
                        boolean isRiptiding = (((byte) riptide.value()) & 0x04) == 0x04;

                        if (!hasSendTransaction) player.sendTransaction();
                        hasSendTransaction = true;

                        player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                            player.isRiptidePose = isRiptiding;
                        });

                        // 1.9 eating:
                        // - Client: I am starting to eat
                        // - Client: I am no longer eating
                        // - Server: Got that, you are eating!
                        // - Client: Okay, starting to eat (no response packet because server caused this)
                        // - Server: I got that you aren't eating, you are not eating!
                        // - Client: Okay, I am no longer eating (no response packet because server caused this)
                        //
                        // 1.8 eating:
                        // - Client: I am starting to eat
                        // - Client: I am no longer eating
                        // - Server: Okay, I will not make you eat or stop eating because it makes sense that the server doesn't control a player's eating.
                        //
                        // This was added for stuff like shields, but IMO it really should be all client sided
                        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                            boolean isActive = (((byte) riptide.value()) & 1) > 0;
                            boolean isOffhand = (((byte) riptide.value()) & 2) > 0;

                            // Player might have gotten this packet
                            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(),
                                    () -> player.packetStateData.setSlowedByUsingItem(false));

                            int markedTransaction = player.lastTransactionSent.get();

                            // Player has gotten this packet
                            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> {
                                ModifiableItemStack item = isOffhand ? player.getInventory().getOffHand() : player.getInventory().getHeldItem();

                                // If the player hasn't overridden this packet by using or stopping using an item
                                // Vanilla update order: Receive this -> process new interacts
                                // Grim update order: Process new interacts -> receive this
                                if (player.packetStateData.slowedByUsingItemTransaction < markedTransaction) {
                                    PacketPlayerDigging.handleUseItem(player, item, isOffhand ? Player.Hand.OFF : Player.Hand.MAIN);
                                    // The above line is a hack to fake activate use item
                                    player.packetStateData.setSlowedByUsingItem(isActive);

                                    player.checkManager.getPostPredictionCheck(NoSlowD.class).startedSprintingBeforeUse = player.packetStateData.isSlowedByUsingItem() && player.isSprinting;

                                    if (isActive) {
                                        player.packetStateData.eatingHand = isOffhand ? Player.Hand.OFF : Player.Hand.MAIN;
                                    }
                                }
                            });

                            // Yes, we do have to use a transaction for eating as otherwise it can desync much easier
                            // todo minestom here
                            event.getTasksAfterSend().add(player::sendTransaction);
                        }
                    }
                }
            }
        }

        // todo minestom ?????
//        if (event.getPacket() instanceof USE_BEDPacket) {
//            WrapperPlayServerUseBed bed = new WrapperPlayServerUseBed(event);
//
//            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
//            if (player != null && player.entityID == bed.getEntityId()) {
//                // Split so packet received after transaction
//                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
//                    player.isInBed = true;
//                    player.bedPosition = new Vector3d(bed.getPosition().getX() + 0.5, bed.getPosition().getY(), bed.getPosition().getZ() + 0.5);
//                });
//            }
//        }

        if (event.getPacket() instanceof EntityAnimationPacket animation) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player != null && player.entityID == animation.entityId()
                    && animation.animation() == EntityAnimationPacket.Animation.LEAVE_BED) {
                // Split so packet received before transaction
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.isInBed = false);
                // todo minestom this
                event.getTasksAfterSend().add(player::sendTransaction);
            }
        }
    }
}

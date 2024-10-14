package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsE;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsF;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.data.TrackerData;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySelf;
import ac.grim.grimac.utils.enums.Pose;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.JoinGamePacket;
import net.minestom.server.network.packet.server.play.RespawnPacket;
import net.minestom.server.network.packet.server.play.UpdateHealthPacket;
import net.minestom.server.world.DimensionType;

import java.util.List;

/**
 * PlayerRespawnS2CPacket info (1.20.2+):
 * If the world is different (check via registry key), world is recreated (all entities etc destroyed).
 * <p>
 * Client player is ALWAYS recreated
 * <p>
 * If the packet has the `KEEP_TRACKED_DATA` flag:
 * Sneaking and Sprinting fields are kept on the new client player.
 * <p>
 * If the packet has the `KEEP_ATTRIBUTES` flag:
 * Attributes are kept.
 * <p>
 * New client player is initialised:
 * Pose is set to standing.
 * Velocity is set to zero.
 * Pitch is set to 0.
 * Yaw is set to -180.
 */
// TODO update for 1.20.2-
public class PacketPlayerRespawn {

    public PacketPlayerRespawn(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-player-respawn");
        node.setPriority(EventPriority.HIGH.ordinal());

        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    private static final byte KEEP_ATTRIBUTES = 1;
    private static final byte KEEP_TRACKED_DATA = 2;
    private static final byte KEEP_ALL = 3;

    private boolean hasFlag(RespawnPacket respawn, byte flag) {
        // This packet was added in 1.16
        // On versions older than 1.15, via does not keep all data.
        // https://github.com/ViaVersion/ViaVersion/blob/master/common/src/main/java/com/viaversion/viaversion/protocols/v1_15_2to1_16/rewriter/EntityPacketRewriter1_16.java#L124
        // todo minestom is this correct?
        return (respawn.copyData() & flag) != 0;
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof UpdateHealthPacket health) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;
            //
//            if (player.packetStateData.lastFood == health.food()
//                    && player.packetStateData.lastHealth == health.health()
//                    && player.packetStateData.lastSaturation == health.foodSaturation()
//                    && PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) return;

            player.packetStateData.lastFood = health.food();
            player.packetStateData.lastHealth = health.health();
            player.packetStateData.lastSaturation = health.foodSaturation();

            player.sendTransaction();

            if (health.food() == 20) { // Split so transaction before packet
                player.latencyUtils.addRealTimeTask(player.lastTransactionReceived.get(), () -> player.food = 20);
            } else { // Split so transaction after packet
                player.latencyUtils.addRealTimeTask(player.lastTransactionReceived.get() + 1, () -> player.food = health.food());
            }

            if (health.health() <= 0) {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> player.compensatedEntities.getSelf().isDead = true);
            } else {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> player.compensatedEntities.getSelf().isDead = false);
            }

            // todo minestom same here
            event.getTasksAfterSend().add(player::sendTransaction);
        }

        if (event.getPacket() instanceof JoinGamePacket joinGame) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            DimensionType dimensionType = MinecraftServer.getDimensionTypeRegistry().get(joinGame.dimensionType());

            player.gamemode = joinGame.gameMode();
            player.entityID = joinGame.entityId();
            player.dimensionType = dimensionType;

            player.compensatedWorld.setDimension(dimensionType, event.getPlayer());
        }

        if (event.getPacket() instanceof RespawnPacket respawn) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            // todo minestom here
            List<Runnable> tasks = event.getTasksAfterSend();
            tasks.add(player::sendTransaction);

            // Force the player to accept a teleport before respawning
            // (We won't process movements until they accept a teleport, we won't let movements though either)
            // Also invalidate previous positions
            player.getSetbackTeleportUtil().hasAcceptedSpawnTeleport = false;
            player.getSetbackTeleportUtil().lastKnownGoodPosition = null;

            // clear server entity positions when the world changes
            if (isWorldChange(player, respawn)) {
                player.compensatedEntities.serverPositionsMap.clear();
            }

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get() + 1, () -> {
                player.isSneaking = false;
                player.lastOnGround = false;
                player.onGround = false;
                player.isInBed = false;
                player.packetStateData.setSlowedByUsingItem(false);
                player.packetStateData.packetPlayerOnGround = false; // If somewhere else pulls last ground to fix other issues
                player.packetStateData.lastClaimedPosition = new Vector3d();
                player.filterMojangStupidityOnMojangStupidity = new Vector3d();

                final boolean keepTrackedData = this.hasFlag(respawn, KEEP_TRACKED_DATA);

                if (!keepTrackedData) {
                    player.powderSnowFrozenTicks = 0;
                    player.compensatedEntities.getSelf().hasGravity = true;
                    player.playerEntityHasGravity = true;
                }

                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_4)) {
                    if (!keepTrackedData) {
                        player.isSprinting = false;
                    }
                } else {
                    player.lastSprintingForSpeed = false;
                }

                player.checkManager.getPacketCheck(BadPacketsE.class).handleRespawn(); // Reminder ticks reset

                // compensate for immediate respawn gamerule
                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15)) {
                    player.checkManager.getPacketCheck(BadPacketsF.class).exemptNext = true;
                }

                // EVERYTHING gets reset on a cross dimensional teleport, clear chunks and entities!
                if (isWorldChange(player, respawn)) {
                    player.compensatedEntities.entityMap.clear();
                    player.compensatedWorld.activePistons.clear();
                    player.compensatedWorld.openShulkerBoxes.clear();
                    player.compensatedWorld.chunks.clear();
                    player.compensatedWorld.isRaining = false;
                }
                DimensionType dimensionType = MinecraftServer.getDimensionTypeRegistry().get(respawn.dimensionType());
                player.dimensionType = dimensionType;

                player.compensatedEntities.serverPlayerVehicle = null; // All entities get removed on respawn
                player.compensatedEntities.playerEntity = new PacketEntitySelf(player, player.compensatedEntities.playerEntity);
                player.compensatedEntities.selfTrackedEntity = new TrackerData(0, 0, 0, 0, 0, EntityType.PLAYER, player.lastTransactionSent.get());

                if (player.getClientVersion().isOlderThan(ClientVersion.V_1_14)) { // 1.14+ players send a packet for this, listen for it instead
                    player.isSprinting = false;
                    player.checkManager.getPacketCheck(BadPacketsF.class).lastSprinting = false; // Pre 1.14 clients set this to false when creating new entity
                    // TODO: What the fuck viaversion, why do you throw out keep all metadata?
                    // The server doesn't even use it... what do we do?
                    player.compensatedEntities.hasSprintingAttributeEnabled = false;
                }
                player.pose = Pose.STANDING;
                player.clientVelocity = new MutableVector();
                player.gamemode = respawn.gameMode();
                player.compensatedWorld.setDimension(dimensionType, event.getPlayer());

                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && !this.hasFlag(respawn, KEEP_ATTRIBUTES)) {
                    // Reset attributes if not kept
                    player.compensatedEntities.getSelf().resetAttributes();
                    player.compensatedEntities.hasSprintingAttributeEnabled = false;
                }
            });
        }
    }

    private boolean isWorldChange(GrimPlayer player, RespawnPacket respawn) {
        DimensionType dimensionType = MinecraftServer.getDimensionTypeRegistry().get(respawn.dimensionType());
        return dimensionType.natural() != player.dimensionType.natural();
    }
}

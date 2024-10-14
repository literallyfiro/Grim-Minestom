package ac.grim.grimac.player;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.GrimUser;
import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.impl.aim.processor.AimProcessor;
import ac.grim.grimac.checks.impl.misc.ClientBrand;
import ac.grim.grimac.checks.impl.misc.TransactionOrder;
import ac.grim.grimac.events.packets.CheckManagerListener;
import ac.grim.grimac.manager.ActionManager;
import ac.grim.grimac.manager.CheckManager;
import ac.grim.grimac.manager.LastInstanceManager;
import ac.grim.grimac.manager.PunishmentManager;
import ac.grim.grimac.manager.SetbackTeleportUtil;
import ac.grim.grimac.predictionengine.MovementCheckRunner;
import ac.grim.grimac.predictionengine.PointThreeEstimator;
import ac.grim.grimac.predictionengine.UncertaintyHandler;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.BlockPlaceSnapshot;
import ac.grim.grimac.utils.data.MainSupportingBlockData;
import ac.grim.grimac.utils.data.PacketStateData;
import ac.grim.grimac.utils.data.Pair;
import ac.grim.grimac.utils.data.TrackerData;
import ac.grim.grimac.utils.data.VectorData;
import ac.grim.grimac.utils.data.VehicleData;
import ac.grim.grimac.utils.data.VelocityData;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySelf;
import ac.grim.grimac.utils.data.tags.SyncedTags;
import ac.grim.grimac.utils.enums.FluidTag;
import ac.grim.grimac.utils.enums.Pose;
import ac.grim.grimac.utils.latency.CompensatedEntities;
import ac.grim.grimac.utils.latency.CompensatedFireworks;
import ac.grim.grimac.utils.latency.CompensatedInventory;
import ac.grim.grimac.utils.latency.CompensatedWorld;
import ac.grim.grimac.utils.latency.LatencyUtils;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.math.TrigHandler;
import ac.grim.grimac.utils.nmsutil.BlockProperties;
import ac.grim.grimac.utils.nmsutil.GetBoundingBox;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.server.common.DisconnectPacket;
import net.minestom.server.network.packet.server.common.PingPacket;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static ac.grim.grimac.GrimAPI.EXECUTOR_SERVICE;

// Everything in this class should be sync'd to the anticheat thread.
// Put variables sync'd to the netty thread in PacketStateData
// Variables that need lag compensation should have their own class
// Soon there will be a generic class for lag compensation
public class GrimPlayer implements GrimUser {
    public UUID playerUUID;
    public int entityID;
    @NotNull
    public Player bukkitPlayer;
    // Start transaction handling stuff
    // Determining player ping
    // The difference between keepalive and transactions is that keepalive is async while transactions are sync
    public final Queue<Pair<Short, Long>> transactionsSent = new ConcurrentLinkedQueue<>();
    public final Set<Short> didWeSendThatTrans = ConcurrentHashMap.newKeySet();
    private final AtomicInteger transactionIDCounter = new AtomicInteger(0);
    public AtomicInteger lastTransactionSent = new AtomicInteger(0);
    public AtomicInteger lastTransactionReceived = new AtomicInteger(0);
    // End transaction handling stuff
    // Manager like classes
    public CheckManager checkManager;
    public ActionManager actionManager;
    public PunishmentManager punishmentManager;
    public MovementCheckRunner movementCheckRunner;
    public SyncedTags tagManager;
    // End manager like classes
    public MutableVector clientVelocity = new MutableVector();
    private long transactionPing = 0;
    public long lastTransSent = 0;
    public long lastTransReceived = 0;
    private long playerClockAtLeast = System.nanoTime();
    public double lastWasClimbing = 0;
    public boolean canSwimHop = false;
    public int riptideSpinAttackTicks = 0;
    public int powderSnowFrozenTicks = 0;
    public boolean hasGravity = true;
    public final long joinTime = System.currentTimeMillis();
    public boolean playerEntityHasGravity = true;
    public VectorData predictedVelocity = new VectorData(new MutableVector(), VectorData.VectorType.Normal);
    public MutableVector actualMovement = new MutableVector();
    public MutableVector stuckSpeedMultiplier = new MutableVector(1, 1, 1);
    public UncertaintyHandler uncertaintyHandler;
    public double gravity;
    public float friction;
    public double speed;
    public Vector3d filterMojangStupidityOnMojangStupidity = new Vector3d();
    public double x;
    public double y;
    public double z;
    public double lastX;
    public double lastY;
    public double lastZ;
    public float xRot;
    public float yRot;
    public float lastXRot;
    public float lastYRot;
    public boolean onGround;
    public boolean lastOnGround;
    public boolean isSneaking;
    public boolean wasSneaking;
    public boolean isSprinting;
    public boolean lastSprinting;
    public String teamName;
    // The client updates sprinting attribute at end of each tick
    // Don't false if the server update's the player's sprinting status
    public boolean lastSprintingForSpeed;
    public boolean isFlying;
    public boolean canFly;
    public boolean wasFlying;
    public boolean isSwimming;
    public boolean wasSwimming;
    public boolean isClimbing;
    public boolean isGliding;
    public boolean wasGliding;
    public boolean isRiptidePose = false;
    public double fallDistance;
    public SimpleCollisionBox boundingBox;
    public Pose pose = Pose.STANDING;
    // Determining slow movement has to be done before pose is updated
    public boolean isSlowMovement = false;
    public boolean isInBed = false;
    public boolean lastInBed = false;
    public int food = 20;
    public float depthStriderLevel;
    public float sneakingSpeedMultiplier = 0.3f;
    public float flySpeed;
    public VehicleData vehicleData = new VehicleData();
    // The client claims this
    public boolean clientClaimsLastOnGround;
    // Set from base tick
    public boolean wasTouchingWater = false;
    public boolean wasTouchingLava = false;
    // For slightly reduced vertical lava friction and jumping
    public boolean slightlyTouchingLava = false;
    // For jumping
    public boolean slightlyTouchingWater = false;
    public boolean wasEyeInWater = false;
    public FluidTag fluidOnEyes;
    public boolean verticalCollision;
    public boolean clientControlledVerticalCollision;
    // Okay, this is our 0.03 detection
    //
    // couldSkipTick determines if an input could have resulted in the player skipping a tick < 0.03
    //
    // skippedTickInActualMovement determines if, relative to actual movement, the player didn't move enough
    // and a 0.03 vector was "close enough" to be an accurate prediction
    public boolean couldSkipTick = false;
    // This determines if the
    public boolean skippedTickInActualMovement = false;
    // You cannot initialize everything here for some reason
    public LastInstanceManager lastInstanceManager;
    public CompensatedFireworks compensatedFireworks;
    public CompensatedWorld compensatedWorld;
    public CompensatedEntities compensatedEntities;
    public LatencyUtils latencyUtils;
    public PointThreeEstimator pointThreeEstimator;
    public TrigHandler trigHandler;
    public PacketStateData packetStateData;
    // Keep track of basetick stuff
    public MutableVector baseTickAddition = new MutableVector();
    public MutableVector baseTickWaterPushing = new MutableVector();
    public MutableVector startTickClientVel = new MutableVector();
    // For syncing the player's full swing in 1.9+
    public int movementPackets = 0;
    public VelocityData firstBreadKB = null;
    public VelocityData likelyKB = null;
    public VelocityData firstBreadExplosion = null;
    public VelocityData likelyExplosions = null;
    public int minPlayerAttackSlow = 0;
    public int maxPlayerAttackSlow = 0;
    public GameMode gamemode;
    public DimensionType dimensionType;
    public Vector3d bedPosition;
    public long lastBlockPlaceUseItem = 0;
    public AtomicInteger cancelledPackets = new AtomicInteger(0);
    public MainSupportingBlockData mainSupportingBlockData = new MainSupportingBlockData(null, false);

    public void onPacketCancel() {
        if (spamThreshold != -1 && cancelledPackets.incrementAndGet() > spamThreshold) {
            LogUtil.info("Disconnecting " + getName() + " for spamming invalid packets, packets cancelled within a second " + cancelledPackets);
            disconnect(Component.translatable("disconnect.closed"));
            cancelledPackets.set(0);
        }
    }

    public int totalFlyingPacketsSent;
    public Queue<BlockPlaceSnapshot> placeUseItemPackets = new LinkedBlockingQueue<>();
    // This variable is for support with test servers that want to be able to disable grim
    // Grim disabler 2022 still working!
    public boolean disableGrim = false;

    public GrimPlayer(Player player) {
        this.bukkitPlayer = player;
        this.playerUUID = player.getUuid();
        reload(GrimAPI.INSTANCE.getConfigManager().getConfig());

        boundingBox = GetBoundingBox.getBoundingBoxFromPosAndSizeRaw(x, y, z, 0.6f, 1.8f);

        compensatedFireworks = new CompensatedFireworks(this); // Must be before checkmanager

        lastInstanceManager = new LastInstanceManager(this);
        actionManager = new ActionManager(this);
        checkManager = new CheckManager(this);
        punishmentManager = new PunishmentManager(this);
        tagManager = new SyncedTags(this);
        movementCheckRunner = new MovementCheckRunner(this);

        compensatedWorld = new CompensatedWorld(this, player.getInstance());
        compensatedEntities = new CompensatedEntities(this);
        latencyUtils = new LatencyUtils(this);
        trigHandler = new TrigHandler(this);
        uncertaintyHandler = new UncertaintyHandler(this); // must be after checkmanager
        pointThreeEstimator = new PointThreeEstimator(this);

        packetStateData = new PacketStateData();

        uncertaintyHandler.collidingEntities.add(0);
    }

    public Set<VectorData> getPossibleVelocities() {
        Set<VectorData> set = new HashSet<>();

        if (firstBreadKB != null) {
            set.add(new VectorData(firstBreadKB.vector, VectorData.VectorType.Knockback).returnNewModified(VectorData.VectorType.FirstBreadKnockback));
        }

        if (likelyKB != null) {
            // Allow water pushing to affect knockback
            set.add(new VectorData(likelyKB.vector, VectorData.VectorType.Knockback));
        }

        set.addAll(getPossibleVelocitiesMinusKnockback());
        return set;
    }

    public Set<VectorData> getPossibleVelocitiesMinusKnockback() {
        Set<VectorData> possibleMovements = new HashSet<>();
        possibleMovements.add(new VectorData(clientVelocity, VectorData.VectorType.Normal));

        // A player cannot swim hop (> 0 y vel) and be on the ground
        // Fixes bug with underwater stepping movement being confused with swim hopping movement
        if (canSwimHop && !onGround) {
            possibleMovements.add(new VectorData(clientVelocity.clone().setY(0.3f), VectorData.VectorType.Swimhop));
        }

        // If the player has that client sided riptide thing and has colliding with an entity
        // This was determined in the previous tick but whatever just include the 2 ticks around it
        // for a bit of safety as I doubt people will try to bypass this, it would be a very useless cheat
        if (riptideSpinAttackTicks >= 0 && Collections.max(uncertaintyHandler.collidingEntities) > 0) {
            possibleMovements.add(new VectorData(clientVelocity.clone().multiply(-0.2), VectorData.VectorType.Trident));
        }

        if (lastWasClimbing != 0) {
            possibleMovements.add(new VectorData(clientVelocity.clone().setY(lastWasClimbing + baseTickAddition.getY()), VectorData.VectorType.Climbable));
        }

        // Knockback takes precedence over piston pushing in my testing
        // It's very difficult to test precedence so if there's issues with this bouncy implementation let me know
        for (VectorData data : new HashSet<>(possibleMovements)) {
            for (BlockFace direction : uncertaintyHandler.slimePistonBounces) {
                if (direction.toDirection().normalX() != 0) {
                    possibleMovements.add(data.returnNewModified(data.vector.clone().setX(direction.toDirection().normalX()), VectorData.VectorType.SlimePistonBounce));
                } else if (direction.toDirection().normalY() != 0) {
                    possibleMovements.add(data.returnNewModified(data.vector.clone().setY(direction.toDirection().normalY()), VectorData.VectorType.SlimePistonBounce));
                } else if (direction.toDirection().normalZ() != 0) {
                    possibleMovements.add(data.returnNewModified(data.vector.clone().setZ(direction.toDirection().normalZ()), VectorData.VectorType.SlimePistonBounce));
                }
            }
        }

        return possibleMovements;
    }

    // Players can get 0 ping by repeatedly sending invalid transaction packets, but that will only hurt them
    // The design is allowing players to miss transaction packets, which shouldn't be possible
    // But if some error made a client miss a packet, then it won't hurt them too bad.
    // Also it forces players to take knockback
    public boolean addTransactionResponse(short id) {
        Pair<Short, Long> data = null;
        boolean hasID = false;
        int skipped = 0;
        for (Pair<Short, Long> iterator : transactionsSent) {
            if (iterator.getFirst() == id) {
                hasID = true;
                break;
            }
            skipped++;
        }

        if (hasID) {
            if (skipped > 0 && System.currentTimeMillis() - joinTime > 5000)
                checkManager.getPacketCheck(TransactionOrder.class).flagAndAlert("skipped: " + skipped);

            do {
                data = transactionsSent.poll();
                if (data == null)
                    break;

                lastTransactionReceived.incrementAndGet();
                lastTransReceived = System.currentTimeMillis();
                transactionPing = (System.nanoTime() - data.getSecond());
                playerClockAtLeast = data.getSecond();
            } while (data.getFirst() != id);

            // A transaction means a new tick, so apply any block places
            CheckManagerListener.handleQueuedPlaces(this, false, 0, 0, System.currentTimeMillis());
            latencyUtils.handleNettySyncTransaction(lastTransactionReceived.get());
        }

        // Were we the ones who sent the packet?
        return data != null && data.getFirst() == id;
    }

    public void baseTickAddWaterPushing(MutableVector vector) {
        baseTickWaterPushing.add(vector);
    }

    public void baseTickAddVector(MutableVector vector) {
        clientVelocity.add(vector);
    }

    public void trackBaseTickAddition(MutableVector vector) {
        baseTickAddition.add(vector);
    }

    public float getMaxUpStep() {
        final PacketEntitySelf self = compensatedEntities.getSelf();
        final PacketEntity riding = self.getRiding();
        if (riding == null) return (float) self.getAttributeValue(Attribute.GENERIC_STEP_HEIGHT);

        if (riding.isBoat()) {
            return 0f;
        }

        // Pigs, horses, striders, and other vehicles all have 1 stepping height by default
        return (float) riding.getAttributeValue(Attribute.GENERIC_STEP_HEIGHT);
    }

    public void sendTransactionDelayed() {
        sendTransaction();
//        MinecraftServer.getSchedulerManager().buildTask(this::sendTransaction)
//                .delay(2, ChronoUnit.SECONDS)
//                .schedule();
    }

    public void sendTransaction() {
        sendTransaction(false);
    }

    public void sendTransaction(boolean async) {
        // don't send transactions outside PLAY phase
        // Sending in non-play corrupts the pipeline, don't waste bandwidth when anticheat disabled
        if (bukkitPlayer.getPlayerConnection().getConnectionState() != ConnectionState.PLAY) return;

        // Send a packet once every 15 seconds to avoid any memory leaks
        if (disableGrim && (System.nanoTime() - getPlayerClockAtLeast()) > 15e9) {
            return;
        }

        lastTransSent = System.currentTimeMillis();
        short transactionID = (short) (-1 * (transactionIDCounter.getAndIncrement() & 0x7FFF));
        try {
            if (async) {
                EXECUTOR_SERVICE.submit(() -> {
                    addTransactionSend(transactionID);
                    bukkitPlayer.sendPacket(new PingPacket(transactionID));
                });
            } else {
                addTransactionSend(transactionID);
                bukkitPlayer.sendPacket(new PingPacket(transactionID));
            }
        } catch (
                Exception ignored) { // Fix protocollib + viaversion support by ignoring any errors :) // TODO: Fix this
            // recompile
        }
    }

    public void addTransactionSend(short id) {
        didWeSendThatTrans.add(id);
    }

    public boolean isEyeInFluid(FluidTag tag) {
        return this.fluidOnEyes == tag;
    }

    public double getEyeHeight() {
        return pose.eyeHeight;
    }

    public void timedOut() {
        disconnect(Component.translatable("disconnect.timeout"));
    }

    public void disconnect(Component reason) {
        String textReason;
        if (reason instanceof TranslatableComponent) {
            TranslatableComponent translatableComponent = (TranslatableComponent) reason;
            textReason = translatableComponent.key();
        } else {
            textReason = LegacyComponentSerializer.legacySection().serialize(reason);
        }
        LogUtil.info("Disconnecting " + bukkitPlayer.getName() + " for " + textReason);
        try {
            bukkitPlayer.sendPacket(new DisconnectPacket(reason));
        } catch (Exception ignored) { // There may (?) be an exception if the player is in the wrong state...
            LogUtil.warn("Failed to send disconnect packet to disconnect " + bukkitPlayer.getName() + "! Disconnecting anyways.");
        }
        bukkitPlayer.getPlayerConnection().disconnect();
    }

    public void pollData() {
        // Send a transaction at least once a tick, for timer and post check purposes
        // Don't be the first to send the transaction, or we will stack overflow
        //
        // This will only really activate if there's no entities around the player being tracked
        // 80 is a magic value that is roughly every other tick, we don't want to spam too many packets.
        if (lastTransSent != 0 && lastTransSent + 80 < System.currentTimeMillis()) {
            sendTransaction(true); // send on netty thread
        }
        if ((System.nanoTime() - getPlayerClockAtLeast()) > maxTransactionTime * 1e9) {
            timedOut();
        }

        if (!GrimAPI.INSTANCE.getPlayerDataManager().shouldCheck(bukkitPlayer)) {
            GrimAPI.INSTANCE.getPlayerDataManager().remove(bukkitPlayer);
        }

        updatePermissions();
    }

    public void updateVelocityMovementSkipping() {
        if (!couldSkipTick) {
            couldSkipTick = pointThreeEstimator.determineCanSkipTick(BlockProperties.getFrictionInfluencedSpeed((float) (speed * (isSprinting ? 1.3 : 1)), this), getPossibleVelocitiesMinusKnockback());
        }

        Set<VectorData> knockback = new HashSet<>();
        if (firstBreadKB != null) knockback.add(new VectorData(firstBreadKB.vector, VectorData.VectorType.Knockback));
        if (likelyKB != null) knockback.add(new VectorData(likelyKB.vector, VectorData.VectorType.Knockback));

        boolean kbPointThree = pointThreeEstimator.determineCanSkipTick(BlockProperties.getFrictionInfluencedSpeed((float) (speed * (isSprinting ? 1.3 : 1)), this), knockback);
        checkManager.getKnockbackHandler().setPointThree(kbPointThree);

        Set<VectorData> explosion = new HashSet<>();
        if (firstBreadExplosion != null)
            explosion.add(new VectorData(firstBreadExplosion.vector, VectorData.VectorType.Explosion));
        if (likelyExplosions != null)
            explosion.add(new VectorData(likelyExplosions.vector, VectorData.VectorType.Explosion));

        boolean explosionPointThree = pointThreeEstimator.determineCanSkipTick(BlockProperties.getFrictionInfluencedSpeed((float) (speed * (isSprinting ? 1.3 : 1)), this), explosion);
        checkManager.getExplosionHandler().setPointThree(explosionPointThree);

        if (kbPointThree || explosionPointThree) {
            uncertaintyHandler.lastPointThree.reset();
        }
    }

    public boolean noModifyPacketPermission = false;
    public boolean noSetbackPermission = false;

    //TODO: Create a configurable timer for this
    @Override
    public void updatePermissions() {
        if (bukkitPlayer == null) return;
        this.noModifyPacketPermission = bukkitPlayer.hasPermission("grim.nomodifypacket");
        this.noSetbackPermission = bukkitPlayer.hasPermission("grim.nosetback");
        EXECUTOR_SERVICE.submit(() -> {
            for (AbstractCheck check : checkManager.allChecks.values()) {
                if (check instanceof Check) {
                    ((Check) check).updateExempted();
                }
            }
        });
    }

    private int spamThreshold = 100;

    public boolean isPointThree() {
        return getClientVersion().isOlderThan(ClientVersion.V_1_18_2);
    }

    public double getMovementThreshold() {
        return isPointThree() ? 0.03 : 0.0002;
    }

    public ClientVersion getClientVersion() {
        return ClientVersion.getById(bukkitPlayer.getPlayerConnection().getProtocolVersion());
    }

    // Alright, someone at mojang decided to not send a flying packet every tick with 1.9
    // Thanks for wasting my time to save 1 MB an hour
    //
    // MEANING, to get an "acceptable" 1.9+ reach check, we must only treat it like a 1.8 clients
    // when it is acting like one and sending a packet every tick.
    //
    // There are two predictable scenarios where this happens:
    // 1. The player moves more than 0.03/0.0002 blocks every tick
    //     - This code runs after the prediction engine to prevent a false when immediately switching back to 1.9-like movements
    //     - 3 ticks is a magic value, but it should buffer out incorrect predictions somewhat.
    // 2. The player is in a vehicle
    public boolean isTickingReliablyFor(int ticks) {
        return (getClientVersion().isOlderThan(ClientVersion.V_1_9)
                || !uncertaintyHandler.lastPointThree.hasOccurredSince(ticks))
                || compensatedEntities.getSelf().inVehicle();
    }

    public boolean canThePlayerBeCloseToZeroMovement(int ticks) {
        return (!uncertaintyHandler.lastPointThree.hasOccurredSince(ticks));
    }

    public CompensatedInventory getInventory() {
        return checkManager.getInventory();
    }

    public List<Double> getPossibleEyeHeights() { // We don't return sleeping eye height
        if (getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14)) { // Elytra, sneaking (1.14), standing
            final float scale = (float) compensatedEntities.getSelf().getAttributeValue(Attribute.GENERIC_SCALE);
            return Arrays.asList(0.4 * scale, 1.27 * scale, 1.62 * scale);
        } else if (getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) { // Elytra, sneaking, standing
            return Arrays.asList(0.4, 1.54, 1.62);
        } else { // Only sneaking or standing
            return Arrays.asList((double) (1.62f - 0.08f), (double) (1.62f));
        }
    }

    @Override
    public int getTransactionPing() {
        return GrimMath.floor(transactionPing / 1e6);
    }

    @Override
    public int getKeepAlivePing() {
        return bukkitPlayer.getLatency();
    }

    public long getPlayerClockAtLeast() {
        return playerClockAtLeast;
    }

    public SetbackTeleportUtil getSetbackTeleportUtil() {
        return checkManager.getSetbackUtil();
    }

    public boolean wouldCollisionResultFlagGroundSpoof(double inputY, double collisionY) {
        boolean verticalCollision = inputY != collisionY;
        boolean calculatedOnGround = verticalCollision && inputY < 0.0D;

        // We don't care about ground results here
        if (exemptOnGround()) return false;

        // If the player is on the ground with a y velocity of 0, let the player decide (too close to call)
        if (inputY == -SimpleCollisionBox.COLLISION_EPSILON && collisionY > -SimpleCollisionBox.COLLISION_EPSILON && collisionY <= 0)
            return false;

        return calculatedOnGround != onGround;
    }

    public boolean exemptOnGround() {
        return compensatedEntities.getSelf().inVehicle()
                || Collections.max(uncertaintyHandler.pistonX) != 0 || Collections.max(uncertaintyHandler.pistonY) != 0
                || Collections.max(uncertaintyHandler.pistonZ) != 0 || uncertaintyHandler.isStepMovement
                || isFlying || compensatedEntities.getSelf().isDead || isInBed || lastInBed || uncertaintyHandler.lastFlyingStatusChange.hasOccurredSince(30)
                || uncertaintyHandler.lastHardCollidingLerpingEntity.hasOccurredSince(3) || uncertaintyHandler.isOrWasNearGlitchyBlock;
    }

    public void handleMountVehicle(int vehicleID) {
        compensatedEntities.serverPlayerVehicle = vehicleID;
        TrackerData data = compensatedEntities.getTrackedEntity(vehicleID);

        if (data != null) {
            // If we actually need to check vehicle movement
            if (getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                // And if the vehicle is a type of vehicle that we track
                if (data.getEntityType() == EntityType.BOAT
                        || data.getEntityType() == EntityType.CHEST_BOAT
                        || data.getEntityType() == EntityType.HORSE
                        || data.getEntityType() == EntityType.SKELETON_HORSE
                        || data.getEntityType() == EntityType.ZOMBIE_HORSE
                        || data.getEntityType() == EntityType.DONKEY
                        || data.getEntityType() == EntityType.CAMEL
                        || data.getEntityType() == EntityType.LLAMA
                        || data.getEntityType() == EntityType.MULE
                        || data.getEntityType() == EntityType.TRADER_LLAMA
                        || data.getEntityType() == EntityType.PIG
                        || data.getEntityType() == EntityType.STRIDER){
                    // We need to set its velocity otherwise it will jump a bit on us, flagging the anticheat
                    // The server does override this with some vehicles. This is intentional.
                    bukkitPlayer.sendPacket(new EntityVelocityPacket(vehicleID, Vec.ZERO));
                }
            }
        }

        // Help prevent transaction split
        sendTransaction();

        latencyUtils.addRealTimeTask(lastTransactionSent.get(), () -> {
            this.vehicleData.wasVehicleSwitch = true;
        });
    }

    public int getRidingVehicleId() {
        return compensatedEntities.getPacketEntityID(compensatedEntities.getSelf().getRiding());
    }

    public void handleDismountVehicle(PlayerPacketOutEvent event) {
        // Help prevent transaction split
        sendTransaction();

        compensatedEntities.serverPlayerVehicle = null;
        // todo minestom here
        event.getTasksAfterSend().add(() -> {
            if (compensatedEntities.getSelf().getRiding() != null) {
                int ridingId = getRidingVehicleId();
                TrackerData data = compensatedEntities.serverPositionsMap.get(ridingId);
                if (data != null) {
                    EntityTeleportPacket teleportPacket = new EntityTeleportPacket(ridingId, new Pos(data.getX(), data.getY(), data.getZ(), data.getXRot(), data.getYRot()), false);
                    bukkitPlayer.sendPacket(teleportPacket);
                }
            }
        });

        latencyUtils.addRealTimeTask(lastTransactionSent.get(), () -> {
            this.vehicleData.wasVehicleSwitch = true;
            // Pre-1.14 players desync sprinting attribute when in vehicle to be false, sprinting itself doesn't change
            if (getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_14)) {
                compensatedEntities.hasSprintingAttributeEnabled = false;
            }
        });
    }

    public boolean canUseGameMasterBlocks() {
        // This check was added in 1.11
        // 1.11+ players must be in creative and have a permission level at or above 2
        return getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_10) || (gamemode == GameMode.CREATIVE && compensatedEntities.getSelf().getOpLevel() >= 2);
    }

    @Override
    public void runSafely(Runnable runnable) {
//        ChannelHelper.runInEventLoop(this.user.getChannel(), runnable);
    }

    @Override
    public String getName() {
        return PlainTextComponentSerializer.plainText().serialize(bukkitPlayer.getName());
    }

    @Override
    public UUID getUniqueId() {
        return bukkitPlayer.getUuid();
    }

    @Override
    public String getBrand() {
        return checkManager.getPacketCheck(ClientBrand.class).getBrand();
    }

    @Override
    public String getVersionName() {
        return getClientVersion().getReleaseName();
    }

    @Override
    public double getHorizontalSensitivity() {
        return checkManager.getRotationCheck(AimProcessor.class).sensitivityX;
    }

    @Override
    public double getVerticalSensitivity() {
        return checkManager.getRotationCheck(AimProcessor.class).sensitivityY;
    }

    @Override
    public boolean isVanillaMath() {
        return trigHandler.isVanillaMath();
    }

    @Override
    public Collection<? extends AbstractCheck> getChecks() {
        return checkManager.allChecks.values();
    }

    public void runNettyTaskInMs(Runnable runnable, int ms) {
        bukkitPlayer.scheduler().buildTask(runnable).delay(TaskSchedule.millis(ms)).schedule();
    }

    private int maxTransactionTime = 60;

    @Override
    public void reload(ConfigManager config) {
        spamThreshold = config.getIntElse("packet-spam-threshold", 100);
        maxTransactionTime = (int) GrimMath.clamp(config.getIntElse("max-transaction-time", 60), 1, 180);
    }

    @Override
    public void reload() {
        reload(GrimAPI.INSTANCE.getConfigManager().getConfig());
    }
}

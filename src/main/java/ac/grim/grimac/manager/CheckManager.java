package ac.grim.grimac.manager;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.checks.impl.aim.AimDuplicateLook;
import ac.grim.grimac.checks.impl.aim.AimModulo360;
import ac.grim.grimac.checks.impl.aim.processor.AimProcessor;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsA;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsB;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsC;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsD;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsE;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsF;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsG;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsH;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsI;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsJ;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsK;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsL;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsM;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsN;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsO;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsP;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsQ;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsR;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsS;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsT;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsU;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsV;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsW;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsX;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsY;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsZ;
import ac.grim.grimac.checks.impl.combat.Reach;
import ac.grim.grimac.checks.impl.crash.CrashA;
import ac.grim.grimac.checks.impl.crash.CrashB;
import ac.grim.grimac.checks.impl.crash.CrashC;
import ac.grim.grimac.checks.impl.crash.CrashD;
import ac.grim.grimac.checks.impl.crash.CrashE;
import ac.grim.grimac.checks.impl.crash.CrashF;
import ac.grim.grimac.checks.impl.crash.CrashG;
import ac.grim.grimac.checks.impl.crash.CrashH;
import ac.grim.grimac.checks.impl.exploit.ExploitA;
import ac.grim.grimac.checks.impl.exploit.ExploitB;
import ac.grim.grimac.checks.impl.groundspoof.NoFallA;
import ac.grim.grimac.checks.impl.misc.ClientBrand;
import ac.grim.grimac.checks.impl.misc.FastBreak;
import ac.grim.grimac.checks.impl.misc.GhostBlockMitigation;
import ac.grim.grimac.checks.impl.misc.TransactionOrder;
import ac.grim.grimac.checks.impl.movement.EntityControl;
import ac.grim.grimac.checks.impl.movement.NegativeTimerCheck;
import ac.grim.grimac.checks.impl.movement.NoSlowA;
import ac.grim.grimac.checks.impl.movement.NoSlowB;
import ac.grim.grimac.checks.impl.movement.NoSlowC;
import ac.grim.grimac.checks.impl.movement.NoSlowD;
import ac.grim.grimac.checks.impl.movement.NoSlowE;
import ac.grim.grimac.checks.impl.movement.PredictionRunner;
import ac.grim.grimac.checks.impl.movement.SetbackBlocker;
import ac.grim.grimac.checks.impl.movement.TimerCheck;
import ac.grim.grimac.checks.impl.movement.VehiclePredictionRunner;
import ac.grim.grimac.checks.impl.movement.VehicleTimer;
import ac.grim.grimac.checks.impl.post.PostCheck;
import ac.grim.grimac.checks.impl.prediction.DebugHandler;
import ac.grim.grimac.checks.impl.prediction.NoFallB;
import ac.grim.grimac.checks.impl.prediction.OffsetHandler;
import ac.grim.grimac.checks.impl.prediction.Phase;
import ac.grim.grimac.checks.impl.scaffolding.AirLiquidPlace;
import ac.grim.grimac.checks.impl.scaffolding.DuplicateRotPlace;
import ac.grim.grimac.checks.impl.scaffolding.FabricatedPlace;
import ac.grim.grimac.checks.impl.scaffolding.FarPlace;
import ac.grim.grimac.checks.impl.scaffolding.InvalidPlaceA;
import ac.grim.grimac.checks.impl.scaffolding.InvalidPlaceB;
import ac.grim.grimac.checks.impl.scaffolding.MultiPlace;
import ac.grim.grimac.checks.impl.scaffolding.PositionPlace;
import ac.grim.grimac.checks.impl.scaffolding.RotationPlace;
import ac.grim.grimac.checks.impl.velocity.ExplosionHandler;
import ac.grim.grimac.checks.impl.velocity.KnockbackHandler;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.checks.type.PositionCheck;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.checks.type.RotationCheck;
import ac.grim.grimac.checks.type.VehicleCheck;
import ac.grim.grimac.events.packets.PacketChangeGameState;
import ac.grim.grimac.events.packets.PacketEntityReplication;
import ac.grim.grimac.events.packets.PacketPlayerAbilities;
import ac.grim.grimac.events.packets.PacketWorldBorder;
import ac.grim.grimac.manager.init.start.SuperDebug;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.predictionengine.GhostBlockDetector;
import ac.grim.grimac.predictionengine.SneakingEstimator;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.anticheat.update.VehiclePositionUpdate;
import ac.grim.grimac.utils.latency.CompensatedCooldown;
import ac.grim.grimac.utils.latency.CompensatedFireworks;
import ac.grim.grimac.utils.latency.CompensatedInventory;
import ac.grim.grimac.utils.team.TeamHandler;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;

public class CheckManager {
    ClassToInstanceMap<PacketCheck> packetChecks;
    ClassToInstanceMap<PositionCheck> positionCheck;
    ClassToInstanceMap<RotationCheck> rotationCheck;
    ClassToInstanceMap<VehicleCheck> vehicleCheck;
    ClassToInstanceMap<PacketCheck> prePredictionChecks;

    ClassToInstanceMap<BlockPlaceCheck> blockPlaceCheck;
    ClassToInstanceMap<PostPredictionCheck> postPredictionCheck;

    public ClassToInstanceMap<AbstractCheck> allChecks;

    public CheckManager(GrimPlayer player) {
        // Include post checks in the packet check too
        packetChecks = new ImmutableClassToInstanceMap.Builder<PacketCheck>()
                .put(Reach.class, new Reach(player))
                .put(PacketEntityReplication.class, new PacketEntityReplication(player))
                .put(PacketChangeGameState.class, new PacketChangeGameState(player))
                .put(CompensatedInventory.class, new CompensatedInventory(player))
                .put(PacketPlayerAbilities.class, new PacketPlayerAbilities(player))
                .put(PacketWorldBorder.class, new PacketWorldBorder(player))
                .put(ActionManager.class, player.actionManager)
                .put(TeamHandler.class, new TeamHandler(player))
                .put(ClientBrand.class, new ClientBrand(player))
                .put(NoFallA.class, new NoFallA(player))
                .put(BadPacketsO.class, new BadPacketsO(player))
                .put(BadPacketsA.class, new BadPacketsA(player))
                .put(BadPacketsB.class, new BadPacketsB(player))
                .put(BadPacketsC.class, new BadPacketsC(player))
                .put(BadPacketsD.class, new BadPacketsD(player))
                .put(BadPacketsE.class, new BadPacketsE(player))
                .put(BadPacketsF.class, new BadPacketsF(player))
                .put(BadPacketsG.class, new BadPacketsG(player))
                .put(BadPacketsH.class, new BadPacketsH(player))
                .put(BadPacketsI.class, new BadPacketsI(player))
                .put(BadPacketsJ.class, new BadPacketsJ(player))
                .put(BadPacketsK.class, new BadPacketsK(player))
                .put(BadPacketsL.class, new BadPacketsL(player))
                .put(BadPacketsM.class, new BadPacketsM(player))
                .put(BadPacketsN.class, new BadPacketsN(player))
                .put(BadPacketsP.class, new BadPacketsP(player))
                .put(BadPacketsQ.class, new BadPacketsQ(player))
                .put(BadPacketsR.class, new BadPacketsR(player))
                .put(BadPacketsS.class, new BadPacketsS(player))
                .put(BadPacketsT.class, new BadPacketsT(player))
                .put(BadPacketsU.class, new BadPacketsU(player))
                .put(BadPacketsV.class, new BadPacketsV(player))
                .put(BadPacketsW.class, new BadPacketsW(player))
                .put(BadPacketsX.class, new BadPacketsX(player))
                .put(BadPacketsY.class, new BadPacketsY(player))
                .put(BadPacketsZ.class, new BadPacketsZ(player))
                .put(FastBreak.class, new FastBreak(player))
                .put(TransactionOrder.class, new TransactionOrder(player))
                .put(NoSlowB.class, new NoSlowB(player))
                .put(SetbackBlocker.class, new SetbackBlocker(player)) // Must be last class otherwise we can't check while blocking packets
                .build();
        positionCheck = new ImmutableClassToInstanceMap.Builder<PositionCheck>()
                .put(PredictionRunner.class, new PredictionRunner(player))
                .put(CompensatedCooldown.class, new CompensatedCooldown(player))
                .build();
        rotationCheck = new ImmutableClassToInstanceMap.Builder<RotationCheck>()
                .put(AimProcessor.class, new AimProcessor(player))
                .put(AimModulo360.class, new AimModulo360(player))
                .put(AimDuplicateLook.class, new AimDuplicateLook(player))
//                .put(Baritone.class, new Baritone(player))
                .build();
        vehicleCheck = new ImmutableClassToInstanceMap.Builder<VehicleCheck>()
                .put(VehiclePredictionRunner.class, new VehiclePredictionRunner(player))
                .build();

        postPredictionCheck = new ImmutableClassToInstanceMap.Builder<PostPredictionCheck>()
                .put(NegativeTimerCheck.class, new NegativeTimerCheck(player))
                .put(ExplosionHandler.class, new ExplosionHandler(player))
                .put(KnockbackHandler.class, new KnockbackHandler(player))
                .put(GhostBlockDetector.class, new GhostBlockDetector(player))
                .put(Phase.class, new Phase(player))
                .put(PostCheck.class, new PostCheck(player))
                .put(NoFallB.class, new NoFallB(player))
                .put(OffsetHandler.class, new OffsetHandler(player))
                .put(SuperDebug.class, new SuperDebug(player))
                .put(DebugHandler.class, new DebugHandler(player))
                .put(EntityControl.class, new EntityControl(player))
                .put(NoSlowA.class, new NoSlowA(player))
                .put(NoSlowC.class, new NoSlowC(player))
                .put(NoSlowD.class, new NoSlowD(player))
                .put(NoSlowE.class, new NoSlowE(player))
                .put(SetbackTeleportUtil.class, new SetbackTeleportUtil(player)) // Avoid teleporting to new position, update safe pos last
                .put(CompensatedFireworks.class, player.compensatedFireworks)
                .put(SneakingEstimator.class, new SneakingEstimator(player))
                .put(LastInstanceManager.class, player.lastInstanceManager)
                .build();

        blockPlaceCheck = new ImmutableClassToInstanceMap.Builder<BlockPlaceCheck>()
                .put(InvalidPlaceA.class, new InvalidPlaceA(player))
                .put(InvalidPlaceB.class, new InvalidPlaceB(player))
                .put(AirLiquidPlace.class, new AirLiquidPlace(player))
                .put(MultiPlace.class, new MultiPlace(player))
                .put(FarPlace.class, new FarPlace(player))
                .put(FabricatedPlace.class, new FabricatedPlace(player))
                .put(PositionPlace.class, new PositionPlace(player))
                .put(RotationPlace.class, new RotationPlace(player))
                .put(DuplicateRotPlace.class, new DuplicateRotPlace(player))
                .put(GhostBlockMitigation.class, new GhostBlockMitigation(player))
                .build();

        prePredictionChecks = new ImmutableClassToInstanceMap.Builder<PacketCheck>()
                .put(TimerCheck.class, new TimerCheck(player))
                .put(CrashA.class, new CrashA(player))
                .put(CrashB.class, new CrashB(player))
                .put(CrashC.class, new CrashC(player))
                .put(CrashD.class, new CrashD(player))
                .put(CrashE.class, new CrashE(player))
                .put(CrashF.class, new CrashF(player))
                .put(CrashG.class, new CrashG(player))
                .put(CrashH.class, new CrashH(player))
                .put(ExploitA.class, new ExploitA(player))
                .put(ExploitB.class, new ExploitB(player))
                .put(VehicleTimer.class, new VehicleTimer(player))
                .build();

        allChecks = new ImmutableClassToInstanceMap.Builder<AbstractCheck>()
                .putAll(packetChecks)
                .putAll(positionCheck)
                .putAll(rotationCheck)
                .putAll(vehicleCheck)
                .putAll(postPredictionCheck)
                .putAll(blockPlaceCheck)
                .putAll(prePredictionChecks)
                .build();
    }

    @SuppressWarnings("unchecked")
    public <T extends PositionCheck> T getPositionCheck(Class<T> check) {
        return (T) positionCheck.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends RotationCheck> T getRotationCheck(Class<T> check) {
        return (T) rotationCheck.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends VehicleCheck> T getVehicleCheck(Class<T> check) {
        return (T) vehicleCheck.get(check);
    }

    public void onPrePredictionReceivePacket(final PlayerPacketEvent packet) {
        for (PacketCheck check : prePredictionChecks.values()) {
            check.onPacketReceive(packet);
        }
    }

    public void onPacketReceive(final PlayerPacketEvent packet) {
        for (PacketCheck check : packetChecks.values()) {
            check.onPacketReceive(packet);
        }
        for (PostPredictionCheck check : postPredictionCheck.values()) {
            check.onPacketReceive(packet);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPacketReceive(packet);
        }
    }

    public void onPacketSend(final PlayerPacketOutEvent packet) {
        for (PacketCheck check : prePredictionChecks.values()) {
            check.onPacketSend(packet);
        }
        for (PacketCheck check : packetChecks.values()) {
            check.onPacketSend(packet);
        }
        for (PostPredictionCheck check : postPredictionCheck.values()) {
            check.onPacketSend(packet);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPacketSend(packet);
        }
    }

    public void onPositionUpdate(final PositionUpdate position) {
        for (PositionCheck check : positionCheck.values()) {
            check.onPositionUpdate(position);
        }
    }

    public void onRotationUpdate(final RotationUpdate rotation) {
        for (RotationCheck check : rotationCheck.values()) {
            check.process(rotation);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.process(rotation);
        }
    }

    public void onVehiclePositionUpdate(final VehiclePositionUpdate update) {
        for (VehicleCheck check : vehicleCheck.values()) {
            check.process(update);
        }
    }

    public void onPredictionFinish(final PredictionComplete complete) {
        for (PostPredictionCheck check : postPredictionCheck.values()) {
            check.onPredictionComplete(complete);
        }
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPredictionComplete(complete);
        }
    }

    public void onBlockPlace(final BlockPlace place) {
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onBlockPlace(place);
        }
    }

    public void onPostFlyingBlockPlace(final BlockPlace place) {
        for (BlockPlaceCheck check : blockPlaceCheck.values()) {
            check.onPostFlyingBlockPlace(place);
        }
    }

    public ExplosionHandler getExplosionHandler() {
        return getPostPredictionCheck(ExplosionHandler.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends PacketCheck> T getPacketCheck(Class<T> check) {
        return (T) packetChecks.get(check);
    }

    @SuppressWarnings("unchecked")
    public <T extends PacketCheck> T getPrePredictionCheck(Class<T> check) {
        return (T) prePredictionChecks.get(check);
    }

    private PacketEntityReplication packetEntityReplication = null;

    public PacketEntityReplication getEntityReplication() {
        if (packetEntityReplication == null) packetEntityReplication = getPacketCheck(PacketEntityReplication.class);
        return packetEntityReplication;
    }

    public NoFallA getNoFall() {
        return getPacketCheck(NoFallA.class);
    }

    private CompensatedInventory inventory = null;

    public CompensatedInventory getInventory() {
        if (inventory == null) inventory = getPacketCheck(CompensatedInventory.class);
        return inventory;
    }

    public KnockbackHandler getKnockbackHandler() {
        return getPostPredictionCheck(KnockbackHandler.class);
    }

    public CompensatedCooldown getCompensatedCooldown() {
        return getPositionCheck(CompensatedCooldown.class);
    }

    public NoSlowA getNoSlow() {
        return getPostPredictionCheck(NoSlowA.class);
    }

    public SetbackTeleportUtil getSetbackUtil() {
        return getPostPredictionCheck(SetbackTeleportUtil.class);
    }

    public DebugHandler getDebugHandler() {
        return getPostPredictionCheck(DebugHandler.class);
    }

    public OffsetHandler getOffsetHandler() {
        return getPostPredictionCheck(OffsetHandler.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends PostPredictionCheck> T getPostPredictionCheck(Class<T> check) {
        return (T) postPredictionCheck.get(check);
    }
}

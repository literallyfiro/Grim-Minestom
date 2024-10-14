package ac.grim.grimac.checks.impl.velocity;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.data.VectorData;
import ac.grim.grimac.utils.data.VelocityData;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.StateValue;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import ac.grim.grimac.utils.vector.Vector3f;
import ac.grim.grimac.utils.vector.Vector3i;
import lombok.Getter;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.ExplosionPacket;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

@CheckData(name = "AntiExplosion", configName = "Explosion", setback = 10)
public class ExplosionHandler extends Check implements PostPredictionCheck {
    Deque<VelocityData> firstBreadMap = new LinkedList<>();

    VelocityData lastExplosionsKnownTaken = null;
    VelocityData firstBreadAddedExplosion = null;

    @Getter
    boolean explosionPointThree = false;

    double offsetToFlag;
    double setbackVL;

    public ExplosionHandler(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(final PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof ExplosionPacket explosion) {
            Vector3f velocity = new Vector3f(explosion.playerMotionX(), explosion.playerMotionY(), explosion.playerMotionZ());

            final ExplosionPacket.BlockInteraction blockInteraction = explosion.blockInteraction();
            final boolean shouldDestroy = blockInteraction != ExplosionPacket.BlockInteraction.KEEP;

            if (!getRecords(explosion).isEmpty() && shouldDestroy) {
                player.sendTransaction();

                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                    for (Vector3i record : getRecords(explosion)) {
                        // Null OR not flip redstone blocks, then set to air
                        if (blockInteraction != ExplosionPacket.BlockInteraction.TRIGGER_BLOCK) {
                            player.compensatedWorld.updateBlock(record.x, record.y, record.z, 0);
                        } else {
                            // We need to flip redstone blocks, or do special things with other blocks
                            final MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(record);
                            final Block type = state.getType();
                            if (BlockTags.CANDLES.contains(type) || BlockTags.CANDLE_CAKES.contains(type)) {
                                state.setLit(false);
                                continue;
                            } else if (type == Block.BELL) {
                                // Does this affect anything? I don't know, I don't see anything that relies on whether a bell is ringing.
                                continue;
                            }

                            // Otherwise try and flip/open it.
                            final Object poweredValue = state.getInternalData().get(StateValue.POWERED);
                            final boolean canFlip = (poweredValue != null && !(Boolean) poweredValue) || type == Block.LEVER;
                            if (canFlip) {
                                player.compensatedWorld.tickOpenable(record.x, record.y, record.z);
                            }
                        }
                    }
                });
            }

            if (velocity.x != 0 || velocity.y != 0 || velocity.z != 0) {
                // No need to spam transactions
                if (getRecords(explosion).isEmpty()) player.sendTransaction();
                addPlayerExplosion(player.lastTransactionSent.get(), velocity);

                // todo minestom what
                event.getTasksAfterSend().add(player::sendTransaction);
            }
        }
    }

    private List<Vector3i> getRecords(ExplosionPacket packet) {
        int recordsLength = packet.records().length;
        List<Vector3i> records = new ArrayList<>(recordsLength);
        Vector3i floor = toFloor(new Vector3d(packet.x(), packet.y(), packet.z()));

        for (int i = 0; i < recordsLength; i++) {
            int chunkPosX = packet.records()[i] + floor.getX();
            int chunkPosY = packet.records()[i] + floor.getY();
            int chunkPosZ = packet.records()[i] + floor.getZ();
            records.add(new Vector3i(chunkPosX, chunkPosY, chunkPosZ));
        }
        return records;
    }

    private Vector3i toFloor(Vector3d position) {
        int floorX;
        int floorY;
        int floorZ;
        floorX = (int) Math.floor(position.x);
        floorY = (int) Math.floor(position.y);
        floorZ = (int) Math.floor(position.z);
        return new Vector3i(floorX, floorY, floorZ);
    }

    public VelocityData getFutureExplosion() {
        // Chronologically in the future
        if (firstBreadMap.size() > 0) {
            return firstBreadMap.peek();
        }
        // Less in the future
        if (lastExplosionsKnownTaken != null) {
            return lastExplosionsKnownTaken;
        }
        // Uncertain, might be in the future
        if (player.firstBreadExplosion != null && player.likelyExplosions == null) {
            return player.firstBreadExplosion;
        } else if (player.likelyExplosions != null) { // Known to be in the present
            return player.likelyExplosions;
        }
        return null;
    }

    public boolean shouldIgnoreForPrediction(VectorData data) {
        if (data.isExplosion() && data.isFirstBreadExplosion()) {
            return player.firstBreadExplosion.offset > offsetToFlag;
        }
        return false;
    }

    public boolean wouldFlag() {
        return (player.likelyExplosions != null && player.likelyExplosions.offset > offsetToFlag) || (player.firstBreadExplosion != null && player.firstBreadExplosion.offset > offsetToFlag);
    }

    public void addPlayerExplosion(int breadOne, Vector3f explosion) {
        firstBreadMap.add(new VelocityData(-1, breadOne, player.getSetbackTeleportUtil().isSendingSetback, new MutableVector(explosion.getX(), explosion.getY(), explosion.getZ())));
    }

    public void setPointThree(boolean isPointThree) {
        explosionPointThree = explosionPointThree || isPointThree;
    }

    public void handlePredictionAnalysis(double offset) {
        if (player.firstBreadExplosion != null) {
            player.firstBreadExplosion.offset = Math.min(player.firstBreadExplosion.offset, offset);
        }

        if (player.likelyExplosions != null) {
            player.likelyExplosions.offset = Math.min(player.likelyExplosions.offset, offset);
        }
    }

    public void forceExempt() {
        // Unsure explosion was taken
        if (player.firstBreadExplosion != null) {
            player.firstBreadExplosion.offset = 0;
        }

        if (player.likelyExplosions != null) {
            player.likelyExplosions.offset = 0;
        }
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        double offset = predictionComplete.getOffset();

        boolean wasZero = explosionPointThree;
        explosionPointThree = false;

        if (player.likelyExplosions == null && player.firstBreadExplosion == null) {
            firstBreadAddedExplosion = null;
            return;
        }

        // We must check to see if knockback has overridden this explosion
        // (Yes, I could make this very simple and exempt on kb, but that allows people to ignore most explosions)
        //
        // We do this by finding the minimum explosion transaction that could have been overridden
        // We then compare this against the maximum velocity transaction that could override
        //
        // If velocity is over transaction, exempt
        int minTrans = Math.min(player.likelyExplosions != null ? player.likelyExplosions.transaction : Integer.MAX_VALUE,
                player.firstBreadExplosion != null ? player.firstBreadExplosion.transaction : Integer.MAX_VALUE);
        int kbTrans = Math.max(player.likelyKB != null ? player.likelyKB.transaction : Integer.MIN_VALUE,
                player.firstBreadKB != null ? player.firstBreadKB.transaction : Integer.MIN_VALUE);

        if (player.predictedVelocity.isFirstBreadExplosion()) {
            firstBreadAddedExplosion = null;
            firstBreadMap.poll(); // Remove from map so we don't pull it again
        }

        if (wasZero || player.predictedVelocity.isExplosion() ||
                (minTrans < kbTrans)) {
            // Unsure knockback was taken
            if (player.firstBreadExplosion != null) {
                player.firstBreadExplosion.offset = Math.min(player.firstBreadExplosion.offset, offset);
            }

            if (player.likelyExplosions != null) {
                player.likelyExplosions.offset = Math.min(player.likelyExplosions.offset, offset);
            }
        }

        // 100% known kb was taken
        if (player.likelyExplosions != null) {
            if (player.likelyExplosions.offset > offsetToFlag) {
                if (flag()) {
                    if (getViolations() > setbackVL) {
                        player.getSetbackTeleportUtil().executeViolationSetback();
                    }
                }

                String formatOffset = "o: " + formatOffset(offset);

                if (player.likelyExplosions.offset == Integer.MAX_VALUE) {
                    formatOffset = "ignored explosion";
                }

                alert(formatOffset);
            } else {
                reward();
            }
        }
    }


    public VelocityData getPossibleExplosions(int lastTransaction, boolean isJustTesting) {
        handleTransactionPacket(lastTransaction);
        if (lastExplosionsKnownTaken == null)
            return null;

        VelocityData returnLastExplosion = lastExplosionsKnownTaken;
        if (!isJustTesting) {
            lastExplosionsKnownTaken = null;
        }
        return returnLastExplosion;
    }

    private void handleTransactionPacket(int transactionID) {
        VelocityData data = firstBreadMap.peek();
        while (data != null) {
            if (data.transaction == transactionID) { // First bread explosion
                if (lastExplosionsKnownTaken != null)
                    firstBreadAddedExplosion = new VelocityData(-1, data.transaction, data.isSetback, lastExplosionsKnownTaken.vector.clone().add(data.vector));
                else
                    firstBreadAddedExplosion = new VelocityData(-1, data.transaction, data.isSetback, data.vector);
                break; // All knockback after this will have not been applied
            } else if (data.transaction < transactionID) {
                if (lastExplosionsKnownTaken != null) {
                    lastExplosionsKnownTaken.vector.add(data.vector);
                } else {
                    lastExplosionsKnownTaken = new VelocityData(-1, data.transaction, data.isSetback, data.vector);
                }

                firstBreadAddedExplosion = null;
                firstBreadMap.poll();
                data = firstBreadMap.peek();
            } else { // We are too far ahead in the future
                break;
            }
        }
    }

    public VelocityData getFirstBreadAddedExplosion(int lastTransaction) {
        handleTransactionPacket(lastTransaction);
        return firstBreadAddedExplosion;
    }

    @Override
    public void onReload(ConfigManager config) {
        offsetToFlag = config.getDoubleElse("Explosion.threshold", 0.00001);
        setbackVL = config.getDoubleElse("Explosion.setbackvl", 10);
        if (setbackVL == -1) setbackVL = Double.MAX_VALUE;
    }

}

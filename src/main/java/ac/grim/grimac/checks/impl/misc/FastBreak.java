package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.nmsutil.BlockBreakSpeed;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.client.play.ClientAnimationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.server.play.AcknowledgeBlockChangePacket;
import net.minestom.server.network.packet.server.play.BlockChangePacket;

// Based loosely off of Hawk BlockBreakSpeedSurvival
// Also based loosely off of NoCheatPlus FastBreak
// Also based off minecraft wiki: https://minecraft.wiki/w/Breaking#Instant_breaking
@CheckData(name = "FastBreak", experimental = false)
public class FastBreak extends Check implements PacketCheck {
    public FastBreak(GrimPlayer playerData) {
        super(playerData);
    }

    // The block the player is currently breaking
    Vector3i targetBlock = null;
    // The maximum amount of damage the player deals to the block
    //
    double maximumBlockDamage = 0;
    // The last time a finish digging packet was sent, to enforce 0.3-second delay after non-instabreak
    long lastFinishBreak = 0;
    // The time the player started to break the block, to know how long the player waited until they finished breaking the block
    long startBreak = 0;

    // The buffer to this check
    double blockBreakBalance = 0;
    double blockDelayBalance = 0;

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        // Find the most optimal block damage using the animation packet, which is sent at least once a tick when breaking blocks
        // On 1.8 clients, via screws with this packet meaning we must fall back to the 1.8 idle flying packet
        if ((player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? event.getPacket() instanceof ClientAnimationPacket : WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) && targetBlock != null) {
            maximumBlockDamage = Math.max(maximumBlockDamage, BlockBreakSpeed.getBlockDamage(player, targetBlock));
        }

        if (event.getPacket() instanceof ClientPlayerDiggingPacket digging) {
            final Vector3i blockPosition = new Vector3i(digging.blockPosition());

            if (digging.status() == ClientPlayerDiggingPacket.Status.STARTED_DIGGING) {
                MinestomWrappedBlockState block = player.compensatedWorld.getWrappedBlockStateAt(blockPosition);
                
                // Exempt all blocks that do not exist in the player version
                if (MinestomWrappedBlockState.getDefaultState(block.getType()).getType() == Block.AIR) {
                    return;
                }
            
                startBreak = System.currentTimeMillis() - (targetBlock == null ? 50 : 0); // ???
                targetBlock = blockPosition;
                
                maximumBlockDamage = BlockBreakSpeed.getBlockDamage(player, targetBlock);

                double breakDelay = System.currentTimeMillis() - lastFinishBreak;

                if (breakDelay >= 275) { // Reduce buffer if "close enough"
                    blockDelayBalance *= 0.9;
                } else { // Otherwise, increase buffer
                    blockDelayBalance += 300 - breakDelay;
                }

                if (blockDelayBalance > 1000) { // If more than a second of advantage
                    flagAndAlert("Delay=" + breakDelay);
                    if (shouldModifyPackets()) {
                        event.setCancelled(true); // Cancelling start digging will cause server to reject block break
                        player.onPacketCancel();
                    }
                }

                clampBalance();
            }

            if (digging.status() == ClientPlayerDiggingPacket.Status.FINISHED_DIGGING && targetBlock != null) {
                double predictedTime = Math.ceil(1 / maximumBlockDamage) * 50;
                double realTime = System.currentTimeMillis() - startBreak;
                double diff = predictedTime - realTime;

                clampBalance();

                if (diff < 25) {  // Reduce buffer if "close enough"
                    blockBreakBalance *= 0.9;
                } else { // Otherwise, increase buffer
                    blockBreakBalance += diff;
                }

                if (blockBreakBalance > 1000) { // If more than a second of advantage
                    Player bukkitPlayer = player.bukkitPlayer;
                    if (bukkitPlayer == null || !bukkitPlayer.isOnline()) return;

                    if (bukkitPlayer.getPosition().distance(new Pos(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())) < 64) {
                        final int chunkX = blockPosition.getX() >> 4;
                        final int chunkZ = blockPosition.getZ() >> 4;
                        if (!bukkitPlayer.getInstance().isChunkLoaded(chunkX, chunkZ)) return; // Don't load chunks sync

                        Chunk chunk = bukkitPlayer.getInstance().getChunkAt(chunkX, chunkZ);
                        if (chunk == null) return;
                        Block block = chunk.getBlock(blockPosition.getX() & 15, blockPosition.getY(), blockPosition.getZ() & 15);

//                        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_13)) {
//                            // Cache this because strings are expensive
//                            blockId = WrappedBlockState.getByString(PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(), block.getBlockData().getAsString(false)).getGlobalId();
//                        } else {
//                            blockId = (block.getType().getId() << 4) | block.getData();
//                        }

                        player.bukkitPlayer.sendPacket(new BlockChangePacket(blockPosition.asPos(), block));
                        player.bukkitPlayer.sendPacket(new AcknowledgeBlockChangePacket(digging.sequence()));
                    }

                    if (flagAndAlert("Diff=" + diff + ",Balance=" + blockBreakBalance) && shouldModifyPackets()) {
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                }

                lastFinishBreak = System.currentTimeMillis();
            }
        }
    }

    private void clampBalance() {
        double balance = Math.max(1000, (player.getTransactionPing()));
        blockBreakBalance = GrimMath.clamp(blockBreakBalance, -balance, balance); // Clamp not Math.max in case other logic changes
        blockDelayBalance = GrimMath.clamp(blockDelayBalance, -balance, balance);
    }
}

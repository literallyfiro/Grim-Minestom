package ac.grim.grimac.checks.impl.post;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.lists.EvictingQueue;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientAnimationPacket;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerAbilitiesPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;

@CheckData(name = "Post")
public class PostCheck extends Check implements PacketCheck, PostPredictionCheck {
    private final ArrayDeque<ClientPacket> post = new ArrayDeque<>();
    // Due to 1.9+ missing the idle packet, we must queue flags
    // 1.8 clients will have the same logic for simplicity, although it's not needed
    private final List<String> flags = new EvictingQueue<>(10);
    private boolean sentFlying = false;
    private int isExemptFromSwingingCheck = Integer.MIN_VALUE;

    public PostCheck(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!flags.isEmpty()) {
            // Okay, the user might be cheating, let's double check
            // 1.8 clients have the idle packet, and this shouldn't false on 1.8 clients
            // 1.9+ clients have predictions, which will determine if hidden tick skipping occurred
            if (player.isTickingReliablyFor(3)) {
                for (String flag : flags) {
                    flagAndAlert(flag);
                }
            }

            flags.clear();
        }
    }

    @Override
    public void onPacketSend(final PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof EntityAnimationPacket animation) {
            if (animation.entityId() == player.entityID) {
                if (animation.animation() == EntityAnimationPacket.Animation.SWING_MAIN_ARM ||
                        animation.animation() == EntityAnimationPacket.Animation.SWING_OFF_HAND) {
                    isExemptFromSwingingCheck = player.lastTransactionSent.get();
                }
            }
        }
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            // Don't count teleports or duplicates as movements
            if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
                return;
            }

            post.clear();
            sentFlying = true;
        } else {
            // 1.13+ clients can click inventory outside tick loop, so we can't post check those two packets on 1.13+
            ClientPacket packetType = event.getPacket();
            if (isTransaction(packetType) && player.packetStateData.lastTransactionPacketWasValid) {
                if (sentFlying && !post.isEmpty()) {
                    flags.add(post.getFirst().toString().toLowerCase(Locale.ROOT).replace("_", " ") + " v" + player.getClientVersion().getReleaseName());
                }
                post.clear();
                sentFlying = false;
            } else if (one(packetType)) {
                if (sentFlying) post.add(event.getPacket());
            } else if (packetType instanceof ClientClickWindowPacket && player.getClientVersion().isOlderThan(ClientVersion.V_1_13)) {
                // Why do 1.13+ players send the click window packet whenever? This doesn't make sense.
                if (sentFlying) post.add(event.getPacket());
            } else if (two(packetType)) { // Exempt when the server sends animations because viaversion
                if (sentFlying) post.add(event.getPacket());
            } else if (packetType instanceof ClientEntityActionPacket packet // ViaRewind sends START_FALL_FLYING packets async for 1.8 clients on 1.9+ servers
                    && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) || packet.action() != ClientEntityActionPacket.Action.START_FLYING_ELYTRA)) {
                // https://github.com/GrimAnticheat/Grim/issues/824
                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19_3) && player.compensatedEntities.getSelf().getRiding() != null) {
                    return;
                }
                if (sentFlying) post.add(event.getPacket());
            }
        }
    }

    private boolean two(ClientPacket packet) {
        /*
        } else if (ANIMATION.equals(packetType)
                    && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) // ViaVersion delays animations for 1.8 clients
                    || PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8_8)) // when on 1.9+ servers
                    && player.getClientVersion().isOlderThan(ClientVersion.V_1_13) // 1.13 clicking inventory causes weird animations
                    && isExemptFromSwingingCheck < player.lastTransactionReceived.get()) { // Exempt when the server sends animations because viaversion
         */
        return packet instanceof ClientAnimationPacket
                && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9))
                && player.getClientVersion().isOlderThan(ClientVersion.V_1_13)
                && isExemptFromSwingingCheck < player.lastTransactionReceived.get();
    }

    private boolean one(ClientPacket packet) {
        return packet instanceof ClientPlayerAbilitiesPacket
                || (packet instanceof ClientHeldItemChangePacket && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8))
                || packet instanceof ClientInteractEntityPacket
                || packet instanceof ClientPlayerBlockPlacementPacket
                || packet instanceof ClientPlayerDiggingPacket
                || packet instanceof ClientUseItemPacket;
        /*
         } else if (PLAYER_ABILITIES.equals(packetType)
                    || (HELD_ITEM_CHANGE.equals(packetType) && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8))
                    || INTERACT_ENTITY.equals(packetType) || PLAYER_BLOCK_PLACEMENT.equals(packetType)
                    || USE_ITEM.equals(packetType) || PLAYER_DIGGING.equals(packetType)) {
         */

    }
}

package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerAbilitiesPacket;
import net.minestom.server.network.packet.server.play.PlayerAbilitiesPacket;

// The client can send ability packets out of order due to Mojang's excellent netcode design.
// We must delay the second ability packet until the tick after the first is received
// Else the player will fly for a tick, and we won't know about it, which is bad.
public class PacketPlayerAbilities extends Check implements PacketCheck {

    public PacketPlayerAbilities(GrimPlayer player) {
        super(player);
    }

    boolean lastSentPlayerCanFly = false;

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPlayerAbilitiesPacket abilities) {
            boolean flying = (abilities.flags() & 0x02) != 0;
            player.isFlying = flying && player.canFly;
        }
    }

    @Override
    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof PlayerAbilitiesPacket abilities) {
            player.sendTransaction();

            boolean isFlying = (abilities.flags() & 0x02) != 0;
            boolean isFlightAllowed = (abilities.flags() & 0x04) != 0;

            if (lastSentPlayerCanFly && !isFlightAllowed) {
                int noFlying = player.lastTransactionSent.get();
                int maxFlyingPing = GrimAPI.INSTANCE.getConfigManager().getConfig().getIntElse("max-ping-out-of-flying", 1000);
                if (maxFlyingPing != -1) {
                    player.runNettyTaskInMs(() -> {
                        if (player.lastTransactionReceived.get() < noFlying) {
                            player.getSetbackTeleportUtil().executeViolationSetback();
                        }
                    }, maxFlyingPing);
                }
            }

            lastSentPlayerCanFly = isFlightAllowed;

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                player.canFly = isFlightAllowed;
                player.isFlying = isFlying;
            });

        }
    }
}

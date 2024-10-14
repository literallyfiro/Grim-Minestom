package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientSteerVehiclePacket;

@CheckData(name = "BadPacketsR", decay = 0.25, experimental = true)
public class BadPacketsR extends Check implements PacketCheck {
    public BadPacketsR(final GrimPlayer player) {
        super(player);
    }

    private int positions = 0;
    private long clock = 0;
    private long lastTransTime;
    private int oldTransId = 0;

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (isTransaction(event.getPacket()) && player.packetStateData.lastTransactionPacketWasValid) {
            long ms = (player.getPlayerClockAtLeast() - clock) / 1000000L;
            long diff = (System.currentTimeMillis() - lastTransTime);
            if (diff > 2000 && ms > 2000) {
                if (positions == 0 && clock != 0 && player.gamemode != GameMode.SPECTATOR && !player.compensatedEntities.getSelf().isDead) {
                    flagAndAlert("time=" + ms + "ms, " + "lst=" + diff + "ms, positions=" + positions);
                } else {
                    reward();
                }
                player.compensatedWorld.removeInvalidPistonLikeStuff(oldTransId);
                positions = 0;
                clock = player.getPlayerClockAtLeast();
                lastTransTime = System.currentTimeMillis();
                oldTransId = player.lastTransactionSent.get();
            }
        }
        //
        if ((event.getPacket() instanceof ClientPlayerPositionAndRotationPacket ||
                event.getPacket() instanceof ClientPlayerPositionPacket) && !player.compensatedEntities.getSelf().inVehicle()) {
            positions++;
        } else if (event.getPacket() instanceof ClientSteerVehiclePacket && player.compensatedEntities.getSelf().inVehicle()) {
            positions++;
        }
    }

}

package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.Pair;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;
import net.minestom.server.network.packet.server.common.KeepAlivePacket;

import java.util.LinkedList;
import java.util.Queue;

@CheckData(name = "BadPacketsO")
public class BadPacketsO extends Check implements PacketCheck {
    Queue<Pair<Long, Long>> keepaliveMap = new LinkedList<>();

    public BadPacketsO(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof KeepAlivePacket packet) {
            keepaliveMap.add(new Pair<>(packet.id(), System.nanoTime()));
        }
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientKeepAlivePacket packet) {
            long id = packet.id();
            boolean hasID = false;

            for (Pair<Long, Long> iterator : keepaliveMap) {
                if (iterator.getFirst() == id) {
                    hasID = true;
                    break;
                }
            }

            if (!hasID) {
                if (flagAndAlert("id=" + id) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            } else { // Found the ID, remove stuff until we get to it (to stop very slow memory leaks)
                Pair<Long, Long> data;
                do {
                    data = keepaliveMap.poll();
                    if (data == null) break;
                } while (data.getFirst() != id);
            }
        }
    }
}

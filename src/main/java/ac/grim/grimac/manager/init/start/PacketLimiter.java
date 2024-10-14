package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.utils.time.Tick;

import static ac.grim.grimac.GrimAPI.EXECUTOR_SERVICE;

public class PacketLimiter implements Initable {
    @Override
    public void start() {
        EXECUTOR_SERVICE.submit(() -> {
            MinecraftServer.getSchedulerManager().buildTask(() -> {
                for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                    player.cancelledPackets.set(0);
                }
            }).delay(1, Tick.SERVER_TICKS).repeat(20, Tick.SERVER_TICKS).schedule();
        });
    }
}

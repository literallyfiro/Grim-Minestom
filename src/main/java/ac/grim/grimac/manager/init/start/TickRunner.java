package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.utils.anticheat.LogUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

import static ac.grim.grimac.GrimAPI.EXECUTOR_SERVICE;

public class TickRunner implements Initable {

    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");

        MinecraftServer.getSchedulerManager().scheduleTask(() ->
                GrimAPI.INSTANCE.getTickManager().tickSync(), TaskSchedule.immediate(), TaskSchedule.tick(1), ExecutionType.TICK_START);

        EXECUTOR_SERVICE.submit(() -> {
            MinecraftServer.getSchedulerManager().scheduleTask(() -> GrimAPI.INSTANCE.getTickManager().tickAsync(),
                    TaskSchedule.immediate(), TaskSchedule.tick(1), ExecutionType.TICK_START);
        });

    }
}

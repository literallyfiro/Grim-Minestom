package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

// Copied from: https://github.com/ThomasOM/Pledge/blob/master/src/main/java/dev/thomazz/pledge/inject/ServerInjector.java
@SuppressWarnings(value = {"unchecked", "deprecated"})
public class TickEndEvent implements Initable {
    boolean hasTicked = true;

    private static void tickRelMove() {
        for (GrimPlayer player : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            if (player.disableGrim) continue; // If we aren't active don't spam extra transactions
            player.checkManager.getEntityReplication().onEndOfTickEvent();
        }
    }

    @Override
    public void start() {
        if (!GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("Reach.enable-post-packet", false)) {
            return;
        }

        MinecraftServer.getSchedulerManager().buildTask(() -> {
            hasTicked = true;
            tickRelMove();
        }).executionType(ExecutionType.TICK_END).repeat(TaskSchedule.tick(1)).schedule();
//        // Inject so we can add the final transaction pre-flush event
//        try {
//            Object connection = SpigotReflectionUtil.getMinecraftServerConnectionInstance();
//
//            Field connectionsList = Reflection.getField(connection.getClass(), List.class, 1);
//            List<Object> endOfTickObject = (List<Object>) connectionsList.get(connection);
//
//            // Use a list wrapper to check when the size method is called
//            // Unsure why synchronized is needed because the object itself gets synchronized
//            // but whatever.  At least plugins can't break it, I guess.
//            //
//            // Pledge injects into another list, so we should be safe injecting into this one
//            List<?> wrapper = Collections.synchronizedList(new HookedListWrapper<Object>(endOfTickObject) {
//                @Override
//                public void onIterator() {
//                    hasTicked = true;
//                    tickRelMove();
//                }
//            });
//
//            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
//            unsafeField.setAccessible(true);
//            Unsafe unsafe = (Unsafe) unsafeField.get(null);
//            unsafe.putObject(connection, unsafe.objectFieldOffset(connectionsList), wrapper);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }
}

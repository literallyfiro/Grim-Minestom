package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public class ExemptOnlinePlayers implements Initable {
    @Override
    public void start() {
        for (@NotNull Instance instance : MinecraftServer.getInstanceManager().getInstances()) {
            for (net.minestom.server.entity.@NotNull Player player : instance.getPlayers()) {
                GrimAPI.INSTANCE.getPlayerDataManager().addUser(player);
            }
        }
    }
}

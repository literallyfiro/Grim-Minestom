package ac.grim.grimac.utils.anticheat;

import lombok.experimental.UtilityClass;
import net.minestom.server.MinecraftServer;

import java.util.logging.Logger;

@UtilityClass
public class LogUtil {
    public void info(final String info) {
        getLogger().info(info);
    }

    public void warn(final String warn) {
        getLogger().warning(warn);
    }

    public void error(final String error) {
        getLogger().severe(error);
    }

    public Logger getLogger() {
        return Logger.getLogger("GrimAC");
    }

    public void console(final String info) {
        MinecraftServer.getCommandManager().getConsoleSender().sendMessage(info);
    }

}

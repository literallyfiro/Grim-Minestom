package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.commands.GrimCommand;
import ac.grim.grimac.manager.init.Initable;
import net.minestom.server.MinecraftServer;

public class CommandRegister implements Initable {
    @Override
    public void start() {
        MinecraftServer.getCommandManager().register(new GrimCommand());
    }
}

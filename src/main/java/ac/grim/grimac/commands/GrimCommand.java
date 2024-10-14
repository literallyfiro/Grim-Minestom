package ac.grim.grimac.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class GrimCommand extends Command {

    public GrimCommand() {
        super("grim", "grimac");

        addSubcommand(new GrimPerf());
        addSubcommand(new GrimDebug.GrimConsoleDebug());
        addSubcommand(new GrimDebug.GrimPlayerDebug());
        addSubcommand(new GrimAlerts());
        addSubcommand(new GrimProfile());
        addSubcommand(new GrimSendAlert());
        addSubcommand(new GrimHelp());
        addSubcommand(new GrimReload());
        addSubcommand(new GrimSpectate());
        addSubcommand(new GrimStopSpectating());
        //addSubcommand(new GrimLog());
        addSubcommand(new GrimVerbose());

        setDefaultExecutor((sender, context) -> MinecraftServer.getCommandManager().execute(sender, "grim help"));
    }
}

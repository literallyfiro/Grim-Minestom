package ac.grim.grimac.commands;

import net.minestom.server.command.builder.Command;

public class GrimCommand extends Command {

    public GrimCommand() {
        super("grim", "grimac");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Running grim");
        });

        // todo minestom here.
        addSubcommand(new GrimPerf());
        //addSubcommand(new GrimDebug());
        addSubcommand(new GrimAlerts());
        //addSubcommand(new GrimProfile());
        addSubcommand(new GrimSendAlert());
        addSubcommand(new GrimHelp());
        //addSubcommand(new GrimReload());
        addSubcommand(new GrimSpectate());
        addSubcommand(new GrimStopSpectating());
        //addSubcommand(new GrimLog());
        addSubcommand(new GrimVerbose());
    }
}

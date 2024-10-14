package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import net.minestom.server.command.builder.Command;

public class GrimHelp extends Command {

    public GrimHelp() {
        super("help");
        //setCondition((sender, commandString) -> sender.hasPermission("grim.help"));

        setDefaultExecutor((sender, context) -> {
            for (String string : GrimAPI.INSTANCE.getConfigManager().getConfig().getStringList("help")) {
                string = MessageUtil.format(string);
                sender.sendMessage(string);
            }
        });
    }
}

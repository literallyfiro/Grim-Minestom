package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class GrimVerbose extends Command {

    public GrimVerbose() {
        super("verbose");
        //setCondition((sender, commandString) -> sender.hasPermission("grim.verbose") && sender instanceof Player);

        setDefaultExecutor((sender, context) -> {
            GrimAPI.INSTANCE.getAlertManager().toggleVerbose((Player) sender);
        });
    }
}

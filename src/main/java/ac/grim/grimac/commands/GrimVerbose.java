package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class GrimVerbose extends Command {

    public GrimVerbose() {
        super("verbose");
        setCondition((sender, commandString) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("&cError: Console may not execute this command.");
                return false;
            }
            return sender.hasPermission("grim.verbose");
        });

        addSyntax((sender, context) -> {
            GrimAPI.INSTANCE.getAlertManager().toggleVerbose((Player) sender);
        });
    }
}

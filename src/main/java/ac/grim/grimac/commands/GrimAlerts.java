package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class GrimAlerts extends Command {

    public GrimAlerts() {
        super("alerts");
        setCondition((sender, commandString) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("&cError: Console may not execute this command.");
                return false;
            }
            return true;
        });

        addSyntax((sender, context) -> {
            if (!sender.hasPermission("grim.alerts")) {
                sender.sendMessage(MessageUtil.format("&cError: You do not have permission to execute this command."));
                return;
            }
            GrimAPI.INSTANCE.getAlertManager().toggleAlerts((net.minestom.server.entity.Player) sender);
        });
    }
}

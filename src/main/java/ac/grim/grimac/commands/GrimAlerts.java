package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class GrimAlerts extends Command {

    public GrimAlerts() {
        super("alerts");
        //setCondition((sender, commandString) -> sender.hasPermission("grim.alerts") && sender instanceof Player);

        setDefaultExecutor((sender, context) -> {
            GrimAPI.INSTANCE.getAlertManager().toggleAlerts((net.minestom.server.entity.Player) sender);
        });
    }
}

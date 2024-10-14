package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrimSendAlert extends Command {
    public GrimSendAlert() {
        super("sendalert");
        setCondition((sender, commandString) -> sender.hasPermission("grim.sendalert"));
        addSyntax((sender, context) -> {
            sender.sendMessage(MessageUtil.format("&eUsage: &agrim sendalert &f<message>"));
        });

        var stringArgument = ArgumentType.String("string");
        addSyntax((sender, context) -> {
            String string = context.get(stringArgument);
            string = MessageUtil.format(string);

            for (Player bukkitPlayer : GrimAPI.INSTANCE.getAlertManager().getEnabledAlerts()) {
                bukkitPlayer.sendMessage(Component.text(string));
            }

            if (GrimAPI.INSTANCE.getConfigManager().isPrintAlertsToConsole()) {
                LogUtil.console(string); // Print alert to console
            }
        }, stringArgument);
    }
}

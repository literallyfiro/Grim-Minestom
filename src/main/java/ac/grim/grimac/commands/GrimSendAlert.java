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
        //setCondition((sender, commandString) -> sender.hasPermission("grim.sendalert"));

        var string = ArgumentType.Literal("string");
        addSyntax((sender, context) -> {
            String string1 = context.get(string);
            string1 = MessageUtil.format(string1);

            for (Player bukkitPlayer : GrimAPI.INSTANCE.getAlertManager().getEnabledAlerts()) {
                bukkitPlayer.sendMessage(Component.text(string1));
            }

            if (GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("alerts.print-to-console", true)) {
                LogUtil.console(string1); // Print alert to console
            }
        }, string);
    }
}

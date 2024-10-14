package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrimProfile extends Command {
    public GrimProfile() {
        super("profile");
        setCondition((sender, commandString) -> sender.hasPermission("grim.profile"));
        addSyntax((sender, context) -> {
            sender.sendMessage(MessageUtil.format("&eUsage: &agrim profile &f<player>"));
        });

        var targetArgument = ArgumentType.Entity("target");
        addSyntax((sender, context) -> {
            Player target = context.get(targetArgument).findFirstPlayer(sender);

            GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(target);
            if (grimPlayer == null) {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-found", "%prefix% &cPlayer is exempt or offline!");
                sender.sendMessage(MessageUtil.format(message));
                return;
            }

            for (String message : GrimAPI.INSTANCE.getConfigManager().getConfig().getStringList("profile")) {
                message = GrimAPI.INSTANCE.getExternalAPI().replaceVariables(grimPlayer, message, true);
                sender.sendMessage(message);
            }
        }, targetArgument);
    }
}

package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrimStopSpectating extends Command {

    public GrimStopSpectating() {
        super("stopspectating");
        setCondition((sender, commandString) -> {
            if (!(sender instanceof Player)) {
                sender.sendMessage("&cError: Console may not execute this command.");
                return false;
            }
            return sender.hasPermission("grim.spectate");
        });

        var stringArgument = ArgumentType.String("here");
        addSyntax((sender, context) -> {
            String string = context.get(stringArgument);
            Player player = (Player) sender;
            if (GrimAPI.INSTANCE.getSpectateManager().isSpectating(player.getUuid())) {
                boolean teleportBack = string == null || !string.equalsIgnoreCase("here");
                GrimAPI.INSTANCE.getSpectateManager().disable(player, teleportBack);
            } else {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("cannot-spectate-return", "%prefix% &cYou can only do this after spectating a player.");
                sender.sendMessage(MessageUtil.format(message));
            }
        }, stringArgument);

        setDefaultExecutor((sender, context) -> {
            Player player = (Player) sender;
            if (GrimAPI.INSTANCE.getSpectateManager().isSpectating(player.getUuid())) {
                GrimAPI.INSTANCE.getSpectateManager().disable(player, true);
            } else {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("cannot-spectate-return", "%prefix% &cYou can only do this after spectating a player.");
                sender.sendMessage(MessageUtil.format(message));
            }
        });
    }
}

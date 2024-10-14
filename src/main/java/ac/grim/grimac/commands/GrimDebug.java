package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import ac.grim.grimac.utils.nmsutil.ChatUtil;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class GrimDebug {

    private GrimDebug() {}

    private static GrimPlayer parseTarget(CommandSender sender, Player player, Player target) {
        Player targetPlayer = target == null ? player : target;
        if (player == null && target == null) {
            sender.sendMessage(ChatUtil.translateAlternateColorCodes("&cYou must specify a target as the console!"));
            return null;
        }

        GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(targetPlayer);
        if (grimPlayer == null) {
            sender.sendMessage(ChatUtil.translateAlternateColorCodes("&cThis player is exempt from all checks!"));

            boolean isExempt = GrimAPI.INSTANCE.getPlayerDataManager().shouldCheck(targetPlayer);
            if (!isExempt) {
                sender.sendMessage(ChatUtil.translateAlternateColorCodes("&cUser connection state: " + targetPlayer.getPlayerConnection().getConnectionState().name()));
            }
        }

        return grimPlayer;
    }

    public static class GrimPlayerDebug extends Command {
        public GrimPlayerDebug() {
            super("debug");
            setCondition((sender, commandString) -> sender.hasPermission("grim.debug"));
            addSyntax((sender, context) -> {
                sender.sendMessage(MessageUtil.format("&eUsage: &agrim debug &f<player>"));
            });

            var playerArgument = ArgumentType.Entity("target");
            addSyntax((sender, context) -> {
                Player player = null;
                if (sender instanceof Player) player = (Player) sender;

                Player target = context.get(playerArgument).findFirstPlayer(sender);

                GrimPlayer grimPlayer = parseTarget(sender, player, target);
                if (grimPlayer == null) return;

                if (sender instanceof ConsoleSender) { // Just debug to console to reduce complexity...
                    grimPlayer.checkManager.getDebugHandler().toggleConsoleOutput();
                } else if (sender instanceof Player) { // This sender is a player
                    grimPlayer.checkManager.getDebugHandler().toggleListener((Player) sender);
                }
            }, playerArgument);
        }
    }

    public static class GrimConsoleDebug extends Command {
        public GrimConsoleDebug() {
            super("consoledebug");
            setCondition((sender, commandString) -> sender.hasPermission("grim.consoledebug"));
            addSyntax((sender, context) -> {
                sender.sendMessage(MessageUtil.format("&eUsage: &agrim consoledebug &f<player>"));
            });

            var playerArgument = ArgumentType.Entity("target");
            addSyntax((sender, context) -> {
                Player player = null;
                if (sender instanceof Player) player = (Player) sender;

                Player target = context.get(playerArgument).findFirstPlayer(sender);

                GrimPlayer grimPlayer = parseTarget(sender, player, target);
                if (grimPlayer == null) return;

                boolean isOutput = grimPlayer.checkManager.getDebugHandler().toggleConsoleOutput();

                sender.sendMessage("Console output for " + grimPlayer.bukkitPlayer.getName() + " is now " + isOutput);
            }, playerArgument);
        }
    }
}

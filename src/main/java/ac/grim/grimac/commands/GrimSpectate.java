package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import ac.grim.grimac.utils.anticheat.MultiLibUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class GrimSpectate extends Command {
    public GrimSpectate() {
        super("spectate");

        //setCondition((sender, commandString) -> sender.hasPermission("grim.sendalert") && sender instanceof Player);
        var playerArgument = ArgumentType.Entity("target");
        addSyntax((sender, context) -> {
            EntityFinder targetString = context.get(playerArgument);
            Player target = targetString.findFirstPlayer(sender);
            Player player = (Player) sender;

            if (target != null && target.getUuid().equals(player.getUuid())) {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("cannot-run-on-self", "%prefix% &cYou cannot use this command on yourself!");
                sender.sendMessage(MessageUtil.format(message));
                return;
            }

            if (target == null || (MultiLibUtil.isExternalPlayer(target))) {
                String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("player-not-this-server", "%prefix% &cThis player isn't on this server!");
                sender.sendMessage(MessageUtil.format(message));
                return;
            }
            //hide player from tab list
            if (GrimAPI.INSTANCE.getSpectateManager().enable(player)) {
                GrimPlayer grimPlayer = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(player);
                if (grimPlayer != null) {
                    String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("spectate-return", "\n%prefix% &fClick here to return to previous location\n");
                    grimPlayer.bukkitPlayer.sendMessage(
                            LegacyComponentSerializer.legacy('&')
                                    .deserialize(MessageUtil.formatWithNoColor(message))
                                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/grim stopspectating"))
                                    .hoverEvent(HoverEvent.showText(Component.text("/grim stopspectating")))
                    );
                }
            }

            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(target.getPosition());
        }, playerArgument);
    }
}

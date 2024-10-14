package ac.grim.grimac.commands;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import net.minestom.server.command.builder.Command;

public class GrimReload extends Command {
    public GrimReload() {
        super("reload");
        setCondition((sender, commandString) -> sender.hasPermission("grim.reload"));

        addSyntax((sender, context) -> {
            sender.sendMessage(MessageUtil.format("%prefix% &7Reloading config..."));
            GrimAPI.INSTANCE.getExternalAPI().reloadAsync().exceptionally(throwable -> false)
                    .thenAccept(bool -> {
                        if (bool) {
                            sender.sendMessage(MessageUtil.format("%prefix% &fConfig has been reloaded."));
                        } else {
                            sender.sendMessage(MessageUtil.format("%prefix% &cFailed to reload config."));
                        }
                    });
        });
    }
}

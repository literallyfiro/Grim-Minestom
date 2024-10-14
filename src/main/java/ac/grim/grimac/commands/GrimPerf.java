package ac.grim.grimac.commands;

import ac.grim.grimac.predictionengine.MovementCheckRunner;
import ac.grim.grimac.utils.nmsutil.ChatUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;

public class GrimPerf extends Command {

    public GrimPerf() {
        super("perf", "performance");
        //setCondition((sender, commandString) -> sender.hasPermission("grim.performance"));

        setDefaultExecutor((sender, context) -> {
            double millis = MovementCheckRunner.predictionNanos / 1000000;
            double longMillis = MovementCheckRunner.longPredictionNanos / 1000000;

            sender.sendMessage(ChatUtil.translateAlternateColorCodes("&7Milliseconds per prediction (avg. 500): &f" + millis));
            sender.sendMessage(ChatUtil.translateAlternateColorCodes("&7Milliseconds per prediction (avg. 20k): &f" + longMillis));
        });
    }
}

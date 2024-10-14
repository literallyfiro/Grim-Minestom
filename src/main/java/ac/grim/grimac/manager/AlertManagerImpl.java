package ac.grim.grimac.manager;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.MessageUtil;
import lombok.Getter;
import net.minestom.server.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AlertManagerImpl {
    @Getter
    private final Set<Player> enabledAlerts = new CopyOnWriteArraySet<>(new HashSet<>());
    @Getter
    private final Set<Player> enabledVerbose = new CopyOnWriteArraySet<>(new HashSet<>());

    public boolean hasAlertsEnabled(Player player) {
        return enabledAlerts.contains(player);
    }

    public void toggleAlerts(Player player) {
        if (!enabledAlerts.remove(player)) {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("alerts-enabled", "%prefix% &fAlerts enabled");
            alertString = MessageUtil.format(alertString);
            player.sendMessage(alertString);
            enabledAlerts.add(player);
        } else {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("alerts-disabled", "%prefix% &fAlerts disabled");
            alertString = MessageUtil.format(alertString);
            player.sendMessage(alertString);
        }
    }

    public boolean hasVerboseEnabled(Player player) {
        return enabledVerbose.contains(player);
    }

    public void toggleVerbose(Player player) {
        if (!enabledVerbose.remove(player)) {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("verbose-enabled", "%prefix% &fVerbose enabled");
            alertString = MessageUtil.format(alertString);
            player.sendMessage(alertString);
            enabledVerbose.add(player);
        } else {
            String alertString = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("verbose-disabled", "%prefix% &fVerbose disabled");
            alertString = MessageUtil.format(alertString);
            player.sendMessage(alertString);
        }
    }

    public void handlePlayerQuit(Player player) {
        enabledAlerts.remove(player);
        enabledVerbose.remove(player);
    }
}

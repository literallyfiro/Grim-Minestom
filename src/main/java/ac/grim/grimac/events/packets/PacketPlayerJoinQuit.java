package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.minestom.EventPriority;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.login.LoginSuccessPacket;

public class PacketPlayerJoinQuit {

    public PacketPlayerJoinQuit(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-player-join-quit");
        node.setPriority(EventPriority.NORMAL.ordinal());

        globalNode.addListener(PlayerPacketOutEvent.class, this::onPacketSend);
        globalNode.addListener(AsyncPlayerPreLoginEvent.class, this::onUserConnect);
        globalNode.addListener(AsyncPlayerConfigurationEvent.class, this::onUserLogin);
        globalNode.addListener(PlayerDisconnectEvent.class, this::onUserDisconnect);

        globalNode.addChild(node);
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof LoginSuccessPacket) {
            // Do this after send to avoid sending packets before the PLAY state
            event.getTasksAfterSend().add(() -> GrimAPI.INSTANCE.getPlayerDataManager().addUser(event.getPlayer()));
        }
    }

    public void onUserConnect(AsyncPlayerPreLoginEvent event) {
        // todo minestom this
        // Player connected too soon, perhaps late bind is off
        // Don't kick everyone on reload
//        if (event.getPlayer().getPlayerConnection().getConnectionState() == ConnectionState.PLAY && !GrimAPI.INSTANCE.getPlayerDataManager().exemptUsers.contains(event.getPlayer())) {
//            event.setCancelled(true);
//        }
    }

    public void onUserLogin(AsyncPlayerConfigurationEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("grim.alerts") && player.hasPermission("grim.alerts.enable-on-join")) {
            GrimAPI.INSTANCE.getAlertManager().toggleAlerts(player);
        }
        if (player.hasPermission("grim.spectate") && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("spectators.hide-regardless", false)) {
            GrimAPI.INSTANCE.getSpectateManager().onLogin(player);
        }
    }

    public void onUserDisconnect(PlayerDisconnectEvent event) {
        GrimAPI.INSTANCE.getPlayerDataManager().remove(event.getPlayer());
        GrimAPI.INSTANCE.getPlayerDataManager().exemptUsers.remove(event.getPlayer());

        GrimAPI.INSTANCE.getAlertManager().handlePlayerQuit(event.getPlayer());
        GrimAPI.INSTANCE.getSpectateManager().onQuit(event.getPlayer());
    }
}

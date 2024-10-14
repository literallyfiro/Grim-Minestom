package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.utils.anticheat.LogUtil;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProxyAlertMessenger {
    private static boolean usingProxy;

    public ProxyAlertMessenger() {
        usingProxy = BungeeCordProxy.isEnabled() || VelocityProxy.isEnabled();

        if (usingProxy) {
            LogUtil.info("Registering an outgoing plugin channel...");
            //GrimAPI.INSTANCE.getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(GrimAPI.INSTANCE.getPlugin(), "BungeeCord");
        }
    }

    public void onPacketReceive(final PlayerPacketEvent event) {
        if ((!(event.getPacket() instanceof ClientPluginMessagePacket wrapper)) || !ProxyAlertMessenger.canReceiveAlerts())
            return;

        if (!wrapper.channel().equals("BungeeCord") && !wrapper.channel().equals("bungeecord:main"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(wrapper.data());

        if (!in.readUTF().equals("GRIMAC")) return;

        final String alert;
        byte[] messageBytes = new byte[in.readShort()];
        in.readFully(messageBytes);

        try {
            alert = new DataInputStream(new ByteArrayInputStream(messageBytes)).readUTF();
        } catch (IOException exception) {
            LogUtil.error("Something went wrong whilst reading an alert forwarded from another server!");
            exception.printStackTrace();
            return;
        }

        for (Player bukkitPlayer : GrimAPI.INSTANCE.getAlertManager().getEnabledAlerts())
            bukkitPlayer.sendMessage(alert);
    }

    public static void sendPluginMessage(String message) {
        if (!canSendAlerts())
            return;

        ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ONLINE");
        out.writeUTF("GRIMAC");

        try {
            new DataOutputStream(messageBytes).writeUTF(message);
        } catch (IOException exception) {
            LogUtil.error("Something went wrong whilst forwarding an alert to other servers!");
            exception.printStackTrace();
            return;
        }

        out.writeShort(messageBytes.toByteArray().length);
        out.write(messageBytes.toByteArray());

        for (@NotNull Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            onlinePlayer.sendPluginMessage("BungeeCord", out.toByteArray());
        }
    }

    public static boolean canSendAlerts() {
        return usingProxy && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("alerts.proxy.send", false) && !MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty();
    }

    public static boolean canReceiveAlerts() {
        return usingProxy && GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("alerts.proxy.receive", false) && !GrimAPI.INSTANCE.getAlertManager().getEnabledAlerts().isEmpty();
    }

}

package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.impl.exploit.ExploitA;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;

public class ClientBrand extends Check implements PacketCheck {
    String brand = "vanilla";
    boolean hasBrand = false;

    public ClientBrand(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPluginMessagePacket packet) {
            String channelName = packet.channel();
            handle(channelName, packet.data());
        }
    }

    public void handle(String channel, byte[] data) {
        if (channel.equalsIgnoreCase("minecraft:brand") || // 1.13+
                channel.equals("MC|Brand")) { // 1.12
            if (data.length > 64 || data.length == 0) {
                brand = "sent " + data.length + " bytes as brand";
            } else if (!hasBrand) {
                byte[] minusLength = new byte[data.length - 1];
                System.arraycopy(data, 1, minusLength, 0, minusLength.length);

                brand = new String(minusLength).replace(" (Velocity)", ""); //removes velocity's brand suffix
                if (player.checkManager.getPrePredictionCheck(ExploitA.class).checkString(brand)) brand = "sent log4j";
                if (!GrimAPI.INSTANCE.getConfigManager().isIgnoredClient(brand)) {
                    String message = GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("client-brand-format", "%prefix% &f%player% joined using %brand%");
                    message = GrimAPI.INSTANCE.getExternalAPI().replaceVariables(getPlayer(), message, true);
                    // sendMessage is async safe while broadcast isn't due to adventure
                    for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("grim.brand")) {
                            onlinePlayer.sendMessage(message);
                        }
                    }
                }
            }

            hasBrand = true;
        }
    }

    public String getBrand() {
        return brand;
    }
}

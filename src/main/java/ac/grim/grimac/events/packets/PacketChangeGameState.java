package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.play.ChangeGameStatePacket;

public class PacketChangeGameState extends Check implements PacketCheck {
    public PacketChangeGameState(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketSend(final PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof ChangeGameStatePacket packet) {
            if (packet.reason() == ChangeGameStatePacket.Reason.CHANGE_GAMEMODE) {
                player.sendTransaction();

                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                    // Bukkit's gamemode order is unreliable, so go from int -> packetevents -> bukkit
                    GameMode previous = player.gamemode;
                    int gamemode = (int) packet.value();

                    // Some plugins send invalid values such as -1, this is what the client does
                    if (gamemode < 0 || gamemode >= GameMode.values().length) {
                        System.out.println("Gamemode1: " + gamemode);
                        player.gamemode = GameMode.SURVIVAL;
                    } else {
                        System.out.println("Gamemode: " + gamemode);
                        player.gamemode = GameMode.values()[gamemode];
                    }

                    if (previous == GameMode.SPECTATOR && player.gamemode != GameMode.SPECTATOR) {
                        GrimAPI.INSTANCE.getSpectateManager().handlePlayerStopSpectating(player.playerUUID);
                    }
                });
            }
        }
    }
}

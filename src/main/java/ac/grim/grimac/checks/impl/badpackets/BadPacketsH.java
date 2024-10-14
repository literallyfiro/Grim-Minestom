package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientAnimationPacket;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;

@CheckData(name = "BadPacketsH")
public class BadPacketsH extends Check implements PacketCheck {

    // 1.9 packet order: INTERACT -> ANIMATION
    // 1.8 packet order: ANIMATION -> INTERACT
    // I personally think 1.8 made much more sense. You swing and THEN you hit!
    private boolean sentAnimation = player.getClientVersion().isNewerThan(ClientVersion.V_1_8);

    public BadPacketsH(final GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientAnimationPacket) {
            sentAnimation = true;
        } else if (event.getPacket() instanceof ClientInteractEntityPacket packet) {
            if (!(packet.type() instanceof ClientInteractEntityPacket.Attack)) return;

            // There is a "bug" in ViaRewind
            // 1.8 packet order: ANIMATION -> INTERACT
            // 1.9 packet order: INTERACT -> ANIMATION
            // ViaRewind, on 1.9+ servers, delays a 1.8 client's ANIMATION to be after INTERACT (but before flying).
            // Which means we see 1.9 packet order for 1.8 clients
            // Due to ViaRewind also delaying the swings, we then see packet order above 20CPS like:
            // INTERACT -> INTERACT -> ANIMATION -> ANIMATION
            // I will simply disable this check for 1.8- clients on 1.9+ servers as I can't be bothered to find a way around this.
            // Stop supporting such old clients on modern servers!
            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) return;

            if (!sentAnimation && flagAndAlert()) {
                event.setCancelled(true);
                player.onPacketCancel();
            }

            sentAnimation = false;
        }
    }
}

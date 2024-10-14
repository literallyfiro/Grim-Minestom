package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientTabCompletePacket;

@CheckData(name = "CrashH")
public class CrashH extends Check implements PacketCheck {

    public CrashH(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientTabCompletePacket wrapper) {
            String text = wrapper.text();
            final int length = text.length();
            // general length limit
            if (length > 256) {
                if (shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
                flagAndAlert("(length) length=" + length);
                return;
            }
            // paper's patch
            final int index;
            if (text.length() > 64 && ((index = text.indexOf(' ')) == -1 || index >= 64)) {
                if (shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
                flagAndAlert("(invalid) length=" + length);
                return;
            }
        }
    }


}

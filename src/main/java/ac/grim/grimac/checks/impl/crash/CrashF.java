package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;

@CheckData(name = "CrashF")
public class CrashF extends Check implements PacketCheck {

    public CrashF(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientClickWindowPacket click) {
            int clickType = click.clickType().ordinal();
            int button = click.button();
            int windowId = click.windowId();
            int slot = click.slot();

            if ((clickType == 1 || clickType == 2) && windowId >= 0 && button < 0) {
                if (flagAndAlert("clickType=" + clickType + " button=" + button)) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }

            else if (windowId >= 0 && clickType == 2 && slot < 0) {
                if (flagAndAlert("clickType=" + clickType + " button=" + button + " slot=" + slot)) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }

        }
    }

}

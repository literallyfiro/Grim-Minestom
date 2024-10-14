package ac.grim.grimac.checks.impl.badpackets;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;

@CheckData(name = "BadPacketsP", experimental = true)
public class BadPacketsP extends Check implements PacketCheck {

    public BadPacketsP(GrimPlayer playerData) {
        super(playerData);
    }

    private int containerType = -1;
    private int containerId = -1;

    @Override
    public void onPacketSend(final PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof OpenWindowPacket window) {
            this.containerType = window.windowType();
            this.containerId = window.windowId();
        }
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientClickWindowPacket wrapper) {
            int clickType = wrapper.clickType().ordinal();
            int button = wrapper.button();

            boolean flag = false;

            //TODO: Adjust for containers
            switch (clickType) {
                case 0:
                case 1:
                case 4:
                    if (button != 0 && button != 1) flag = true;
                    break;
                case 2:
                    if ((button > 8 || button < 0) && button != 40) flag = true;
                    break;
                case 3:
                    if (button != 2) flag = true;
                    break;
                case 5:
                    if (button == 3 || button == 7 || button > 10 || button < 0) flag = true;
                    break;
                case 6:
                    if (button != 0) flag = true;
                    break;
            }

            //Allowing this to false flag to debug and find issues faster
            if (flag) {
                if (flagAndAlert("clickType=" + clickType + " button=" + button + (wrapper.windowId() == containerId ? " container=" + containerType : "")) && shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}

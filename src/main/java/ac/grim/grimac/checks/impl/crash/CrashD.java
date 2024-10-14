package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.inventory.MenuType;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;

@CheckData(name = "CrashD", experimental = false)
public class CrashD extends Check implements PacketCheck {

    public CrashD(GrimPlayer playerData) {
        super(playerData);
    }

    private MenuType type = MenuType.UNKNOWN;
    private int lecternId = -1;

    @Override
    public void onPacketSend(final PlayerPacketOutEvent event) {
        if (event.getPacket() instanceof OpenWindowPacket window && isSupportedVersion()) {
            this.type = MenuType.getMenuType(window.windowType());
            if (type == MenuType.LECTERN) lecternId = window.windowId();
        }
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientClickWindowPacket click && isSupportedVersion()) {
            int clickType = click.clickType().ordinal();
            int button = click.button();
            int windowId = click.windowId();

            if (type == MenuType.LECTERN && windowId > 0 && windowId == lecternId) {
                if (flagAndAlert("clickType=" + clickType + " button=" + button)) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }

    private boolean isSupportedVersion() {
//        return PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_14);
        return true;
    }

}

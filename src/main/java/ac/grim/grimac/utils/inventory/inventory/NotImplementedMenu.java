package ac.grim.grimac.utils.inventory.inventory;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.Inventory;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;

public class NotImplementedMenu extends AbstractContainerMenu {
    public NotImplementedMenu(GrimPlayer player, Inventory playerInventory) {
        super(player, playerInventory);
        player.getInventory().isPacketInventoryActive = false;
        player.getInventory().needResend = true;
    }

    @Override
    public void doClick(int button, int slotID, ClientClickWindowPacket.ClickType clickType) {

    }
}

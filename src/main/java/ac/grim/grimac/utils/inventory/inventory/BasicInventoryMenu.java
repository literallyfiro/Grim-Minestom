package ac.grim.grimac.utils.inventory.inventory;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.Inventory;
import ac.grim.grimac.utils.inventory.InventoryStorage;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.inventory.slot.Slot;
import net.minestom.server.item.ItemStack;

public class BasicInventoryMenu extends AbstractContainerMenu {
    int rows;

    public BasicInventoryMenu(GrimPlayer player, Inventory playerInventory, int rows) {
        super(player, playerInventory);
        this.rows = rows;

        InventoryStorage containerStorage = new InventoryStorage(rows * 9);

        for (int i = 0; i < rows * 9; i++) {
            addSlot(new Slot(containerStorage, i));
        }

        addFourRowPlayerInventory();
    }

    @Override
    public ModifiableItemStack quickMoveStack(int slotID) {
        ModifiableItemStack itemstack = ModifiableItemStack.EMPTY;
        Slot slot = this.slots.get(slotID);
        if (slot != null && slot.hasItem()) {
            ModifiableItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotID < this.rows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.rows * 9, this.slots.size(), true)) {
                    return ModifiableItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.rows * 9, false)) {
                return ModifiableItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ModifiableItemStack.EMPTY);
            }
        }

        return itemstack;
    }
}

package ac.grim.grimac.utils.inventory.inventory;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.Inventory;
import ac.grim.grimac.utils.inventory.InventoryStorage;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.inventory.slot.Slot;

public class HopperMenu extends AbstractContainerMenu {
    public HopperMenu(GrimPlayer player, Inventory playerInventory) {
        super(player, playerInventory);

        InventoryStorage containerStorage = new InventoryStorage(5);
        for (int i = 0; i < 5; i++) {
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
            if (slotID < 5) {
                if (!this.moveItemStackTo(itemstack1, 5, this.slots.size(), true)) {
                    return ModifiableItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 5, false)) {
                return ModifiableItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ModifiableItemStack.EMPTY);
            }
        }

        return itemstack;
    }

}

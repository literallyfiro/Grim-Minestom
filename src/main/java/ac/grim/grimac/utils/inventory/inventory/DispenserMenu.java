package ac.grim.grimac.utils.inventory.inventory;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.inventory.Inventory;
import ac.grim.grimac.utils.inventory.InventoryStorage;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.inventory.slot.Slot;

public class DispenserMenu extends AbstractContainerMenu {
    public DispenserMenu(GrimPlayer player, Inventory playerInventory) {
        super(player, playerInventory);

        InventoryStorage containerStorage = new InventoryStorage(9);

        for (int i = 0; i < 9; i++) {
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
            if (slotID < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ModifiableItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                return ModifiableItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ModifiableItemStack.EMPTY);
            }

            if (itemstack1.getAmount() == itemstack.getAmount()) {
                return ModifiableItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1.getItemStack());
        }

        return itemstack;
    }
}

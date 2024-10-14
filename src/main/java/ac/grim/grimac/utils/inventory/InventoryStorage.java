package ac.grim.grimac.utils.inventory;

import net.minestom.server.item.ItemStack;

public class InventoryStorage {
    protected ModifiableItemStack[] items;
    int size;

    public InventoryStorage(int size) {
        this.items = new ModifiableItemStack[size];
        this.size = size;

        for (int i = 0; i < size; i++) {
            items[i] = ModifiableItemStack.EMPTY;
        }
    }

    public int getSize() {
        return size;
    }

    public void setItem(int item, ModifiableItemStack stack) {
        items[item] = stack == null ? ModifiableItemStack.EMPTY : stack;
    }

    public ModifiableItemStack getItem(int index) {
        return items[index];
    }

    public ModifiableItemStack removeItem(int slot, int amount) {
        return slot >= 0 && slot < items.length && !items[slot].isEmpty() && amount > 0 ? items[slot].split2(amount) : new ModifiableItemStack(ItemStack.AIR);
    }

    public int getMaxStackSize() {
        return 64;
    }
}

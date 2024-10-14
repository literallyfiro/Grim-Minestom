package ac.grim.grimac.utils.inventory;

import net.minestom.server.item.ItemStack;

public class NBTHelper {
    public static int getBaseRepairCost(ItemStack itemStack) {
        return itemStack.toItemNBT().getInt("RepairCost");
    }
}

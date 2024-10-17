package ac.grim.grimac.utils.inventory.slot;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.inventory.EquipmentType;
import ac.grim.grimac.utils.inventory.InventoryStorage;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import net.minestom.server.entity.GameMode;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.enchant.Enchantment;

public class EquipmentSlot extends Slot {
    EquipmentType type;

    public EquipmentSlot(EquipmentType type, InventoryStorage menu, int slot) {
        super(menu, slot);
        this.type = type;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack p_39746_) {
        return type == EquipmentType.getEquipmentSlotForItem(p_39746_);
    }

    public boolean mayPickup(GrimPlayer p_39744_) {
        ModifiableItemStack itemstack = this.getItem();
        return (itemstack.isEmpty() || p_39744_.gamemode == GameMode.CREATIVE || EnchantmentUtils.getEnchantmentLevel(itemstack.getItemStack(), Enchantment.BINDING_CURSE) == 0) && super.mayPickup(p_39744_);
    }
}

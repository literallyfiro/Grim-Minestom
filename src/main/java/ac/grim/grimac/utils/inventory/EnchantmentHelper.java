package ac.grim.grimac.utils.inventory;

import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.latency.CompensatedInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

public class EnchantmentHelper {
    public static boolean isCurse(DynamicRegistry.Key<Enchantment> type) {
        return type == Enchantment.BINDING_CURSE || type == Enchantment.VANISHING_CURSE;
    }

    // Some enchants work on any armor piece but only the maximum level counts
    public static int getMaximumEnchantLevel(CompensatedInventory inventory, DynamicRegistry.Key<Enchantment> enchantmentType) {
        int maxEnchantLevel = 0;

        ModifiableItemStack helmet = inventory.getHelmet();
        if (helmet.getItemStack() != ItemStack.AIR) {
            maxEnchantLevel = Math.max(maxEnchantLevel, EnchantmentUtils.getEnchantmentLevel(helmet.getItemStack(), enchantmentType));
        }

        ModifiableItemStack chestplate = inventory.getChestplate();
        if (chestplate.getItemStack() != ItemStack.AIR) {
            maxEnchantLevel = Math.max(maxEnchantLevel, EnchantmentUtils.getEnchantmentLevel(chestplate.getItemStack(), enchantmentType));
        }

        ModifiableItemStack leggings = inventory.getLeggings();
        if (leggings.getItemStack() != ItemStack.AIR) {
            maxEnchantLevel = Math.max(maxEnchantLevel, EnchantmentUtils.getEnchantmentLevel(leggings.getItemStack(), enchantmentType));
        }

        ModifiableItemStack boots = inventory.getBoots();
        if (boots.getItemStack() != ItemStack.AIR) {
            maxEnchantLevel = Math.max(maxEnchantLevel, EnchantmentUtils.getEnchantmentLevel(boots.getItemStack(), enchantmentType));
        }

        return maxEnchantLevel;
    }
}

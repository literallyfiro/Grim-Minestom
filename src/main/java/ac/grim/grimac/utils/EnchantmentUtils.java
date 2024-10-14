package ac.grim.grimac.utils;

import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

import java.util.Map;

public class EnchantmentUtils {
    public static int getEnchantmentLevel(ItemStack item, DynamicRegistry.Key<Enchantment> enchantmentType) {
        EnchantmentList enchantmentList = item.get(ItemComponent.ENCHANTMENTS);
        if (enchantmentList != null) {
            for (Map.Entry<DynamicRegistry.Key<Enchantment>, Integer> entry : enchantmentList.enchantments().entrySet()) {
                DynamicRegistry.Key<Enchantment> enchantment = entry.getKey();
                int level = entry.getValue();
                if (enchantment == enchantmentType) {
                    return level;
                }
            }
        }
        return 0;
    }
}

package ac.grim.grimac.utils.inventory;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public enum EquipmentType {
    MAINHAND,
    OFFHAND,
    FEET,
    LEGS,
    CHEST,
    HEAD;

    public static EquipmentType byArmorID(int id) {
        return switch (id) {
            case 0 -> HEAD;
            case 1 -> CHEST;
            case 2 -> LEGS;
            case 3 -> FEET;
            default -> MAINHAND;
        };
    }

    public static EquipmentType getEquipmentSlotForItem(ItemStack p_147234_) {
        Material item = p_147234_.material();
        if (item == Material.CARVED_PUMPKIN || (item.namespace().key().asString().contains("SKULL") ||
                (item.namespace().key().asString().contains("HEAD") && !item.namespace().key().asString().contains("PISTON")))) {
            return HEAD;
        }
        if (item == Material.ELYTRA) {
            return CHEST;
        }
        if (item == Material.LEATHER_BOOTS || item == Material.CHAINMAIL_BOOTS
                || item == Material.IRON_BOOTS || item == Material.DIAMOND_BOOTS
                || item == Material.GOLDEN_BOOTS || item == Material.NETHERITE_BOOTS) {
            return FEET;
        }
        if (item == Material.LEATHER_LEGGINGS || item == Material.CHAINMAIL_LEGGINGS
                || item == Material.IRON_LEGGINGS || item == Material.DIAMOND_LEGGINGS
                || item == Material.GOLDEN_LEGGINGS || item == Material.NETHERITE_LEGGINGS) {
            return LEGS;
        }
        if (item == Material.LEATHER_CHESTPLATE || item == Material.CHAINMAIL_CHESTPLATE
                || item == Material.IRON_CHESTPLATE || item == Material.DIAMOND_CHESTPLATE
                || item == Material.GOLDEN_CHESTPLATE || item == Material.NETHERITE_CHESTPLATE) {
            return CHEST;
        }
        if (item == Material.LEATHER_HELMET || item == Material.CHAINMAIL_HELMET
                || item == Material.IRON_HELMET || item == Material.DIAMOND_HELMET
                || item == Material.GOLDEN_HELMET || item == Material.NETHERITE_HELMET) {
            return HEAD;
        }
        return Material.SHIELD == item ? OFFHAND : MAINHAND;
    }

    public boolean isArmor() {
        return this == FEET || this == LEGS || this == CHEST || this == HEAD;
    }

    public int getIndex() {
        return switch (this) {
            case MAINHAND, FEET -> 0;
            case OFFHAND, LEGS -> 1;
            case CHEST -> 2;
            case HEAD -> 3;
            default -> -1;
        };
    }
}

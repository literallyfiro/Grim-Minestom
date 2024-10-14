package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.vector.MutableVector;
import net.minestom.server.item.Material;
import net.minestom.server.item.enchant.Enchantment;

public class Riptide {
    public static MutableVector getRiptideVelocity(GrimPlayer player) {
        ModifiableItemStack main = player.getInventory().getHeldItem();
        ModifiableItemStack off = player.getInventory().getOffHand();

        int j;
        if (main.getType() == Material.TRIDENT) {
            j = EnchantmentUtils.getEnchantmentLevel(main.getItemStack(), Enchantment.RIPTIDE);
        } else if (off.getType() == Material.TRIDENT) {
            j = EnchantmentUtils.getEnchantmentLevel(off.getItemStack(), Enchantment.RIPTIDE);
        } else {
            return new MutableVector(); // Can't riptide
        }

        float f7 = player.xRot;
        float f = player.yRot;
        float f1 = -player.trigHandler.sin(f7 * ((float) Math.PI / 180F)) * player.trigHandler.cos(f * ((float) Math.PI / 180F));
        float f2 = -player.trigHandler.sin(f * ((float) Math.PI / 180F));
        float f3 = player.trigHandler.cos(f7 * ((float) Math.PI / 180F)) * player.trigHandler.cos(f * ((float) Math.PI / 180F));
        float f4 = (float) Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
        float f5 = 3.0F * ((1.0F + j) / 4.0F);
        f1 = f1 * (f5 / f4);
        f2 = f2 * (f5 / f4);
        f3 = f3 * (f5 / f4);

        // If the player collided vertically with the 1.199999F pushing movement, then the Y additional movement was added
        // (We switched the order around as our prediction engine isn't designed for the proper implementation)
        if (player.verticalCollision) return new MutableVector(f1, 0, f3);

        return new MutableVector(f1, f2, f3);
    }
}

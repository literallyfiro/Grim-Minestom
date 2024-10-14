package ac.grim.grimac.utils.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

@Setter
@Getter
@AllArgsConstructor
public class ModifiableItemStack {

    public static final ModifiableItemStack EMPTY = new ModifiableItemStack(ItemStack.AIR);

    private ItemStack itemStack;

    public void grow(int amount) {
        itemStack = itemStack.withAmount(itemStack.amount() + amount);
    }

    public int getAmount() {
        return itemStack.amount();
    }

    public void setAmount(int amount) {
        itemStack = itemStack.withAmount(amount);
    }

    public Material getType() {
        return itemStack.material();
    }

    public boolean isEmpty() {
        return itemStack.isAir();
    }

    public void split(int amount) {
        this.setAmount(this.getAmount() - amount);
    }

    public void shrink(int amount) {
        split(amount);
    }

    public int getMaxStackSize() {
        return itemStack.material().maxStackSize();
    }

    public boolean isDamaged() {
        return itemStack.get(ItemComponent.DAMAGE, 0) > 0;
    }

    public int getDamageValue() {
        return itemStack.get(ItemComponent.DAMAGE, 0);
    }

    public int getMaxDamage() {
        return itemStack.get(ItemComponent.MAX_DAMAGE, 0);
    }

    public boolean isSimilar(ModifiableItemStack stack) {
        return itemStack.isSimilar(stack.itemStack);
    }

    public ModifiableItemStack split2(int amount) {
        int i = Math.min(amount, getAmount());
        ModifiableItemStack stack = this.copy();
        stack.setAmount(i);
        this.shrink(i);
        return stack;
    }

    public ModifiableItemStack copy() {
        return new ModifiableItemStack(itemStack);
    }

}

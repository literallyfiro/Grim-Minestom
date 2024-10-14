package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.enums.FluidTag;
import ac.grim.grimac.utils.inventory.EnchantmentHelper;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.ItemTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.potion.PotionEffect;

import java.util.OptionalInt;

public class BlockBreakSpeed {
    public static double getBlockDamage(GrimPlayer player, Vector3i position) {
        // GET destroy speed
        // Starts with itemstack get destroy speed
        ModifiableItemStack tool = player.getInventory().getHeldItem();

        boolean isSword = tool.getType() == Material.WOODEN_SWORD || tool.getType() == Material.STONE_SWORD || tool.getType() == Material.IRON_SWORD || tool.getType() == Material.DIAMOND_SWORD || tool.getType() == Material.GOLDEN_SWORD || tool.getType() == Material.NETHERITE_SWORD;

        MinestomWrappedBlockState block = player.compensatedWorld.getWrappedBlockStateAt(position);
        double blockHardness = block.getType().registry().hardness();

        // 1.15.2 and below need this hack
        if ((block.getType() == Block.PISTON
                || block.getType() == Block.PISTON_HEAD
                || block.getType() == Block.STICKY_PISTON) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_15_2)) {
            blockHardness = 0.5f;
        }

        if (player.gamemode == GameMode.CREATIVE) {
            // A creative mode player cannot break things with a sword!
            if (isSword) {
                return 0;
            }
            // Instabreak
            return 1;
        }

        if (blockHardness == -1) return 0; // Unbreakable block

        boolean isCorrectToolForDrop = false;
        float speedMultiplier = 1.0F;

        // 1.13 and below need their own huge methods to support this...
        if (ItemTags.AXES.contains(tool.getType())) {
            isCorrectToolForDrop = BlockTags.MINEABLE_AXE.contains(block.getType());
        } else if (ItemTags.PICKAXES.contains(tool.getType())) {
            isCorrectToolForDrop = BlockTags.MINEABLE_PICKAXE.contains(block.getType());
        } else if (ItemTags.SHOVELS.contains(tool.getType())) {
            isCorrectToolForDrop = BlockTags.MINEABLE_SHOVEL.contains(block.getType());
        } else if (ItemTags.HOES.contains(tool.getType())) {
            isCorrectToolForDrop = BlockTags.MINEABLE_HOE.contains(block.getType());
        }

        if (isCorrectToolForDrop) {
            int tier = 0;

            boolean isWood = tool.getType() == Material.WOODEN_AXE || tool.getType() == Material.WOODEN_PICKAXE || tool.getType() == Material.WOODEN_SHOVEL || tool.getType() == Material.WOODEN_HOE;
            boolean isStone = tool.getType() == Material.STONE_AXE || tool.getType() == Material.STONE_PICKAXE || tool.getType() == Material.STONE_SHOVEL || tool.getType() == Material.STONE_HOE;
            boolean isIron = tool.getType() == Material.IRON_AXE || tool.getType() == Material.IRON_PICKAXE || tool.getType() == Material.IRON_SHOVEL || tool.getType() == Material.IRON_HOE;
            boolean isDiamond = tool.getType() == Material.DIAMOND_AXE || tool.getType() == Material.DIAMOND_PICKAXE || tool.getType() == Material.DIAMOND_SHOVEL || tool.getType() == Material.DIAMOND_HOE;
            boolean isGold = tool.getType() == Material.GOLDEN_AXE || tool.getType() == Material.GOLDEN_PICKAXE || tool.getType() == Material.GOLDEN_SHOVEL || tool.getType() == Material.GOLDEN_HOE;
            boolean isNetherite = tool.getType() == Material.NETHERITE_AXE || tool.getType() == Material.NETHERITE_PICKAXE || tool.getType() == Material.NETHERITE_SHOVEL || tool.getType() == Material.NETHERITE_HOE;

            if (isWood) { // Tier 0
                speedMultiplier = 2.0f;
            } else if (isStone) { // Tier 1
                speedMultiplier = 4.0f;
                tier = 1;
            } else if (isIron) { // Tier 2
                speedMultiplier = 6.0f;
                tier = 2;
            } else if (isDiamond) { // Tier 3
                speedMultiplier = 8.0f;
                tier = 3;
            } else if (isGold) { // Tier 0
                speedMultiplier = 12.0f;
            } else if (isNetherite) { // Tier 4
                speedMultiplier = 9.0f;
                tier = 4;
            }

            if (tier < 3 && BlockTags.NEEDS_DIAMOND_TOOL.contains(block.getType())) {
                isCorrectToolForDrop = false;
            } else if (tier < 2 && BlockTags.NEEDS_IRON_TOOL.contains(block.getType())) {
                isCorrectToolForDrop = false;
            } else if (tier < 1 && BlockTags.NEEDS_STONE_TOOL.contains(block.getType())) {
                isCorrectToolForDrop = false;
            }
        }

        // Shears can mine some blocks faster
        if (tool.getType() == Material.SHEARS) {
            isCorrectToolForDrop = true;

            if (block.getType() == Block.COBWEB || Materials.isLeaves(block.getType())) {
                speedMultiplier = 15.0f;
            } else if (BlockTags.WOOL.contains(block.getType())) {
                speedMultiplier = 5.0f;
            } else if (block.getType() == Block.VINE ||
                    block.getType() == Block.GLOW_LICHEN) {
                speedMultiplier = 2.0f;
            } else {
                isCorrectToolForDrop = block.getType() == Block.COBWEB ||
                        block.getType() == Block.REDSTONE_WIRE ||
                        block.getType() == Block.TRIPWIRE;
            }
        }

        // Swords can also mine some blocks faster
        if (isSword) {
            if (block.getType() == Block.COBWEB) {
                speedMultiplier = 15.0f;
            } else if (
                    // todo minestom
                    //block.getType().getMaterialType() == MaterialType.PLANT ||
                    BlockTags.LEAVES.contains(block.getType())
                    || block.getType() == Block.VINE
                    || block.getType() == Block.PUMPKIN
                    || block.getType() == Block.MELON) {
                speedMultiplier = 1.5f;
            }

            isCorrectToolForDrop = block.getType() == Block.COBWEB;
        }

        if (speedMultiplier > 1.0f) {
            if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21)) {
                speedMultiplier += (float) player.compensatedEntities.getSelf().getAttributeValue(Attribute.PLAYER_MINING_EFFICIENCY);
            } else {
                int digSpeed = EnchantmentUtils.getEnchantmentLevel(tool.getItemStack(), Enchantment.EFFICIENCY);
                if (digSpeed > 0) {
                    speedMultiplier += digSpeed * digSpeed + 1;
                }
            }
        }

        OptionalInt digSpeed = player.compensatedEntities.getPotionLevelForPlayer(PotionEffect.HASTE);
        OptionalInt conduit = player.compensatedEntities.getPotionLevelForPlayer(PotionEffect.CONDUIT_POWER);

        if (digSpeed.isPresent() || conduit.isPresent()) {
            int hasteLevel = Math.max(digSpeed.isEmpty() ? 0 : digSpeed.getAsInt(), conduit.isEmpty() ? 0 : conduit.getAsInt());
            speedMultiplier *= (float) (1 + (0.2 * (hasteLevel + 1)));
        }

        OptionalInt miningFatigue = player.compensatedEntities.getPotionLevelForPlayer(PotionEffect.MINING_FATIGUE);

        if (miningFatigue.isPresent()) {
            switch (miningFatigue.getAsInt()) {
                case 0:
                    speedMultiplier *= 0.3f;
                    break;
                case 1:
                    speedMultiplier *= 0.09f;
                    break;
                case 2:
                    speedMultiplier *= 0.0027f;
                    break;
                default:
                    speedMultiplier *= 0.00081f;
            }
        }

        speedMultiplier *= (float) player.compensatedEntities.getSelf().getAttributeValue(Attribute.PLAYER_BLOCK_BREAK_SPEED);

        if (player.fluidOnEyes == FluidTag.WATER) {
            if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21)) {
                speedMultiplier *= (float) player.compensatedEntities.getSelf().getAttributeValue(Attribute.PLAYER_SUBMERGED_MINING_SPEED);
            } else {
                if (EnchantmentHelper.getMaximumEnchantLevel(player.getInventory(), Enchantment.AQUA_AFFINITY) == 0) {
                    speedMultiplier /= 5;
                }
            }
        }

        if (!player.packetStateData.packetPlayerOnGround) {
            speedMultiplier /= 5;
        }

        double damage = speedMultiplier / blockHardness;

        // todo minestom
       // boolean canHarvest = !block.getType().isRequiresCorrectTool() || isCorrectToolForDrop;
        boolean canHarvest = isCorrectToolForDrop;
        if (canHarvest) {
            damage /= 30F;
        } else {
            damage /= 100F;
        }

        return damage;
    }
}

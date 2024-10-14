package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.ItemTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.StateValue;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Materials {
    private static final Set<Block> NO_PLACE_LIQUIDS = new HashSet<>();
    // Includes iron panes in addition to glass panes
    private static final Set<Block> PANES = new HashSet<>();
    private static final Set<Block> WATER_LIQUIDS = new HashSet<>();
    private static final Set<Block> WATER_LIQUIDS_LEGACY = new HashSet<>();
    private static final Set<Block> WATER_SOURCES = new HashSet<>();
    private static final Set<Block> WATER_SOURCES_LEGACY = new HashSet<>();

    private static final Set<Block> CLIENT_SIDE = new HashSet<>();

    static {
        // Base water, flowing on 1.12- but not on 1.13+ servers
        WATER_LIQUIDS.add(Block.WATER);
        WATER_LIQUIDS_LEGACY.add(Block.WATER);

        // Becomes grass for legacy versions
        WATER_LIQUIDS.add(Block.KELP);
        WATER_SOURCES.add(Block.KELP);
        WATER_LIQUIDS.add(Block.KELP_PLANT);
        WATER_SOURCES.add(Block.KELP_PLANT);

        // Is translated to air for legacy versions
        WATER_SOURCES.add(Block.BUBBLE_COLUMN);
        WATER_LIQUIDS_LEGACY.add(Block.BUBBLE_COLUMN);
        WATER_LIQUIDS.add(Block.BUBBLE_COLUMN);
        WATER_SOURCES_LEGACY.add(Block.BUBBLE_COLUMN);

        // This is not water on 1.12- players
        WATER_SOURCES.add(Block.SEAGRASS);
        WATER_LIQUIDS.add(Block.SEAGRASS);

        // This is not water on 1.12- players`
        WATER_SOURCES.add(Block.TALL_SEAGRASS);
        WATER_LIQUIDS.add(Block.TALL_SEAGRASS);

        NO_PLACE_LIQUIDS.add(Block.WATER);
        NO_PLACE_LIQUIDS.add(Block.LAVA);

        // Important blocks where we need to ignore right-clicking on for placing blocks
        // We can ignore stuff like right-clicking a pumpkin with shears...
        CLIENT_SIDE.add(Block.BARREL);
        CLIENT_SIDE.add(Block.BEACON);
        CLIENT_SIDE.add(Block.BREWING_STAND);
        CLIENT_SIDE.add(Block.CARTOGRAPHY_TABLE);
        CLIENT_SIDE.add(Block.CHEST);
        CLIENT_SIDE.add(Block.TRAPPED_CHEST);
        CLIENT_SIDE.add(Block.COMPARATOR);
        CLIENT_SIDE.add(Block.CRAFTING_TABLE);
        CLIENT_SIDE.add(Block.DAYLIGHT_DETECTOR);
        CLIENT_SIDE.add(Block.DISPENSER);
        CLIENT_SIDE.add(Block.DRAGON_EGG);
        CLIENT_SIDE.add(Block.ENCHANTING_TABLE);
        CLIENT_SIDE.add(Block.ENDER_CHEST);
        CLIENT_SIDE.add(Block.GRINDSTONE);
        CLIENT_SIDE.add(Block.HOPPER);
        CLIENT_SIDE.add(Block.LEVER);
        CLIENT_SIDE.add(Block.LIGHT);
        CLIENT_SIDE.add(Block.LOOM);
        CLIENT_SIDE.add(Block.NOTE_BLOCK);
        CLIENT_SIDE.add(Block.REPEATER);
        CLIENT_SIDE.add(Block.SMITHING_TABLE);
        CLIENT_SIDE.add(Block.STONECUTTER);
        CLIENT_SIDE.add(Block.LECTERN);
        CLIENT_SIDE.add(Block.FURNACE);
        CLIENT_SIDE.add(Block.BLAST_FURNACE);

        CLIENT_SIDE.addAll(BlockTags.FENCE_GATES.getStates());
        CLIENT_SIDE.addAll(BlockTags.ANVIL.getStates());
        CLIENT_SIDE.addAll(BlockTags.BEDS.getStates());
        CLIENT_SIDE.addAll(BlockTags.BUTTONS.getStates());
        CLIENT_SIDE.addAll(BlockTags.SHULKER_BOXES.getStates());
        CLIENT_SIDE.addAll(BlockTags.SIGNS.getStates());
        CLIENT_SIDE.addAll(BlockTags.FLOWER_POTS.getStates());
        CLIENT_SIDE.addAll(BlockTags.TRAPDOORS.getStates().stream().filter(type -> type != Block.IRON_TRAPDOOR).collect(Collectors.toSet()));
        CLIENT_SIDE.addAll(BlockTags.WOODEN_DOORS.getStates());

        PANES.addAll(BlockTags.GLASS_PANES.getStates());
        PANES.add(Block.IRON_BARS);
    }

    public static boolean isStairs(Block type) {
        return BlockTags.STAIRS.contains(type);
    }

    public static boolean isSlab(Block type) {
        return BlockTags.SLABS.contains(type);
    }

    public static boolean isWall(Block type) {
        return BlockTags.WALLS.contains(type);
    }

    public static boolean isButton(Block type) {
        return BlockTags.BUTTONS.contains(type);
    }

    public static boolean isFence(Block type) {
        return BlockTags.FENCES.contains(type);
    }

    public static boolean isGate(Block type) {
        return BlockTags.FENCE_GATES.contains(type);
    }

    public static boolean isBed(Block type) {
        return BlockTags.BEDS.contains(type);
    }

    public static boolean isAir(Block type) {
        return type.isAir();
    }

    public static boolean isLeaves(Block type) {
        return BlockTags.LEAVES.contains(type);
    }

    public static boolean isDoor(Block type) {
        return BlockTags.DOORS.contains(type);
    }

    public static boolean isShulker(Block type) {
        return BlockTags.SHULKER_BOXES.contains(type);
    }

    public static boolean isGlassBlock(Block type) {
        return BlockTags.GLASS_BLOCKS.contains(type);
    }

    public static Set<Block> getPanes() {
        return new HashSet<>(PANES);
    }

    public static boolean isGlassPane(Block type) {
        return PANES.contains(type);
    }

    public static boolean isCauldron(Block type) {
        return BlockTags.CAULDRONS.contains(type);
    }

    public static boolean isWaterModern(Block type) {
        return WATER_LIQUIDS.contains(type);
    }

    public static boolean isWaterLegacy(Block type) {
        return WATER_LIQUIDS_LEGACY.contains(type);
    }

    public static boolean isShapeExceedsCube(Block type) {
        return BlockTags.exceedsCube(type);
    }

//    public static boolean isUsable(Material material) {
//        return material != null && (material.hasAttribute(ItemTypes.ItemAttribute.EDIBLE) || material == Material.POTION || material == Material.MILK_BUCKET
//                || material == Material.CROSSBOW || material == Material.BOW || material.toString().endsWith("SWORD")
//                || material == Material.TRIDENT || material == Material.SHIELD);
//    }

    public static boolean isWater(ClientVersion clientVersion, MinestomWrappedBlockState state) {
        boolean modern = clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13);

        if (modern && isWaterModern(state.getType())) {
            return true;
        }

        if (!modern && isWaterLegacy(state.getType())) {
            return true;
        }

        return isWaterlogged(clientVersion, state);
    }

    public static boolean isWaterSource(ClientVersion clientVersion, MinestomWrappedBlockState state) {
        if (isWaterlogged(clientVersion, state)) {
            return true;
        }
        if (state.getType() == Block.WATER && state.getLevel() == 0) {
            return true;
        }
        boolean modern = clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13);
        return modern ? WATER_SOURCES.contains(state.getType()) : WATER_SOURCES_LEGACY.contains(state.getType());
    }

    public static boolean isWaterlogged(ClientVersion clientVersion, MinestomWrappedBlockState state) {
        if (clientVersion.isOlderThanOrEquals(ClientVersion.V_1_12_2)) return false;

        Block type = state.getType();

        // Waterlogged lanterns were added in 1.16.2
        if (clientVersion.isOlderThan(ClientVersion.V_1_16_2) && (type == Block.LANTERN || type == Block.SOUL_LANTERN))
            return false;
        // ViaVersion small dripleaf -> fern (not waterlogged)
        if (clientVersion.isOlderThan(ClientVersion.V_1_17) && type == Block.SMALL_DRIPLEAF)
            return false;
        // Waterlogged rails were added in 1.17
        if (clientVersion.isOlderThan(ClientVersion.V_1_17) && BlockTags.RAILS.contains(type))
            return false;
        // Nice check to see if waterlogged :)
        return (boolean) state.getInternalData().getOrDefault(StateValue.WATERLOGGED, false);
    }

    public static boolean isPlaceableWaterBucket(Material mat) {
        return mat == Material.AXOLOTL_BUCKET || mat == Material.COD_BUCKET || mat == Material.PUFFERFISH_BUCKET
                || mat == Material.SALMON_BUCKET || mat == Material.TROPICAL_FISH_BUCKET || mat == Material.WATER_BUCKET
                || mat == Material.TADPOLE_BUCKET;
    }

    public static Block transformBucketMaterial(Material mat) {
        if (mat == Material.LAVA_BUCKET) return Block.LAVA;
        if (isPlaceableWaterBucket(mat)) return Block.WATER;
        return null;
    }

    // We are taking a shortcut here for the sake of speed and reducing world lookups
    // As we have already assumed that the player does not have water at this block
    // We do not have to track all the version differences in terms of looking for water
    // For 1.7-1.12 clients, it is safe to check SOLID_BLACKLIST directly
    public static boolean isSolidBlockingBlacklist(Block mat, ClientVersion ver) {
        // Thankfully Mojang has not changed this code much across versions
        // There very likely is a few lurking issues though, I've done my best but can't thoroughly compare 11 versions
        // but from a look, Mojang seems to keep this definition consistent throughout their game (thankfully)
        //
        // What I do is look at 1.8, 1.12, and 1.17 source code, and when I see a difference, I find the version
        // that added it.  I could have missed something if something was added to the blacklist in 1.9 but
        // was removed from it in 1.10 (although this is unlikely as the blacklist rarely changes)
        if (!new MinestomWrappedBlockState(mat).isBlocking()) return true;

        // 1.13-1.15 had banners on the blacklist - removed in 1.16, not implemented in 1.12 and below
        if (BlockTags.BANNERS.contains(mat))
            return ver.isNewerThanOrEquals(ClientVersion.V_1_13) && ver.isOlderThan(ClientVersion.V_1_16);

        return false;
    }

    public static boolean isAnvil(Block mat) {
        return BlockTags.ANVIL.contains(mat);
    }

    public static boolean isWoodenChest(Block mat) {
        return mat == Block.CHEST || mat == Block.TRAPPED_CHEST;
    }

    public static boolean isNoPlaceLiquid(Block material) {
        return NO_PLACE_LIQUIDS.contains(material);
    }

    public static boolean isWaterIgnoringWaterlogged(ClientVersion clientVersion, MinestomWrappedBlockState state) {
        if (clientVersion.isNewerThanOrEquals(ClientVersion.V_1_13)) return isWaterModern(state.getType());
        return isWaterLegacy(state.getType());
    }

    public static boolean isClientSideInteractable(Block material) {
        return CLIENT_SIDE.contains(material);
    }

    public static boolean isCompostable(Material material) {
        // This 3772 character line was auto generated
        // todo minestom oh hell nah we ain't doing this;
        // todo do this hsit correctly
        return ItemTags.LEAVES.contains(material) || ItemTags.SAPLINGS.contains(material) || ItemTags.VILLAGER_PLANTABLE_SEEDS.contains(material);
        //return ItemTypes.OAK_LEAVES.equals(material) || ItemTypes.OAK_LEAVES.equals(material) || ItemTypes.SPRUCE_LEAVES.equals(material) || ItemTypes.DARK_OAK_LEAVES.equals(material) || ItemTypes.ACACIA_LEAVES.equals(material) || ItemTypes.BIRCH_LEAVES.equals(material) || ItemTypes.AZALEA_LEAVES.equals(material) || ItemTypes.OAK_SAPLING.equals(material) || ItemTypes.SPRUCE_SAPLING.equals(material) || ItemTypes.BIRCH_SAPLING.equals(material) || ItemTypes.JUNGLE_SAPLING.equals(material) || ItemTypes.ACACIA_SAPLING.equals(material) || ItemTypes.DARK_OAK_SAPLING.equals(material) || ItemTypes.BEETROOT_SEEDS.equals(material) || ItemTypes.DRIED_KELP.equals(material) || ItemTypes.GRASS.equals(material) || ItemTypes.KELP.equals(material) || ItemTypes.MELON_SEEDS.equals(material) || ItemTypes.PUMPKIN_SEEDS.equals(material) || ItemTypes.SEAGRASS.equals(material) || ItemTypes.SWEET_BERRIES.equals(material) || ItemTypes.GLOW_BERRIES.equals(material) || ItemTypes.WHEAT_SEEDS.equals(material) || ItemTypes.MOSS_CARPET.equals(material) || ItemTypes.SMALL_DRIPLEAF.equals(material) || ItemTypes.HANGING_ROOTS.equals(material) || ItemTypes.DRIED_KELP_BLOCK.equals(material) || ItemTypes.TALL_GRASS.equals(material) || ItemTypes.AZALEA.equals(material) || ItemTypes.CACTUS.equals(material) || ItemTypes.SUGAR_CANE.equals(material) || ItemTypes.VINE.equals(material) || ItemTypes.NETHER_SPROUTS.equals(material) || ItemTypes.WEEPING_VINES.equals(material) || ItemTypes.TWISTING_VINES.equals(material) || ItemTypes.MELON_SLICE.equals(material) || ItemTypes.GLOW_LICHEN.equals(material) || ItemTypes.SEA_PICKLE.equals(material) || ItemTypes.LILY_PAD.equals(material) || ItemTypes.PUMPKIN.equals(material) || ItemTypes.CARVED_PUMPKIN.equals(material) || ItemTypes.MELON.equals(material) || ItemTypes.APPLE.equals(material) || ItemTypes.BEETROOT.equals(material) || ItemTypes.CARROT.equals(material) || ItemTypes.COCOA_BEANS.equals(material) || ItemTypes.POTATO.equals(material) || ItemTypes.WHEAT.equals(material) || ItemTypes.BROWN_MUSHROOM.equals(material) || ItemTypes.RED_MUSHROOM.equals(material) || ItemTypes.MUSHROOM_STEM.equals(material) || ItemTypes.CRIMSON_FUNGUS.equals(material) || ItemTypes.WARPED_FUNGUS.equals(material) || ItemTypes.NETHER_WART.equals(material) || ItemTypes.CRIMSON_ROOTS.equals(material) || ItemTypes.WARPED_ROOTS.equals(material) || ItemTypes.SHROOMLIGHT.equals(material) || ItemTypes.DANDELION.equals(material) || ItemTypes.POPPY.equals(material) || ItemTypes.BLUE_ORCHID.equals(material) || ItemTypes.ALLIUM.equals(material) || ItemTypes.AZURE_BLUET.equals(material) || ItemTypes.RED_TULIP.equals(material) || ItemTypes.ORANGE_TULIP.equals(material) || ItemTypes.WHITE_TULIP.equals(material) || ItemTypes.PINK_TULIP.equals(material) || ItemTypes.OXEYE_DAISY.equals(material) || ItemTypes.CORNFLOWER.equals(material) || ItemTypes.LILY_OF_THE_VALLEY.equals(material) || ItemTypes.WITHER_ROSE.equals(material) || ItemTypes.FERN.equals(material) || ItemTypes.SUNFLOWER.equals(material) || ItemTypes.LILAC.equals(material) || ItemTypes.ROSE_BUSH.equals(material) || ItemTypes.PEONY.equals(material) || ItemTypes.LARGE_FERN.equals(material) || ItemTypes.SPORE_BLOSSOM.equals(material) || ItemTypes.MOSS_BLOCK.equals(material) || ItemTypes.BIG_DRIPLEAF.equals(material) || ItemTypes.HAY_BLOCK.equals(material) || ItemTypes.BROWN_MUSHROOM_BLOCK.equals(material) || ItemTypes.RED_MUSHROOM_BLOCK.equals(material) || ItemTypes.NETHER_WART_BLOCK.equals(material) || ItemTypes.WARPED_WART_BLOCK.equals(material) || ItemTypes.FLOWERING_AZALEA.equals(material) || ItemTypes.BREAD.equals(material) || ItemTypes.BAKED_POTATO.equals(material) || ItemTypes.COOKIE.equals(material) || ItemTypes.CAKE.equals(material) || ItemTypes.PUMPKIN_PIE.equals(material);
    }
}

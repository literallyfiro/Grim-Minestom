/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2022 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ac.grim.grimac.utils.minestom;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class allows you to use block tags on outdated versions.
 * If you are on a version that sends tags to the player, you are suggested to listen to {@link com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTags}
 * <p>
 * Might as well just fucking shade the entire vanilla jar if we are going to copy SO MANY HUNDREDS OF LINES
 *
 * @author DefineOutside
 */
public class BlockTags {
    private static final HashMap<String, BlockTags> byName = new HashMap<>();

    public static final BlockTags WOOL = bind("wool");
    public static final BlockTags PLANKS = bind("planks");
    public static final BlockTags STONE_BRICKS = bind("stone_bricks");
    public static final BlockTags WOODEN_BUTTONS = bind("wooden_buttons");
    public static final BlockTags STONE_BUTTONS = bind("stone_buttons");
    public static final BlockTags BUTTONS = bind("buttons");
    public static final BlockTags WOOL_CARPETS = bind("wool_carpets");
    public static final BlockTags WOODEN_DOORS = bind("wooden_doors");
    public static final BlockTags WOODEN_STAIRS = bind("wooden_stairs");
    public static final BlockTags WOODEN_SLABS = bind("wooden_slabs");
    public static final BlockTags WOODEN_FENCES = bind("wooden_fences");
    public static final BlockTags PRESSURE_PLATES = bind("pressure_plates");
    public static final BlockTags WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
    public static final BlockTags STONE_PRESSURE_PLATES = bind("stone_pressure_plates");
    public static final BlockTags WOODEN_TRAPDOORS = bind("wooden_trapdoors");
    public static final BlockTags DOORS = bind("doors");
    public static final BlockTags SAPLINGS = bind("saplings");
    public static final BlockTags LOGS_THAT_BURN = bind("logs_that_burn");
    public static final BlockTags OVERWORLD_NATURAL_LOGS = bind("overworld_natural_logs");
    public static final BlockTags LOGS = bind("logs");
    public static final BlockTags DARK_OAK_LOGS = bind("dark_oak_logs");
    public static final BlockTags OAK_LOGS = bind("oak_logs");
    public static final BlockTags BIRCH_LOGS = bind("birch_logs");
    public static final BlockTags ACACIA_LOGS = bind("acacia_logs");
    public static final BlockTags CHERRY_LOGS = bind("cherry_logs");
    public static final BlockTags JUNGLE_LOGS = bind("jungle_logs");
    public static final BlockTags SPRUCE_LOGS = bind("spruce_logs");
    public static final BlockTags MANGROVE_LOGS = bind("mangrove_logs");
    public static final BlockTags CRIMSON_STEMS = bind("crimson_stems");
    public static final BlockTags WARPED_STEMS = bind("warped_stems");
    public static final BlockTags BAMBOO_BLOCKS = bind("bamboo_blocks");
    public static final BlockTags WART_BLOCKS = bind("wart_blocks");
    public static final BlockTags BANNERS = bind("banners");
    public static final BlockTags SAND = bind("sand");
    public static final BlockTags SMELTS_TO_GLASS = bind("smelts_to_glass");
    public static final BlockTags STAIRS = bind("stairs");
    public static final BlockTags SLABS = bind("slabs");
    public static final BlockTags WALLS = bind("walls");
    public static final BlockTags ANVIL = bind("anvil");
    public static final BlockTags RAILS = bind("rails");
    public static final BlockTags LEAVES = bind("leaves");
    public static final BlockTags TRAPDOORS = bind("trapdoors");
    public static final BlockTags SMALL_FLOWERS = bind("small_flowers");
    public static final BlockTags BEDS = bind("beds");
    public static final BlockTags FENCES = bind("fences");
    public static final BlockTags TALL_FLOWERS = bind("tall_flowers");
    public static final BlockTags FLOWERS = bind("flowers");
    public static final BlockTags PIGLIN_REPELLENTS = bind("piglin_repellents");
    public static final BlockTags GOLD_ORES = bind("gold_ores");
    public static final BlockTags IRON_ORES = bind("iron_ores");
    public static final BlockTags DIAMOND_ORES = bind("diamond_ores");
    public static final BlockTags REDSTONE_ORES = bind("redstone_ores");
    public static final BlockTags LAPIS_ORES = bind("lapis_ores");
    public static final BlockTags COAL_ORES = bind("coal_ores");
    public static final BlockTags EMERALD_ORES = bind("emerald_ores");
    public static final BlockTags COPPER_ORES = bind("copper_ores");
    public static final BlockTags CANDLES = bind("candles");
    public static final BlockTags DIRT = bind("dirt");
    public static final BlockTags TERRACOTTA = bind("terracotta");
    public static final BlockTags CONCRETE_POWDER = bind("concrete_powder");
    public static final BlockTags COMPLETES_FIND_TREE_TUTORIAL = bind("completes_find_tree_tutorial");
    public static final BlockTags FLOWER_POTS = bind("flower_pots");
    public static final BlockTags ENDERMAN_HOLDABLE = bind("enderman_holdable");
    public static final BlockTags ICE = bind("ice");
    public static final BlockTags VALID_SPAWN = bind("valid_spawn");
    public static final BlockTags IMPERMEABLE = bind("impermeable");
    public static final BlockTags UNDERWATER_BONEMEALS = bind("underwater_bonemeals");
    public static final BlockTags CORAL_BLOCKS = bind("coral_blocks");
    public static final BlockTags WALL_CORALS = bind("wall_corals");
    public static final BlockTags CORAL_PLANTS = bind("coral_plants");
    public static final BlockTags CORALS = bind("corals");
    public static final BlockTags BAMBOO_PLANTABLE_ON = bind("bamboo_plantable_on");
    public static final BlockTags STANDING_SIGNS = bind("standing_signs");
    public static final BlockTags WALL_SIGNS = bind("wall_signs");
    public static final BlockTags SIGNS = bind("signs");
    public static final BlockTags CEILING_HANGING_SIGNS = bind("ceiling_hanging_signs");
    public static final BlockTags WALL_HANGING_SIGNS = bind("wall_hanging_signs");
    public static final BlockTags ALL_HANGING_SIGNS = bind("all_hanging_signs");
    public static final BlockTags ALL_SIGNS = bind("all_signs");
    public static final BlockTags DRAGON_IMMUNE = bind("dragon_immune");
    public static final BlockTags DRAGON_TRANSPARENT = bind("dragon_transparent");
    public static final BlockTags WITHER_IMMUNE = bind("wither_immune");
    public static final BlockTags WITHER_SUMMON_BASE_BLOCKS = bind("wither_summon_base_blocks");
    public static final BlockTags BEEHIVES = bind("beehives");
    public static final BlockTags CROPS = bind("crops");
    public static final BlockTags BEE_GROWABLES = bind("bee_growables");
    public static final BlockTags PORTALS = bind("portals");
    public static final BlockTags FIRE = bind("fire");
    public static final BlockTags NYLIUM = bind("nylium");
    public static final BlockTags BEACON_BASE_BLOCKS = bind("beacon_base_blocks");
    public static final BlockTags SOUL_SPEED_BLOCKS = bind("soul_speed_blocks");
    public static final BlockTags WALL_POST_OVERRIDE = bind("wall_post_override");
    public static final BlockTags CLIMBABLE = bind("climbable");
    public static final BlockTags FALL_DAMAGE_RESETTING = bind("fall_damage_resetting");
    public static final BlockTags SHULKER_BOXES = bind("shulker_boxes");
    public static final BlockTags HOGLIN_REPELLENTS = bind("hoglin_repellents");
    public static final BlockTags SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
    public static final BlockTags STRIDER_WARM_BLOCKS = bind("strider_warm_blocks");
    public static final BlockTags CAMPFIRES = bind("campfires");
    public static final BlockTags GUARDED_BY_PIGLINS = bind("guarded_by_piglins");
    public static final BlockTags PREVENT_MOB_SPAWNING_INSIDE = bind("prevent_mob_spawning_inside");
    public static final BlockTags FENCE_GATES = bind("fence_gates");
    public static final BlockTags UNSTABLE_BOTTOM_CENTER = bind("unstable_bottom_center");
    public static final BlockTags MUSHROOM_GROW_BLOCK = bind("mushroom_grow_block");
    public static final BlockTags INFINIBURN_OVERWORLD = bind("infiniburn_overworld");
    public static final BlockTags INFINIBURN_NETHER = bind("infiniburn_nether");
    public static final BlockTags INFINIBURN_END = bind("infiniburn_end");
    public static final BlockTags BASE_STONE_OVERWORLD = bind("base_stone_overworld");
    public static final BlockTags STONE_ORE_REPLACEABLES = bind("stone_ore_replaceables");
    public static final BlockTags DEEPSLATE_ORE_REPLACEABLES = bind("deepslate_ore_replaceables");
    public static final BlockTags BASE_STONE_NETHER = bind("base_stone_nether");
    public static final BlockTags OVERWORLD_CARVER_REPLACEABLES = bind("overworld_carver_replaceables");
    public static final BlockTags NETHER_CARVER_REPLACEABLES = bind("nether_carver_replaceables");
    public static final BlockTags CANDLE_CAKES = bind("candle_cakes");
    public static final BlockTags CAULDRONS = bind("cauldrons");
    public static final BlockTags CRYSTAL_SOUND_BLOCKS = bind("crystal_sound_blocks");
    public static final BlockTags INSIDE_STEP_SOUND_BLOCKS = bind("inside_step_sound_blocks");
    public static final BlockTags COMBINATION_STEP_SOUND_BLOCKS = bind("combination_step_sound_blocks");
    public static final BlockTags CAMEL_SAND_STEP_SOUND_BLOCKS = bind("camel_sand_step_sound_blocks");
    public static final BlockTags OCCLUDES_VIBRATION_SIGNALS = bind("occludes_vibration_signals");
    public static final BlockTags DAMPENS_VIBRATIONS = bind("dampens_vibrations");
    public static final BlockTags DRIPSTONE_REPLACEABLE_BLOCKS = bind("dripstone_replaceable_blocks");
    @Deprecated
    public static final BlockTags DRIPSTONE_REPLACEABLE = DRIPSTONE_REPLACEABLE_BLOCKS;
    public static final BlockTags CAVE_VINES = bind("cave_vines");
    public static final BlockTags MOSS_REPLACEABLE = bind("moss_replaceable");
    public static final BlockTags LUSH_GROUND_REPLACEABLE = bind("lush_ground_replaceable");
    public static final BlockTags AZALEA_ROOT_REPLACEABLE = bind("azalea_root_replaceable");
    public static final BlockTags SMALL_DRIPLEAF_PLACEABLE = bind("small_dripleaf_placeable");
    public static final BlockTags BIG_DRIPLEAF_PLACEABLE = bind("big_dripleaf_placeable");
    public static final BlockTags SNOW = bind("snow");
    public static final BlockTags MINEABLE_AXE = bind("mineable/axe");
    public static final BlockTags MINEABLE_HOE = bind("mineable/hoe");
    public static final BlockTags MINEABLE_PICKAXE = bind("mineable/pickaxe");
    public static final BlockTags MINEABLE_SHOVEL = bind("mineable/shovel");
    @Deprecated
    public static final BlockTags MINEABLE_WITH_AXE = MINEABLE_AXE;
    @Deprecated
    public static final BlockTags MINEABLE_WITH_HOE = MINEABLE_HOE;
    @Deprecated
    public static final BlockTags MINEABLE_WITH_PICKAXE = MINEABLE_PICKAXE;
    @Deprecated
    public static final BlockTags MINEABLE_WITH_SHOVEL = MINEABLE_SHOVEL;
    public static final BlockTags SWORD_EFFICIENT = bind("sword_efficient");
    public static final BlockTags NEEDS_DIAMOND_TOOL = bind("needs_diamond_tool");
    public static final BlockTags NEEDS_IRON_TOOL = bind("needs_iron_tool");
    public static final BlockTags NEEDS_STONE_TOOL = bind("needs_stone_tool");
    public static final BlockTags FEATURES_CANNOT_REPLACE = bind("features_cannot_replace");
    public static final BlockTags LAVA_POOL_STONE_CANNOT_REPLACE = bind("lava_pool_stone_cannot_replace");
    public static final BlockTags GEODE_INVALID_BLOCKS = bind("geode_invalid_blocks");
    public static final BlockTags FROG_PREFER_JUMP_TO = bind("frog_prefer_jump_to");
    public static final BlockTags SCULK_REPLACEABLE = bind("sculk_replaceable");
    public static final BlockTags SCULK_REPLACEABLE_WORLD_GEN = bind("sculk_replaceable_world_gen");
    public static final BlockTags ANCIENT_CITY_REPLACEABLE = bind("ancient_city_replaceable");
    public static final BlockTags VIBRATION_RESONATORS = bind("vibration_resonators");
    public static final BlockTags ANIMALS_SPAWNABLE_ON = bind("animals_spawnable_on");
    public static final BlockTags AXOLOTLS_SPAWNABLE_ON = bind("axolotls_spawnable_on");
    public static final BlockTags GOATS_SPAWNABLE_ON = bind("goats_spawnable_on");
    public static final BlockTags MOOSHROOMS_SPAWNABLE_ON = bind("mooshrooms_spawnable_on");
    public static final BlockTags PARROTS_SPAWNABLE_ON = bind("parrots_spawnable_on");
    public static final BlockTags POLAR_BEARS_SPAWNABLE_ON_ALTERNATE = bind("polar_bears_spawnable_on_alternate");
    public static final BlockTags RABBITS_SPAWNABLE_ON = bind("rabbits_spawnable_on");
    public static final BlockTags FOXES_SPAWNABLE_ON = bind("foxes_spawnable_on");
    public static final BlockTags WOLVES_SPAWNABLE_ON = bind("wolves_spawnable_on");
    public static final BlockTags FROGS_SPAWNABLE_ON = bind("frogs_spawnable_on");
    public static final BlockTags AZALEA_GROWS_ON = bind("azalea_grows_on");
    @Deprecated
    public static final BlockTags REPLACEABLE_PLANTS = bind("replaceable_plants");
    public static final BlockTags CONVERTABLE_TO_MUD = bind("convertable_to_mud");
    public static final BlockTags MANGROVE_LOGS_CAN_GROW_THROUGH = bind("mangrove_logs_can_grow_through");
    public static final BlockTags MANGROVE_ROOTS_CAN_GROW_THROUGH = bind("mangrove_roots_can_grow_through");
    public static final BlockTags DEAD_BUSH_MAY_PLACE_ON = bind("dead_bush_may_place_on");
    public static final BlockTags SNAPS_GOAT_HORN = bind("snaps_goat_horn");
    public static final BlockTags REPLACEABLE_BY_TREES = bind("replaceable_by_trees");
    public static final BlockTags SNOW_LAYER_CANNOT_SURVIVE_ON = bind("snow_layer_cannot_survive_on");
    public static final BlockTags SNOW_LAYER_CAN_SURVIVE_ON = bind("snow_layer_can_survive_on");
    public static final BlockTags INVALID_SPAWN_INSIDE = bind("invalid_spawn_inside");
    public static final BlockTags SNIFFER_DIGGABLE_BLOCK = bind("sniffer_diggable_block");
    public static final BlockTags SNIFFER_EGG_HATCH_BOOST = bind("sniffer_egg_hatch_boost");
    public static final BlockTags TRAIL_RUINS_REPLACEABLE = bind("trail_ruins_replaceable");
    public static final BlockTags REPLACEABLE = bind("replaceable");
    public static final BlockTags ENCHANTMENT_POWER_PROVIDER = bind("enchantment_power_provider");
    public static final BlockTags ENCHANTMENT_POWER_TRANSMITTER = bind("enchantment_power_transmitter");
    public static final BlockTags MAINTAINS_FARMLAND = bind("maintains_farmland");

    // Added in 1.20.5
    public static final BlockTags ARMADILLO_SPAWNABLE_ON = bind("armadillo_spawnable_on");
    public static final BlockTags BADLANDS_TERRACOTTA = bind("badlands_terracotta");
    public static final BlockTags BLOCKS_WIND_CHARGE_EXPLOSIONS = bind("blocks_wind_charge_explosions");
    public static final BlockTags DOES_NOT_BLOCK_HOPPERS = bind("does_not_block_hoppers");
    public static final BlockTags INCORRECT_FOR_DIAMOND_TOOL = bind("incorrect_for_diamond_tool");
    public static final BlockTags INCORRECT_FOR_GOLD_TOOL = bind("incorrect_for_gold_tool");
    public static final BlockTags INCORRECT_FOR_IRON_TOOL = bind("incorrect_for_iron_tool");
    public static final BlockTags INCORRECT_FOR_NETHERITE_TOOL = bind("incorrect_for_netherite_tool");
    public static final BlockTags INCORRECT_FOR_STONE_TOOL = bind("incorrect_for_stone_tool");
    public static final BlockTags INCORRECT_FOR_WOODEN_TOOL = bind("incorrect_for_wooden_tool");

    /**
     * Unofficial tag for all glass blocks
     */
    public static final BlockTags GLASS_BLOCKS = bind("glass_blocks");
    /**
     * Unofficial tag for all glass panes
     */
    public static final BlockTags GLASS_PANES = bind("glass_panes");

    static {
        BlockTags.WOOL.add(Block.WHITE_WOOL, Block.ORANGE_WOOL, Block.MAGENTA_WOOL, Block.LIGHT_BLUE_WOOL, Block.YELLOW_WOOL, Block.LIME_WOOL, Block.PINK_WOOL, Block.GRAY_WOOL, Block.LIGHT_GRAY_WOOL, Block.CYAN_WOOL, Block.PURPLE_WOOL, Block.BLUE_WOOL, Block.BROWN_WOOL, Block.GREEN_WOOL, Block.RED_WOOL, Block.BLACK_WOOL);
        BlockTags.PLANKS.add(Block.OAK_PLANKS, Block.SPRUCE_PLANKS, Block.BIRCH_PLANKS, Block.JUNGLE_PLANKS, Block.ACACIA_PLANKS, Block.DARK_OAK_PLANKS, Block.CRIMSON_PLANKS, Block.WARPED_PLANKS, Block.MANGROVE_PLANKS, Block.BAMBOO_PLANKS, Block.CHERRY_PLANKS);
        BlockTags.STONE_BRICKS.add(Block.STONE_BRICKS, Block.MOSSY_STONE_BRICKS, Block.CRACKED_STONE_BRICKS, Block.CHISELED_STONE_BRICKS);
        BlockTags.WOODEN_BUTTONS.add(Block.OAK_BUTTON, Block.SPRUCE_BUTTON, Block.BIRCH_BUTTON, Block.JUNGLE_BUTTON, Block.ACACIA_BUTTON, Block.DARK_OAK_BUTTON, Block.CRIMSON_BUTTON, Block.WARPED_BUTTON, Block.MANGROVE_BUTTON, Block.BAMBOO_BUTTON, Block.CHERRY_BUTTON);
        BlockTags.STONE_BUTTONS.add(Block.STONE_BUTTON, Block.POLISHED_BLACKSTONE_BUTTON);
        BlockTags.WOOL_CARPETS.add(Block.WHITE_CARPET, Block.ORANGE_CARPET, Block.MAGENTA_CARPET, Block.LIGHT_BLUE_CARPET, Block.YELLOW_CARPET, Block.LIME_CARPET, Block.PINK_CARPET, Block.GRAY_CARPET, Block.LIGHT_GRAY_CARPET, Block.CYAN_CARPET, Block.PURPLE_CARPET, Block.BLUE_CARPET, Block.BROWN_CARPET, Block.GREEN_CARPET, Block.RED_CARPET, Block.BLACK_CARPET);
        BlockTags.WOODEN_DOORS.add(Block.OAK_DOOR, Block.SPRUCE_DOOR, Block.BIRCH_DOOR, Block.JUNGLE_DOOR, Block.ACACIA_DOOR, Block.DARK_OAK_DOOR, Block.CRIMSON_DOOR, Block.WARPED_DOOR, Block.MANGROVE_DOOR, Block.BAMBOO_DOOR, Block.CHERRY_DOOR, Block.COPPER_DOOR, Block.EXPOSED_COPPER_DOOR, Block.WEATHERED_COPPER_DOOR, Block.OXIDIZED_COPPER_DOOR, Block.WAXED_COPPER_DOOR, Block.WAXED_EXPOSED_COPPER_DOOR, Block.WAXED_WEATHERED_COPPER_DOOR, Block.WAXED_OXIDIZED_COPPER_DOOR);
        BlockTags.WOODEN_STAIRS.add(Block.OAK_STAIRS, Block.SPRUCE_STAIRS, Block.BIRCH_STAIRS, Block.JUNGLE_STAIRS, Block.ACACIA_STAIRS, Block.DARK_OAK_STAIRS, Block.CRIMSON_STAIRS, Block.WARPED_STAIRS, Block.MANGROVE_STAIRS, Block.BAMBOO_STAIRS, Block.CHERRY_STAIRS);
        BlockTags.WOODEN_SLABS.add(Block.OAK_SLAB, Block.SPRUCE_SLAB, Block.BIRCH_SLAB, Block.JUNGLE_SLAB, Block.ACACIA_SLAB, Block.DARK_OAK_SLAB, Block.CRIMSON_SLAB, Block.WARPED_SLAB, Block.MANGROVE_SLAB, Block.BAMBOO_SLAB, Block.CHERRY_SLAB);
        BlockTags.WOODEN_FENCES.add(Block.OAK_FENCE, Block.ACACIA_FENCE, Block.DARK_OAK_FENCE, Block.SPRUCE_FENCE, Block.BIRCH_FENCE, Block.JUNGLE_FENCE, Block.CRIMSON_FENCE, Block.WARPED_FENCE, Block.MANGROVE_FENCE, Block.BAMBOO_FENCE, Block.CHERRY_FENCE);
        BlockTags.WOODEN_PRESSURE_PLATES.add(Block.OAK_PRESSURE_PLATE, Block.SPRUCE_PRESSURE_PLATE, Block.BIRCH_PRESSURE_PLATE, Block.JUNGLE_PRESSURE_PLATE, Block.ACACIA_PRESSURE_PLATE, Block.DARK_OAK_PRESSURE_PLATE, Block.CRIMSON_PRESSURE_PLATE, Block.WARPED_PRESSURE_PLATE, Block.MANGROVE_PRESSURE_PLATE, Block.BAMBOO_PRESSURE_PLATE, Block.CHERRY_PRESSURE_PLATE);
        BlockTags.STONE_PRESSURE_PLATES.add(Block.STONE_PRESSURE_PLATE, Block.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        BlockTags.WOODEN_TRAPDOORS.add(Block.ACACIA_TRAPDOOR, Block.BIRCH_TRAPDOOR, Block.DARK_OAK_TRAPDOOR, Block.JUNGLE_TRAPDOOR, Block.OAK_TRAPDOOR, Block.SPRUCE_TRAPDOOR, Block.CRIMSON_TRAPDOOR, Block.WARPED_TRAPDOOR, Block.MANGROVE_TRAPDOOR, Block.BAMBOO_TRAPDOOR, Block.CHERRY_TRAPDOOR);
        BlockTags.SAPLINGS.add(Block.OAK_SAPLING, Block.SPRUCE_SAPLING, Block.BIRCH_SAPLING, Block.JUNGLE_SAPLING, Block.ACACIA_SAPLING, Block.DARK_OAK_SAPLING, Block.AZALEA, Block.FLOWERING_AZALEA, Block.MANGROVE_PROPAGULE, Block.CHERRY_SAPLING);
        BlockTags.OVERWORLD_NATURAL_LOGS.add(Block.ACACIA_LOG, Block.BIRCH_LOG, Block.OAK_LOG, Block.JUNGLE_LOG, Block.SPRUCE_LOG, Block.DARK_OAK_LOG, Block.MANGROVE_LOG, Block.CHERRY_LOG);
        BlockTags.DARK_OAK_LOGS.add(Block.DARK_OAK_LOG, Block.DARK_OAK_WOOD, Block.STRIPPED_DARK_OAK_LOG, Block.STRIPPED_DARK_OAK_WOOD);
        BlockTags.OAK_LOGS.add(Block.OAK_LOG, Block.OAK_WOOD, Block.STRIPPED_OAK_LOG, Block.STRIPPED_OAK_WOOD);
        BlockTags.BIRCH_LOGS.add(Block.BIRCH_LOG, Block.BIRCH_WOOD, Block.STRIPPED_BIRCH_LOG, Block.STRIPPED_BIRCH_WOOD);
        BlockTags.ACACIA_LOGS.add(Block.ACACIA_LOG, Block.ACACIA_WOOD, Block.STRIPPED_ACACIA_LOG, Block.STRIPPED_ACACIA_WOOD);
        BlockTags.CHERRY_LOGS.add(Block.CHERRY_LOG, Block.CHERRY_WOOD, Block.STRIPPED_CHERRY_LOG, Block.STRIPPED_CHERRY_WOOD);
        BlockTags.JUNGLE_LOGS.add(Block.JUNGLE_LOG, Block.JUNGLE_WOOD, Block.STRIPPED_JUNGLE_LOG, Block.STRIPPED_JUNGLE_WOOD);
        BlockTags.SPRUCE_LOGS.add(Block.SPRUCE_LOG, Block.SPRUCE_WOOD, Block.STRIPPED_SPRUCE_LOG, Block.STRIPPED_SPRUCE_WOOD);
        BlockTags.MANGROVE_LOGS.add(Block.MANGROVE_LOG, Block.MANGROVE_WOOD, Block.STRIPPED_MANGROVE_LOG, Block.STRIPPED_MANGROVE_WOOD);
        BlockTags.CRIMSON_STEMS.add(Block.CRIMSON_STEM, Block.STRIPPED_CRIMSON_STEM, Block.CRIMSON_HYPHAE, Block.STRIPPED_CRIMSON_HYPHAE);
        BlockTags.WARPED_STEMS.add(Block.WARPED_STEM, Block.STRIPPED_WARPED_STEM, Block.WARPED_HYPHAE, Block.STRIPPED_WARPED_HYPHAE);
        BlockTags.BAMBOO_BLOCKS.add(Block.BAMBOO_BLOCK, Block.STRIPPED_BAMBOO_BLOCK);
        BlockTags.WART_BLOCKS.add(Block.NETHER_WART_BLOCK, Block.WARPED_WART_BLOCK);
        BlockTags.BANNERS.add(Block.WHITE_BANNER, Block.ORANGE_BANNER, Block.MAGENTA_BANNER, Block.LIGHT_BLUE_BANNER, Block.YELLOW_BANNER, Block.LIME_BANNER, Block.PINK_BANNER, Block.GRAY_BANNER, Block.LIGHT_GRAY_BANNER, Block.CYAN_BANNER, Block.PURPLE_BANNER, Block.BLUE_BANNER, Block.BROWN_BANNER, Block.GREEN_BANNER, Block.RED_BANNER, Block.BLACK_BANNER, Block.WHITE_WALL_BANNER, Block.ORANGE_WALL_BANNER, Block.MAGENTA_WALL_BANNER, Block.LIGHT_BLUE_WALL_BANNER, Block.YELLOW_WALL_BANNER, Block.LIME_WALL_BANNER, Block.PINK_WALL_BANNER, Block.GRAY_WALL_BANNER, Block.LIGHT_GRAY_WALL_BANNER, Block.CYAN_WALL_BANNER, Block.PURPLE_WALL_BANNER, Block.BLUE_WALL_BANNER, Block.BROWN_WALL_BANNER, Block.GREEN_WALL_BANNER, Block.RED_WALL_BANNER, Block.BLACK_WALL_BANNER);
        BlockTags.SAND.add(Block.SAND, Block.RED_SAND, Block.SUSPICIOUS_SAND, Block.SUSPICIOUS_SAND);
        BlockTags.SMELTS_TO_GLASS.add(Block.SAND, Block.RED_SAND);
        BlockTags.WALLS.add(Block.COBBLESTONE_WALL, Block.MOSSY_COBBLESTONE_WALL, Block.BRICK_WALL, Block.PRISMARINE_WALL, Block.RED_SANDSTONE_WALL, Block.MOSSY_STONE_BRICK_WALL, Block.GRANITE_WALL, Block.STONE_BRICK_WALL, Block.NETHER_BRICK_WALL, Block.ANDESITE_WALL, Block.RED_NETHER_BRICK_WALL, Block.SANDSTONE_WALL, Block.END_STONE_BRICK_WALL, Block.DIORITE_WALL, Block.BLACKSTONE_WALL, Block.POLISHED_BLACKSTONE_BRICK_WALL, Block.POLISHED_BLACKSTONE_WALL, Block.COBBLED_DEEPSLATE_WALL, Block.POLISHED_DEEPSLATE_WALL, Block.DEEPSLATE_TILE_WALL, Block.DEEPSLATE_BRICK_WALL, Block.MUD_BRICK_WALL, Block.TUFF_WALL, Block.POLISHED_TUFF_WALL, Block.TUFF_BRICK_WALL);
        BlockTags.ANVIL.add(Block.ANVIL, Block.CHIPPED_ANVIL, Block.DAMAGED_ANVIL);
        BlockTags.RAILS.add(Block.RAIL, Block.POWERED_RAIL, Block.DETECTOR_RAIL, Block.ACTIVATOR_RAIL);
        BlockTags.LEAVES.add(Block.JUNGLE_LEAVES, Block.OAK_LEAVES, Block.SPRUCE_LEAVES, Block.DARK_OAK_LEAVES, Block.ACACIA_LEAVES, Block.BIRCH_LEAVES, Block.AZALEA_LEAVES, Block.FLOWERING_AZALEA_LEAVES, Block.MANGROVE_LEAVES, Block.CHERRY_LEAVES);
        BlockTags.SMALL_FLOWERS.add(Block.DANDELION, Block.POPPY, Block.BLUE_ORCHID, Block.ALLIUM, Block.AZURE_BLUET, Block.RED_TULIP, Block.ORANGE_TULIP, Block.WHITE_TULIP, Block.PINK_TULIP, Block.OXEYE_DAISY, Block.CORNFLOWER, Block.LILY_OF_THE_VALLEY, Block.WITHER_ROSE, Block.TORCHFLOWER);
        BlockTags.BEDS.add(Block.RED_BED, Block.BLACK_BED, Block.BLUE_BED, Block.BROWN_BED, Block.CYAN_BED, Block.GRAY_BED, Block.GREEN_BED, Block.LIGHT_BLUE_BED, Block.LIGHT_GRAY_BED, Block.LIME_BED, Block.MAGENTA_BED, Block.ORANGE_BED, Block.PINK_BED, Block.PURPLE_BED, Block.WHITE_BED, Block.YELLOW_BED);
        BlockTags.TALL_FLOWERS.add(Block.SUNFLOWER, Block.LILAC, Block.PEONY, Block.ROSE_BUSH, Block.PITCHER_PLANT);
        BlockTags.PIGLIN_REPELLENTS.add(Block.SOUL_FIRE, Block.SOUL_TORCH, Block.SOUL_LANTERN, Block.SOUL_WALL_TORCH, Block.SOUL_CAMPFIRE);
        BlockTags.GOLD_ORES.add(Block.GOLD_ORE, Block.NETHER_GOLD_ORE, Block.DEEPSLATE_GOLD_ORE);
        BlockTags.IRON_ORES.add(Block.IRON_ORE, Block.DEEPSLATE_IRON_ORE);
        BlockTags.DIAMOND_ORES.add(Block.DIAMOND_ORE, Block.DEEPSLATE_DIAMOND_ORE);
        BlockTags.REDSTONE_ORES.add(Block.REDSTONE_ORE, Block.DEEPSLATE_REDSTONE_ORE);
        BlockTags.LAPIS_ORES.add(Block.LAPIS_ORE, Block.DEEPSLATE_LAPIS_ORE);
        BlockTags.COAL_ORES.add(Block.COAL_ORE, Block.DEEPSLATE_COAL_ORE);
        BlockTags.EMERALD_ORES.add(Block.EMERALD_ORE, Block.DEEPSLATE_EMERALD_ORE);
        BlockTags.COPPER_ORES.add(Block.COPPER_ORE, Block.DEEPSLATE_COPPER_ORE);
        BlockTags.CANDLES.add(Block.CANDLE, Block.WHITE_CANDLE, Block.ORANGE_CANDLE, Block.MAGENTA_CANDLE, Block.LIGHT_BLUE_CANDLE, Block.YELLOW_CANDLE, Block.LIME_CANDLE, Block.PINK_CANDLE, Block.GRAY_CANDLE, Block.LIGHT_GRAY_CANDLE, Block.CYAN_CANDLE, Block.PURPLE_CANDLE, Block.BLUE_CANDLE, Block.BROWN_CANDLE, Block.GREEN_CANDLE, Block.RED_CANDLE, Block.BLACK_CANDLE);
        BlockTags.DIRT.add(Block.DIRT, Block.GRASS_BLOCK, Block.PODZOL, Block.COARSE_DIRT, Block.MYCELIUM, Block.ROOTED_DIRT, Block.MOSS_BLOCK, Block.MUD, Block.MUDDY_MANGROVE_ROOTS);
        BlockTags.TERRACOTTA.add(Block.TERRACOTTA, Block.WHITE_TERRACOTTA, Block.ORANGE_TERRACOTTA, Block.MAGENTA_TERRACOTTA, Block.LIGHT_BLUE_TERRACOTTA, Block.YELLOW_TERRACOTTA, Block.LIME_TERRACOTTA, Block.PINK_TERRACOTTA, Block.GRAY_TERRACOTTA, Block.LIGHT_GRAY_TERRACOTTA, Block.CYAN_TERRACOTTA, Block.PURPLE_TERRACOTTA, Block.BLUE_TERRACOTTA, Block.BROWN_TERRACOTTA, Block.GREEN_TERRACOTTA, Block.RED_TERRACOTTA, Block.BLACK_TERRACOTTA);
        BlockTags.BADLANDS_TERRACOTTA.add(Block.TERRACOTTA, Block.WHITE_TERRACOTTA, Block.YELLOW_TERRACOTTA, Block.ORANGE_TERRACOTTA, Block.RED_TERRACOTTA, Block.BROWN_TERRACOTTA, Block.LIGHT_GRAY_TERRACOTTA);
        BlockTags.CONCRETE_POWDER.add(Block.WHITE_CONCRETE_POWDER, Block.ORANGE_CONCRETE_POWDER, Block.MAGENTA_CONCRETE_POWDER, Block.LIGHT_BLUE_CONCRETE_POWDER, Block.YELLOW_CONCRETE_POWDER, Block.LIME_CONCRETE_POWDER, Block.PINK_CONCRETE_POWDER, Block.GRAY_CONCRETE_POWDER, Block.LIGHT_GRAY_CONCRETE_POWDER, Block.CYAN_CONCRETE_POWDER, Block.PURPLE_CONCRETE_POWDER, Block.BLUE_CONCRETE_POWDER, Block.BROWN_CONCRETE_POWDER, Block.GREEN_CONCRETE_POWDER, Block.RED_CONCRETE_POWDER, Block.BLACK_CONCRETE_POWDER);
        BlockTags.FLOWER_POTS.add(Block.FLOWER_POT, Block.POTTED_POPPY, Block.POTTED_BLUE_ORCHID, Block.POTTED_ALLIUM, Block.POTTED_AZURE_BLUET, Block.POTTED_RED_TULIP, Block.POTTED_ORANGE_TULIP, Block.POTTED_WHITE_TULIP, Block.POTTED_PINK_TULIP, Block.POTTED_OXEYE_DAISY, Block.POTTED_DANDELION, Block.POTTED_OAK_SAPLING, Block.POTTED_SPRUCE_SAPLING, Block.POTTED_BIRCH_SAPLING, Block.POTTED_JUNGLE_SAPLING, Block.POTTED_ACACIA_SAPLING, Block.POTTED_DARK_OAK_SAPLING, Block.POTTED_RED_MUSHROOM, Block.POTTED_BROWN_MUSHROOM, Block.POTTED_DEAD_BUSH, Block.POTTED_FERN, Block.POTTED_CACTUS, Block.POTTED_CORNFLOWER, Block.POTTED_LILY_OF_THE_VALLEY, Block.POTTED_WITHER_ROSE, Block.POTTED_BAMBOO, Block.POTTED_CRIMSON_FUNGUS, Block.POTTED_WARPED_FUNGUS, Block.POTTED_CRIMSON_ROOTS, Block.POTTED_WARPED_ROOTS, Block.POTTED_AZALEA_BUSH, Block.POTTED_FLOWERING_AZALEA_BUSH, Block.POTTED_MANGROVE_PROPAGULE, Block.POTTED_CHERRY_SAPLING, Block.POTTED_TORCHFLOWER);
        BlockTags.ICE.add(Block.ICE, Block.PACKED_ICE, Block.BLUE_ICE, Block.FROSTED_ICE);
        BlockTags.VALID_SPAWN.add(Block.GRASS_BLOCK, Block.PODZOL);
        BlockTags.IMPERMEABLE.add(Block.GLASS, Block.WHITE_STAINED_GLASS, Block.ORANGE_STAINED_GLASS, Block.MAGENTA_STAINED_GLASS, Block.LIGHT_BLUE_STAINED_GLASS, Block.YELLOW_STAINED_GLASS, Block.LIME_STAINED_GLASS, Block.PINK_STAINED_GLASS, Block.GRAY_STAINED_GLASS, Block.LIGHT_GRAY_STAINED_GLASS, Block.CYAN_STAINED_GLASS, Block.PURPLE_STAINED_GLASS, Block.BLUE_STAINED_GLASS, Block.BROWN_STAINED_GLASS, Block.GREEN_STAINED_GLASS, Block.RED_STAINED_GLASS, Block.BLACK_STAINED_GLASS, Block.TINTED_GLASS);
        BlockTags.CORAL_BLOCKS.add(Block.TUBE_CORAL_BLOCK, Block.BRAIN_CORAL_BLOCK, Block.BUBBLE_CORAL_BLOCK, Block.FIRE_CORAL_BLOCK, Block.HORN_CORAL_BLOCK);
        BlockTags.WALL_CORALS.add(Block.TUBE_CORAL_WALL_FAN, Block.BRAIN_CORAL_WALL_FAN, Block.BUBBLE_CORAL_WALL_FAN, Block.FIRE_CORAL_WALL_FAN, Block.HORN_CORAL_WALL_FAN);
        BlockTags.CORAL_PLANTS.add(Block.TUBE_CORAL, Block.BRAIN_CORAL, Block.BUBBLE_CORAL, Block.FIRE_CORAL, Block.HORN_CORAL);
        BlockTags.STANDING_SIGNS.add(Block.OAK_SIGN, Block.SPRUCE_SIGN, Block.BIRCH_SIGN, Block.ACACIA_SIGN, Block.JUNGLE_SIGN, Block.DARK_OAK_SIGN, Block.CRIMSON_SIGN, Block.WARPED_SIGN, Block.MANGROVE_SIGN, Block.BAMBOO_SIGN, Block.CHERRY_SIGN);
        BlockTags.WALL_SIGNS.add(Block.OAK_WALL_SIGN, Block.SPRUCE_WALL_SIGN, Block.BIRCH_WALL_SIGN, Block.ACACIA_WALL_SIGN, Block.JUNGLE_WALL_SIGN, Block.DARK_OAK_WALL_SIGN, Block.CRIMSON_WALL_SIGN, Block.WARPED_WALL_SIGN, Block.MANGROVE_WALL_SIGN, Block.BAMBOO_WALL_SIGN, Block.CHERRY_WALL_SIGN);
        BlockTags.CEILING_HANGING_SIGNS.add(Block.OAK_HANGING_SIGN, Block.SPRUCE_HANGING_SIGN, Block.BIRCH_HANGING_SIGN, Block.ACACIA_HANGING_SIGN, Block.CHERRY_HANGING_SIGN, Block.JUNGLE_HANGING_SIGN, Block.DARK_OAK_HANGING_SIGN, Block.CRIMSON_HANGING_SIGN, Block.WARPED_HANGING_SIGN, Block.MANGROVE_HANGING_SIGN, Block.BAMBOO_HANGING_SIGN);
        BlockTags.WALL_HANGING_SIGNS.add(Block.OAK_WALL_HANGING_SIGN, Block.SPRUCE_WALL_HANGING_SIGN, Block.BIRCH_WALL_HANGING_SIGN, Block.ACACIA_WALL_HANGING_SIGN, Block.CHERRY_WALL_HANGING_SIGN, Block.JUNGLE_WALL_HANGING_SIGN, Block.DARK_OAK_WALL_HANGING_SIGN, Block.CRIMSON_WALL_HANGING_SIGN, Block.WARPED_WALL_HANGING_SIGN, Block.MANGROVE_WALL_HANGING_SIGN, Block.BAMBOO_WALL_HANGING_SIGN);
        BlockTags.DRAGON_IMMUNE.add(Block.BARRIER, Block.BEDROCK, Block.END_PORTAL, Block.END_PORTAL_FRAME, Block.END_GATEWAY, Block.COMMAND_BLOCK, Block.REPEATING_COMMAND_BLOCK, Block.CHAIN_COMMAND_BLOCK, Block.STRUCTURE_BLOCK, Block.JIGSAW, Block.MOVING_PISTON, Block.OBSIDIAN, Block.CRYING_OBSIDIAN, Block.END_STONE, Block.IRON_BARS, Block.RESPAWN_ANCHOR, Block.REINFORCED_DEEPSLATE);
        BlockTags.WITHER_IMMUNE.add(Block.BARRIER, Block.BEDROCK, Block.END_PORTAL, Block.END_PORTAL_FRAME, Block.END_GATEWAY, Block.COMMAND_BLOCK, Block.REPEATING_COMMAND_BLOCK, Block.CHAIN_COMMAND_BLOCK, Block.STRUCTURE_BLOCK, Block.JIGSAW, Block.MOVING_PISTON, Block.LIGHT, Block.REINFORCED_DEEPSLATE);
        BlockTags.WITHER_SUMMON_BASE_BLOCKS.add(Block.SOUL_SAND, Block.SOUL_SOIL);
        BlockTags.BEEHIVES.add(Block.BEE_NEST, Block.BEEHIVE);
        BlockTags.CROPS.add(Block.BEETROOTS, Block.CARROTS, Block.POTATOES, Block.WHEAT, Block.MELON_STEM, Block.PUMPKIN_STEM, Block.TORCHFLOWER_CROP, Block.PITCHER_CROP);
        BlockTags.PORTALS.add(Block.NETHER_PORTAL, Block.END_PORTAL, Block.END_GATEWAY);
        BlockTags.FIRE.add(Block.FIRE, Block.SOUL_FIRE);
        BlockTags.NYLIUM.add(Block.CRIMSON_NYLIUM, Block.WARPED_NYLIUM);
        BlockTags.BEACON_BASE_BLOCKS.add(Block.NETHERITE_BLOCK, Block.EMERALD_BLOCK, Block.DIAMOND_BLOCK, Block.GOLD_BLOCK, Block.IRON_BLOCK);
        copy(BlockTags.WITHER_SUMMON_BASE_BLOCKS, BlockTags.SOUL_SPEED_BLOCKS);
        BlockTags.CLIMBABLE.add(Block.LADDER, Block.VINE, Block.SCAFFOLDING, Block.WEEPING_VINES, Block.WEEPING_VINES_PLANT, Block.TWISTING_VINES, Block.TWISTING_VINES_PLANT, Block.CAVE_VINES, Block.CAVE_VINES_PLANT);
        BlockTags.SHULKER_BOXES.add(Block.SHULKER_BOX, Block.BLACK_SHULKER_BOX, Block.BLUE_SHULKER_BOX, Block.BROWN_SHULKER_BOX, Block.CYAN_SHULKER_BOX, Block.GRAY_SHULKER_BOX, Block.GREEN_SHULKER_BOX, Block.LIGHT_BLUE_SHULKER_BOX, Block.LIGHT_GRAY_SHULKER_BOX, Block.LIME_SHULKER_BOX, Block.MAGENTA_SHULKER_BOX, Block.ORANGE_SHULKER_BOX, Block.PINK_SHULKER_BOX, Block.PURPLE_SHULKER_BOX, Block.RED_SHULKER_BOX, Block.WHITE_SHULKER_BOX, Block.YELLOW_SHULKER_BOX);
        BlockTags.HOGLIN_REPELLENTS.add(Block.WARPED_FUNGUS, Block.POTTED_WARPED_FUNGUS, Block.NETHER_PORTAL, Block.RESPAWN_ANCHOR);
        copy(BlockTags.WITHER_SUMMON_BASE_BLOCKS, BlockTags.SOUL_FIRE_BASE_BLOCKS);
        BlockTags.STRIDER_WARM_BLOCKS.add(Block.LAVA);
        BlockTags.CAMPFIRES.add(Block.CAMPFIRE, Block.SOUL_CAMPFIRE);
        BlockTags.FENCE_GATES.add(Block.ACACIA_FENCE_GATE, Block.BIRCH_FENCE_GATE, Block.DARK_OAK_FENCE_GATE, Block.JUNGLE_FENCE_GATE, Block.OAK_FENCE_GATE, Block.SPRUCE_FENCE_GATE, Block.CRIMSON_FENCE_GATE, Block.WARPED_FENCE_GATE, Block.MANGROVE_FENCE_GATE, Block.BAMBOO_FENCE_GATE, Block.CHERRY_FENCE_GATE);
        BlockTags.MUSHROOM_GROW_BLOCK.add(Block.MYCELIUM, Block.PODZOL, Block.CRIMSON_NYLIUM, Block.WARPED_NYLIUM);
        BlockTags.INFINIBURN_OVERWORLD.add(Block.NETHERRACK, Block.MAGMA_BLOCK);
        BlockTags.BASE_STONE_OVERWORLD.add(Block.STONE, Block.GRANITE, Block.DIORITE, Block.ANDESITE, Block.TUFF, Block.DEEPSLATE);
        BlockTags.STONE_ORE_REPLACEABLES.add(Block.STONE, Block.GRANITE, Block.DIORITE, Block.ANDESITE);
        BlockTags.DEEPSLATE_ORE_REPLACEABLES.add(Block.DEEPSLATE, Block.TUFF);
        BlockTags.BASE_STONE_NETHER.add(Block.NETHERRACK, Block.BASALT, Block.BLACKSTONE);
        BlockTags.CANDLE_CAKES.add(Block.CANDLE_CAKE, Block.WHITE_CANDLE_CAKE, Block.ORANGE_CANDLE_CAKE, Block.MAGENTA_CANDLE_CAKE, Block.LIGHT_BLUE_CANDLE_CAKE, Block.YELLOW_CANDLE_CAKE, Block.LIME_CANDLE_CAKE, Block.PINK_CANDLE_CAKE, Block.GRAY_CANDLE_CAKE, Block.LIGHT_GRAY_CANDLE_CAKE, Block.CYAN_CANDLE_CAKE, Block.PURPLE_CANDLE_CAKE, Block.BLUE_CANDLE_CAKE, Block.BROWN_CANDLE_CAKE, Block.GREEN_CANDLE_CAKE, Block.RED_CANDLE_CAKE, Block.BLACK_CANDLE_CAKE);
        BlockTags.CAULDRONS.add(Block.CAULDRON, Block.WATER_CAULDRON, Block.LAVA_CAULDRON, Block.POWDER_SNOW_CAULDRON);
        BlockTags.CRYSTAL_SOUND_BLOCKS.add(Block.AMETHYST_BLOCK, Block.BUDDING_AMETHYST);
        BlockTags.INSIDE_STEP_SOUND_BLOCKS.add(Block.POWDER_SNOW, Block.SCULK_VEIN, Block.GLOW_LICHEN, Block.LILY_PAD, Block.SMALL_AMETHYST_BUD, Block.PINK_PETALS);
        BlockTags.CAVE_VINES.add(Block.CAVE_VINES_PLANT, Block.CAVE_VINES);
        BlockTags.SMALL_DRIPLEAF_PLACEABLE.add(Block.CLAY, Block.MOSS_BLOCK);
        BlockTags.SNOW.add(Block.SNOW, Block.SNOW_BLOCK, Block.POWDER_SNOW);
        BlockTags.MINEABLE_HOE.add(Block.NETHER_WART_BLOCK, Block.WARPED_WART_BLOCK, Block.HAY_BLOCK, Block.DRIED_KELP_BLOCK, Block.TARGET, Block.SHROOMLIGHT, Block.SPONGE, Block.WET_SPONGE, Block.JUNGLE_LEAVES, Block.OAK_LEAVES, Block.SPRUCE_LEAVES, Block.DARK_OAK_LEAVES, Block.ACACIA_LEAVES, Block.BIRCH_LEAVES, Block.AZALEA_LEAVES, Block.FLOWERING_AZALEA_LEAVES, Block.MANGROVE_LEAVES, Block.SCULK_SENSOR, Block.CALIBRATED_SCULK_SENSOR, Block.MOSS_BLOCK, Block.MOSS_CARPET, Block.SCULK, Block.SCULK_CATALYST, Block.SCULK_VEIN, Block.SCULK_SHRIEKER, Block.PINK_PETALS, Block.CHERRY_LEAVES);
        BlockTags.NEEDS_DIAMOND_TOOL.add(Block.OBSIDIAN, Block.CRYING_OBSIDIAN, Block.NETHERITE_BLOCK, Block.RESPAWN_ANCHOR, Block.ANCIENT_DEBRIS);
        BlockTags.NEEDS_IRON_TOOL.add(Block.DIAMOND_BLOCK, Block.DIAMOND_ORE, Block.DEEPSLATE_DIAMOND_ORE, Block.EMERALD_ORE, Block.DEEPSLATE_EMERALD_ORE, Block.EMERALD_BLOCK, Block.GOLD_BLOCK, Block.RAW_GOLD_BLOCK, Block.GOLD_ORE, Block.DEEPSLATE_GOLD_ORE, Block.REDSTONE_ORE, Block.DEEPSLATE_REDSTONE_ORE);
        BlockTags.NEEDS_STONE_TOOL.add(Block.IRON_BLOCK, Block.RAW_IRON_BLOCK, Block.IRON_ORE, Block.DEEPSLATE_IRON_ORE, Block.LAPIS_BLOCK, Block.LAPIS_ORE, Block.DEEPSLATE_LAPIS_ORE, Block.COPPER_BLOCK, Block.RAW_COPPER_BLOCK, Block.COPPER_ORE, Block.DEEPSLATE_COPPER_ORE, Block.CUT_COPPER_SLAB, Block.CUT_COPPER_STAIRS, Block.CUT_COPPER, Block.WEATHERED_COPPER, Block.WEATHERED_CUT_COPPER_SLAB, Block.WEATHERED_CUT_COPPER_STAIRS, Block.WEATHERED_CUT_COPPER, Block.OXIDIZED_COPPER, Block.OXIDIZED_CUT_COPPER_SLAB, Block.OXIDIZED_CUT_COPPER_STAIRS, Block.OXIDIZED_CUT_COPPER, Block.EXPOSED_COPPER, Block.EXPOSED_CUT_COPPER_SLAB, Block.EXPOSED_CUT_COPPER_STAIRS, Block.EXPOSED_CUT_COPPER, Block.WAXED_COPPER_BLOCK, Block.WAXED_CUT_COPPER_SLAB, Block.WAXED_CUT_COPPER_STAIRS, Block.WAXED_CUT_COPPER, Block.WAXED_WEATHERED_COPPER, Block.WAXED_WEATHERED_CUT_COPPER_SLAB, Block.WAXED_WEATHERED_CUT_COPPER_STAIRS, Block.WAXED_WEATHERED_CUT_COPPER, Block.WAXED_EXPOSED_COPPER, Block.WAXED_EXPOSED_CUT_COPPER_SLAB, Block.WAXED_EXPOSED_CUT_COPPER_STAIRS, Block.WAXED_EXPOSED_CUT_COPPER, Block.WAXED_OXIDIZED_COPPER, Block.WAXED_OXIDIZED_CUT_COPPER_SLAB, Block.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Block.WAXED_OXIDIZED_CUT_COPPER, Block.LIGHTNING_ROD, Block.CRAFTER, Block.CHISELED_COPPER, Block.EXPOSED_CHISELED_COPPER, Block.WEATHERED_CHISELED_COPPER, Block.OXIDIZED_CHISELED_COPPER, Block.WAXED_CHISELED_COPPER, Block.WAXED_EXPOSED_CHISELED_COPPER, Block.WAXED_WEATHERED_CHISELED_COPPER, Block.WAXED_OXIDIZED_CHISELED_COPPER, Block.COPPER_GRATE, Block.EXPOSED_COPPER_GRATE, Block.WEATHERED_COPPER_GRATE, Block.OXIDIZED_COPPER_GRATE, Block.WAXED_COPPER_GRATE, Block.WAXED_EXPOSED_COPPER_GRATE, Block.WAXED_WEATHERED_COPPER_GRATE, Block.WAXED_OXIDIZED_COPPER_GRATE, Block.COPPER_BULB, Block.EXPOSED_COPPER_BULB, Block.WEATHERED_COPPER_BULB, Block.OXIDIZED_COPPER_BULB, Block.WAXED_COPPER_BULB, Block.WAXED_EXPOSED_COPPER_BULB, Block.WAXED_WEATHERED_COPPER_BULB, Block.WAXED_OXIDIZED_COPPER_BULB, Block.COPPER_TRAPDOOR, Block.EXPOSED_COPPER_TRAPDOOR, Block.WEATHERED_COPPER_TRAPDOOR, Block.OXIDIZED_COPPER_TRAPDOOR, Block.WAXED_COPPER_TRAPDOOR, Block.WAXED_EXPOSED_COPPER_TRAPDOOR, Block.WAXED_WEATHERED_COPPER_TRAPDOOR, Block.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        copy(null, BlockTags.INCORRECT_FOR_NETHERITE_TOOL);
        copy(null, BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
        BlockTags.FEATURES_CANNOT_REPLACE.add(Block.BEDROCK, Block.SPAWNER, Block.CHEST, Block.END_PORTAL_FRAME, Block.REINFORCED_DEEPSLATE, Block.TRIAL_SPAWNER, Block.VAULT);
        BlockTags.GEODE_INVALID_BLOCKS.add(Block.BEDROCK, Block.WATER, Block.LAVA, Block.ICE, Block.PACKED_ICE, Block.BLUE_ICE);
        BlockTags.FROG_PREFER_JUMP_TO.add(Block.LILY_PAD, Block.BIG_DRIPLEAF);
        BlockTags.ANCIENT_CITY_REPLACEABLE.add(Block.DEEPSLATE, Block.DEEPSLATE_BRICKS, Block.DEEPSLATE_TILES, Block.DEEPSLATE_BRICK_SLAB, Block.DEEPSLATE_TILE_SLAB, Block.DEEPSLATE_BRICK_STAIRS, Block.DEEPSLATE_TILE_WALL, Block.DEEPSLATE_BRICK_WALL, Block.COBBLED_DEEPSLATE, Block.CRACKED_DEEPSLATE_BRICKS, Block.CRACKED_DEEPSLATE_TILES, Block.GRAY_WOOL);
        BlockTags.VIBRATION_RESONATORS.add(Block.AMETHYST_BLOCK);
        BlockTags.ANIMALS_SPAWNABLE_ON.add(Block.GRASS_BLOCK);
        BlockTags.AXOLOTLS_SPAWNABLE_ON.add(Block.CLAY);
        BlockTags.MOOSHROOMS_SPAWNABLE_ON.add(Block.MYCELIUM);
        BlockTags.POLAR_BEARS_SPAWNABLE_ON_ALTERNATE.add(Block.ICE);
        BlockTags.RABBITS_SPAWNABLE_ON.add(Block.GRASS_BLOCK, Block.SNOW, Block.SNOW_BLOCK, Block.SAND);
        BlockTags.FOXES_SPAWNABLE_ON.add(Block.GRASS_BLOCK, Block.SNOW, Block.SNOW_BLOCK, Block.PODZOL, Block.COARSE_DIRT);
        BlockTags.WOLVES_SPAWNABLE_ON.add(Block.GRASS_BLOCK, Block.SNOW, Block.SNOW_BLOCK, Block.COARSE_DIRT, Block.PODZOL);
        BlockTags.FROGS_SPAWNABLE_ON.add(Block.GRASS_BLOCK, Block.MUD, Block.MANGROVE_ROOTS, Block.MUDDY_MANGROVE_ROOTS);
        BlockTags.CONVERTABLE_TO_MUD.add(Block.DIRT, Block.COARSE_DIRT, Block.ROOTED_DIRT);
        BlockTags.MANGROVE_LOGS_CAN_GROW_THROUGH.add(Block.MUD, Block.MUDDY_MANGROVE_ROOTS, Block.MANGROVE_ROOTS, Block.MANGROVE_LEAVES, Block.MANGROVE_LOG, Block.MANGROVE_PROPAGULE, Block.MOSS_CARPET, Block.VINE);
        BlockTags.MANGROVE_ROOTS_CAN_GROW_THROUGH.add(Block.MUD, Block.MUDDY_MANGROVE_ROOTS, Block.MANGROVE_ROOTS, Block.MOSS_CARPET, Block.VINE, Block.MANGROVE_PROPAGULE, Block.SNOW);
        BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON.add(Block.ICE, Block.PACKED_ICE, Block.BARRIER);
        BlockTags.SNOW_LAYER_CAN_SURVIVE_ON.add(Block.HONEY_BLOCK, Block.SOUL_SAND, Block.MUD);
        BlockTags.INVALID_SPAWN_INSIDE.add(Block.END_PORTAL, Block.END_GATEWAY);
        BlockTags.SNIFFER_DIGGABLE_BLOCK.add(Block.DIRT, Block.GRASS_BLOCK, Block.PODZOL, Block.COARSE_DIRT, Block.ROOTED_DIRT, Block.MOSS_BLOCK, Block.MUD, Block.MUDDY_MANGROVE_ROOTS);
        BlockTags.SNIFFER_EGG_HATCH_BOOST.add(Block.MOSS_BLOCK);
        BlockTags.TRAIL_RUINS_REPLACEABLE.add(Block.GRAVEL);
        BlockTags.REPLACEABLE.add(Block.AIR, Block.WATER, Block.LAVA, Block.SHORT_GRASS, Block.FERN, Block.DEAD_BUSH, Block.SEAGRASS, Block.TALL_SEAGRASS, Block.FIRE, Block.SOUL_FIRE, Block.SNOW, Block.VINE, Block.GLOW_LICHEN, Block.LIGHT, Block.TALL_GRASS, Block.LARGE_FERN, Block.STRUCTURE_VOID, Block.VOID_AIR, Block.CAVE_AIR, Block.BUBBLE_COLUMN, Block.WARPED_ROOTS, Block.NETHER_SPROUTS, Block.CRIMSON_ROOTS, Block.HANGING_ROOTS);
        BlockTags.ENCHANTMENT_POWER_PROVIDER.add(Block.BOOKSHELF);
        BlockTags.MAINTAINS_FARMLAND.add(Block.PUMPKIN_STEM, Block.ATTACHED_PUMPKIN_STEM, Block.MELON_STEM, Block.ATTACHED_MELON_STEM, Block.BEETROOTS, Block.CARROTS, Block.POTATOES, Block.TORCHFLOWER_CROP, Block.TORCHFLOWER, Block.PITCHER_CROP, Block.WHEAT);
        BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS.add(Block.BARRIER, Block.BEDROCK);
        BlockTags.BUTTONS.addTag(BlockTags.WOODEN_BUTTONS).addTag(BlockTags.STONE_BUTTONS);
        BlockTags.PRESSURE_PLATES.addTag(BlockTags.WOODEN_PRESSURE_PLATES).addTag(BlockTags.STONE_PRESSURE_PLATES).add(Block.LIGHT_WEIGHTED_PRESSURE_PLATE, Block.HEAVY_WEIGHTED_PRESSURE_PLATE);
        BlockTags.DOORS.addTag(BlockTags.WOODEN_DOORS).add(Block.IRON_DOOR, Block.COPPER_DOOR, Block.EXPOSED_COPPER_DOOR, Block.WEATHERED_COPPER_DOOR, Block.OXIDIZED_COPPER_DOOR, Block.WAXED_COPPER_DOOR, Block.WAXED_EXPOSED_COPPER_DOOR, Block.WAXED_WEATHERED_COPPER_DOOR, Block.WAXED_OXIDIZED_COPPER_DOOR);
        BlockTags.LOGS_THAT_BURN.addTag(BlockTags.DARK_OAK_LOGS).addTag(BlockTags.OAK_LOGS).addTag(BlockTags.ACACIA_LOGS).addTag(BlockTags.BIRCH_LOGS).addTag(BlockTags.JUNGLE_LOGS).addTag(BlockTags.SPRUCE_LOGS).addTag(BlockTags.MANGROVE_LOGS).addTag(BlockTags.CHERRY_LOGS);
        BlockTags.STAIRS.addTag(BlockTags.WOODEN_STAIRS).add(Block.BAMBOO_MOSAIC_STAIRS, Block.COBBLESTONE_STAIRS, Block.SANDSTONE_STAIRS, Block.NETHER_BRICK_STAIRS, Block.STONE_BRICK_STAIRS, Block.BRICK_STAIRS, Block.PURPUR_STAIRS, Block.QUARTZ_STAIRS, Block.RED_SANDSTONE_STAIRS, Block.PRISMARINE_BRICK_STAIRS, Block.PRISMARINE_STAIRS, Block.DARK_PRISMARINE_STAIRS, Block.POLISHED_GRANITE_STAIRS, Block.SMOOTH_RED_SANDSTONE_STAIRS, Block.MOSSY_STONE_BRICK_STAIRS, Block.POLISHED_DIORITE_STAIRS, Block.MOSSY_COBBLESTONE_STAIRS, Block.END_STONE_BRICK_STAIRS, Block.STONE_STAIRS, Block.SMOOTH_SANDSTONE_STAIRS, Block.SMOOTH_QUARTZ_STAIRS, Block.GRANITE_STAIRS, Block.ANDESITE_STAIRS, Block.RED_NETHER_BRICK_STAIRS, Block.POLISHED_ANDESITE_STAIRS, Block.DIORITE_STAIRS, Block.BLACKSTONE_STAIRS, Block.POLISHED_BLACKSTONE_BRICK_STAIRS, Block.POLISHED_BLACKSTONE_STAIRS, Block.COBBLED_DEEPSLATE_STAIRS, Block.POLISHED_DEEPSLATE_STAIRS, Block.DEEPSLATE_TILE_STAIRS, Block.DEEPSLATE_BRICK_STAIRS, Block.OXIDIZED_CUT_COPPER_STAIRS, Block.WEATHERED_CUT_COPPER_STAIRS, Block.EXPOSED_CUT_COPPER_STAIRS, Block.CUT_COPPER_STAIRS, Block.WAXED_WEATHERED_CUT_COPPER_STAIRS, Block.WAXED_EXPOSED_CUT_COPPER_STAIRS, Block.WAXED_CUT_COPPER_STAIRS, Block.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Block.MUD_BRICK_STAIRS, Block.TUFF_STAIRS, Block.POLISHED_TUFF_STAIRS, Block.TUFF_BRICK_STAIRS);
        BlockTags.SLABS.addTag(BlockTags.WOODEN_SLABS).add(Block.BAMBOO_MOSAIC_SLAB, Block.STONE_SLAB, Block.SMOOTH_STONE_SLAB, Block.STONE_BRICK_SLAB, Block.SANDSTONE_SLAB, Block.PURPUR_SLAB, Block.QUARTZ_SLAB, Block.RED_SANDSTONE_SLAB, Block.BRICK_SLAB, Block.COBBLESTONE_SLAB, Block.NETHER_BRICK_SLAB, Block.PETRIFIED_OAK_SLAB, Block.PRISMARINE_SLAB, Block.PRISMARINE_BRICK_SLAB, Block.DARK_PRISMARINE_SLAB, Block.POLISHED_GRANITE_SLAB, Block.SMOOTH_RED_SANDSTONE_SLAB, Block.MOSSY_STONE_BRICK_SLAB, Block.POLISHED_DIORITE_SLAB, Block.MOSSY_COBBLESTONE_SLAB, Block.END_STONE_BRICK_SLAB, Block.SMOOTH_SANDSTONE_SLAB, Block.SMOOTH_QUARTZ_SLAB, Block.GRANITE_SLAB, Block.ANDESITE_SLAB, Block.RED_NETHER_BRICK_SLAB, Block.POLISHED_ANDESITE_SLAB, Block.DIORITE_SLAB, Block.CUT_SANDSTONE_SLAB, Block.CUT_RED_SANDSTONE_SLAB, Block.BLACKSTONE_SLAB, Block.POLISHED_BLACKSTONE_BRICK_SLAB, Block.POLISHED_BLACKSTONE_SLAB, Block.COBBLED_DEEPSLATE_SLAB, Block.POLISHED_DEEPSLATE_SLAB, Block.DEEPSLATE_TILE_SLAB, Block.DEEPSLATE_BRICK_SLAB, Block.WAXED_WEATHERED_CUT_COPPER_SLAB, Block.WAXED_EXPOSED_CUT_COPPER_SLAB, Block.WAXED_CUT_COPPER_SLAB, Block.OXIDIZED_CUT_COPPER_SLAB, Block.WEATHERED_CUT_COPPER_SLAB, Block.EXPOSED_CUT_COPPER_SLAB, Block.CUT_COPPER_SLAB, Block.WAXED_OXIDIZED_CUT_COPPER_SLAB, Block.MUD_BRICK_SLAB, Block.TUFF_SLAB, Block.POLISHED_TUFF_SLAB, Block.TUFF_BRICK_SLAB);
        BlockTags.TRAPDOORS.addTag(BlockTags.WOODEN_TRAPDOORS).add(Block.IRON_TRAPDOOR, Block.COPPER_TRAPDOOR, Block.EXPOSED_COPPER_TRAPDOOR, Block.WEATHERED_COPPER_TRAPDOOR, Block.OXIDIZED_COPPER_TRAPDOOR, Block.WAXED_COPPER_TRAPDOOR, Block.WAXED_EXPOSED_COPPER_TRAPDOOR, Block.WAXED_WEATHERED_COPPER_TRAPDOOR, Block.WAXED_OXIDIZED_COPPER_TRAPDOOR);
        BlockTags.FENCES.addTag(BlockTags.WOODEN_FENCES).add(Block.NETHER_BRICK_FENCE);
        BlockTags.FLOWERS.addTag(BlockTags.SMALL_FLOWERS).addTag(BlockTags.TALL_FLOWERS).add(Block.FLOWERING_AZALEA_LEAVES, Block.FLOWERING_AZALEA, Block.MANGROVE_PROPAGULE, Block.CHERRY_LEAVES, Block.PINK_PETALS, Block.CHORUS_FLOWER, Block.SPORE_BLOSSOM);
        BlockTags.ENDERMAN_HOLDABLE.addTag(BlockTags.SMALL_FLOWERS).addTag(BlockTags.DIRT).add(Block.SAND, Block.RED_SAND, Block.GRAVEL, Block.BROWN_MUSHROOM, Block.RED_MUSHROOM, Block.TNT, Block.CACTUS, Block.CLAY, Block.PUMPKIN, Block.CARVED_PUMPKIN, Block.MELON, Block.CRIMSON_FUNGUS, Block.CRIMSON_NYLIUM, Block.CRIMSON_ROOTS, Block.WARPED_FUNGUS, Block.WARPED_NYLIUM, Block.WARPED_ROOTS);
        BlockTags.CORALS.addTag(BlockTags.CORAL_PLANTS).add(Block.TUBE_CORAL_FAN, Block.BRAIN_CORAL_FAN, Block.BUBBLE_CORAL_FAN, Block.FIRE_CORAL_FAN, Block.HORN_CORAL_FAN);
        BlockTags.BAMBOO_PLANTABLE_ON.addTag(BlockTags.SAND).addTag(BlockTags.DIRT).add(Block.BAMBOO, Block.BAMBOO_SAPLING, Block.GRAVEL, Block.SUSPICIOUS_GRAVEL);
        BlockTags.SIGNS.addTag(BlockTags.STANDING_SIGNS).addTag(BlockTags.WALL_SIGNS);
        BlockTags.ALL_HANGING_SIGNS.addTag(BlockTags.CEILING_HANGING_SIGNS).addTag(BlockTags.WALL_HANGING_SIGNS);
        BlockTags.DRAGON_TRANSPARENT.addTag(BlockTags.FIRE).add(Block.LIGHT);
        BlockTags.BEE_GROWABLES.addTag(BlockTags.CROPS).add(Block.SWEET_BERRY_BUSH, Block.CAVE_VINES, Block.CAVE_VINES_PLANT);
        BlockTags.FALL_DAMAGE_RESETTING.addTag(BlockTags.CLIMBABLE).add(Block.SWEET_BERRY_BUSH, Block.COBWEB);
        BlockTags.GUARDED_BY_PIGLINS.addTag(BlockTags.SHULKER_BOXES).addTag(BlockTags.GOLD_ORES).add(Block.GOLD_BLOCK, Block.BARREL, Block.CHEST, Block.ENDER_CHEST, Block.GILDED_BLACKSTONE, Block.TRAPPED_CHEST, Block.RAW_GOLD_BLOCK);
        BlockTags.PREVENT_MOB_SPAWNING_INSIDE.addTag(BlockTags.RAILS);
        BlockTags.UNSTABLE_BOTTOM_CENTER.addTag(BlockTags.FENCE_GATES);
        BlockTags.INFINIBURN_NETHER.addTag(BlockTags.INFINIBURN_OVERWORLD);
        BlockTags.INFINIBURN_END.addTag(BlockTags.INFINIBURN_OVERWORLD).add(Block.BEDROCK);
        BlockTags.OVERWORLD_CARVER_REPLACEABLES.addTag(BlockTags.BASE_STONE_OVERWORLD).addTag(BlockTags.DIRT).addTag(BlockTags.SAND).addTag(BlockTags.TERRACOTTA).addTag(BlockTags.IRON_ORES).addTag(BlockTags.COPPER_ORES).add(Block.WATER, Block.GRAVEL, Block.SUSPICIOUS_GRAVEL, Block.SANDSTONE, Block.RED_SANDSTONE, Block.CALCITE, Block.SNOW, Block.PACKED_ICE, Block.RAW_IRON_BLOCK, Block.RAW_COPPER_BLOCK);
        BlockTags.NETHER_CARVER_REPLACEABLES.addTag(BlockTags.BASE_STONE_OVERWORLD).addTag(BlockTags.BASE_STONE_NETHER).addTag(BlockTags.DIRT).addTag(BlockTags.NYLIUM).addTag(BlockTags.WART_BLOCKS).add(Block.SOUL_SAND, Block.SOUL_SOIL);
        BlockTags.COMBINATION_STEP_SOUND_BLOCKS.addTag(BlockTags.WOOL_CARPETS).add(Block.MOSS_CARPET, Block.SNOW, Block.NETHER_SPROUTS, Block.WARPED_ROOTS, Block.CRIMSON_ROOTS);
        BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS.addTag(BlockTags.SAND).addTag(BlockTags.CONCRETE_POWDER);
        BlockTags.OCCLUDES_VIBRATION_SIGNALS.addTag(BlockTags.WOOL);
        BlockTags.DAMPENS_VIBRATIONS.addTag(BlockTags.WOOL).addTag(BlockTags.WOOL_CARPETS);
        BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS.addTag(BlockTags.BASE_STONE_OVERWORLD);
        BlockTags.MOSS_REPLACEABLE.addTag(BlockTags.BASE_STONE_OVERWORLD).addTag(BlockTags.CAVE_VINES).addTag(BlockTags.DIRT);
        BlockTags.AZALEA_ROOT_REPLACEABLE.addTag(BlockTags.BASE_STONE_OVERWORLD).addTag(BlockTags.DIRT).addTag(BlockTags.TERRACOTTA).add(Block.RED_SAND, Block.CLAY, Block.GRAVEL, Block.SAND, Block.SNOW_BLOCK, Block.POWDER_SNOW);
        BlockTags.BIG_DRIPLEAF_PLACEABLE.addTag(BlockTags.SMALL_DRIPLEAF_PLACEABLE).addTag(BlockTags.DIRT).add(Block.FARMLAND);
        BlockTags.MINEABLE_PICKAXE.addTag(BlockTags.STONE_BUTTONS).addTag(BlockTags.WALLS).addTag(BlockTags.SHULKER_BOXES).addTag(BlockTags.ANVIL).addTag(BlockTags.CAULDRONS).addTag(BlockTags.RAILS).add(Block.STONE, Block.GRANITE, Block.POLISHED_GRANITE, Block.DIORITE, Block.POLISHED_DIORITE, Block.ANDESITE, Block.POLISHED_ANDESITE, Block.COBBLESTONE, Block.GOLD_ORE, Block.DEEPSLATE_GOLD_ORE, Block.IRON_ORE, Block.DEEPSLATE_IRON_ORE, Block.COAL_ORE, Block.DEEPSLATE_COAL_ORE, Block.NETHER_GOLD_ORE, Block.LAPIS_ORE, Block.DEEPSLATE_LAPIS_ORE, Block.LAPIS_BLOCK, Block.DISPENSER, Block.SANDSTONE, Block.CHISELED_SANDSTONE, Block.CUT_SANDSTONE, Block.GOLD_BLOCK, Block.IRON_BLOCK, Block.BRICKS, Block.MOSSY_COBBLESTONE, Block.OBSIDIAN, Block.SPAWNER, Block.DIAMOND_ORE, Block.DEEPSLATE_DIAMOND_ORE, Block.DIAMOND_BLOCK, Block.FURNACE, Block.COBBLESTONE_STAIRS, Block.STONE_PRESSURE_PLATE, Block.IRON_DOOR, Block.REDSTONE_ORE, Block.DEEPSLATE_REDSTONE_ORE, Block.NETHERRACK, Block.BASALT, Block.POLISHED_BASALT, Block.STONE_BRICKS, Block.MOSSY_STONE_BRICKS, Block.CRACKED_STONE_BRICKS, Block.CHISELED_STONE_BRICKS, Block.IRON_BARS, Block.CHAIN, Block.BRICK_STAIRS, Block.STONE_BRICK_STAIRS, Block.NETHER_BRICKS, Block.NETHER_BRICK_FENCE, Block.NETHER_BRICK_STAIRS, Block.ENCHANTING_TABLE, Block.BREWING_STAND, Block.END_STONE, Block.SANDSTONE_STAIRS, Block.EMERALD_ORE, Block.DEEPSLATE_EMERALD_ORE, Block.ENDER_CHEST, Block.EMERALD_BLOCK, Block.LIGHT_WEIGHTED_PRESSURE_PLATE, Block.HEAVY_WEIGHTED_PRESSURE_PLATE, Block.REDSTONE_BLOCK, Block.NETHER_QUARTZ_ORE, Block.HOPPER, Block.QUARTZ_BLOCK, Block.CHISELED_QUARTZ_BLOCK, Block.QUARTZ_PILLAR, Block.QUARTZ_STAIRS, Block.DROPPER, Block.WHITE_TERRACOTTA, Block.ORANGE_TERRACOTTA, Block.MAGENTA_TERRACOTTA, Block.LIGHT_BLUE_TERRACOTTA, Block.YELLOW_TERRACOTTA, Block.LIME_TERRACOTTA, Block.PINK_TERRACOTTA, Block.GRAY_TERRACOTTA, Block.LIGHT_GRAY_TERRACOTTA, Block.CYAN_TERRACOTTA, Block.PURPLE_TERRACOTTA, Block.BLUE_TERRACOTTA, Block.BROWN_TERRACOTTA, Block.GREEN_TERRACOTTA, Block.RED_TERRACOTTA, Block.BLACK_TERRACOTTA, Block.IRON_TRAPDOOR, Block.PRISMARINE, Block.PRISMARINE_BRICKS, Block.DARK_PRISMARINE, Block.PRISMARINE_STAIRS, Block.PRISMARINE_BRICK_STAIRS, Block.DARK_PRISMARINE_STAIRS, Block.PRISMARINE_SLAB, Block.PRISMARINE_BRICK_SLAB, Block.DARK_PRISMARINE_SLAB, Block.TERRACOTTA, Block.COAL_BLOCK, Block.RED_SANDSTONE, Block.CHISELED_RED_SANDSTONE, Block.CUT_RED_SANDSTONE, Block.RED_SANDSTONE_STAIRS, Block.STONE_SLAB, Block.SMOOTH_STONE_SLAB, Block.SANDSTONE_SLAB, Block.CUT_SANDSTONE_SLAB, Block.PETRIFIED_OAK_SLAB, Block.COBBLESTONE_SLAB, Block.BRICK_SLAB, Block.STONE_BRICK_SLAB, Block.NETHER_BRICK_SLAB, Block.QUARTZ_SLAB, Block.RED_SANDSTONE_SLAB, Block.CUT_RED_SANDSTONE_SLAB, Block.PURPUR_SLAB, Block.SMOOTH_STONE, Block.SMOOTH_SANDSTONE, Block.SMOOTH_QUARTZ, Block.SMOOTH_RED_SANDSTONE, Block.PURPUR_BLOCK, Block.PURPUR_PILLAR, Block.PURPUR_STAIRS, Block.END_STONE_BRICKS, Block.MAGMA_BLOCK, Block.RED_NETHER_BRICKS, Block.BONE_BLOCK, Block.OBSERVER, Block.WHITE_GLAZED_TERRACOTTA, Block.ORANGE_GLAZED_TERRACOTTA, Block.MAGENTA_GLAZED_TERRACOTTA, Block.LIGHT_BLUE_GLAZED_TERRACOTTA, Block.YELLOW_GLAZED_TERRACOTTA, Block.LIME_GLAZED_TERRACOTTA, Block.PINK_GLAZED_TERRACOTTA, Block.GRAY_GLAZED_TERRACOTTA, Block.LIGHT_GRAY_GLAZED_TERRACOTTA, Block.CYAN_GLAZED_TERRACOTTA, Block.PURPLE_GLAZED_TERRACOTTA, Block.BLUE_GLAZED_TERRACOTTA, Block.BROWN_GLAZED_TERRACOTTA, Block.GREEN_GLAZED_TERRACOTTA, Block.RED_GLAZED_TERRACOTTA, Block.BLACK_GLAZED_TERRACOTTA, Block.WHITE_CONCRETE, Block.ORANGE_CONCRETE, Block.MAGENTA_CONCRETE, Block.LIGHT_BLUE_CONCRETE, Block.YELLOW_CONCRETE, Block.LIME_CONCRETE, Block.PINK_CONCRETE, Block.GRAY_CONCRETE, Block.LIGHT_GRAY_CONCRETE, Block.CYAN_CONCRETE, Block.PURPLE_CONCRETE, Block.BLUE_CONCRETE, Block.BROWN_CONCRETE, Block.GREEN_CONCRETE, Block.RED_CONCRETE, Block.BLACK_CONCRETE, Block.DEAD_TUBE_CORAL_BLOCK, Block.DEAD_BRAIN_CORAL_BLOCK, Block.DEAD_BUBBLE_CORAL_BLOCK, Block.DEAD_FIRE_CORAL_BLOCK, Block.DEAD_HORN_CORAL_BLOCK, Block.TUBE_CORAL_BLOCK, Block.BRAIN_CORAL_BLOCK, Block.BUBBLE_CORAL_BLOCK, Block.FIRE_CORAL_BLOCK, Block.HORN_CORAL_BLOCK, Block.DEAD_TUBE_CORAL, Block.DEAD_BRAIN_CORAL, Block.DEAD_BUBBLE_CORAL, Block.DEAD_FIRE_CORAL, Block.DEAD_HORN_CORAL, Block.DEAD_TUBE_CORAL_FAN, Block.DEAD_BRAIN_CORAL_FAN, Block.DEAD_BUBBLE_CORAL_FAN, Block.DEAD_FIRE_CORAL_FAN, Block.DEAD_HORN_CORAL_FAN, Block.DEAD_TUBE_CORAL_WALL_FAN, Block.DEAD_BRAIN_CORAL_WALL_FAN, Block.DEAD_BUBBLE_CORAL_WALL_FAN, Block.DEAD_FIRE_CORAL_WALL_FAN, Block.DEAD_HORN_CORAL_WALL_FAN, Block.POLISHED_GRANITE_STAIRS, Block.SMOOTH_RED_SANDSTONE_STAIRS, Block.MOSSY_STONE_BRICK_STAIRS, Block.POLISHED_DIORITE_STAIRS, Block.MOSSY_COBBLESTONE_STAIRS, Block.END_STONE_BRICK_STAIRS, Block.STONE_STAIRS, Block.SMOOTH_SANDSTONE_STAIRS, Block.SMOOTH_QUARTZ_STAIRS, Block.GRANITE_STAIRS, Block.ANDESITE_STAIRS, Block.RED_NETHER_BRICK_STAIRS, Block.POLISHED_ANDESITE_STAIRS, Block.DIORITE_STAIRS, Block.POLISHED_GRANITE_SLAB, Block.SMOOTH_RED_SANDSTONE_SLAB, Block.MOSSY_STONE_BRICK_SLAB, Block.POLISHED_DIORITE_SLAB, Block.MOSSY_COBBLESTONE_SLAB, Block.END_STONE_BRICK_SLAB, Block.SMOOTH_SANDSTONE_SLAB, Block.SMOOTH_QUARTZ_SLAB, Block.GRANITE_SLAB, Block.ANDESITE_SLAB, Block.RED_NETHER_BRICK_SLAB, Block.POLISHED_ANDESITE_SLAB, Block.DIORITE_SLAB, Block.SMOKER, Block.BLAST_FURNACE, Block.GRINDSTONE, Block.STONECUTTER, Block.BELL, Block.LANTERN, Block.SOUL_LANTERN, Block.WARPED_NYLIUM, Block.CRIMSON_NYLIUM, Block.NETHERITE_BLOCK, Block.ANCIENT_DEBRIS, Block.CRYING_OBSIDIAN, Block.RESPAWN_ANCHOR, Block.LODESTONE, Block.BLACKSTONE, Block.BLACKSTONE_STAIRS, Block.BLACKSTONE_SLAB, Block.POLISHED_BLACKSTONE, Block.POLISHED_BLACKSTONE_BRICKS, Block.CRACKED_POLISHED_BLACKSTONE_BRICKS, Block.CHISELED_POLISHED_BLACKSTONE, Block.POLISHED_BLACKSTONE_BRICK_SLAB, Block.POLISHED_BLACKSTONE_BRICK_STAIRS, Block.GILDED_BLACKSTONE, Block.POLISHED_BLACKSTONE_STAIRS, Block.POLISHED_BLACKSTONE_SLAB, Block.POLISHED_BLACKSTONE_PRESSURE_PLATE, Block.CHISELED_NETHER_BRICKS, Block.CRACKED_NETHER_BRICKS, Block.QUARTZ_BRICKS, Block.TUFF, Block.CALCITE, Block.OXIDIZED_COPPER, Block.WEATHERED_COPPER, Block.EXPOSED_COPPER, Block.COPPER_BLOCK, Block.COPPER_ORE, Block.DEEPSLATE_COPPER_ORE, Block.OXIDIZED_CUT_COPPER, Block.WEATHERED_CUT_COPPER, Block.EXPOSED_CUT_COPPER, Block.CUT_COPPER, Block.OXIDIZED_CUT_COPPER_STAIRS, Block.WEATHERED_CUT_COPPER_STAIRS, Block.EXPOSED_CUT_COPPER_STAIRS, Block.CUT_COPPER_STAIRS, Block.OXIDIZED_CUT_COPPER_SLAB, Block.WEATHERED_CUT_COPPER_SLAB, Block.EXPOSED_CUT_COPPER_SLAB, Block.CUT_COPPER_SLAB, Block.WAXED_COPPER_BLOCK, Block.WAXED_WEATHERED_COPPER, Block.WAXED_EXPOSED_COPPER, Block.WAXED_OXIDIZED_COPPER, Block.WAXED_OXIDIZED_CUT_COPPER, Block.WAXED_WEATHERED_CUT_COPPER, Block.WAXED_EXPOSED_CUT_COPPER, Block.WAXED_CUT_COPPER, Block.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Block.WAXED_WEATHERED_CUT_COPPER_STAIRS, Block.WAXED_EXPOSED_CUT_COPPER_STAIRS, Block.WAXED_CUT_COPPER_STAIRS, Block.WAXED_OXIDIZED_CUT_COPPER_SLAB, Block.WAXED_WEATHERED_CUT_COPPER_SLAB, Block.WAXED_EXPOSED_CUT_COPPER_SLAB, Block.WAXED_CUT_COPPER_SLAB, Block.LIGHTNING_ROD, Block.POINTED_DRIPSTONE, Block.DRIPSTONE_BLOCK, Block.DEEPSLATE, Block.COBBLED_DEEPSLATE, Block.COBBLED_DEEPSLATE_STAIRS, Block.COBBLED_DEEPSLATE_SLAB, Block.POLISHED_DEEPSLATE, Block.POLISHED_DEEPSLATE_STAIRS, Block.POLISHED_DEEPSLATE_SLAB, Block.DEEPSLATE_TILES, Block.DEEPSLATE_TILE_STAIRS, Block.DEEPSLATE_TILE_SLAB, Block.DEEPSLATE_BRICKS, Block.DEEPSLATE_BRICK_STAIRS, Block.DEEPSLATE_BRICK_SLAB, Block.CHISELED_DEEPSLATE, Block.CRACKED_DEEPSLATE_BRICKS, Block.CRACKED_DEEPSLATE_TILES, Block.SMOOTH_BASALT, Block.RAW_IRON_BLOCK, Block.RAW_COPPER_BLOCK, Block.RAW_GOLD_BLOCK, Block.ICE, Block.PACKED_ICE, Block.BLUE_ICE, Block.PISTON, Block.STICKY_PISTON, Block.PISTON_HEAD, Block.AMETHYST_CLUSTER, Block.SMALL_AMETHYST_BUD, Block.MEDIUM_AMETHYST_BUD, Block.LARGE_AMETHYST_BUD, Block.AMETHYST_BLOCK, Block.BUDDING_AMETHYST, Block.INFESTED_COBBLESTONE, Block.INFESTED_CHISELED_STONE_BRICKS, Block.INFESTED_CRACKED_STONE_BRICKS, Block.INFESTED_DEEPSLATE, Block.INFESTED_STONE, Block.INFESTED_MOSSY_STONE_BRICKS, Block.INFESTED_STONE_BRICKS, Block.CONDUIT, Block.MUD_BRICKS, Block.MUD_BRICK_STAIRS, Block.MUD_BRICK_SLAB, Block.PACKED_MUD, Block.CRAFTER, Block.TUFF_SLAB, Block.TUFF_STAIRS, Block.TUFF_WALL, Block.CHISELED_TUFF, Block.POLISHED_TUFF, Block.POLISHED_TUFF_SLAB, Block.POLISHED_TUFF_STAIRS, Block.POLISHED_TUFF_WALL, Block.TUFF_BRICKS, Block.TUFF_BRICK_SLAB, Block.TUFF_BRICK_STAIRS, Block.TUFF_BRICK_WALL, Block.CHISELED_TUFF_BRICKS, Block.CHISELED_COPPER, Block.EXPOSED_CHISELED_COPPER, Block.WEATHERED_CHISELED_COPPER, Block.OXIDIZED_CHISELED_COPPER, Block.WAXED_CHISELED_COPPER, Block.WAXED_EXPOSED_CHISELED_COPPER, Block.WAXED_WEATHERED_CHISELED_COPPER, Block.WAXED_OXIDIZED_CHISELED_COPPER, Block.COPPER_GRATE, Block.EXPOSED_COPPER_GRATE, Block.WEATHERED_COPPER_GRATE, Block.OXIDIZED_COPPER_GRATE, Block.WAXED_COPPER_GRATE, Block.WAXED_EXPOSED_COPPER_GRATE, Block.WAXED_WEATHERED_COPPER_GRATE, Block.WAXED_OXIDIZED_COPPER_GRATE, Block.COPPER_BULB, Block.EXPOSED_COPPER_BULB, Block.WEATHERED_COPPER_BULB, Block.OXIDIZED_COPPER_BULB, Block.WAXED_COPPER_BULB, Block.WAXED_EXPOSED_COPPER_BULB, Block.WAXED_WEATHERED_COPPER_BULB, Block.WAXED_OXIDIZED_COPPER_BULB, Block.COPPER_DOOR, Block.EXPOSED_COPPER_DOOR, Block.WEATHERED_COPPER_DOOR, Block.OXIDIZED_COPPER_DOOR, Block.WAXED_COPPER_DOOR, Block.WAXED_EXPOSED_COPPER_DOOR, Block.WAXED_WEATHERED_COPPER_DOOR, Block.WAXED_OXIDIZED_COPPER_DOOR, Block.COPPER_TRAPDOOR, Block.EXPOSED_COPPER_TRAPDOOR, Block.WEATHERED_COPPER_TRAPDOOR, Block.OXIDIZED_COPPER_TRAPDOOR, Block.WAXED_COPPER_TRAPDOOR, Block.WAXED_EXPOSED_COPPER_TRAPDOOR, Block.WAXED_WEATHERED_COPPER_TRAPDOOR, Block.WAXED_OXIDIZED_COPPER_TRAPDOOR, Block.HEAVY_CORE);
        BlockTags.MINEABLE_SHOVEL.addTag(BlockTags.CONCRETE_POWDER).add(Block.CLAY, Block.DIRT, Block.COARSE_DIRT, Block.PODZOL, Block.FARMLAND, Block.GRASS_BLOCK, Block.GRAVEL, Block.MYCELIUM, Block.SAND, Block.RED_SAND, Block.SNOW_BLOCK, Block.SNOW, Block.SOUL_SAND, Block.DIRT_PATH, Block.SOUL_SOIL, Block.ROOTED_DIRT, Block.MUDDY_MANGROVE_ROOTS, Block.MUD, Block.SUSPICIOUS_SAND, Block.SUSPICIOUS_GRAVEL);
        BlockTags.SWORD_EFFICIENT.addTag(BlockTags.LEAVES).addTag(BlockTags.SAPLINGS).addTag(BlockTags.SMALL_FLOWERS).addTag(BlockTags.CROPS).add(Block.SHORT_GRASS, Block.FERN, Block.DEAD_BUSH, Block.VINE, Block.GLOW_LICHEN, Block.SUNFLOWER, Block.LILAC, Block.ROSE_BUSH, Block.PEONY, Block.TALL_GRASS, Block.LARGE_FERN, Block.HANGING_ROOTS, Block.PITCHER_PLANT, Block.BROWN_MUSHROOM, Block.RED_MUSHROOM, Block.SUGAR_CANE, Block.PUMPKIN, Block.CARVED_PUMPKIN, Block.JACK_O_LANTERN, Block.MELON, Block.ATTACHED_PUMPKIN_STEM, Block.ATTACHED_MELON_STEM, Block.LILY_PAD, Block.COCOA, Block.PITCHER_CROP, Block.SWEET_BERRY_BUSH, Block.CAVE_VINES, Block.CAVE_VINES_PLANT, Block.SPORE_BLOSSOM, Block.MOSS_CARPET, Block.PINK_PETALS, Block.BIG_DRIPLEAF, Block.BIG_DRIPLEAF_STEM, Block.SMALL_DRIPLEAF, Block.NETHER_WART, Block.WARPED_FUNGUS, Block.WARPED_ROOTS, Block.NETHER_SPROUTS, Block.CRIMSON_FUNGUS, Block.WEEPING_VINES, Block.WEEPING_VINES_PLANT, Block.TWISTING_VINES, Block.TWISTING_VINES_PLANT, Block.CRIMSON_ROOTS, Block.CHORUS_PLANT, Block.CHORUS_FLOWER);
        BlockTags.INCORRECT_FOR_IRON_TOOL.addTag(BlockTags.NEEDS_DIAMOND_TOOL);
        BlockTags.INCORRECT_FOR_STONE_TOOL.addTag(BlockTags.NEEDS_DIAMOND_TOOL).addTag(BlockTags.NEEDS_IRON_TOOL);
        BlockTags.INCORRECT_FOR_GOLD_TOOL.addTag(BlockTags.NEEDS_DIAMOND_TOOL).addTag(BlockTags.NEEDS_IRON_TOOL).addTag(BlockTags.NEEDS_STONE_TOOL);
        copy(BlockTags.INCORRECT_FOR_GOLD_TOOL, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
        BlockTags.SCULK_REPLACEABLE.addTag(BlockTags.BASE_STONE_OVERWORLD).addTag(BlockTags.DIRT).addTag(BlockTags.TERRACOTTA).addTag(BlockTags.NYLIUM).addTag(BlockTags.BASE_STONE_NETHER).add(Block.SAND, Block.RED_SAND, Block.GRAVEL, Block.SOUL_SAND, Block.SOUL_SOIL, Block.CALCITE, Block.SMOOTH_BASALT, Block.CLAY, Block.DRIPSTONE_BLOCK, Block.END_STONE, Block.RED_SANDSTONE, Block.SANDSTONE);
        BlockTags.ARMADILLO_SPAWNABLE_ON.addTag(BlockTags.ANIMALS_SPAWNABLE_ON).addTag(BlockTags.BADLANDS_TERRACOTTA).add(Block.RED_SAND, Block.COARSE_DIRT);
        BlockTags.GOATS_SPAWNABLE_ON.addTag(BlockTags.ANIMALS_SPAWNABLE_ON).add(Block.STONE, Block.SNOW, Block.SNOW_BLOCK, Block.PACKED_ICE, Block.GRAVEL);
        BlockTags.AZALEA_GROWS_ON.addTag(BlockTags.DIRT).addTag(BlockTags.SAND).addTag(BlockTags.TERRACOTTA).add(Block.SNOW_BLOCK, Block.POWDER_SNOW);
        BlockTags.DEAD_BUSH_MAY_PLACE_ON.addTag(BlockTags.SAND).addTag(BlockTags.TERRACOTTA).addTag(BlockTags.DIRT);
        BlockTags.SNAPS_GOAT_HORN.addTag(BlockTags.OVERWORLD_NATURAL_LOGS).add(Block.STONE, Block.PACKED_ICE, Block.IRON_ORE, Block.COAL_ORE, Block.COPPER_ORE, Block.EMERALD_ORE);
        BlockTags.REPLACEABLE_BY_TREES.addTag(BlockTags.LEAVES).add(Block.SHORT_GRASS, Block.FERN, Block.DEAD_BUSH, Block.VINE, Block.GLOW_LICHEN, Block.SUNFLOWER, Block.LILAC, Block.ROSE_BUSH, Block.PEONY, Block.TALL_GRASS, Block.LARGE_FERN, Block.HANGING_ROOTS, Block.PITCHER_PLANT, Block.WATER, Block.SEAGRASS, Block.TALL_SEAGRASS, Block.WARPED_ROOTS, Block.NETHER_SPROUTS, Block.CRIMSON_ROOTS);
        BlockTags.ENCHANTMENT_POWER_TRANSMITTER.addTag(BlockTags.REPLACEABLE);
        BlockTags.DOES_NOT_BLOCK_HOPPERS.addTag(BlockTags.BEEHIVES);
        BlockTags.LOGS.addTag(BlockTags.LOGS_THAT_BURN).addTag(BlockTags.CRIMSON_STEMS).addTag(BlockTags.WARPED_STEMS);
        BlockTags.UNDERWATER_BONEMEALS.addTag(BlockTags.CORALS).addTag(BlockTags.WALL_CORALS).add(Block.SEAGRASS);
        BlockTags.ALL_SIGNS.addTag(BlockTags.SIGNS).addTag(BlockTags.ALL_HANGING_SIGNS);
        BlockTags.WALL_POST_OVERRIDE.addTag(BlockTags.SIGNS).addTag(BlockTags.BANNERS).addTag(BlockTags.PRESSURE_PLATES).add(Block.TORCH, Block.SOUL_TORCH, Block.REDSTONE_TORCH, Block.TRIPWIRE);
        BlockTags.LUSH_GROUND_REPLACEABLE.addTag(BlockTags.MOSS_REPLACEABLE).add(Block.CLAY, Block.GRAVEL, Block.SAND);
        BlockTags.SCULK_REPLACEABLE_WORLD_GEN.addTag(BlockTags.SCULK_REPLACEABLE).add(Block.DEEPSLATE_BRICKS, Block.DEEPSLATE_TILES, Block.COBBLED_DEEPSLATE, Block.CRACKED_DEEPSLATE_BRICKS, Block.CRACKED_DEEPSLATE_TILES, Block.POLISHED_DEEPSLATE);
        BlockTags.COMPLETES_FIND_TREE_TUTORIAL.addTag(BlockTags.LOGS).addTag(BlockTags.LEAVES).addTag(BlockTags.WART_BLOCKS);
        BlockTags.MINEABLE_AXE.addTag(BlockTags.BANNERS).addTag(BlockTags.FENCE_GATES).addTag(BlockTags.LOGS).addTag(BlockTags.PLANKS).addTag(BlockTags.SAPLINGS).addTag(BlockTags.SIGNS).addTag(BlockTags.WOODEN_BUTTONS).addTag(BlockTags.WOODEN_DOORS).addTag(BlockTags.WOODEN_FENCES).addTag(BlockTags.WOODEN_PRESSURE_PLATES).addTag(BlockTags.WOODEN_SLABS).addTag(BlockTags.WOODEN_STAIRS).addTag(BlockTags.WOODEN_TRAPDOORS).addTag(BlockTags.ALL_HANGING_SIGNS).addTag(BlockTags.BAMBOO_BLOCKS).add(Block.NOTE_BLOCK, Block.ATTACHED_MELON_STEM, Block.ATTACHED_PUMPKIN_STEM, Block.AZALEA, Block.BAMBOO, Block.BARREL, Block.BEE_NEST, Block.BEEHIVE, Block.BEETROOTS, Block.BIG_DRIPLEAF_STEM, Block.BIG_DRIPLEAF, Block.BOOKSHELF, Block.BROWN_MUSHROOM_BLOCK, Block.BROWN_MUSHROOM, Block.CAMPFIRE, Block.CARROTS, Block.CARTOGRAPHY_TABLE, Block.CARVED_PUMPKIN, Block.CAVE_VINES_PLANT, Block.CAVE_VINES, Block.CHEST, Block.CHORUS_FLOWER, Block.CHORUS_PLANT, Block.COCOA, Block.COMPOSTER, Block.CRAFTING_TABLE, Block.CRIMSON_FUNGUS, Block.DAYLIGHT_DETECTOR, Block.DEAD_BUSH, Block.FERN, Block.FLETCHING_TABLE, Block.GLOW_LICHEN, Block.SHORT_GRASS, Block.HANGING_ROOTS, Block.JACK_O_LANTERN, Block.JUKEBOX, Block.LADDER, Block.LARGE_FERN, Block.LECTERN, Block.LILY_PAD, Block.LOOM, Block.MELON_STEM, Block.MELON, Block.MUSHROOM_STEM, Block.NETHER_WART, Block.POTATOES, Block.PUMPKIN_STEM, Block.PUMPKIN, Block.RED_MUSHROOM_BLOCK, Block.RED_MUSHROOM, Block.SCAFFOLDING, Block.SMALL_DRIPLEAF, Block.SMITHING_TABLE, Block.SOUL_CAMPFIRE, Block.SPORE_BLOSSOM, Block.SUGAR_CANE, Block.SWEET_BERRY_BUSH, Block.TALL_GRASS, Block.TRAPPED_CHEST, Block.TWISTING_VINES_PLANT, Block.TWISTING_VINES, Block.VINE, Block.WARPED_FUNGUS, Block.WEEPING_VINES_PLANT, Block.WEEPING_VINES, Block.WHEAT, Block.MANGROVE_ROOTS, Block.BAMBOO_MOSAIC, Block.BAMBOO_MOSAIC_SLAB, Block.BAMBOO_MOSAIC_STAIRS, Block.CHISELED_BOOKSHELF);
        BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE.addTag(BlockTags.FEATURES_CANNOT_REPLACE).addTag(BlockTags.LEAVES).addTag(BlockTags.LOGS);
        BlockTags.PARROTS_SPAWNABLE_ON.addTag(BlockTags.LEAVES).addTag(BlockTags.LOGS).add(Block.GRASS_BLOCK, Block.AIR);
        // Legacy/Unofficial, I don't know
        BlockTags.AZALEA_GROWS_ON.addTag(BlockTags.DIRT).addTag(BlockTags.SAND).addTag(BlockTags.TERRACOTTA).add(Block.SNOW_BLOCK, Block.POWDER_SNOW);
        BlockTags.REPLACEABLE_PLANTS.add(Block.SHORT_GRASS, Block.FERN, Block.DEAD_BUSH, Block.VINE, Block.GLOW_LICHEN, Block.SUNFLOWER, Block.LILAC, Block.ROSE_BUSH, Block.PEONY, Block.TALL_GRASS, Block.LARGE_FERN, Block.HANGING_ROOTS, Block.PITCHER_PLANT);
        // Unofficial blocks to help packetevents users
        BlockTags.GLASS_BLOCKS.add(Block.GLASS, Block.WHITE_STAINED_GLASS, Block.ORANGE_STAINED_GLASS, Block.MAGENTA_STAINED_GLASS, Block.LIGHT_BLUE_STAINED_GLASS, Block.YELLOW_STAINED_GLASS, Block.LIME_STAINED_GLASS, Block.PINK_STAINED_GLASS, Block.GRAY_STAINED_GLASS, Block.LIGHT_GRAY_STAINED_GLASS, Block.CYAN_STAINED_GLASS, Block.PURPLE_STAINED_GLASS, Block.BLUE_STAINED_GLASS, Block.BROWN_STAINED_GLASS, Block.GREEN_STAINED_GLASS, Block.RED_STAINED_GLASS, Block.BLACK_STAINED_GLASS, Block.TINTED_GLASS);
        BlockTags.GLASS_PANES.add(Block.GLASS_PANE, Block.WHITE_STAINED_GLASS_PANE, Block.ORANGE_STAINED_GLASS_PANE, Block.MAGENTA_STAINED_GLASS_PANE, Block.LIGHT_BLUE_STAINED_GLASS_PANE, Block.YELLOW_STAINED_GLASS_PANE, Block.LIME_STAINED_GLASS_PANE, Block.PINK_STAINED_GLASS_PANE, Block.GRAY_STAINED_GLASS_PANE, Block.LIGHT_GRAY_STAINED_GLASS_PANE, Block.CYAN_STAINED_GLASS_PANE, Block.PURPLE_STAINED_GLASS_PANE, Block.BLUE_STAINED_GLASS_PANE, Block.BROWN_STAINED_GLASS_PANE, Block.GREEN_STAINED_GLASS_PANE, Block.RED_STAINED_GLASS_PANE, Block.BLACK_STAINED_GLASS_PANE);
    }

    String name;
    Set<Block> states = new HashSet<>(); // o(1)
    boolean reallyEmpty;

    public BlockTags(final String name) {
        byName.put(name, this);
        this.name = name;
    }

    private static BlockTags bind(final String s) {
        return new BlockTags(s);
    }

    private static void copy(@Nullable BlockTags src, BlockTags dst) {
        if (src != null) {
            dst.states.addAll(src.states);
        } else {
            dst.reallyEmpty = true;
        }
    }

    public static boolean exceedsCube(Block block) {
        Point start = block.registry().collisionShape().relativeStart();
        Point end = block.registry().collisionShape().relativeEnd();

        double shapeWidth = Math.abs(end.x() - start.x());
        double shapeHeight = Math.abs(end.y() - start.y());
        double shapeDepth = Math.abs(end.z() - start.z());

        return shapeWidth > 1.0 || shapeHeight > 1.0 || shapeDepth > 1.0;
    }

    private BlockTags add(Block... state) {
        Collections.addAll(this.states, state);
        return this;
    }

    private BlockTags addTag(BlockTags tags) {
        if (tags.states.isEmpty()) {
            throw new IllegalArgumentException("Tag " + tags.name + " is empty when adding to " + this.name + ", you (packetevents updater) probably messed up the block tags order!!");
        }
        this.states.addAll(tags.states);
        return this;
    }

    public boolean contains(Block state) {
        return this.states.contains(state);
    }

    public String getName() {
        return this.name;
    }

    public static BlockTags getByName(String name) {
        return byName.get(name);
    }

    public Set<Block> getStates() {
        return this.states;
    }

    @VisibleForTesting
    public boolean isReallyEmpty() {
        return this.reallyEmpty;
    }
}

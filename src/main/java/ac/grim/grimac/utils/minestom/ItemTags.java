package ac.grim.grimac.utils.minestom;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ItemTags {
    private static final HashMap<String, ItemTags> byName = new HashMap<>();

    public static final ItemTags WOOL = bind("wool");
    public static final ItemTags PLANKS = bind("planks");
    public static final ItemTags STONE_BRICKS = bind("stone_bricks");
    public static final ItemTags WOODEN_BUTTONS = bind("wooden_buttons");
    public static final ItemTags STONE_BUTTONS = bind("stone_buttons");
    public static final ItemTags BUTTONS = bind("buttons");
    public static final ItemTags WOOL_CARPETS = bind("carpets");
    public static final ItemTags WOODEN_DOORS = bind("wooden_doors");
    public static final ItemTags WOODEN_STAIRS = bind("wooden_stairs");
    public static final ItemTags WOODEN_SLABS = bind("wooden_slabs");
    public static final ItemTags WOODEN_FENCES = bind("wooden_fences");
    public static final ItemTags FENCE_GATES = bind("fence_gates");
    public static final ItemTags WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
    public static final ItemTags WOODEN_TRAPDOORS = bind("wooden_trapdoors");
    public static final ItemTags DOORS = bind("doors");
    public static final ItemTags SAPLINGS = bind("saplings");
    public static final ItemTags LOGS_THAT_BURN = bind("logs_that_burn");
    public static final ItemTags LOGS = bind("logs");
    public static final ItemTags DARK_OAK_LOGS = bind("dark_oak_logs");
    public static final ItemTags OAK_LOGS = bind("oak_logs");
    public static final ItemTags BIRCH_LOGS = bind("birch_logs");
    public static final ItemTags ACACIA_LOGS = bind("acacia_logs");
    public static final ItemTags CHERRY_LOGS = bind("cherry_logs");
    public static final ItemTags JUNGLE_LOGS = bind("jungle_logs");
    public static final ItemTags SPRUCE_LOGS = bind("spruce_logs");
    public static final ItemTags MANGROVE_LOGS = bind("mangrove_logs");
    public static final ItemTags CRIMSON_STEMS = bind("crimson_stems");
    public static final ItemTags WARPED_STEMS = bind("warped_stems");
    public static final ItemTags BAMBOO_BLOCKS = bind("bamboo_blocks");
    public static final ItemTags WART_BLOCKS = bind("wart_blocks");
    public static final ItemTags BANNERS = bind("banners");
    public static final ItemTags SAND = bind("sand");
    public static final ItemTags SMELTS_TO_GLASS = bind("smelts_to_glass");
    public static final ItemTags STAIRS = bind("stairs");
    public static final ItemTags SLABS = bind("slabs");
    public static final ItemTags WALLS = bind("walls");
    public static final ItemTags ANVIL = bind("anvil");
    public static final ItemTags RAILS = bind("rails");
    public static final ItemTags LEAVES = bind("leaves");
    public static final ItemTags TRAPDOORS = bind("trapdoors");
    public static final ItemTags SMALL_FLOWERS = bind("small_flowers");
    public static final ItemTags BEDS = bind("beds");
    public static final ItemTags FENCES = bind("fences");
    public static final ItemTags TALL_FLOWERS = bind("tall_flowers");
    public static final ItemTags FLOWERS = bind("flowers");
    public static final ItemTags PIGLIN_REPELLENTS = bind("piglin_repellents");
    public static final ItemTags PIGLIN_LOVED = bind("piglin_loved");
    public static final ItemTags IGNORED_BY_PIGLIN_BABIES = bind("ignored_by_piglin_babies");
    public static final ItemTags PIGLIN_FOOD = bind("piglin_food");
    public static final ItemTags FOX_FOOD = bind("fox_food");
    public static final ItemTags GOLD_ORES = bind("gold_ores");
    public static final ItemTags IRON_ORES = bind("iron_ores");
    public static final ItemTags DIAMOND_ORES = bind("diamond_ores");
    public static final ItemTags REDSTONE_ORES = bind("redstone_ores");
    public static final ItemTags LAPIS_ORES = bind("lapis_ores");
    public static final ItemTags COAL_ORES = bind("coal_ores");
    public static final ItemTags EMERALD_ORES = bind("emerald_ores");
    public static final ItemTags COPPER_ORES = bind("copper_ores");
    public static final ItemTags NON_FLAMMABLE_WOOD = bind("non_flammable_wood");
    public static final ItemTags SOUL_FIRE_BASE_BLOCKS = bind("soul_fire_base_blocks");
    public static final ItemTags CANDLES = bind("candles");
    public static final ItemTags DIRT = bind("dirt");
    public static final ItemTags TERRACOTTA = bind("terracotta");
    public static final ItemTags COMPLETES_FIND_TREE_TUTORIAL = bind("completes_find_tree_tutorial");
    public static final ItemTags BOATS = bind("boats");
    public static final ItemTags CHEST_BOATS = bind("chest_boats");
    public static final ItemTags FISHES = bind("fishes");
    public static final ItemTags SIGNS = bind("signs");
    public static final ItemTags MUSIC_DISCS = bind("music_discs");
    public static final ItemTags CREEPER_DROP_MUSIC_DISCS = bind("creeper_drop_music_discs");
    public static final ItemTags COALS = bind("coals");
    public static final ItemTags ARROWS = bind("arrows");
    public static final ItemTags LECTERN_BOOKS = bind("lectern_books");
    public static final ItemTags BOOKSHELF_BOOKS = bind("bookshelf_books");
    public static final ItemTags BEACON_PAYMENT_ITEMS = bind("beacon_payment_items");
    public static final ItemTags STONE_TOOL_MATERIALS = bind("stone_tool_materials");
    public static final ItemTags STONE_CRAFTING_MATERIALS = bind("stone_crafting_materials");
    public static final ItemTags FREEZE_IMMUNE_WEARABLES = bind("freeze_immune_wearables");
    public static final ItemTags DAMPENS_VIBRATIONS = bind("dampens_vibrations");
    public static final ItemTags CLUSTER_MAX_HARVESTABLES = bind("cluster_max_harvestables");
    public static final ItemTags COMPASSES = bind("compasses");
    public static final ItemTags HANGING_SIGNS = bind("hanging_signs");
    public static final ItemTags CREEPER_IGNITERS = bind("creeper_igniters");
    public static final ItemTags NOTEBLOCK_TOP_INSTRUMENTS = bind("noteblock_top_instruments");
    public static final ItemTags TRIMMABLE_ARMOR = bind("trimmable_armor");
    public static final ItemTags TRIM_MATERIALS = bind("trim_materials");
    public static final ItemTags TRIM_TEMPLATES = bind("trim_templates");
    public static final ItemTags SNIFFER_FOOD = bind("sniffer_food");
    public static final ItemTags DECORATED_POT_SHERDS = bind("decorated_pot_sherds");
    public static final ItemTags DECORATED_POT_INGREDIENTS = bind("decorated_pot_ingredients");
    public static final ItemTags SWORDS = bind("swords");
    public static final ItemTags EDIBLE = bind("edible");
    public static final ItemTags AXES = bind("axes");
    public static final ItemTags HOES = bind("hoes");
    public static final ItemTags PICKAXES = bind("pickaxes");
    public static final ItemTags SHOVELS = bind("shovels");
    public static final ItemTags BREAKS_DECORATED_POTS = bind("breaks_decorated_pots");
    @Deprecated // removed in 1.20.5 as "duplicate"
    public static final ItemTags TOOLS = BREAKS_DECORATED_POTS;
    public static final ItemTags VILLAGER_PLANTABLE_SEEDS = bind("villager_plantable_seeds");

    // Added in 1.20.5
    public static final ItemTags ARMADILLO_FOOD = bind("armadillo_food");
    public static final ItemTags AXOLOTL_FOOD = bind("axolotl_food");
    @Deprecated // renamed in 1.20.5
    public static final ItemTags AXOLOTL_TEMPT_ITEMS = AXOLOTL_FOOD;
    public static final ItemTags BEE_FOOD = bind("bee_food");
    public static final ItemTags CAMEL_FOOD = bind("camel_food");
    public static final ItemTags CAT_FOOD = bind("cat_food");
    public static final ItemTags CHEST_ARMOR = bind("chest_armor");
    public static final ItemTags CHICKEN_FOOD = bind("chicken_food");
    public static final ItemTags COW_FOOD = bind("cow_food");
    public static final ItemTags DYEABLE = bind("dyeable");
    public static final ItemTags ENCHANTABLE_ARMOR = bind("enchantable/armor");
    public static final ItemTags ENCHANTABLE_BOW = bind("enchantable/bow");
    public static final ItemTags ENCHANTABLE_CHEST_ARMOR = bind("enchantable/chest_armor");
    public static final ItemTags ENCHANTABLE_CROSSBOW = bind("enchantable/crossbow");
    public static final ItemTags ENCHANTABLE_DURABILITY = bind("enchantable/durability");
    public static final ItemTags ENCHANTABLE_EQUIPPABLE = bind("enchantable/equippable");
    public static final ItemTags ENCHANTABLE_FIRE_ASPECT = bind("enchantable/fire_aspect");
    public static final ItemTags ENCHANTABLE_FISHING = bind("enchantable/fishing");
    public static final ItemTags ENCHANTABLE_FOOT_ARMOR = bind("enchantable/foot_armor");
    public static final ItemTags ENCHANTABLE_HEAD_ARMOR = bind("enchantable/head_armor");
    public static final ItemTags ENCHANTABLE_LEG_ARMOR = bind("enchantable/leg_armor");
    public static final ItemTags ENCHANTABLE_MACE = bind("enchantable/mace");
    public static final ItemTags ENCHANTABLE_MINING = bind("enchantable/mining");
    public static final ItemTags ENCHANTABLE_MINING_LOOT = bind("enchantable/mining_loot");
    public static final ItemTags ENCHANTABLE_SHARP_WEAPON = bind("enchantable/sharp_weapon");
    public static final ItemTags ENCHANTABLE_SWORD = bind("enchantable/sword");
    public static final ItemTags ENCHANTABLE_TRIDENT = bind("enchantable/trident");
    public static final ItemTags ENCHANTABLE_VANISHING = bind("enchantable/vanishing");
    public static final ItemTags ENCHANTABLE_WEAPON = bind("enchantable/weapon");
    public static final ItemTags FOOT_ARMOR = bind("foot_armor");
    public static final ItemTags FROG_FOOD = bind("frog_food");
    public static final ItemTags GOAT_FOOD = bind("goat_food");
    public static final ItemTags HEAD_ARMOR = bind("head_armor");
    public static final ItemTags HOGLIN_FOOD = bind("hoglin_food");
    public static final ItemTags HORSE_FOOD = bind("horse_food");
    public static final ItemTags HORSE_TEMPT_ITEMS = bind("horse_tempt_items");
    public static final ItemTags LEG_ARMOR = bind("leg_armor");
    public static final ItemTags LLAMA_FOOD = bind("llama_food");
    public static final ItemTags LLAMA_TEMPT_ITEMS = bind("llama_tempt_items");
    public static final ItemTags MEAT = bind("meat");
    public static final ItemTags OCELOT_FOOD = bind("ocelot_food");
    public static final ItemTags PANDA_FOOD = bind("panda_food");
    public static final ItemTags PARROT_FOOD = bind("parrot_food");
    public static final ItemTags PARROT_POISONOUS_FOOD = bind("parrot_poisonous_food");
    public static final ItemTags PIG_FOOD = bind("pig_food");
    public static final ItemTags RABBIT_FOOD = bind("rabbit_food");
    public static final ItemTags SHEEP_FOOD = bind("sheep_food");
    public static final ItemTags SKULLS = bind("skulls");
    public static final ItemTags STRIDER_FOOD = bind("strider_food");
    public static final ItemTags STRIDER_TEMPT_ITEMS = bind("strider_tempt_items");
    public static final ItemTags TURTLE_FOOD = bind("turtle_food");
    public static final ItemTags WOLF_FOOD = bind("wolf_food");

    static {
        copy(BlockTags.WOOL, ItemTags.WOOL);
        copy(BlockTags.PLANKS, ItemTags.PLANKS);
        copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
        copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        copy(BlockTags.STONE_BUTTONS, ItemTags.STONE_BUTTONS);
        copy(BlockTags.WOOL_CARPETS, ItemTags.WOOL_CARPETS);
        copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
        copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
        copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
        copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
        copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
        copy(BlockTags.CHERRY_LOGS, ItemTags.CHERRY_LOGS);
        copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
        copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
        copy(BlockTags.MANGROVE_LOGS, ItemTags.MANGROVE_LOGS);
        copy(BlockTags.CRIMSON_STEMS, ItemTags.CRIMSON_STEMS);
        copy(BlockTags.WARPED_STEMS, ItemTags.WARPED_STEMS);
        copy(BlockTags.BAMBOO_BLOCKS, ItemTags.BAMBOO_BLOCKS);
        copy(BlockTags.WART_BLOCKS, ItemTags.WART_BLOCKS);
        ItemTags.BANNERS.add(Material.WHITE_BANNER, Material.ORANGE_BANNER, Material.MAGENTA_BANNER, Material.LIGHT_BLUE_BANNER, Material.YELLOW_BANNER, Material.LIME_BANNER, Material.PINK_BANNER, Material.GRAY_BANNER, Material.LIGHT_GRAY_BANNER, Material.CYAN_BANNER, Material.PURPLE_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER, Material.GREEN_BANNER, Material.RED_BANNER, Material.BLACK_BANNER);
        copy(BlockTags.SAND, ItemTags.SAND);
        copy(BlockTags.SMELTS_TO_GLASS, ItemTags.SMELTS_TO_GLASS);
        copy(BlockTags.WALLS, ItemTags.WALLS);
        copy(BlockTags.ANVIL, ItemTags.ANVIL);
        copy(BlockTags.RAILS, ItemTags.RAILS);
        copy(BlockTags.LEAVES, ItemTags.LEAVES);
        copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
        copy(BlockTags.BEDS, ItemTags.BEDS);
        copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
        ItemTags.PIGLIN_REPELLENTS.add(Material.SOUL_TORCH, Material.SOUL_LANTERN, Material.SOUL_CAMPFIRE);
        ItemTags.IGNORED_BY_PIGLIN_BABIES.add(Material.LEATHER);
        ItemTags.MEAT.add(Material.BEEF, Material.CHICKEN, Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.COOKED_MUTTON, Material.COOKED_PORKCHOP, Material.COOKED_RABBIT, Material.MUTTON, Material.PORKCHOP, Material.RABBIT, Material.ROTTEN_FLESH);
        ItemTags.SNIFFER_FOOD.add(Material.TORCHFLOWER_SEEDS);
        ItemTags.PIGLIN_FOOD.add(Material.PORKCHOP, Material.COOKED_PORKCHOP);
        ItemTags.FOX_FOOD.add(Material.SWEET_BERRIES, Material.GLOW_BERRIES);
        ItemTags.COW_FOOD.add(Material.WHEAT);
        copy(ItemTags.COW_FOOD, ItemTags.GOAT_FOOD);
        copy(ItemTags.COW_FOOD, ItemTags.SHEEP_FOOD);
        ItemTags.CAT_FOOD.add(Material.COD, Material.SALMON);
        ItemTags.HORSE_FOOD.add(Material.WHEAT, Material.SUGAR, Material.HAY_BLOCK, Material.APPLE, Material.GOLDEN_CARROT, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE);
        ItemTags.HORSE_TEMPT_ITEMS.add(Material.GOLDEN_CARROT, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE);
        ItemTags.CAMEL_FOOD.add(Material.CACTUS);
        ItemTags.ARMADILLO_FOOD.add(Material.SPIDER_EYE);
        ItemTags.CHICKEN_FOOD.add(Material.WHEAT_SEEDS, Material.MELON_SEEDS, Material.PUMPKIN_SEEDS, Material.BEETROOT_SEEDS, Material.TORCHFLOWER_SEEDS, Material.PITCHER_POD);
        ItemTags.FROG_FOOD.add(Material.SLIME_BALL);
        ItemTags.HOGLIN_FOOD.add(Material.CRIMSON_FUNGUS);
        ItemTags.LLAMA_FOOD.add(Material.WHEAT, Material.HAY_BLOCK);
        ItemTags.LLAMA_TEMPT_ITEMS.add(Material.HAY_BLOCK);
        copy(ItemTags.CAT_FOOD, ItemTags.OCELOT_FOOD);
        ItemTags.PANDA_FOOD.add(Material.BAMBOO);
        ItemTags.PIG_FOOD.add(Material.CARROT, Material.POTATO, Material.BEETROOT);
        ItemTags.RABBIT_FOOD.add(Material.CARROT, Material.GOLDEN_CARROT, Material.DANDELION);
        ItemTags.STRIDER_FOOD.add(Material.WARPED_FUNGUS);
        ItemTags.TURTLE_FOOD.add(Material.SEAGRASS);
        copy(ItemTags.CHICKEN_FOOD, ItemTags.PARROT_FOOD);
        ItemTags.PARROT_POISONOUS_FOOD.add(Material.COOKIE);
        ItemTags.AXOLOTL_FOOD.add(Material.TROPICAL_FISH_BUCKET);
        copy(BlockTags.GOLD_ORES, ItemTags.GOLD_ORES);
        copy(BlockTags.IRON_ORES, ItemTags.IRON_ORES);
        copy(BlockTags.DIAMOND_ORES, ItemTags.DIAMOND_ORES);
        copy(BlockTags.REDSTONE_ORES, ItemTags.REDSTONE_ORES);
        copy(BlockTags.LAPIS_ORES, ItemTags.LAPIS_ORES);
        copy(BlockTags.COAL_ORES, ItemTags.COAL_ORES);
        copy(BlockTags.EMERALD_ORES, ItemTags.EMERALD_ORES);
        copy(BlockTags.COPPER_ORES, ItemTags.COPPER_ORES);
        ItemTags.NON_FLAMMABLE_WOOD.add(Material.WARPED_STEM, Material.STRIPPED_WARPED_STEM, Material.WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE, Material.CRIMSON_STEM, Material.STRIPPED_CRIMSON_STEM, Material.CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE, Material.CRIMSON_PLANKS, Material.WARPED_PLANKS, Material.CRIMSON_SLAB, Material.WARPED_SLAB, Material.CRIMSON_PRESSURE_PLATE, Material.WARPED_PRESSURE_PLATE, Material.CRIMSON_FENCE, Material.WARPED_FENCE, Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR, Material.CRIMSON_FENCE_GATE, Material.WARPED_FENCE_GATE, Material.CRIMSON_STAIRS, Material.WARPED_STAIRS, Material.CRIMSON_BUTTON, Material.WARPED_BUTTON, Material.CRIMSON_DOOR, Material.WARPED_DOOR, Material.CRIMSON_SIGN, Material.WARPED_SIGN, Material.WARPED_HANGING_SIGN, Material.CRIMSON_HANGING_SIGN);
        copy(BlockTags.WITHER_SUMMON_BASE_BLOCKS, ItemTags.SOUL_FIRE_BASE_BLOCKS);
        copy(BlockTags.CANDLES, ItemTags.CANDLES);
        copy(BlockTags.DIRT, ItemTags.DIRT);
        copy(BlockTags.TERRACOTTA, ItemTags.TERRACOTTA);
        ItemTags.CHEST_BOATS.add(Material.OAK_CHEST_BOAT, Material.SPRUCE_CHEST_BOAT, Material.BIRCH_CHEST_BOAT, Material.JUNGLE_CHEST_BOAT, Material.ACACIA_CHEST_BOAT, Material.DARK_OAK_CHEST_BOAT, Material.MANGROVE_CHEST_BOAT, Material.BAMBOO_CHEST_RAFT, Material.CHERRY_CHEST_BOAT);
        ItemTags.FISHES.add(Material.COD, Material.COOKED_COD, Material.SALMON, Material.COOKED_SALMON, Material.PUFFERFISH, Material.TROPICAL_FISH);
        copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        ItemTags.CREEPER_DROP_MUSIC_DISCS.add(Material.MUSIC_DISC_13, Material.MUSIC_DISC_CAT, Material.MUSIC_DISC_BLOCKS, Material.MUSIC_DISC_CHIRP, Material.MUSIC_DISC_FAR, Material.MUSIC_DISC_MALL, Material.MUSIC_DISC_MELLOHI, Material.MUSIC_DISC_STAL, Material.MUSIC_DISC_STRAD, Material.MUSIC_DISC_WARD, Material.MUSIC_DISC_11, Material.MUSIC_DISC_WAIT);
        ItemTags.COALS.add(Material.COAL, Material.CHARCOAL);
        ItemTags.ARROWS.add(Material.ARROW, Material.TIPPED_ARROW, Material.SPECTRAL_ARROW);
        ItemTags.LECTERN_BOOKS.add(Material.WRITTEN_BOOK, Material.WRITABLE_BOOK);
        ItemTags.BOOKSHELF_BOOKS.add(Material.BOOK, Material.WRITTEN_BOOK, Material.ENCHANTED_BOOK, Material.WRITABLE_BOOK, Material.KNOWLEDGE_BOOK);
        ItemTags.BEACON_PAYMENT_ITEMS.add(Material.NETHERITE_INGOT, Material.EMERALD, Material.DIAMOND, Material.GOLD_INGOT, Material.IRON_INGOT);
        ItemTags.STONE_TOOL_MATERIALS.add(Material.COBBLESTONE, Material.BLACKSTONE, Material.COBBLED_DEEPSLATE);
        copy(ItemTags.STONE_TOOL_MATERIALS, ItemTags.STONE_CRAFTING_MATERIALS);
        ItemTags.FREEZE_IMMUNE_WEARABLES.add(Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_HORSE_ARMOR);
        ItemTags.CLUSTER_MAX_HARVESTABLES.add(Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, Material.NETHERITE_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE);
        ItemTags.COMPASSES.add(Material.COMPASS, Material.RECOVERY_COMPASS);
        copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
        ItemTags.CREEPER_IGNITERS.add(Material.FLINT_AND_STEEL, Material.FIRE_CHARGE);
        ItemTags.NOTEBLOCK_TOP_INSTRUMENTS.add(Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.CREEPER_HEAD, Material.DRAGON_HEAD, Material.WITHER_SKELETON_SKULL, Material.PIGLIN_HEAD, Material.PLAYER_HEAD);
        ItemTags.FOOT_ARMOR.add(Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS);
        ItemTags.LEG_ARMOR.add(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS);
        ItemTags.CHEST_ARMOR.add(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
        ItemTags.HEAD_ARMOR.add(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET);
        ItemTags.SKULLS.add(Material.PLAYER_HEAD, Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.SKELETON_SKULL, Material.WITHER_SKELETON_SKULL, Material.DRAGON_HEAD, Material.PIGLIN_HEAD);
        ItemTags.TRIM_MATERIALS.add(Material.IRON_INGOT, Material.COPPER_INGOT, Material.GOLD_INGOT, Material.LAPIS_LAZULI, Material.EMERALD, Material.DIAMOND, Material.NETHERITE_INGOT, Material.REDSTONE, Material.QUARTZ, Material.AMETHYST_SHARD);
        ItemTags.TRIM_TEMPLATES.add(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
        ItemTags.DECORATED_POT_SHERDS.add(Material.ANGLER_POTTERY_SHERD, Material.ARCHER_POTTERY_SHERD, Material.ARMS_UP_POTTERY_SHERD, Material.BLADE_POTTERY_SHERD, Material.BREWER_POTTERY_SHERD, Material.BURN_POTTERY_SHERD, Material.DANGER_POTTERY_SHERD, Material.EXPLORER_POTTERY_SHERD, Material.FRIEND_POTTERY_SHERD, Material.HEART_POTTERY_SHERD, Material.HEARTBREAK_POTTERY_SHERD, Material.HOWL_POTTERY_SHERD, Material.MINER_POTTERY_SHERD, Material.MOURNER_POTTERY_SHERD, Material.PLENTY_POTTERY_SHERD, Material.PRIZE_POTTERY_SHERD, Material.SHEAF_POTTERY_SHERD, Material.SHELTER_POTTERY_SHERD, Material.SKULL_POTTERY_SHERD, Material.SNORT_POTTERY_SHERD, Material.FLOW_POTTERY_SHERD, Material.GUSTER_POTTERY_SHERD, Material.SCRAPE_POTTERY_SHERD);
        ItemTags.SWORDS.add(Material.DIAMOND_SWORD, Material.STONE_SWORD, Material.GOLDEN_SWORD, Material.NETHERITE_SWORD, Material.WOODEN_SWORD, Material.IRON_SWORD);
        ItemTags.AXES.add(Material.DIAMOND_AXE, Material.STONE_AXE, Material.GOLDEN_AXE, Material.NETHERITE_AXE, Material.WOODEN_AXE, Material.IRON_AXE);
        ItemTags.HOES.add(Material.DIAMOND_HOE, Material.STONE_HOE, Material.GOLDEN_HOE, Material.NETHERITE_HOE, Material.WOODEN_HOE, Material.IRON_HOE);
        ItemTags.PICKAXES.add(Material.DIAMOND_PICKAXE, Material.STONE_PICKAXE, Material.GOLDEN_PICKAXE, Material.NETHERITE_PICKAXE, Material.WOODEN_PICKAXE, Material.IRON_PICKAXE);
        ItemTags.SHOVELS.add(Material.DIAMOND_SHOVEL, Material.STONE_SHOVEL, Material.GOLDEN_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_SHOVEL, Material.IRON_SHOVEL);
        ItemTags.VILLAGER_PLANTABLE_SEEDS.add(Material.WHEAT_SEEDS, Material.POTATO, Material.CARROT, Material.BEETROOT_SEEDS, Material.TORCHFLOWER_SEEDS, Material.PITCHER_POD);
        ItemTags.DYEABLE.add(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, Material.LEATHER_HORSE_ARMOR, Material.WOLF_ARMOR);
        ItemTags.ENCHANTABLE_FISHING.add(Material.FISHING_ROD);
        ItemTags.ENCHANTABLE_TRIDENT.add(Material.TRIDENT);
        ItemTags.ENCHANTABLE_BOW.add(Material.BOW);
        ItemTags.ENCHANTABLE_CROSSBOW.add(Material.CROSSBOW);
        ItemTags.ENCHANTABLE_MACE.add(Material.MACE);
        copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        copy(BlockTags.DOORS, ItemTags.DOORS);
        copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        copy(BlockTags.STAIRS, ItemTags.STAIRS);
        copy(BlockTags.SLABS, ItemTags.SLABS);
        copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        copy(BlockTags.FENCES, ItemTags.FENCES);
        copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
        ItemTags.PIGLIN_LOVED.addTag(ItemTags.GOLD_ORES).add(Material.GOLD_BLOCK, Material.GILDED_BLACKSTONE, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.GOLD_INGOT, Material.BELL, Material.CLOCK, Material.GOLDEN_CARROT, Material.GLISTERING_MELON_SLICE, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS, Material.GOLDEN_HORSE_ARMOR, Material.GOLDEN_SWORD, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.RAW_GOLD, Material.RAW_GOLD_BLOCK);
        ItemTags.WOLF_FOOD.addTag(ItemTags.MEAT);
        ItemTags.STRIDER_TEMPT_ITEMS.addTag(ItemTags.STRIDER_FOOD).add(Material.WARPED_FUNGUS_ON_A_STICK);
        ItemTags.BOATS.addTag(ItemTags.CHEST_BOATS).add(Material.OAK_BOAT, Material.SPRUCE_BOAT, Material.BIRCH_BOAT, Material.JUNGLE_BOAT, Material.ACACIA_BOAT, Material.DARK_OAK_BOAT, Material.MANGROVE_BOAT, Material.BAMBOO_RAFT, Material.CHERRY_BOAT);
        ItemTags.MUSIC_DISCS.addTag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(Material.MUSIC_DISC_PIGSTEP, Material.MUSIC_DISC_OTHERSIDE, Material.MUSIC_DISC_5, Material.MUSIC_DISC_RELIC, Material.MUSIC_DISC_CREATOR, Material.MUSIC_DISC_CREATOR_MUSIC_BOX, Material.MUSIC_DISC_PRECIPICE);
        copy(BlockTags.DAMPENS_VIBRATIONS, ItemTags.DAMPENS_VIBRATIONS);
        ItemTags.TRIMMABLE_ARMOR.addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR);
        ItemTags.DECORATED_POT_INGREDIENTS.addTag(ItemTags.DECORATED_POT_SHERDS).add(Material.BRICK);
        ItemTags.BREAKS_DECORATED_POTS.addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Material.TRIDENT, Material.MACE);
        ItemTags.ENCHANTABLE_FOOT_ARMOR.addTag(ItemTags.FOOT_ARMOR);
        ItemTags.ENCHANTABLE_LEG_ARMOR.addTag(ItemTags.LEG_ARMOR);
        ItemTags.ENCHANTABLE_CHEST_ARMOR.addTag(ItemTags.CHEST_ARMOR);
        ItemTags.ENCHANTABLE_HEAD_ARMOR.addTag(ItemTags.HEAD_ARMOR);
        ItemTags.ENCHANTABLE_SWORD.addTag(ItemTags.SWORDS);
        ItemTags.ENCHANTABLE_SHARP_WEAPON.addTag(ItemTags.SWORDS).addTag(ItemTags.AXES);
        ItemTags.ENCHANTABLE_MINING.addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Material.SHEARS);
        ItemTags.ENCHANTABLE_MINING_LOOT.addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES);
        ItemTags.ENCHANTABLE_DURABILITY.addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).addTag(ItemTags.SWORDS).addTag(ItemTags.AXES).addTag(ItemTags.PICKAXES).addTag(ItemTags.SHOVELS).addTag(ItemTags.HOES).add(Material.ELYTRA, Material.SHIELD, Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.FLINT_AND_STEEL, Material.SHEARS, Material.BRUSH, Material.FISHING_ROD, Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK, Material.MACE);
        ItemTags.ENCHANTABLE_EQUIPPABLE.addTag(ItemTags.FOOT_ARMOR).addTag(ItemTags.LEG_ARMOR).addTag(ItemTags.CHEST_ARMOR).addTag(ItemTags.HEAD_ARMOR).addTag(ItemTags.SKULLS).add(Material.ELYTRA, Material.CARVED_PUMPKIN);
        copy(BlockTags.LOGS, ItemTags.LOGS);
        ItemTags.BEE_FOOD.addTag(ItemTags.FLOWERS);
        ItemTags.ENCHANTABLE_ARMOR.addTag(ItemTags.ENCHANTABLE_FOOT_ARMOR).addTag(ItemTags.ENCHANTABLE_LEG_ARMOR).addTag(ItemTags.ENCHANTABLE_CHEST_ARMOR).addTag(ItemTags.ENCHANTABLE_HEAD_ARMOR);
        ItemTags.ENCHANTABLE_FIRE_ASPECT.addTag(ItemTags.ENCHANTABLE_SWORD).add(Material.MACE);
        ItemTags.ENCHANTABLE_WEAPON.addTag(ItemTags.ENCHANTABLE_SHARP_WEAPON).add(Material.MACE);
        ItemTags.ENCHANTABLE_VANISHING.addTag(ItemTags.ENCHANTABLE_DURABILITY).addTag(ItemTags.SKULLS).add(Material.COMPASS, Material.CARVED_PUMPKIN);
        copy(BlockTags.COMPLETES_FIND_TREE_TUTORIAL, ItemTags.COMPLETES_FIND_TREE_TUTORIAL);
    }

    String name;
    Set<Material> states = new HashSet<>(); // o(1);
    boolean reallyEmpty;

    public ItemTags(final String name) {
        byName.put(name, this);
        this.name = name;
    }

    private static ItemTags bind(final String s) {
        return new ItemTags(s);
    }

    private static void copy(ItemTags src, ItemTags dst) {
        dst.states.addAll(src.states);
    }

    private static void copy(BlockTags tag, ItemTags itemTag) {
        for (Block state : tag.getStates()) {
            // todo minestom this sucks
            itemTag.states.add(state.defaultState().registry().material());
        }
        itemTag.states.remove(null); // In case getTypePlacingState returned null
    }

    private ItemTags add(Material... state) {
        Collections.addAll(this.states, state);
        return this;
    }

    private ItemTags addTag(ItemTags tags) {
        if (tags.states.isEmpty()) {
            throw new IllegalArgumentException("Tag " + tags.name + " is empty when adding to " + this.name + ", you (packetevents updater) probably messed up the item tags order!!");
        }
        this.states.addAll(tags.states);
        return this;
    }

    public boolean contains(Material state) {
        return this.states.contains(state);
    }

    public String getName() {
        return this.name;
    }

    public ItemTags getByName(String name) {
        return byName.get(name);
    }

    public Set<Material> getStates() {
        return this.states;
    }

    @VisibleForTesting
    public boolean isReallyEmpty() {
        return this.reallyEmpty;
    }
}

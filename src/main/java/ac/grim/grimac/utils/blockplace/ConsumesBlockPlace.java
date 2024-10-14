package ac.grim.grimac.utils.blockplace;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.collisions.AxisUtil;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.ItemTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.minestom.enums.*;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.Material;

// HOW DIFFICULT CAN IT BE TO TELL THE SERVER THAT YOU RANG A BELL, AND NOT CREATE A GHOST BLOCK???
public class ConsumesBlockPlace {
    public static boolean consumesPlace(GrimPlayer player, MinestomWrappedBlockState state, BlockPlace place) {
        // Hey look, it's another DESYNC MOJANG
        if (state.getType() == Block.BELL) {
            return goodBellHit(state, place);
        }
        if (BlockTags.CANDLE_CAKES.contains(state.getType())) {
            MinestomWrappedBlockState cake = new MinestomWrappedBlockState(Block.CAKE);
            cake.setBites(1);
            player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), cake);
            return true;
        }
        if (state.getType() == Block.CAKE) {
            if (state.getBites() == 0 && BlockTags.CANDLES.contains(place.getMaterial())) {
                player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), new MinestomWrappedBlockState(Block.CANDLE_CAKE));
                return true;
            }

            if (player.gamemode == GameMode.CREATIVE || (player.food < 20)) {
                if (state.getBites() != 6) {
                    state.setBites(state.getBites() + 1);
                    player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), state);
                } else {
                    player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), new MinestomWrappedBlockState(Block.AIR));
                }
                return true;
            }

            return false;
        }
        if (state.getType() == Block.CAVE_VINES || state.getType() == Block.CAVE_VINES_PLANT) {
            if (state.isBerries()) {
                state.setBerries(false);
                player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), state);
                return true;
            }
            return false;
        }
        if (state.getType() == Block.SWEET_BERRY_BUSH) {
            if (state.getAge() != 3 && place.getItemStack().getType() == Material.BONE_MEAL) {
                return false;
            } else if (state.getAge() > 1) {
                state.setAge(1);
                player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), state);
                return true;
            } else {
                return false;
            }
        }
        if (state.getType() == Block.TNT) {
            if (place.getItemStack().getType() == Material.FIRE_CHARGE || place.getItemStack().getType() == Material.FLINT_AND_STEEL) {
                player.compensatedWorld.updateBlock(place.getPlacedAgainstBlockLocation(), new MinestomWrappedBlockState(Block.AIR));
                return true;
            }
        }
        if (state.getType() == Block.RESPAWN_ANCHOR) {
            if (place.getItemStack().getType() == Material.GLOWSTONE) return true;
            return !place.isBlock() && player.getInventory().getOffHand().getType() == Material.GLOWSTONE;
        }
        if (state.getType() == Block.COMMAND_BLOCK || state.getType() == Block.CHAIN_COMMAND_BLOCK ||
                state.getType() == Block.REPEATING_COMMAND_BLOCK || state.getType() == Block.JIGSAW
                || state.getType() == Block.STRUCTURE_BLOCK) {
            return player.canUseGameMasterBlocks();
        }
        if (state.getType() == Block.COMPOSTER) {
            if (Materials.isCompostable(place.getItemStack().getType()) && state.getLevel() < 8) {
                return true;
            }
            return state.getLevel() == 8;
        }
        if (state.getType() == Block.JUKEBOX) {
            return state.isHasRecord();
        }
        if (state.getType() == Block.LECTERN) {
            if (state.isHasBook()) return true;
            return ItemTags.LECTERN_BOOKS.contains(place.getItemStack().getType());
        }

        return false;
    }

    private static boolean goodBellHit(MinestomWrappedBlockState bell, BlockPlace place) {
        BlockFace direction = place.getDirection();
        return place.getHitData() != null && isProperHit(bell, direction, place.getHitData().getRelativeBlockHitLocation().getY());
    }

    private static boolean isProperHit(MinestomWrappedBlockState bell, BlockFace direction, double p_49742_) {
        if (direction != BlockFace.TOP && direction != BlockFace.BOTTOM && !(p_49742_ > (double) 0.8124F)) {
            BlockFace dir = bell.getFacing();
            Attachment attachment = bell.getAttachment();
            BlockFace dir2 = BlockFace.valueOf(direction.name());

            return switch (attachment) {
                case FLOOR -> AxisUtil.isSameAxis(dir, dir2);
                case SINGLE_WALL, DOUBLE_WALL -> !AxisUtil.isSameAxis(dir, dir2);
                case CEILING -> true;
                default -> false;
            };
        } else {
            return false;
        }
    }
}

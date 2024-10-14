package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.data.MainSupportingBlockData;
import ac.grim.grimac.utils.data.packetentity.PacketEntityHorse;
import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.enchant.Enchantment;

public class BlockProperties {
    public static float getFrictionInfluencedSpeed(float f, GrimPlayer player) {
        if (player.lastOnGround) {
            return (float) (player.speed * (0.21600002f / (f * f * f)));
        }

        // The game uses values known as flyingSpeed for some vehicles in the air
        if (player.compensatedEntities.getSelf().getRiding() != null) {
            if (player.compensatedEntities.getSelf().getRiding().getType() == EntityType.PIG || player.compensatedEntities.getSelf().getRiding() instanceof PacketEntityHorse) {
                return (float) (player.speed * 0.1f);
            }

            if (player.compensatedEntities.getSelf().getRiding() instanceof PacketEntityStrider) {
                // Unsure which version the speed changed in
                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20)) {
                    return (float) player.speed * 0.1f;
                }

                PacketEntityStrider strider = (PacketEntityStrider) player.compensatedEntities.getSelf().getRiding();
                // Vanilla multiplies by 0.1 to calculate speed
                return (float) strider.getAttributeValue(Attribute.GENERIC_MOVEMENT_SPEED) * (strider.isShaking ? 0.66F : 1.0F) * 0.1f;
            }
        }

        if (player.isFlying) {
            return player.flySpeed * 20 * (player.isSprinting ? 0.1f : 0.05f);
        }

        // In 1.19.4, air sprinting is based on current sprinting, not last sprinting
        if (player.getClientVersion().getProtocolVersion() > ClientVersion.V_1_19_3.getProtocolVersion()) {
            return player.isSprinting ? (float) ((double) 0.02f + 0.005999999865889549D) : 0.02f;
        }

        return player.lastSprintingForSpeed ? (float) ((double) 0.02f + 0.005999999865889549D) : 0.02f;
    }


    /**
     * This is used for falling onto a block (We care if there is a bouncy block)
     * This is also used for striders checking if they are on lava
     * <p>
     * For soul speed (server-sided only)
     * (we don't account for this and instead remove this debuff) And powder snow block attribute
     */
    public static Block getOnPos(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Point playerPos) {
        if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4)) {
            return BlockProperties.getOnBlock(player, playerPos.x(), playerPos.y(), playerPos.z());
        }

        Point pos = getOnPos(player, playerPos, mainSupportingBlockData, 0.2F);
        return player.compensatedWorld.getStateTypeAt(pos.blockX(), pos.blockY(), pos.blockZ());
    }

    public static float getFriction(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Point playerPos) {
        if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4)) {
            double searchBelowAmount = 0.5000001;

            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15))
                searchBelowAmount = 1;

            Block type = player.compensatedWorld.getStateTypeAt(playerPos.x(), playerPos.y() - searchBelowAmount, playerPos.z());
            return getMaterialFriction(player, type);
        }

        Block underPlayer = getBlockPosBelowThatAffectsMyMovement(player, mainSupportingBlockData, playerPos);
        return getMaterialFriction(player, underPlayer);
    }

    public static float getBlockSpeedFactor(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Point playerPos) {
        // This system was introduces in 1.15 players to add support for honey blocks slowing players down
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) return 1.0f;
        if (player.isGliding || player.isFlying) return 1.0f;

        if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_19_4)) {
            return getBlockSpeedFactorLegacy(player, playerPos);
        }

        MinestomWrappedBlockState inBlock = player.compensatedWorld.getWrappedBlockStateAt(playerPos.x(), playerPos.y(), playerPos.z());
        float inBlockSpeedFactor = getBlockSpeedFactor(player, inBlock.getType());
        if (inBlockSpeedFactor != 1.0f || inBlock.getType() == Block.WATER || inBlock.getType() == Block.BUBBLE_COLUMN) {
            return getModernVelocityMultiplier(player, inBlockSpeedFactor);
        }

        Block underPlayer = getBlockPosBelowThatAffectsMyMovement(player, mainSupportingBlockData, playerPos);
        return getModernVelocityMultiplier(player, getBlockSpeedFactor(player, underPlayer));
    }

    public static boolean onHoneyBlock(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Point playerPos) {
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) return false;

        Block inBlock = player.compensatedWorld.getStateTypeAt(playerPos.x(), playerPos.y(), playerPos.z());
        return inBlock == Block.HONEY_BLOCK || getOnPos(player, mainSupportingBlockData, playerPos) == Block.HONEY_BLOCK;
    }

    /**
     * Friction
     * Block jump factor
     * Block speed factor
     * <p>
     * On soul speed block (server-sided only)
     */
    private static Block getBlockPosBelowThatAffectsMyMovement(GrimPlayer player, MainSupportingBlockData mainSupportingBlockData, Point playerPos) {
        Point pos = getOnPos(player, playerPos, mainSupportingBlockData, 0.500001F);
        return player.compensatedWorld.getStateTypeAt(pos.blockX(), pos.blockY(), pos.blockZ());
    }

    private static Point getOnPos(GrimPlayer player, Point playerPos, MainSupportingBlockData mainSupportingBlockData, float searchBelowPlayer) {
        Point mainBlockPos = mainSupportingBlockData.getBlockPos();
        if (mainBlockPos != null) {
            Block blockstate = player.compensatedWorld.getStateTypeAt(mainBlockPos.blockX(), mainBlockPos.blockY(), mainBlockPos.blockZ());

            // I genuinely don't understand this code, or why fences are special
            boolean shouldReturn = (!((double)searchBelowPlayer <= 0.5D) || !BlockTags.FENCES.contains(blockstate)) &&
                    !BlockTags.WALLS.contains(blockstate) &&
                    !BlockTags.FENCE_GATES.contains(blockstate);

            return shouldReturn ? mainBlockPos.withY(GrimMath.floor(playerPos.y() - (double) searchBelowPlayer)) : mainBlockPos;
        } else {
            return new Vec(GrimMath.floor(playerPos.x()), GrimMath.floor(playerPos.y() - searchBelowPlayer), GrimMath.floor(playerPos.z()));
        }
    }

    public static float getMaterialFriction(GrimPlayer player, Block material) {
        float friction = 0.6f;

        if (material == Block.ICE) friction = 0.98f;
        if (material == Block.SLIME_BLOCK && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8))
            friction = 0.8f;
        // ViaVersion honey block replacement
        if (material == Block.HONEY_BLOCK && player.getClientVersion().isOlderThan(ClientVersion.V_1_15))
            friction = 0.8f;
        if (material == Block.PACKED_ICE) friction = 0.98f;
        if (material == Block.FROSTED_ICE) friction = 0.98f;
        if (material == Block.BLUE_ICE) {
            friction = 0.98f;
            if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) friction = 0.989f;
        }

        return friction;
    }

    private static Block getOnBlock(GrimPlayer player, double x, double y, double z) {
        Block block1 = player.compensatedWorld.getStateTypeAt(GrimMath.floor(x), GrimMath.floor(y - 0.2F), GrimMath.floor(z));

        if (block1.isAir()) {
            Block block2 = player.compensatedWorld.getStateTypeAt(GrimMath.floor(x), GrimMath.floor(y - 1.2F), GrimMath.floor(z));

            if (Materials.isFence(block2) || Materials.isWall(block2) || Materials.isGate(block2)) {
                return block2;
            }
        }

        return block1;
    }

    private static float getBlockSpeedFactorLegacy(GrimPlayer player, Point pos) {
        Block block = player.compensatedWorld.getStateTypeAt(pos.x(), pos.y(), pos.z());

        // This is the 1.16.0 and 1.16.1 method for detecting if the player is on soul speed
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16) && player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_16_1)) {
            Block onBlock = BlockProperties.getOnBlock(player, pos.x(), pos.y(), pos.z());
            if (onBlock == Block.SOUL_SAND && EnchantmentUtils.getEnchantmentLevel(player.getInventory().getBoots().getItemStack(), Enchantment.SOUL_SPEED) > 0)
                return 1.0f;
        }

        float speed = getBlockSpeedFactor(player, block);
        if (speed != 1.0f || block == Block.SOUL_SAND || block == Block.WATER || block == Block.BUBBLE_COLUMN) return speed;

        Block block2 = player.compensatedWorld.getStateTypeAt(pos.x(), pos.y() - 0.5000001, pos.z());
        return getBlockSpeedFactor(player, block2);
    }

    private static float getBlockSpeedFactor(GrimPlayer player, Block type) {
        if (type == Block.HONEY_BLOCK) return 0.4f;
        if (type == Block.SOUL_SAND) {
            // Soul speed is a 1.16+ enchantment
            // This new method for detecting soul speed was added in 1.16.2
            // On 1.21, let attributes handle this
            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21)
                    && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_16_2)
                    && EnchantmentUtils.getEnchantmentLevel(player.getInventory().getBoots().getItemStack(), Enchantment.SOUL_SPEED) > 0)
                return 1.0f;
            return 0.4f;
        }
        return 1.0f;
    }

    private static float getModernVelocityMultiplier(GrimPlayer player, float blockSpeedFactor) {
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21)) return blockSpeedFactor;
        return (float) GrimMath.lerp((float) player.compensatedEntities.getSelf().getAttributeValue(Attribute.GENERIC_MOVEMENT_EFFICIENCY), blockSpeedFactor, 1.0F);
    }
}

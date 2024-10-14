package ac.grim.grimac.utils.data.packetentity;

import ac.grim.grimac.checks.impl.movement.NoSlowE;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
import ac.grim.grimac.utils.inventory.EnchantmentHelper;
import ac.grim.grimac.utils.math.GrimMath;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.potion.PotionEffect;

import java.util.ArrayList;

public class PacketEntitySelf extends PacketEntity {

    private final GrimPlayer player;
    @Getter
    @Setter
    int opLevel;

    public PacketEntitySelf(GrimPlayer player) {
        super(player, player.bukkitPlayer);
        this.player = player;
    }

    public PacketEntitySelf(GrimPlayer player, PacketEntitySelf old) {
        super(player, player.bukkitPlayer);
        this.player = player;
        this.opLevel = old.opLevel;
        this.attributeMap.putAll(old.attributeMap);
    }

    @Override
    protected void initAttributes(GrimPlayer player) {
        super.initAttributes(player);
        if (player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
            setAttribute(Attribute.GENERIC_STEP_HEIGHT, 0.5f);
        }

        final ValuedAttribute movementSpeed = ValuedAttribute.ranged(Attribute.GENERIC_MOVEMENT_SPEED, 0.1f, 0, 1024);
        movementSpeed.with(new EntityAttributesPacket.Property(Attribute.GENERIC_MOVEMENT_SPEED, 0.1f, new ArrayList<>()));
        trackAttribute(movementSpeed);
        trackAttribute(ValuedAttribute.ranged(Attribute.GENERIC_JUMP_STRENGTH, 0.42f, 0, 32)
                .requiredVersion(player, ClientVersion.V_1_20_5));
        trackAttribute(ValuedAttribute.ranged(Attribute.PLAYER_BLOCK_BREAK_SPEED, 1.0, 0, 1024)
                .requiredVersion(player, ClientVersion.V_1_20_5));
        trackAttribute(ValuedAttribute.ranged(Attribute.PLAYER_MINING_EFFICIENCY, 0, 0, 1024)
                .requiredVersion(player, ClientVersion.V_1_21));
        trackAttribute(ValuedAttribute.ranged(Attribute.PLAYER_SUBMERGED_MINING_SPEED, 0.2, 0, 20)
                .requiredVersion(player, ClientVersion.V_1_21));
        trackAttribute(ValuedAttribute.ranged(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 3, 0, 64)
                .requiredVersion(player, ClientVersion.V_1_20_5));
        trackAttribute(ValuedAttribute.ranged(Attribute.PLAYER_BLOCK_INTERACTION_RANGE, 4.5, 0, 64)
                .withGetRewriter(value -> {
                    // Server versions older than 1.20.5 don't send the attribute, if the player is in creative then assume legacy max reach distance.
                    // < 1.20.5 is unchanged due to requiredVersion, otherwise controlled by the server
                    return value;
                })
                .requiredVersion(player, ClientVersion.V_1_20_5));
        trackAttribute(ValuedAttribute.ranged(Attribute.GENERIC_WATER_MOVEMENT_EFFICIENCY, 0, 0, 1)
                .withGetRewriter(value -> {
                    // Depth strider was added in 1.8
                    if (player.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
                        return 0d;
                    }

                    // On clients < 1.21, use depth strider enchant level always
                    final double depthStrider = EnchantmentHelper.getMaximumEnchantLevel(player.getInventory(), Enchantment.DEPTH_STRIDER);
                    if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21)) {
                        return depthStrider;
                    }

                    // We are on a version that fully supports this value!
                    return value;
                })
                .requiredVersion(player, ClientVersion.V_1_21));
        trackAttribute(ValuedAttribute.ranged(Attribute.GENERIC_MOVEMENT_EFFICIENCY, 0, 0, 1)
                .requiredVersion(player, ClientVersion.V_1_21));
        trackAttribute(ValuedAttribute.ranged(Attribute.PLAYER_SNEAKING_SPEED, 0.3, 0, 1)
                .withGetRewriter(value -> {
                    if (player.getClientVersion().isOlderThan(ClientVersion.V_1_19)) {
                        return (double) 0.3f;
                    }

                    final int swiftSneak = EnchantmentUtils.getEnchantmentLevel(player.getInventory().getLeggings().getItemStack(), Enchantment.SWIFT_SNEAK);
                    final double clamped = GrimMath.clampFloat(0.3F + (swiftSneak * 0.15F), 0f, 1f);
                    if (player.getClientVersion().isOlderThan(ClientVersion.V_1_21)) {
                        return clamped;
                    }

                    // https://github.com/ViaVersion/ViaVersion/blob/dc503cd613f5cf00a6f11b78e52b1a76a42acf91/common/src/main/java/com/viaversion/viaversion/protocols/v1_20_5to1_21/storage/EfficiencyAttributeStorage.java#L32
//                    if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21)) {
//                        return clamped;
//                    }

                    // We are on a version that fully supports this value!
                    return value;
                })
                .requiredVersion(player, ClientVersion.V_1_21));
    }

    public boolean inVehicle() {
        return getRiding() != null;
    }

    @Override
    public void addPotionEffect(PotionEffect effect, int amplifier) {
        if (effect == PotionEffect.BLINDNESS && !hasPotionEffect(PotionEffect.BLINDNESS)) {
            player.checkManager.getPostPredictionCheck(NoSlowE.class).startedSprintingBeforeBlind = player.isSprinting;
        }

        player.pointThreeEstimator.updatePlayerPotions(effect, amplifier);
        super.addPotionEffect(effect, amplifier);
    }

    @Override
    public void removePotionEffect(PotionEffect effect) {
        player.pointThreeEstimator.updatePlayerPotions(effect, null);
        super.removePotionEffect(effect);
    }

    @Override
    public void onFirstTransaction(boolean relative, boolean hasPos, double relX, double relY, double relZ, GrimPlayer player) {
        // Player ignores this
    }

    @Override
    public void onSecondTransaction() {
        // Player ignores this
    }

    @Override
    public SimpleCollisionBox getPossibleCollisionBoxes() {
        return player.boundingBox.copy(); // Copy to retain behavior of PacketEntity
    }
}

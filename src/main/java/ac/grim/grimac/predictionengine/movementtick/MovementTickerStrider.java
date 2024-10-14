package ac.grim.grimac.predictionengine.movementtick;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.data.attribute.ValuedAttribute;
import ac.grim.grimac.utils.data.packetentity.PacketEntityStrider;
import ac.grim.grimac.utils.minestom.BlockTags;
import ac.grim.grimac.utils.nmsutil.BlockProperties;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;
import net.minestom.server.utils.NamespaceID;

import java.util.ArrayList;

public class MovementTickerStrider extends MovementTickerRideable {

    private static final NamespaceID SUFFOCATING = NamespaceID.from(Key.MINECRAFT_NAMESPACE, "minecraft:suffocating");

    public MovementTickerStrider(GrimPlayer player) {
        super(player);
        movementInput = new MutableVector(0, 0, 1);
    }

    public static void floatStrider(GrimPlayer player) {
        if (player.wasTouchingLava) {
            if (isAbove(player) && player.compensatedWorld.getLavaFluidLevelAt((int) Math.floor(player.x), (int) Math.floor(player.y + 1), (int) Math.floor(player.z)) == 0) {
                player.onGround = true;
            } else {
                player.clientVelocity.multiply(0.5).add(new MutableVector(0, 0.05, 0));
            }
        }
    }

    public static boolean isAbove(GrimPlayer player) {
        return player.y > Math.floor(player.y) + 0.5 - 1.0E-5F;
    }

    @Override
    public void livingEntityAIStep() {
        super.livingEntityAIStep();

        Block posMaterial = player.compensatedWorld.getStateTypeAt(player.x, player.y, player.z);
        Block belowMaterial = BlockProperties.getOnPos(player, player.mainSupportingBlockData, new Vector3d(player.x, player.y, player.z));

        final PacketEntityStrider strider = (PacketEntityStrider) player.compensatedEntities.getSelf().getRiding();
        strider.isShaking = !BlockTags.STRIDER_WARM_BLOCKS.contains(posMaterial) &&
                        !BlockTags.STRIDER_WARM_BLOCKS.contains(belowMaterial) &&
                        !player.wasTouchingLava;
    }

    private static final AttributeModifier SUFFOCATING_MODIFIER = new AttributeModifier(SUFFOCATING, -0.34F, AttributeOperation.MULTIPLY_BASE);

    @Override
    public float getSteeringSpeed() {
        PacketEntityStrider strider = (PacketEntityStrider) player.compensatedEntities.getSelf().getRiding();
        // Unsure which version the speed changed in
        final boolean newSpeed = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20);
        final float coldSpeed = newSpeed ? 0.35F : 0.23F;

        // Client desyncs the attribute
        // Again I don't know when this was changed, or whether it always existed, so I will just put it behind 1.20+
        final ValuedAttribute movementSpeedAttr = strider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).get();
        float updatedMovementSpeed = (float) movementSpeedAttr.get();
        if (newSpeed) {
            final EntityAttributesPacket.Property lastProperty = movementSpeedAttr.property().orElse(null);
            if (lastProperty != null && (!strider.isShaking || lastProperty.modifiers().stream().noneMatch(mod -> mod.id().key().equals(SUFFOCATING)))) {
                EntityAttributesPacket.Property newProperty = new EntityAttributesPacket.Property(lastProperty.attribute(), lastProperty.value(), new ArrayList<>(lastProperty.modifiers()));
                if (!strider.isShaking) {
                    newProperty.modifiers().removeIf(modifier -> modifier.id().key().equals(SUFFOCATING));
                } else {
                    newProperty.modifiers().add(SUFFOCATING_MODIFIER);
                }
                movementSpeedAttr.with(newProperty);
                updatedMovementSpeed = (float) movementSpeedAttr.get();
                movementSpeedAttr.with(lastProperty);
            }
        }

        return updatedMovementSpeed * (strider.isShaking ? coldSpeed : 0.55F);
    }

    @Override
    public boolean canStandOnLava() {
        return true;
    }
}

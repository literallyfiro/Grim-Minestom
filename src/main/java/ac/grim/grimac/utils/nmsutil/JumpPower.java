package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.potion.PotionEffect;

import java.util.OptionalInt;

public class JumpPower {
    public static void jumpFromGround(GrimPlayer player, MutableVector vector) {
        float jumpPower = getJumpPower(player);

        final OptionalInt jumpBoost = player.compensatedEntities.getPotionLevelForPlayer(PotionEffect.JUMP_BOOST);
        if (jumpBoost.isPresent()) {
            jumpPower += 0.1f * (jumpBoost.getAsInt() + 1);
        }

        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) && jumpPower <= 1.0E-5F) return;

        vector.setY(jumpPower);

        if (player.isSprinting) {
            float radRotation = player.xRot * ((float) Math.PI / 180F);
            vector.add(new MutableVector(-player.trigHandler.sin(radRotation) * 0.2f, 0.0, player.trigHandler.cos(radRotation) * 0.2f));
        }
    }

    public static float getJumpPower(GrimPlayer player) {
        return (float) player.compensatedEntities.getSelf().getAttributeValue(Attribute.GENERIC_JUMP_STRENGTH) * getPlayerJumpFactor(player);
    }

    public static float getPlayerJumpFactor(GrimPlayer player) {
        return BlockProperties.onHoneyBlock(player, player.mainSupportingBlockData, new Vector3d(player.lastX, player.lastY, player.lastZ)) ? 0.5f : 1f;
    }
}

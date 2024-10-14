package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.vector.MutableVector;

public class FluidFallingAdjustedMovement {
    public static MutableVector getFluidFallingAdjustedMovement(GrimPlayer player, double d, boolean bl, MutableVector vec3) {
        if (player.hasGravity && !player.isSprinting) {
            boolean falling = player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14) ? bl : vec3.getY() < 0;
            double d2 = falling && Math.abs(vec3.getY() - 0.005) >= 0.003 && Math.abs(vec3.getY() - d / 16.0) < 0.003 ? -0.003 : vec3.getY() - d / 16.0;
            return new MutableVector(vec3.getX(), d2, vec3.getZ());
        }
        return vec3;
    }
}

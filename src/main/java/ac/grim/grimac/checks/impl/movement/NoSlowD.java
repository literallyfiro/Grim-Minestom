package ac.grim.grimac.checks.impl.movement;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;

@CheckData(name = "NoSlowD", setback = 5, experimental = true)
public class NoSlowD extends Check implements PostPredictionCheck, PacketCheck {
    public NoSlowD(GrimPlayer player) {
        super(player);
    }

    public boolean startedSprintingBeforeUse = false;
    private boolean flaggedLastTick = false;

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientEntityActionPacket packet) {
            if (packet.action() == ClientEntityActionPacket.Action.START_SPRINTING) {
                startedSprintingBeforeUse = false;
            }
        }
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        if (player.packetStateData.isSlowedByUsingItem()) {
            // https://bugs.mojang.com/browse/MC-152728
            if (startedSprintingBeforeUse && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_14_2)) {
                reward();
                flaggedLastTick = false;
                return;
            }

            if (player.isSprinting) {
                if (flaggedLastTick && flagWithSetback()) alert("");
                flaggedLastTick = true;
            } else {
                reward();
                flaggedLastTick = false;
            }
        }
    }
}

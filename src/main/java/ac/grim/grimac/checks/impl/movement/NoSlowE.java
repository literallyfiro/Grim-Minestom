package ac.grim.grimac.checks.impl.movement;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;
import net.minestom.server.potion.PotionEffect;

@CheckData(name = "NoSlowE", setback = 5, experimental = true)
public class NoSlowE extends Check implements PostPredictionCheck, PacketCheck {
    public NoSlowE(GrimPlayer player) {
        super(player);
    }

    public boolean startedSprintingBeforeBlind = false;

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientEntityActionPacket packet) {
            if (packet.action() == ClientEntityActionPacket.Action.START_SPRINTING) {
                startedSprintingBeforeBlind = false;
            }
        }
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        if (player.compensatedEntities.getSelf().hasPotionEffect(PotionEffect.BLINDNESS)) {
            if (player.isSprinting && !startedSprintingBeforeBlind) {
                if (flagWithSetback()) alert("");
            } else reward();
        }
    }
}

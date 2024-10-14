package ac.grim.grimac.manager;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import lombok.Getter;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;

@Getter
public class ActionManager extends Check implements PacketCheck {
    private boolean attacking = false;
    private long lastAttack = 0;

    public ActionManager(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientInteractEntityPacket action) {
            if (action.type() instanceof ClientInteractEntityPacket.Attack) {
                player.totalFlyingPacketsSent = 0;
                attacking = true;
                lastAttack = System.currentTimeMillis();
            }
        } else if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            player.totalFlyingPacketsSent++;
            attacking = false;
        }
    }

    public boolean hasAttackedSince(long time) {
        return System.currentTimeMillis() - lastAttack < time;
    }


}

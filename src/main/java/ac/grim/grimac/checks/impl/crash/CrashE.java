package ac.grim.grimac.checks.impl.crash;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.impl.exploit.ExploitA;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.common.ClientSettingsPacket;

import java.util.Locale;

@CheckData(name = "CrashE", experimental = false)
public class CrashE extends Check implements PacketCheck {

    public CrashE(GrimPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientSettingsPacket wrapper) {
            int viewDistance = wrapper.viewDistance();
            boolean invalidLocale = player.checkManager.getPrePredictionCheck(ExploitA.class).checkString(wrapper.locale());
            if (viewDistance < 2) {
                flagAndAlert("distance=" + viewDistance);
                // todo minestom
                //wrapper.setViewDistance(2);
            }
            if (invalidLocale) player.bukkitPlayer.setLocale(Locale.ENGLISH);
        }
    }

}

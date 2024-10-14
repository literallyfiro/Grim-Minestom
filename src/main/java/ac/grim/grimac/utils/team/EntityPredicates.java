package ac.grim.grimac.utils.team;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.network.packet.server.play.TeamsPacket;

import java.util.function.Predicate;

public final class EntityPredicates {

    public static Predicate<GrimPlayer> canBePushedBy(GrimPlayer player, PacketEntity entity, TeamHandler teamHandler) {
        if (player.gamemode == GameMode.SPECTATOR) return p -> false;
        final EntityTeam entityTeam = teamHandler.getEntityTeam(entity).orElse(null);
        TeamsPacket.CollisionRule collisionRule = entityTeam == null ? TeamsPacket.CollisionRule.ALWAYS : entityTeam.getCollisionRule();
        if (collisionRule == TeamsPacket.CollisionRule.NEVER) return p -> false;

        return p -> {
            final EntityTeam playersTeam = teamHandler.getPlayersTeam().orElse(null);
            TeamsPacket.CollisionRule collisionRule2 = playersTeam == null ? TeamsPacket.CollisionRule.ALWAYS : playersTeam.getCollisionRule();
            if (collisionRule2 == TeamsPacket.CollisionRule.NEVER) {
                return false;
            } else {
                boolean bl = entityTeam != null && entityTeam.equals(playersTeam);
                if ((collisionRule == TeamsPacket.CollisionRule.PUSH_OWN_TEAM || collisionRule2 == TeamsPacket.CollisionRule.PUSH_OWN_TEAM) && bl) {
                    return false;
                } else {
                    return collisionRule != TeamsPacket.CollisionRule.PUSH_OTHER_TEAMS && collisionRule2 != TeamsPacket.CollisionRule.PUSH_OTHER_TEAMS || bl;
                }
            }
        };
    }
}

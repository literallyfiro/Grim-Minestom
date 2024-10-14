package ac.grim.grimac.utils.team;

import ac.grim.grimac.player.GrimPlayer;
import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.TeamsPacket;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class EntityTeam {

    private final GrimPlayer player;
    @Getter private final String name;
    @Getter private final Set<String> entries = new HashSet<>();
    @Getter private TeamsPacket.CollisionRule collisionRule;

    public EntityTeam(GrimPlayer player, String name) {
        this.player = player;
        this.name = name;
    }

    public void update(TeamsPacket teams) {
        if (teams.action() instanceof TeamsPacket.CreateTeamAction || teams.action() instanceof TeamsPacket.UpdateTeamAction) {
            TeamsPacket.CollisionRule collisionRule1 = teams.action() instanceof TeamsPacket.CreateTeamAction ?
                    ((TeamsPacket.CreateTeamAction) teams.action()).collisionRule() :
                    ((TeamsPacket.UpdateTeamAction) teams.action()).collisionRule();
            if (collisionRule1 != null) {
                this.collisionRule = collisionRule1;
            }
        }

        if (teams.action() instanceof TeamsPacket.AddEntitiesToTeamAction || teams.action() instanceof TeamsPacket.CreateTeamAction) {
            final TeamHandler teamHandler = player.checkManager.getPacketCheck(TeamHandler.class);
            Collection<String> entities = teams.action() instanceof TeamsPacket.AddEntitiesToTeamAction ?
                    ((TeamsPacket.AddEntitiesToTeamAction) teams.action()).entities() :
                    ((TeamsPacket.CreateTeamAction) teams.action()).entities();
            for (String teamsPlayer : entities) {
                if (teamsPlayer.equals(player.getName())) {
                    player.teamName = name;
                    continue;
                }

                boolean flag = false;
                for (Player profile : player.compensatedEntities.profiles.values()) {
                    if (profile.getName() != null && profile.getName().equals(teamsPlayer)) {
                        teamHandler.addEntityToTeam(profile.getUuid().toString(), this);
                        flag = true;
                    }
                }

                if (flag) continue;

                teamHandler.addEntityToTeam(teamsPlayer, this);
            }
        } else if (teams.action() instanceof TeamsPacket.RemoveEntitiesToTeamAction action) {
            for (String teamsPlayer : action.entities()) {
                if (teamsPlayer.equals(player.getName())) {
                    player.teamName = null;
                    continue;
                }
                entries.remove(teamsPlayer);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityTeam)) return false;
        return Objects.equals(getName(), ((EntityTeam) o).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}

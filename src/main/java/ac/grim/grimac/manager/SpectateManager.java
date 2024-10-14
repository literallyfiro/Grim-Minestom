package ac.grim.grimac.manager;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpectateManager implements Initable {

    private final Map<UUID, PreviousState> spectatingPlayers = new ConcurrentHashMap<>();
    private final Set<UUID> hiddenPlayers = ConcurrentHashMap.newKeySet();
    private final Set<UUID> allowedWorlds = ConcurrentHashMap.newKeySet();

    private boolean checkWorld = false;

    @Override
    public void start() {
        allowedWorlds.clear();
        // todo  allowedWorlds.addAll(GrimAPI.INSTANCE.getConfigManager().getConfig().getStringListElse("spectators.allowed-worlds", new ArrayList<>()));
        //        checkWorld = !(allowedWorlds.isEmpty() || new ArrayList<>(allowedWorlds).get(0).isEmpty());
        //
        List<UUID> worlds = GrimAPI.INSTANCE.getConfigManager().getConfig().get("spectators.allowed-worlds");
        allowedWorlds.addAll(worlds);
        // todo minestom here
        checkWorld = !(allowedWorlds.isEmpty()); //|| new ArrayList<>(allowedWorlds).getFirst().toString().isEmpty());
    }

    public boolean isSpectating(UUID uuid) {
        return spectatingPlayers.containsKey(uuid);
    }

//    public boolean shouldHidePlayer(GrimPlayer receiver, PlayerInfoUpdatePacket playerData) {
//        return playerData.() != null
//                && playerData.getUser().getUUID() != null
//                && shouldHidePlayer(receiver, playerData.getUser().getUUID());
//    }

    public boolean shouldHidePlayer(GrimPlayer receiver, UUID uuid) {
        return !Objects.equals(uuid, receiver.playerUUID) // don't hide to yourself
                && (spectatingPlayers.containsKey(uuid) || hiddenPlayers.contains(uuid)) //hide if you are a spectator
                && !(receiver.playerUUID != null && (spectatingPlayers.containsKey(receiver.playerUUID) || hiddenPlayers.contains(receiver.playerUUID))) // don't hide to other spectators
                && (!checkWorld || (receiver.bukkitPlayer != null && allowedWorlds.contains(receiver.bukkitPlayer.getInstance().getUniqueId()))); // hide if you are in a specific world
    }

    public boolean enable(Player player) {
        if (spectatingPlayers.containsKey(player.getUuid())) return false;
        spectatingPlayers.put(player.getUuid(), new PreviousState(player.getGameMode(), player.getPosition()));
        return true;
    }

    public void onLogin(Player player) {
        hiddenPlayers.add(player.getUuid());
    }

    public void onQuit(Player player) {
        hiddenPlayers.remove(player.getUuid());
        handlePlayerStopSpectating(player.getUuid());
    }

    //only call this synchronously
    public void disable(Player player, boolean teleportBack) {
        PreviousState previousState = spectatingPlayers.get(player.getUuid());
        if (previousState != null) {
            if (teleportBack) player.teleport(previousState.location);
            player.setGameMode(previousState.gameMode);
        }
        handlePlayerStopSpectating(player.getUuid());
    }

    public void handlePlayerStopSpectating(UUID uuid) {
        spectatingPlayers.remove(uuid);
    }

    private static class PreviousState {
        public PreviousState(GameMode gameMode, Pos location) {
            this.gameMode = gameMode;
            this.location = location;
        }

        private final GameMode gameMode;
        private final Pos location;
    }

}

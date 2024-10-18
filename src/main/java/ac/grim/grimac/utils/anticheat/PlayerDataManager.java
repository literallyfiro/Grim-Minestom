package ac.grim.grimac.utils.anticheat;

import ac.grim.grimac.player.GrimPlayer;
import net.minestom.server.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final ConcurrentHashMap<Player, GrimPlayer> playerDataMap = new ConcurrentHashMap<>();
    public final Collection<Player> exemptUsers = Collections.synchronizedCollection(new HashSet<>());

    public GrimPlayer getPlayer(final Player player) {
        // Is it safe to interact with this, or is this internal PacketEvents code?
        return playerDataMap.get(player);
    }

    public boolean shouldCheck(Player player) {
        if (exemptUsers.contains(player)) return false;

        if (player.getUuid() != null) {
            // Geyser players don't have Java movement
            // Floodgate is the authentication system for Geyser on servers that use Geyser as a proxy instead of installing it as a plugin directly on the server
            // todo minestom geyser???
//            if (GeyserUtil.isGeyserPlayer(user.getUUID()) || FloodgateUtil.isFloodgatePlayer(user.getUUID())) {
//                exemptUsers.add(user);
//                return false;
//            }

            // Has exempt permission
            if (player.hasPermission("grim.exempt")) {
                exemptUsers.add(player);
                return false;
            }

            // Geyser formatted player string
            // This will never happen for Java players, as the first character in the 3rd group is always 4 (xxxxxxxx-xxxx-4xxx-xxxx-xxxxxxxxxxxx)
            if (player.getUuid().toString().startsWith("00000000-0000-0000-0009")) {
                exemptUsers.add(player);
                return false;
            }
        }

        return true;
    }

    public void addUser(final Player user) {
        if (shouldCheck(user)) {
            GrimPlayer player = new GrimPlayer(user);
            playerDataMap.put(user, player);
        }
    }

    public void remove(final Player player) {
        playerDataMap.remove(player);
    }

    public Collection<GrimPlayer> getEntries() {
        return playerDataMap.values();
    }

    public int size() {
        return playerDataMap.size();
    }
}

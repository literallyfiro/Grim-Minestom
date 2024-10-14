package ac.grim.grimac.checks.impl.prediction;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.CheckData;
import ac.grim.grimac.checks.type.PostPredictionCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.LogUtil;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.lists.EvictingQueue;
import ac.grim.grimac.utils.vector.MutableVector;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@CheckData(name = "Prediction (Debug)")
public class DebugHandler extends Check implements PostPredictionCheck {

    Set<Player> listeners = new CopyOnWriteArraySet<>(new HashSet<>());
    boolean outputToConsole = false;

    boolean enabledFlags = false;
    boolean lastMovementIsFlag = false;

    EvictingQueue<String> predicted = new EvictingQueue<>(5);
    EvictingQueue<String> actually = new EvictingQueue<>(5);
    EvictingQueue<String> offset = new EvictingQueue<>(5);

    public DebugHandler(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (!predictionComplete.isChecked()) return;

        double offset = predictionComplete.getOffset();

        // No one is listening to this debug
        if (listeners.isEmpty() && !outputToConsole) return;
        // This is pointless debug!
        if (player.predictedVelocity.vector.lengthSquared() == 0 && offset == 0) return;

        TextColor color = pickColor(offset, offset);

        MutableVector predicted = player.predictedVelocity.vector;
        MutableVector actually = player.actualMovement;

        TextColor xColor = pickColor(Math.abs(predicted.getX() - actually.getX()), offset);
        TextColor yColor = pickColor(Math.abs(predicted.getY() - actually.getY()), offset);
        TextColor zColor = pickColor(Math.abs(predicted.getZ() - actually.getZ()), offset);

        String p = color + "P: " + xColor + predicted.getX() + " " + yColor + predicted.getY() + " " + zColor + predicted.getZ();
        String a = color + "A: " + xColor + actually.getX() + " " + yColor + actually.getY() + " " + zColor + actually.getZ();
        String canSkipTick = (player.couldSkipTick + " ").substring(0, 1);
        String actualMovementSkip = (player.skippedTickInActualMovement + " ").substring(0, 1);
        String o = NamedTextColor.GRAY + canSkipTick + "→0.03→" + actualMovementSkip + color + " O: " + offset;

        String prefix = player.bukkitPlayer == null ? "null" : player.bukkitPlayer.getName() + " ";

        boolean thisFlag = color != NamedTextColor.GRAY && color != NamedTextColor.GREEN;
        if (enabledFlags) {
            // If the last movement was a flag, don't duplicate messages to the player
            if (lastMovementIsFlag) {
                this.predicted.clear();
                this.actually.clear();
                this.offset.clear();
            }
            // Even if last was a flag, we must send the new message if the player flagged
            this.predicted.add(p);
            this.actually.add(a);
            this.offset.add(o);

            lastMovementIsFlag = thisFlag;
        }

        if (thisFlag) {
            for (int i = 0; i < this.predicted.size(); i++) {
                player.bukkitPlayer.sendMessage(this.predicted.get(i));
                player.bukkitPlayer.sendMessage(this.actually.get(i));
                player.bukkitPlayer.sendMessage(this.offset.get(i));
            }
        }

        for (Player player : listeners) {
            // Don't add prefix if the player is listening to oneself
            player.sendMessage((player == getPlayer().bukkitPlayer ? "" : prefix) + p);
            player.sendMessage((player == getPlayer().bukkitPlayer ? "" : prefix) + a);
            player.sendMessage((player == getPlayer().bukkitPlayer ? "" : prefix) + o);
        }

        // Don't memory leak player references
        listeners.removeIf(player -> !player.isOnline());

        if (outputToConsole) {
            LogUtil.info(prefix + p);
            LogUtil.info(prefix + a);
            LogUtil.info(prefix + o);
        }
    }

    private TextColor pickColor(double offset, double totalOffset) {
        if (player.getSetbackTeleportUtil().blockOffsets) return NamedTextColor.GRAY;
        if (offset <= 0 || totalOffset <= 0) { // If exempt don't bother coloring, so I stop getting false false reports
            return NamedTextColor.GRAY;
        } else if (offset < 0.0001) {
            return NamedTextColor.GREEN;
        } else if (offset < 0.01) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }

    public void toggleListener(Player player) {
        // Toggle, if already added, remove.  If not added, then add
        if (!listeners.remove(player)) listeners.add(player);
    }

    public boolean toggleConsoleOutput() {
        this.outputToConsole = !outputToConsole;
        return this.outputToConsole;
    }
}

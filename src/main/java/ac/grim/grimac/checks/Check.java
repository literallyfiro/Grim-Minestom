package ac.grim.grimac.checks;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.common.ConfigReloadObserver;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.common.ClientPongPacket;

// Class from https://github.com/Tecnio/AntiCheatBase/blob/master/src/main/java/me/tecnio/anticheat/check/Check.java
@Getter
public class Check implements AbstractCheck, ConfigReloadObserver {
    protected final GrimPlayer player;

    public double violations;
    private double decay;
    private double setbackVL;

    private String checkName;
    private String configName;
    private String alternativeName;
    private String description;

    private boolean experimental;
    @Setter
    private boolean isEnabled;
    private boolean exempted;

    @Override
    public boolean isExperimental() {
        return experimental;
    }

    public Check(final GrimPlayer player) {
        this.player = player;

        final Class<?> checkClass = this.getClass();

        if (checkClass.isAnnotationPresent(CheckData.class)) {
            final CheckData checkData = checkClass.getAnnotation(CheckData.class);
            this.checkName = checkData.name();
            this.configName = checkData.configName();
            // Fall back to check name
            if (this.configName.equals("DEFAULT")) this.configName = this.checkName;
            this.decay = checkData.decay();
            this.setbackVL = checkData.setback();
            this.alternativeName = checkData.alternativeName();
            this.experimental = checkData.experimental();
            this.description = checkData.description();
        }
        //
        reload(GrimAPI.INSTANCE.getConfigManager().getConfig());
    }

    public boolean shouldModifyPackets() {
        return isEnabled && !player.disableGrim && !player.noModifyPacketPermission && !exempted;
    }

    public void updateExempted() {
        if (checkName == null) return;
        exempted = player.bukkitPlayer.hasPermission("grim.exempt." + checkName.toLowerCase());
    }

    public final boolean flagAndAlert(String verbose) {
        if (flag()) {
            alert(verbose);
            return true;
        }
        return false;
    }

    public final boolean flagAndAlert() {
        return flagAndAlert("");
    }

    public final boolean flag() {
        if (player.disableGrim || (experimental && !GrimAPI.INSTANCE.getConfigManager().isExperimentalChecks()) || exempted)
            return false; // Avoid calling event if disabled

//        TODO minestom event
//        FlagEvent event = new FlagEvent(player, this);
//        Bukkit.getPluginManager().callEvent(event);
//        if (event.isCancelled()) return false;
//

        player.punishmentManager.handleViolation(this);

        violations++;
        return true;
    }

    public final boolean flagWithSetback() {
        if (flag()) {
            setbackIfAboveSetbackVL();
            return true;
        }
        return false;
    }

    public final void reward() {
        violations = Math.max(0, violations - decay);
    }

    @Override
    public void reload(ConfigManager configuration) {
        decay = configuration.getDoubleElse(configName + ".decay", decay);
        setbackVL = configuration.getDoubleElse(configName + ".setbackvl", setbackVL);
        if (setbackVL == -1) setbackVL = Double.MAX_VALUE;
        updateExempted();
        onReload(configuration);
    }

    @Override
    public void onReload(ConfigManager config) {

    }

    public boolean alert(String verbose) {
        return player.punishmentManager.handleAlert(player, verbose, this);
    }

    public boolean setbackIfAboveSetbackVL() {
        if (getViolations() > setbackVL) {
            return player.getSetbackTeleportUtil().executeViolationSetback();
        }
        return false;
    }

    public boolean isAboveSetbackVl() {
        return getViolations() > setbackVL;
    }

    public String formatOffset(double offset) {
        return offset > 0.001 ? String.format("%.5f", offset) : String.format("%.2E", offset);
    }

    public boolean isTransaction(ClientPacket packetType) {
        return packetType instanceof ClientPongPacket;
    }

    @Override
    public void reload() {
        reload(GrimAPI.INSTANCE.getConfigManager().getConfig());
    }

}


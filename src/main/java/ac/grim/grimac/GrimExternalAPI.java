package ac.grim.grimac;

import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.GrimUser;
import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.manager.AlertManagerImpl;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.common.ConfigReloadObserver;
import ac.grim.grimac.utils.nmsutil.ChatUtil;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static ac.grim.grimac.GrimAPI.EXECUTOR_SERVICE;

//This is used for grim's external API. It has its own class just for organization.

public class GrimExternalAPI implements ConfigReloadObserver, Initable {

    private final GrimAPI api;

    public GrimExternalAPI(GrimAPI api) {
        this.api = api;
    }

    @Nullable
    public GrimUser getGrimUser(Player player) {
        return api.getPlayerDataManager().getPlayer(player);
    }

    public void setServerName(String name) {
        variableReplacements.put("%server%", user -> name);
    }

    @Getter
    private final Map<String, Function<GrimUser, String>> variableReplacements = new ConcurrentHashMap<>();

    @Getter
    private final Map<String, String> staticReplacements = new ConcurrentHashMap<>();

    public String replaceVariables(GrimUser user, String content, boolean colors) {
        if (colors) content = ChatUtil.translateAlternateColorCodes(content);
        for (Map.Entry<String, String> entry : staticReplacements.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, Function<GrimUser, String>> entry : variableReplacements.entrySet()) {
            content = content.replace(entry.getKey(), entry.getValue().apply(user));
        }
        return content;
    }

    public void registerVariable(String string, Function<GrimUser, String> replacement) {
        if (replacement == null) {
            variableReplacements.remove(string);
        } else {
            variableReplacements.put(string, replacement);
        }
    }

    public void registerVariable(String variable, String replacement) {
        if (replacement == null) {
            staticReplacements.remove(variable);
        } else {
            staticReplacements.put(variable, replacement);
        }
    }

    public String getGrimVersion() {
//        PluginDescriptionFile description = GrimAPI.INSTANCE.getPlugin().getDescription();
//        return description.getVersion();
        return "1.0.0";
    }

    private final Map<String, Function<Object, Object>> functions = new ConcurrentHashMap<>();

    public void registerFunction(String key, Function<Object, Object> function) {
        if (function == null) {
            functions.remove(key);
        } else {
            functions.put(key, function);
        }
    }

    public Function<Object, Object> getFunction(String key) {
        return functions.get(key);
    }

    public AlertManagerImpl getAlertManager() {
        return GrimAPI.INSTANCE.getAlertManager();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private ConfigManager configManager = null;

    @Override
    public void start() {
        if (configManager == null) configManager = GrimAPI.INSTANCE.getConfigManager();
        variableReplacements.put("%player%", GrimUser::getName);
        variableReplacements.put("%uuid%", user -> user.getUniqueId().toString());
        variableReplacements.put("%ping%", user -> user.getTransactionPing() + "");
        variableReplacements.put("%brand%", GrimUser::getBrand);
        variableReplacements.put("%h_sensitivity%", user -> ((int) Math.round(user.getHorizontalSensitivity() * 200)) + "");
        variableReplacements.put("%v_sensitivity%", user -> ((int) Math.round(user.getVerticalSensitivity() * 200)) + "");
        variableReplacements.put("%fast_math%", user -> !user.isVanillaMath() + "");
//        variableReplacements.put("%tps%", user -> String.format("%.2f", SpigotReflectionUtil.getTPS()));
        variableReplacements.put("%version%", GrimUser::getVersionName);
        variableReplacements.put("%prefix%", user -> GrimAPI.INSTANCE.getConfigManager().getConfig().getStringElse("prefix", "&bGrim &8»"));
    }

    public void reload(ConfigManager config) {
        successfulReload(config);
    }

    public CompletableFuture<Boolean> reloadAsync(ConfigManager config) {
        return CompletableFuture.completedFuture(successfulReload(config));
    }

    private boolean successfulReload(ConfigManager config) {
        try {
            config.reload();
            onReload(config);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onReload(ConfigManager newConfig) {
        configManager = newConfig != null ? newConfig : configManager;
        //Reload checks for all players
        for (GrimPlayer grimPlayer : GrimAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            EXECUTOR_SERVICE.submit(() -> {
                grimPlayer.reload(configManager);
                grimPlayer.updatePermissions();
                grimPlayer.punishmentManager.reload(configManager);
                for (AbstractCheck value : grimPlayer.checkManager.allChecks.values()) {
                    value.reload(configManager);
                }
            });
//            ChannelHelper.runInEventLoop(grimPlayer.user.getChannel(), () -> {
//                grimPlayer.reload(configManager);
//                grimPlayer.updatePermissions();
//                grimPlayer.punishmentManager.reload(configManager);
//                for (AbstractCheck value : grimPlayer.checkManager.allChecks.values()) {
//                    value.reload(configManager);
//                }
//            });
        }
        //Restart
        GrimAPI.INSTANCE.getDiscordManager().start();
        GrimAPI.INSTANCE.getSpectateManager().start();
        GrimAPI.INSTANCE.getExternalAPI().start();
    }

}

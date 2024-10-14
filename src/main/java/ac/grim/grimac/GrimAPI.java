package ac.grim.grimac;

import ac.grim.grimac.manager.*;
import ac.grim.grimac.manager.config.BaseConfigManager;
import ac.grim.grimac.manager.config.ConfigManagerFileImpl;
import ac.grim.grimac.utils.anticheat.PlayerDataManager;
import lombok.Getter;

import java.io.File;
import java.util.concurrent.ExecutorService;

@Getter
public enum GrimAPI {
    INSTANCE;

    public static final ExecutorService EXECUTOR_SERVICE = java.util.concurrent.Executors.newSingleThreadExecutor();

    private final BaseConfigManager configManager = new BaseConfigManager();
    private final AlertManagerImpl alertManager = new AlertManagerImpl();
    private final SpectateManager spectateManager = new SpectateManager();
    private final DiscordManager discordManager = new DiscordManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final TickManager tickManager = new TickManager();
    private final GrimExternalAPI externalAPI = new GrimExternalAPI(this);
    private InitManager initManager;

    private File dataFolder;

    public void load(File dataFolder) {
        this.dataFolder = dataFolder;

        initManager = new InitManager();
        initManager.load();
    }

    public void start() {
        initManager.start();
    }

    public void stop() {
        initManager.stop();
    }
}

package ac.grim.grimac.manager;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.GrimExternalAPI;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.manager.init.start.CommandRegister;
import ac.grim.grimac.manager.init.start.EventManager;
import ac.grim.grimac.manager.init.start.ExemptOnlinePlayers;
import ac.grim.grimac.manager.init.start.JavaVersion;
import ac.grim.grimac.manager.init.start.PacketLimiter;
import ac.grim.grimac.manager.init.start.PacketManager;
import ac.grim.grimac.manager.init.start.TickEndEvent;
import ac.grim.grimac.manager.init.start.TickRunner;
import ac.grim.grimac.manager.init.start.ViaBackwardsManager;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import lombok.Getter;

public class InitManager {
    private final ClassToInstanceMap<Initable> initializersOnStart;

    @Getter private boolean started = false;
    @Getter private boolean stopped = false;

    public InitManager() {
        initializersOnStart = new ImmutableClassToInstanceMap.Builder<Initable>()
                .put(GrimExternalAPI.class, GrimAPI.INSTANCE.getExternalAPI())
                .put(ExemptOnlinePlayers.class, new ExemptOnlinePlayers())
                .put(EventManager.class, new EventManager())
                .put(PacketManager.class, new PacketManager())
                .put(ViaBackwardsManager.class, new ViaBackwardsManager())
                .put(TickRunner.class, new TickRunner())
                .put(TickEndEvent.class, new TickEndEvent())
                .put(CommandRegister.class, new CommandRegister())
                .put(PacketLimiter.class, new PacketLimiter())
                .put(DiscordManager.class, GrimAPI.INSTANCE.getDiscordManager())
                .put(SpectateManager.class, GrimAPI.INSTANCE.getSpectateManager())
                .put(JavaVersion.class, new JavaVersion())
//                .put(ViaVersion.class, new ViaVersion())
                .build();
    }

    public void start() {
        for (Initable initable : initializersOnStart.values()) {
            initable.start();
        }
        started = true;
    }

    public void stop() {
        stopped = true;
    }
}

package ac.grim.grimac.manager.init.start;

import ac.grim.grimac.events.packets.CheckManagerListener;
import ac.grim.grimac.events.packets.PacketBlockAction;
import ac.grim.grimac.events.packets.PacketConfigurationListener;
import ac.grim.grimac.events.packets.PacketEntityAction;
import ac.grim.grimac.events.packets.PacketPingListener;
import ac.grim.grimac.events.packets.PacketPlayerAttack;
import ac.grim.grimac.events.packets.PacketPlayerCooldown;
import ac.grim.grimac.events.packets.PacketPlayerDigging;
import ac.grim.grimac.events.packets.PacketPlayerJoinQuit;
import ac.grim.grimac.events.packets.PacketPlayerRespawn;
import ac.grim.grimac.events.packets.PacketPlayerSteer;
import ac.grim.grimac.events.packets.PacketSelfMetadataListener;
import ac.grim.grimac.events.packets.PacketServerTags;
import ac.grim.grimac.events.packets.PacketServerTeleport;
import ac.grim.grimac.events.packets.worldreader.BasePacketWorldReader;
import ac.grim.grimac.manager.init.Initable;
import ac.grim.grimac.utils.anticheat.LogUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public class PacketManager implements Initable {
    @Override
    public void start() {
        LogUtil.info("Registering packets...");

        EventNode<Event> node = EventNode.all("packet-events");
        new PacketConfigurationListener(node);
        new PacketPlayerJoinQuit(node);
        new PacketPingListener(node);
        new PacketPlayerDigging(node);
        new PacketPlayerAttack(node);
        new PacketEntityAction(node);
        new PacketBlockAction(node);
        new PacketSelfMetadataListener(node);
        new PacketServerTeleport(node);
        new PacketPlayerCooldown(node);
        new PacketPlayerRespawn(node);
        new CheckManagerListener(node);
        new PacketPlayerSteer(node);

        new PacketServerTags(node);
        new BasePacketWorldReader(node);

//        PacketEvents.getAPI().getEventManager().registerListener(new ProxyAlertMessenger());
//        PacketEvents.getAPI().getEventManager().registerListener(new PacketHidePlayerInfo());

        MinecraftServer.getGlobalEventHandler().addChild(node);
    }
}

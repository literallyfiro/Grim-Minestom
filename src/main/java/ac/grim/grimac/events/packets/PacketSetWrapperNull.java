//package ac.grim.grimac.events.packets;
//
//import ac.grim.grimac.GrimAPI;
//import ac.grim.grimac.player.GrimPlayer;
//import ac.grim.grimac.utils.minestom.EventPriority;
//import com.github.retrooper.packetevents.protocol.packettype.PacketType;
//import net.minestom.server.entity.GameMode;
//import net.minestom.server.event.Event;
//import net.minestom.server.event.EventNode;
//import net.minestom.server.event.player.PlayerPacketEvent;
//import net.minestom.server.event.player.PlayerPacketOutEvent;
//import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
//import net.minestom.server.network.packet.client.common.ClientSettingsPacket;
//import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
//import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.EnumSet;
//import java.util.List;
//
//public class PacketSetWrapperNull {
//
//    public PacketSetWrapperNull(EventNode<Event> globalNode) {
//        EventNode<Event> node = EventNode.all("packet-set-wrapper-null");
//        node.setPriority(EventPriority.HIGHEST.ordinal());
//
//        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);
////        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);
//
//        globalNode.addChild(node);
//    }
//
//    public void onPacketSend(PlayerPacketOutEvent event) {
////        if (event.getPacket() instanceof EntityMetaDataPacket wrapper) {
////            WrapperPlayServerEntityMetadata wrapper = new WrapperPlayServerEntityMetadata(event);
////            if (wrapper.getEntityId() != event.getUser().getEntityId()) {
////                event.setLastUsedWrapper(null);
////            }
////        } else
//        if (event.getPacket() instanceof PlayerInfoUpdatePacket packet) {
//            //iterate through players and fake their game mode if they are spectating via grim spectate
//            GrimPlayer receiver = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
//
//            if (receiver == null) { // Exempt
//                return;
//            }
//
//            if (packet.actions().contains(PlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE)
//                    || packet.actions().contains(PlayerInfoUpdatePacket.Action.ADD_PLAYER)) {
//                List<PlayerInfoUpdatePacket.Entry> nmsPlayerInfoDataList = packet.entries();
//
//                int hideCount = 0;
//                for (PlayerInfoUpdatePacket.Entry playerData : nmsPlayerInfoDataList) {
//                    if (GrimAPI.INSTANCE.getSpectateManager().shouldHidePlayer(receiver, playerData.uuid())) {
//                        hideCount++;
//                        if (playerData.gameMode() == GameMode.SPECTATOR) {
////                            playerData.setGameMode(GameMode.SURVIVAL);
//                        }
//                    }
//                }
//
//                //if amount of hidden players is the amount of players updated & is an update game mode action just cancel it
//
//                if (hideCount == nmsPlayerInfoDataList.size() && info.getAction() == WrapperPlayServerPlayerInfo.Action.UPDATE_GAME_MODE) {
//                    event.setCancelled(true);
//                }
////                else if (hideCount <= 0) {
////                    event.setLastUsedWrapper(null);
////                }
//            }
//        } else if (event.getPacket() instanceof PLAYER_INFO_UPDATEPacket) {
//            GrimPlayer receiver = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getUser());
//            if (receiver == null) return;
//            //create wrappers
//            WrapperPlayServerPlayerInfoUpdate wrapper = new WrapperPlayServerPlayerInfoUpdate(event);
//            EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions = wrapper.getActions();
//            //player's game mode updated
//            if (actions.contains(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE)) {
//                boolean onlyGameMode = actions.size() == 1; // packet is being sent to only update game modes
//                int hideCount = 0;
//                List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> modified = new ArrayList<>(wrapper.getEntries().size());
//                //iterate through the player entries
//                for (WrapperPlayServerPlayerInfoUpdate.PlayerInfo entry : wrapper.getEntries()) {
//                    //check if the player should be hidden
//                    WrapperPlayServerPlayerInfoUpdate.PlayerInfo modifiedPacket = null;
//                    if (GrimAPI.INSTANCE.getSpectateManager().shouldHidePlayer(receiver, entry.getProfileId())) {
//                        hideCount++;
//                        //modify & create a new packet from pre-existing one if they are a spectator
//                        if (entry.getGameMode() == GameMode.SPECTATOR) {
//                            modifiedPacket = new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
//                                    entry.getGameProfile(),
//                                    entry.isListed(),
//                                    entry.getLatency(),
//                                    GameMode.SURVIVAL,
//                                    entry.getDisplayName(),
//                                    entry.getChatSession()
//                            );
//                            modified.add(modifiedPacket);
//                        }
//                    }
//
//                    if (modifiedPacket == null) {  //if the packet wasn't modified, send original
//                        modified.add(entry);
//                    } else if (!onlyGameMode) { //if more than just the game mode updated, modify the packet
//                        modified.add(modifiedPacket);
//                    } //if only the game mode was updated and the packet was modified, don't send anything
//
//                }
//                //if no hidden players, don't modify packet
//                if (hideCount <= 0) {
//                    event.setLastUsedWrapper(null);
//                } else if (hideCount == modified.size()) { //if the amount of hidden players & modified entries are the same
//                    if (onlyGameMode) { // if only the game mode changed, cancel
//                        event.setCancelled(true);
//                    } else { //if more than the game mode changed, remove the action
//                        wrapper.getActions().remove(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE);
//                    }
//                } else { //modify entries
//                    wrapper.setEntries(modified);
//                }
//            }
//
//        }
////        else if (event.getPacketType() != PacketType.Play.Server.PLAYER_POSITION_AND_LOOK) {
////            event.setLastUsedWrapper(null);
////        }
//    }
//
////    public void onPacketReceive(PlayerPacketEvent event) {
////        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacket()) && !(event.getPacket() instanceof ClientSettingsPacket) && !event.isCancelled()) {
////            event.setLastUsedWrapper(null);
////        }
////    }
//}

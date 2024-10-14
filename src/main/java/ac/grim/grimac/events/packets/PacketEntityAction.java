package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.minestom.EventPriority;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.play.ClientEntityActionPacket;

public class PacketEntityAction {

//    public PacketEntityAction() {
//        super(PacketListenerPriority.LOW);
//    }

    public PacketEntityAction(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-entity-action");
        node.setPriority(EventPriority.LOW.ordinal());

        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);

        globalNode.addChild(node);
    }

    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientEntityActionPacket action) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());

            if (player == null) return;

            switch (action.action()) {
                case START_SPRINTING:
                    player.isSprinting = true;
                    break;
                case STOP_SPRINTING:
                    player.isSprinting = false;
                    break;
                case START_SNEAKING:
                    player.isSneaking = true;
                    break;
                case STOP_SNEAKING:
                    player.isSneaking = false;
                    break;
                case START_FLYING_ELYTRA:
                    if (player.onGround || player.lastOnGround) {
                        player.getSetbackTeleportUtil().executeForceResync();

                        if (player.bukkitPlayer != null) {
                            // Client ignores sneaking, use it to resync
                            player.bukkitPlayer.setSneaking(!player.bukkitPlayer.isSneaking());
                        }

                        event.setCancelled(true);
                        player.onPacketCancel();
                        break;
                    }
                    // Starting fall flying is server sided on 1.14 and below
                    if (player.getClientVersion().isOlderThan(ClientVersion.V_1_15)) return;
                    ModifiableItemStack chestPlate = player.getInventory().getChestplate();

                    // This shouldn't be needed with latency compensated inventories
                    // TODO: Remove this?
                    if (chestPlate != null && chestPlate.getType() == Material.ELYTRA
                            && chestPlate.getDamageValue() < chestPlate.getMaxDamage()) {
                        player.isGliding = true;
                        player.pointThreeEstimator.updatePlayerGliding();
                    } else {
                        // A client is flying with a ghost elytra, resync
                        player.getSetbackTeleportUtil().executeForceResync();
                        if (player.bukkitPlayer != null) {
                            // Client ignores sneaking, use it to resync
                            player.bukkitPlayer.setSneaking(!player.bukkitPlayer.isSneaking());
                        }
                        event.setCancelled(true);
                        player.onPacketCancel();
                    }
                    break;
                case START_JUMP_HORSE:
                    if (action.horseJumpBoost() >= 90) {
                        player.vehicleData.nextHorseJump = 1;
                    } else {
                        player.vehicleData.nextHorseJump = 0.4F + 0.4F * action.horseJumpBoost() / 90.0F;
                    }
                    break;
            }
        }
    }
}

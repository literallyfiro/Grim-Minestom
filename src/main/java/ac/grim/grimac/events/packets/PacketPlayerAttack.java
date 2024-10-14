package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsW;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.minestom.ItemTags;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EntityType;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.network.packet.client.play.ClientInteractEntityPacket;

import static ac.grim.grimac.utils.inventory.Inventory.HOTBAR_OFFSET;

public class PacketPlayerAttack {

//    public PacketPlayerAttack() {
//        super(PacketListenerPriority.LOW);
//    }

    public PacketPlayerAttack(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-player-attack");
        node.setPriority(EventPriority.LOW.ordinal());

        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);

        globalNode.addChild(node);
    }

    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientInteractEntityPacket interact) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());

            if (player == null) return;

            // The entity does not exist
            if (!player.compensatedEntities.entityMap.containsKey(interact.targetId()) && !player.compensatedEntities.serverPositionsMap.containsKey(interact.targetId())) {
                if (player.checkManager.getPacketCheck(BadPacketsW.class).flagAndAlert("entityId=" + interact.targetId()) && player.checkManager.getPacketCheck(BadPacketsW.class).shouldModifyPackets()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
                return;
            }

            if (interact.type() instanceof ClientInteractEntityPacket.Attack) {
                ModifiableItemStack heldItem = player.getInventory().getHeldItem();
                PacketEntity entity = player.compensatedEntities.getEntity(interact.targetId());

                // You don't get a release use item with block hitting with a sword?
                if (player.getClientVersion().isOlderThan(ClientVersion.V_1_9) && player.packetStateData.isSlowedByUsingItem()) {
                    ModifiableItemStack item = player.getInventory().inventory.getPlayerInventoryItem(player.packetStateData.getSlowedByUsingItemSlot() + HOTBAR_OFFSET);
                    if (ItemTags.SWORDS.contains(item.getType())) {
                        player.packetStateData.setSlowedByUsingItem(false);
                    }
                }

                if (entity != null && (!(entity.isLivingEntity()) || entity.getType() == EntityType.PLAYER)) {
                    boolean hasKnockbackSword = heldItem != null && EnchantmentUtils.getEnchantmentLevel(heldItem.getItemStack(), Enchantment.KNOCKBACK) > 0;
                    boolean isLegacyPlayer = player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8);
                    boolean hasNegativeKB = heldItem != null && EnchantmentUtils.getEnchantmentLevel(heldItem.getItemStack(), Enchantment.KNOCKBACK) < 0;

                    // 1.8 players who are packet sprinting WILL get slowed
                    // 1.9+ players who are packet sprinting might not, based on attack cooldown
                    // Players with knockback enchantments always get slowed
                    if ((player.isSprinting && !hasNegativeKB && isLegacyPlayer) || hasKnockbackSword) {
                        player.minPlayerAttackSlow += 1;
                        player.maxPlayerAttackSlow += 1;

                        // Players cannot slow themselves twice in one tick without a knockback sword
                        if (!hasKnockbackSword) {
                            player.minPlayerAttackSlow = 0;
                            player.maxPlayerAttackSlow = 1;
                        }
                    } else if (!isLegacyPlayer && player.isSprinting) {
                        // 1.9+ player who might have been slowed, but we can't be sure
                        player.maxPlayerAttackSlow += 1;
                    }
                }
            }
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            player.minPlayerAttackSlow = 0;
        }
    }
}

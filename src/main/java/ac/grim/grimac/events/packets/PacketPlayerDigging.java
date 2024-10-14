package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.impl.movement.NoSlowA;
import ac.grim.grimac.checks.impl.movement.NoSlowD;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.EnchantmentUtils;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.minestom.ItemTags;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.Food;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;

public class PacketPlayerDigging {

//    public PacketPlayerDigging() {
//        super(PacketListenerPriority.LOW);
//    }
    public PacketPlayerDigging(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("packet-player-digging");
        node.setPriority(EventPriority.LOW.ordinal());

        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);

        globalNode.addChild(node);
    }

    public static void handleUseItem(GrimPlayer player, ModifiableItemStack item, Player.Hand hand) {
        if (item == null) {
            player.packetStateData.setSlowedByUsingItem(false);
            return;
        }

        final Material material = item.getType();

        if (player.checkManager.getCompensatedCooldown().hasMaterial(material)) {
            player.packetStateData.setSlowedByUsingItem(false); // resync, not required
            return; // The player has a cooldown, and therefore cannot use this item!
        }

        // Check for data component stuff on 1.20.5+
        Food foodComponent = item.getItemStack().get(ItemComponent.FOOD);
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5) && foodComponent != null) {
            if (foodComponent.canAlwaysEat() || player.food < 20 || player.gamemode == GameMode.CREATIVE) {
                player.packetStateData.setSlowedByUsingItem(true);
                player.packetStateData.eatingHand = hand;
                return;
            } else {
                player.packetStateData.setSlowedByUsingItem(false);
            }
        }

        // 1.14 and below players cannot eat in creative, exceptions are potions or milk
        boolean isEdible = item.getItemStack().get(ItemComponent.FOOD) != null;
        if (isEdible &&
                (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15) || player.gamemode != GameMode.CREATIVE)
                || material == Material.POTION || material == Material.MILK_BUCKET) {

            // Pls have this mapped correctly retrooper
            if (item.getType() == Material.SPLASH_POTION)
                return;
            // 1.8 splash potion
//            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9) && item.getLegacyData() > 16384) {
//                return;
//            }

            // Eatable items that don't require any hunger to eat
            if (material == Material.POTION || material == Material.MILK_BUCKET
                    || material == Material.GOLDEN_APPLE || material == Material.ENCHANTED_GOLDEN_APPLE
                    || material == Material.HONEY_BOTTLE || material == Material.SUSPICIOUS_STEW ||
                    material == Material.CHORUS_FRUIT) {
                player.packetStateData.setSlowedByUsingItem(true);
                player.packetStateData.eatingHand = hand;
                return;
            }

            // The other items that do require it
            if (isEdible && ((player.food < 20) || player.gamemode == GameMode.CREATIVE)) {
                player.packetStateData.setSlowedByUsingItem(true);
                player.packetStateData.eatingHand = hand;
                return;
            }

            // The player cannot eat this item, resync use status
            player.packetStateData.setSlowedByUsingItem(false);
        }

        if (material == Material.SHIELD && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            player.packetStateData.setSlowedByUsingItem(true);
            player.packetStateData.eatingHand = hand;
            return;
        }

        // Avoid releasing crossbow as being seen as slowing player
        final CompoundBinaryTag nbt = item.getItemStack().toItemNBT(); // How can this be null?
        if (material == Material.CROSSBOW && nbt.getBoolean("Charged")) {
            player.packetStateData.setSlowedByUsingItem(false); // TODO: Fix this
            return;
        }

        // The client and server don't agree on trident status because mojang is incompetent at netcode.
        if (material == Material.TRIDENT
                && item.getDamageValue() < item.getMaxDamage() - 1 // Player can't use item if it's "about to break"
                && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13_2)
                || player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8))) {
            player.packetStateData.setSlowedByUsingItem(EnchantmentUtils.getEnchantmentLevel(item.getItemStack(), Enchantment.RIPTIDE) <= 0);
            player.packetStateData.eatingHand = hand;
        }

        // Players in survival can't use a bow without an arrow
        // Crossbow charge checked previously
        if (material == Material.BOW || material == Material.CROSSBOW) {
                /*player.packetStateData.slowedByUsingItem = player.gamemode == GameMode.CREATIVE ||
                        player.getInventory().hasItemType(ItemTypes.ARROW) ||
                        player.getInventory().hasItemType(ItemTypes.TIPPED_ARROW) ||
                        player.getInventory().hasItemType(ItemTypes.SPECTRAL_ARROW);
                player.packetStateData.eatingHand = place.getHand();*/
            // TODO: How do we lag compensate arrows? Mojang removed idle packet.
            // I think we may have to cancel the bukkit event if the player isn't slowed
            // On 1.8, it wouldn't be too bad to handle bows correctly
            // But on 1.9+, no idle packet and clients/servers don't agree on bow status
            // Mojang pls fix
            player.packetStateData.setSlowedByUsingItem(false);
        }

        if (material == Material.SPYGLASS && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17)) {
            player.packetStateData.setSlowedByUsingItem(true);
            player.packetStateData.eatingHand = hand;
        }

        if (material == Material.GOAT_HORN && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19)) {
            player.packetStateData.setSlowedByUsingItem(true);
            player.packetStateData.eatingHand = hand;
        }

        // Only 1.8 and below players can block with swords
        if (ItemTags.SWORDS.contains(material)) {
            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8))
                player.packetStateData.setSlowedByUsingItem(true);
//            else if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) // ViaVersion stuff
//                player.packetStateData.setSlowedByUsingItem(false);
        }
    }

    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientPlayerDiggingPacket dig) {
            if (dig.status() == ClientPlayerDiggingPacket.Status.UPDATE_ITEM_STATE) {
                final GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
                if (player == null) return;

                player.packetStateData.setSlowedByUsingItem(false);
                player.packetStateData.slowedByUsingItemTransaction = player.lastTransactionReceived.get();

                ModifiableItemStack hand = player.packetStateData.eatingHand == Player.Hand.OFF ? player.getInventory().getOffHand() : player.getInventory().getHeldItem();

                if (hand.getType() == Material.TRIDENT && EnchantmentUtils.getEnchantmentLevel(hand.getItemStack(), Enchantment.RIPTIDE) > 0) {
                    player.packetStateData.tryingToRiptide = true;
                }
            }
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            final GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            if (!player.packetStateData.lastPacketWasTeleport && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
                if (player.packetStateData.isSlowedByUsingItem() && player.packetStateData.eatingHand != Player.Hand.OFF && player.packetStateData.getSlowedByUsingItemSlot() != player.packetStateData.lastSlotSelected) {
                    player.packetStateData.setSlowedByUsingItem(false);
                    player.checkManager.getPostPredictionCheck(NoSlowA.class).didSlotChangeLastTick = true;
                }
            }
        }

        if (event.getPacket() instanceof ClientHeldItemChangePacket packet) {
            final int slot = packet.slot();

            // Stop people from spamming the server with out of bounds exceptions
            if (slot > 8 || slot < 0) return;

            final GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            // Prevent issues if the player switches slots, while lagging, standing still, and is placing blocks
            CheckManagerListener.handleQueuedPlaces(player, false, 0, 0, System.currentTimeMillis());

            if (player.packetStateData.lastSlotSelected != slot) {
                // just assume they tick after this
                if (!player.isTickingReliablyFor(3) && player.skippedTickInActualMovement && player.packetStateData.eatingHand != Player.Hand.OFF) {
                    player.packetStateData.setSlowedByUsingItem(false);
                }
            }
            player.packetStateData.lastSlotSelected = slot;
        }

        // todo minestom event.getPacket() instanceof ClientPlayerBlockPlacementPacket placement && placement.blockFace() == BlockFace.OTHER)
        if (event.getPacket() instanceof ClientUseItemPacket || (event.getPacket() instanceof ClientPlayerBlockPlacementPacket placement)) {
            final GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
            if (player == null) return;

            final Player.Hand hand = event.getPacket() instanceof ClientUseItemPacket packet
                    ? packet.hand()
                    : Player.Hand.MAIN;

            if (player.gamemode == GameMode.SPECTATOR)
                return;

            player.packetStateData.slowedByUsingItemTransaction = player.lastTransactionReceived.get();

            final ModifiableItemStack item = hand == Player.Hand.MAIN ?
                    player.getInventory().getHeldItem() : player.getInventory().getOffHand();

            final boolean wasSlow = player.packetStateData.isSlowedByUsingItem();

            handleUseItem(player, item, hand);

            if (!wasSlow) {
                player.checkManager.getPostPredictionCheck(NoSlowD.class).startedSprintingBeforeUse = player.packetStateData.isSlowedByUsingItem() && player.isSprinting;
            }
        }
    }
}

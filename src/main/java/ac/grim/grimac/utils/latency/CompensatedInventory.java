package ac.grim.grimac.utils.latency;

import ac.grim.grimac.checks.Check;
import ac.grim.grimac.checks.type.PacketCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.inventory.EquipmentType;
import ac.grim.grimac.utils.inventory.Inventory;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.inventory.inventory.AbstractContainerMenu;
import ac.grim.grimac.utils.inventory.inventory.MenuType;
import ac.grim.grimac.utils.inventory.inventory.NotImplementedMenu;
import ac.grim.grimac.utils.lists.CorrectingPlayerInventoryStorage;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.network.packet.client.play.ClientCloseWindowPacket;
import net.minestom.server.network.packet.client.play.ClientCreativeInventoryActionPacket;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;
import net.minestom.server.network.packet.server.play.CloseWindowPacket;
import net.minestom.server.network.packet.server.play.OpenHorseWindowPacket;
import net.minestom.server.network.packet.server.play.OpenWindowPacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;
import net.minestom.server.network.packet.server.play.WindowItemsPacket;

import java.util.List;

// Updated to support modern 1.17 protocol
public class CompensatedInventory extends Check implements PacketCheck {
    // "Temporarily" public for debugging
    public Inventory inventory;
    // "Temporarily" public for debugging
    public AbstractContainerMenu menu;
    // Not all inventories are supported due to complexity and version differences
    public boolean isPacketInventoryActive = true;
    // Special values:
    // Player inventory is -1
    // Unsupported inventory is -2
    private int packetSendingInventorySize = PLAYER_INVENTORY_CASE;
    private static final int PLAYER_INVENTORY_CASE = -1;
    private static final int UNSUPPORTED_INVENTORY_CASE = -2;
    public boolean needResend = false;
    int openWindowID = 0;
    public int stateID = 0; // Don't mess up the last sent state ID by changing it

    public CompensatedInventory(GrimPlayer playerData) {
        super(playerData);

        CorrectingPlayerInventoryStorage storage = new CorrectingPlayerInventoryStorage(player, 46);
        inventory = new Inventory(playerData, storage);

        menu = inventory;
    }

    // Taken from https://www.spigotmc.org/threads/mapping-protocol-to-bukkit-slots.577724/
    public int getBukkitSlot(int packetSlot) {
        // 0 -> 5 are crafting slots, don't exist in bukkit
        if (packetSlot <= 4) {
            return -1;
        }
        // 5 -> 8 are armor slots in protocol, ordered helmets to boots
        if (packetSlot <= 8) {
            // 36 -> 39 are armor slots in bukkit, ordered boots to helmet. tbh I got this from trial and error.
            return (7 - packetSlot) + 36;
        }
        // By a coincidence, non-hotbar inventory slots match.
        if (packetSlot <= 35) {
            return packetSlot;
        }
        // 36 -> 44 are hotbar slots in protocol
        if (packetSlot <= 44) {
            // 0 -> 9 are hotbar slots in bukkit
            return packetSlot - 36;
        }
        // 45 is offhand is packet, it is 40 in bukkit
        if (packetSlot == 45) {
            return 40;
        }
        return -1;
    }

    // Meant for 1.17+ clients who send changed slots, making the server not send the entire inventory
    private void markPlayerSlotAsChanged(int clicked) {
        // Player inventory
        if (openWindowID == 0) {
            inventory.getInventoryStorage().handleClientClaimedSlotSet(clicked);
            return;
        }

        // We don't know size of the inventory, so we can't do anything
        // We will resync later.
        if (menu instanceof NotImplementedMenu) return;

        // 9-45 are the player inventory slots that are used
        // There are 36 player slots in each menu that we care about and track.
        int nonPlayerInvSize = menu.getSlots().size() - 36 + 9;
        int playerInvSlotclicked = clicked - nonPlayerInvSize;
        // Bypass player inventory
        inventory.getInventoryStorage().handleClientClaimedSlotSet(playerInvSlotclicked);
    }

    public ModifiableItemStack getItemInHand(Player.Hand hand) {
        return hand == Player.Hand.MAIN ? getHeldItem() : getOffHand();
    }

    private void markServerForChangingSlot(int clicked, int windowID) {
        // Unsupported inventory
        if (packetSendingInventorySize == -2) return;
        // Player inventory
        if (packetSendingInventorySize == PLAYER_INVENTORY_CASE || windowID == 0) {
            // Result slot isn't included in storage, we must ignore it
            inventory.getInventoryStorage().handleServerCorrectSlot(clicked);
            return;
        }
        // See note in above method.
        int nonPlayerInvSize = menu.getSlots().size() - 36 + 9;
        int playerInvSlotclicked = clicked - nonPlayerInvSize;

        inventory.getInventoryStorage().handleServerCorrectSlot(playerInvSlotclicked);
    }

    public ModifiableItemStack getHeldItem() {
        ModifiableItemStack item = isPacketInventoryActive ? inventory.getHeldItem() : new ModifiableItemStack(player.bukkitPlayer.getInventory().getItemInMainHand());
        return item == null ? ModifiableItemStack.EMPTY : item;
    }

    public ModifiableItemStack getOffHand() {
        ModifiableItemStack item = isPacketInventoryActive ? inventory.getOffhand() : new ModifiableItemStack(player.bukkitPlayer.getInventory().getItemInOffHand());
        return item == null ? ModifiableItemStack.EMPTY : item;
    }

    public ModifiableItemStack getHelmet() {
        ModifiableItemStack item = isPacketInventoryActive ? inventory.getHelmet() : new ModifiableItemStack(player.bukkitPlayer.getInventory().getHelmet());
        return item == null ? ModifiableItemStack.EMPTY : item;
    }

    public ModifiableItemStack getChestplate() {
        ModifiableItemStack item = isPacketInventoryActive ? inventory.getChestplate() : new ModifiableItemStack(player.bukkitPlayer.getInventory().getChestplate());
        return item == null ? ModifiableItemStack.EMPTY : item;
    }

    public ModifiableItemStack getLeggings() {
        ModifiableItemStack item = isPacketInventoryActive ? inventory.getLeggings() : new ModifiableItemStack(player.bukkitPlayer.getInventory().getLeggings());
        return item == null ? ModifiableItemStack.EMPTY : item;
    }

    public ModifiableItemStack getBoots() {
        ModifiableItemStack item = isPacketInventoryActive ? inventory.getBoots() : new ModifiableItemStack(player.bukkitPlayer.getInventory().getBoots());
        return item == null ? ModifiableItemStack.EMPTY : item;
    }

    private ModifiableItemStack getByEquipmentType(EquipmentType type) {
        return switch (type) {
            case HEAD -> getHelmet();
            case CHEST -> getChestplate();
            case LEGS -> getLeggings();
            case FEET -> getBoots();
            case OFFHAND -> getOffHand();
            case MAINHAND -> getHeldItem();
            default -> ModifiableItemStack.EMPTY;
        };
    }


    public boolean hasItemType(Material type) {
        if (isPacketInventoryActive) return inventory.hasItemType(type);

        // Fall back to bukkit inventories
        for (ItemStack item : player.bukkitPlayer.getInventory().getItemStacks()) {
            if (item.material() == type) return true;
        }
        return false;
    }

    public void onPacketReceive(final PlayerPacketEvent event) {
        if (event.getPacket() instanceof ClientUseItemPacket item) {
            ModifiableItemStack use = item.hand() == Player.Hand.MAIN ? player.getInventory().getHeldItem() : player.getInventory().getOffHand();

            EquipmentType equipmentType = EquipmentType.getEquipmentSlotForItem(use.getItemStack());
            if (equipmentType != null) {
                int slot;
                switch (equipmentType) {
                    case HEAD:
                        slot = Inventory.SLOT_HELMET;
                        break;
                    case CHEST:
                        slot = Inventory.SLOT_CHESTPLATE;
                        break;
                    case LEGS:
                        slot = Inventory.SLOT_LEGGINGS;
                        break;
                    case FEET:
                        slot = Inventory.SLOT_BOOTS;
                        break;
                    default: // Not armor, therefore we shouldn't run this code
                        return;
                }

                ModifiableItemStack itemstack1 = getByEquipmentType(equipmentType);
                // Only 1.19.4+ clients support swapping with non-empty items
                if (player.getClientVersion().isOlderThan(ClientVersion.V_1_19_4) && !itemstack1.isEmpty()) return;

                // 1.19.4+ clients support swapping with non-empty items
                int swapItemSlot = item.hand() == Player.Hand.MAIN ? inventory.selected + Inventory.HOTBAR_OFFSET : Inventory.SLOT_OFFHAND;

                // Mojang implemented this stupidly, I rewrote their item swap code to make it somewhat cleaner.
                inventory.getInventoryStorage().handleClientClaimedSlotSet(swapItemSlot);
                inventory.getInventoryStorage().setItem(swapItemSlot, itemstack1);

                inventory.getInventoryStorage().handleClientClaimedSlotSet(slot);
                inventory.getInventoryStorage().setItem(slot, use);
            }
        }

        if (event.getPacket() instanceof ClientPlayerDiggingPacket dig) {
            // 1.8 clients don't predict dropping items
            if (player.getClientVersion().isOlderThanOrEquals(ClientVersion.V_1_8)) return;

            if (dig.status() == ClientPlayerDiggingPacket.Status.DROP_ITEM) {
                ModifiableItemStack heldItem = getHeldItem();
                if (heldItem != null) {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                    if (heldItem.getAmount() <= 0) {
                        heldItem = null;
                    }
                }
                inventory.setHeldItem(heldItem);
                inventory.getInventoryStorage().handleClientClaimedSlotSet(Inventory.HOTBAR_OFFSET + player.packetStateData.lastSlotSelected);
            }

            if (dig.status() == ClientPlayerDiggingPacket.Status.DROP_ITEM_STACK) {
                inventory.setHeldItem(null);
                inventory.getInventoryStorage().handleClientClaimedSlotSet(Inventory.HOTBAR_OFFSET + player.packetStateData.lastSlotSelected);
            }
        }

        if (event.getPacket() instanceof ClientHeldItemChangePacket packet) {
            final int slot = packet.slot();

            // Stop people from spamming the server with an out-of-bounds exception
            if (slot > 8 || slot < 0) return;

            inventory.selected = slot;
        }

        if (event.getPacket() instanceof ClientCreativeInventoryActionPacket action) {
            if (player.gamemode != GameMode.CREATIVE) return;

            boolean valid = action.slot() >= 1 &&
                    (action.slot() <= 45);

            if (valid) {
                player.getInventory().inventory.getSlot(action.slot()).set(new ModifiableItemStack(action.item()));
                inventory.getInventoryStorage().handleClientClaimedSlotSet(action.slot());
            }
        }

        if (event.getPacket() instanceof ClientClickWindowPacket click && !event.isCancelled()) {
            // How is this possible? Maybe transaction splitting.
            if (click.windowId() != openWindowID) {
                return;
            }

            // Don't care about this click since we can't track it.
            if (menu instanceof NotImplementedMenu) {
                return;
            }

            // Mark the slots the player has changed as changed, then continue simulating what they changed
            List<ClientClickWindowPacket.ChangedSlot> slots = click.changedSlots();
            slots.stream().mapToInt(ClientClickWindowPacket.ChangedSlot::slot).forEach(this::markPlayerSlotAsChanged);

            // 0 for left click
            // 1 for right click
            byte button = click.button();
            // Offset by the number of slots in the inventory actively open
            // Is -999 when clicking off the screen
            short slot = click.slot();
            // Self-explanatory, look at the enum's values
            ClientClickWindowPacket.ClickType clickType = click.clickType();

            if (slot == -1 || slot == -999 || slot < menu.getSlots().size()) {
                menu.doClick(button, slot, clickType);
            }
        }

        if (event.getPacket() instanceof ClientCloseWindowPacket) {
            menu = inventory;
            openWindowID = 0;
            menu.setCarried(ModifiableItemStack.EMPTY); // Reset carried item
        }
    }

    public void markSlotAsResyncing(BlockPlace place) {
        // Update held item tracking
        if (place.getHand() == Player.Hand.MAIN) {
            inventory.getInventoryStorage().handleClientClaimedSlotSet(Inventory.HOTBAR_OFFSET + player.packetStateData.lastSlotSelected);
        } else {
            inventory.getInventoryStorage().handleServerCorrectSlot(Inventory.SLOT_OFFHAND);
        }
    }

    public void onBlockPlace(BlockPlace place) {
        if (player.gamemode != GameMode.CREATIVE && place.getItemStack().getType() != Material.POWDER_SNOW_BUCKET) {
            markSlotAsResyncing(place);
            place.getItemStack().setAmount(place.getItemStack().getAmount() - 1);
        }
    }

    public void onPacketSend(final PlayerPacketOutEvent event) {
        // Not 1:1 MCP, based on Wiki.VG to be simpler as we need less logic...
        // For example, we don't need permanent storage, only storing data until the client closes the window
        // We also don't need a lot of server-sided only logic
        if (event.getPacket() instanceof OpenWindowPacket open) {
            MenuType menuType = MenuType.getMenuType(open.windowType());

            AbstractContainerMenu newMenu = MenuType.getMenuFromID(player, inventory, menuType);
            packetSendingInventorySize = newMenu instanceof NotImplementedMenu ? UNSUPPORTED_INVENTORY_CASE : newMenu.getSlots().size();

            // There doesn't seem to be a check against using 0 as the window ID - let's consider that an invalid packet
            // It will probably mess up a TON of logic both client and server sided, so don't do that!
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                openWindowID = open.windowId();
                menu = newMenu;
                isPacketInventoryActive = !(newMenu instanceof NotImplementedMenu);
                needResend = newMenu instanceof NotImplementedMenu;
            });
        }

        // I'm not implementing this lol
        if (event.getPacket() instanceof OpenHorseWindowPacket open) {
            packetSendingInventorySize = UNSUPPORTED_INVENTORY_CASE;
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                isPacketInventoryActive = false;
                needResend = true;
                openWindowID = open.windowId();
            });
        }

        // 1:1 MCP
        if (event.getPacket() instanceof CloseWindowPacket) {
            packetSendingInventorySize = PLAYER_INVENTORY_CASE;

            // Disregard provided window ID, client doesn't care...
            // We need to do this because the client doesn't send a packet when closing the window
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                openWindowID = 0;
                menu = inventory;
                menu.setCarried(ModifiableItemStack.EMPTY); // Reset carried item
            });
        }

        // Should be 1:1 MCP
        if (event.getPacket() instanceof WindowItemsPacket items) {
            stateID = items.stateId();

            List<ItemStack> slots = items.items();
            for (int i = 0; i < slots.size(); i++) {
                markServerForChangingSlot(i, items.windowId());
            }

            int cachedPacketInvSize = packetSendingInventorySize;
            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                // Never true when the inventory is unsupported.
                // Vanilla ALWAYS sends the entire inventory to resync, this is a valid thing to check
                if (slots.size() == cachedPacketInvSize || items.windowId() == 0) {
                    isPacketInventoryActive = true;
                }
            });

            if (items.windowId() == 0) { // Player inventory
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                    if (!isPacketInventoryActive) return;
                    for (int i = 0; i < slots.size(); i++) {
                        inventory.getSlot(i).set(new ModifiableItemStack(slots.get(i)));
                    }
                    inventory.setCarried(new ModifiableItemStack(items.carriedItem()));
                });
            } else {
                player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                    if (!isPacketInventoryActive) return;
                    if (items.windowId() == openWindowID) {
                        for (int i = 0; i < slots.size(); i++) {
                            menu.getSlot(i).set(new ModifiableItemStack(slots.get(i)));
                        }
                    }
                    inventory.setCarried(new ModifiableItemStack(items.carriedItem()));
                });
            }
        }

        // Also 1:1 MCP
        if (event.getPacket() instanceof SetSlotPacket slot) {
            // Only edit hotbar (36 to 44) if window ID is 0
            // Set cursor by putting -1 as window ID and as slot
            // Window ID -2 means any slot can be used

            if (slot.windowId() == -2) { // Direct inventory change
                inventory.getInventoryStorage().handleServerCorrectSlot(slot.slot());
            } else if (slot.windowId() == 0) { // Inventory change through window ID, no crafting result
                inventory.getInventoryStorage().handleServerCorrectSlot(slot.slot());
            } else {
                markServerForChangingSlot(slot.slot(), slot.windowId());
            }

            stateID = slot.stateId();

            player.latencyUtils.addRealTimeTask(player.lastTransactionSent.get(), () -> {
                if (!isPacketInventoryActive) return;
                if (slot.windowId() == -1) { // Carried item
                    inventory.setCarried(new ModifiableItemStack(slot.itemStack()));
                } else if (slot.windowId() == -2) { // Direct inventory change (only applied if valid slot)
                    if (inventory.getInventoryStorage().getSize() > slot.slot() && slot.slot() >= 0) {
                        inventory.getInventoryStorage().setItem(slot.slot(), new ModifiableItemStack(slot.itemStack()));
                    }
                } else if (slot.windowId() == 0) { // Player inventory
                    // This packet can only be used to edit the hotbar and offhand of the player's inventory if
                    // window ID is set to 0 (slots 36 through 45) if the player is in creative, with their inventory open,
                    // and not in their survival inventory tab. Otherwise, when window ID is 0, it can edit any slot in the player's inventory.
                    if (slot.slot() >= 0 && slot.slot() <= 45) {
                        inventory.getSlot(slot.slot()).set(new ModifiableItemStack(slot.itemStack()));
                    }
                } else if (slot.windowId() == openWindowID) { // Opened inventory (if not valid, client crashes)
                    menu.getSlot(slot.slot()).set(new ModifiableItemStack(slot.itemStack()));
                }
            });
        }
    }
}

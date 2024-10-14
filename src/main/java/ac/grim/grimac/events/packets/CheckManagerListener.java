package ac.grim.grimac.events.packets;

import ac.grim.grimac.GrimAPI;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsX;
import ac.grim.grimac.checks.impl.badpackets.BadPacketsZ;
import ac.grim.grimac.events.packets.patch.ResyncWorldUtil;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.WrapperPlayClientPlayerFlying;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import ac.grim.grimac.utils.anticheat.update.PositionUpdate;
import ac.grim.grimac.utils.anticheat.update.PredictionComplete;
import ac.grim.grimac.utils.anticheat.update.RotationUpdate;
import ac.grim.grimac.utils.anticheat.update.VehiclePositionUpdate;
import ac.grim.grimac.utils.blockplace.BlockPlaceResult;
import ac.grim.grimac.utils.blockplace.ConsumesBlockPlace;
import ac.grim.grimac.utils.collisions.HitboxData;
import ac.grim.grimac.utils.collisions.datatypes.CollisionBox;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.BlockPlaceSnapshot;
import ac.grim.grimac.utils.data.HeadRotation;
import ac.grim.grimac.utils.data.HitData;
import ac.grim.grimac.utils.data.Pair;
import ac.grim.grimac.utils.data.TeleportAcceptData;
import ac.grim.grimac.utils.data.VelocityData;
import ac.grim.grimac.utils.inventory.Inventory;
import ac.grim.grimac.utils.inventory.ModifiableItemStack;
import ac.grim.grimac.utils.math.GrimMath;
import ac.grim.grimac.utils.math.VectorUtils;
import ac.grim.grimac.utils.minestom.EventPriority;
import ac.grim.grimac.utils.minestom.MinestomWrappedBlockState;
import ac.grim.grimac.utils.minestom.StateValue;
import ac.grim.grimac.utils.nmsutil.BlockBreakSpeed;
import ac.grim.grimac.utils.nmsutil.BoundingBoxSize;
import ac.grim.grimac.utils.nmsutil.Collisions;
import ac.grim.grimac.utils.nmsutil.Materials;
import ac.grim.grimac.utils.nmsutil.Ray;
import ac.grim.grimac.utils.nmsutil.ReachUtils;
import ac.grim.grimac.utils.vector.MutableVector;
import ac.grim.grimac.utils.vector.Vector3d;
import ac.grim.grimac.utils.vector.Vector3f;
import ac.grim.grimac.utils.vector.Vector3i;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.ConnectionState;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import net.minestom.server.network.packet.client.play.ClientUseItemPacket;
import net.minestom.server.network.packet.client.play.ClientVehicleMovePacket;
import net.minestom.server.network.packet.server.play.AcknowledgeBlockChangePacket;
import net.minestom.server.network.packet.server.play.SetSlotPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class CheckManagerListener {

    public CheckManagerListener(EventNode<Event> globalNode) {
        EventNode<Event> node = EventNode.all("check-manager-listener");
        node.setPriority(EventPriority.LOW.ordinal());

        node.addListener(PlayerPacketEvent.class, this::onPacketReceive);
        node.addListener(PlayerPacketOutEvent.class, this::onPacketSend);

        globalNode.addChild(node);
    }

    // Copied from MCP...
    // Returns null if there isn't anything.
    //
    // I do have to admit that I'm starting to like bifunctions/new java 8 things more than I originally did.
    // although I still don't understand Mojang's obsession with streams in some of the hottest methods... that kills performance
    public static HitData traverseBlocks(GrimPlayer player, Vector3d start, Vector3d end, BiFunction<MinestomWrappedBlockState, Vector3i, HitData> predicate) {
        // I guess go back by the collision epsilon?
        double endX = GrimMath.lerp(-1.0E-7D, end.x, start.x);
        double endY = GrimMath.lerp(-1.0E-7D, end.y, start.y);
        double endZ = GrimMath.lerp(-1.0E-7D, end.z, start.z);
        double startX = GrimMath.lerp(-1.0E-7D, start.x, end.x);
        double startY = GrimMath.lerp(-1.0E-7D, start.y, end.y);
        double startZ = GrimMath.lerp(-1.0E-7D, start.z, end.z);
        int floorStartX = GrimMath.floor(startX);
        int floorStartY = GrimMath.floor(startY);
        int floorStartZ = GrimMath.floor(startZ);


        if (start.equals(end)) return null;

        MinestomWrappedBlockState state = player.compensatedWorld.getWrappedBlockStateAt(floorStartX, floorStartY, floorStartZ);
        HitData apply = predicate.apply(state, new Vector3i(floorStartX, floorStartY, floorStartZ));

        if (apply != null) {
            return apply;
        }

        double xDiff = endX - startX;
        double yDiff = endY - startY;
        double zDiff = endZ - startZ;
        double xSign = Math.signum(xDiff);
        double ySign = Math.signum(yDiff);
        double zSign = Math.signum(zDiff);

        double posXInverse = xSign == 0 ? Double.MAX_VALUE : xSign / xDiff;
        double posYInverse = ySign == 0 ? Double.MAX_VALUE : ySign / yDiff;
        double posZInverse = zSign == 0 ? Double.MAX_VALUE : zSign / zDiff;

        double d12 = posXInverse * (xSign > 0 ? 1.0D - GrimMath.frac(startX) : GrimMath.frac(startX));
        double d13 = posYInverse * (ySign > 0 ? 1.0D - GrimMath.frac(startY) : GrimMath.frac(startY));
        double d14 = posZInverse * (zSign > 0 ? 1.0D - GrimMath.frac(startZ) : GrimMath.frac(startZ));

        // Can't figure out what this code does currently
        while (d12 <= 1.0D || d13 <= 1.0D || d14 <= 1.0D) {
            if (d12 < d13) {
                if (d12 < d14) {
                    floorStartX += xSign;
                    d12 += posXInverse;
                } else {
                    floorStartZ += zSign;
                    d14 += posZInverse;
                }
            } else if (d13 < d14) {
                floorStartY += ySign;
                d13 += posYInverse;
            } else {
                floorStartZ += zSign;
                d14 += posZInverse;
            }

            state = player.compensatedWorld.getWrappedBlockStateAt(floorStartX, floorStartY, floorStartZ);
            apply = predicate.apply(state, new Vector3i(floorStartX, floorStartY, floorStartZ));

            if (apply != null) {
                return apply;
            }
        }

        return null;
    }

    private static void placeWaterLavaSnowBucket(GrimPlayer player, ModifiableItemStack held, Block toPlace, Player.Hand hand) {
        HitData data = getNearestHitResult(player, Block.AIR, false);
        if (data != null) {
            BlockPlace blockPlace = new BlockPlace(player, hand, data.getPosition(), data.getClosestDirection().ordinal(), data.getClosestDirection(), held, data);

            boolean didPlace = false;

            // Powder snow, lava, and water all behave like placing normal blocks after checking for waterlogging (replace clicked always false though)
            // If we hit a waterloggable block, then the bucket is directly placed
            // Otherwise, use the face to determine where to place the bucket
            if (Materials.isPlaceableWaterBucket(blockPlace.getItemStack().getType())) {
                blockPlace.setReplaceClicked(true); // See what's in the existing place
                MinestomWrappedBlockState existing = blockPlace.getExistingBlockData();
                if (!(boolean) existing.getInternalData().getOrDefault(StateValue.WATERLOGGED, true)) {
                    // Strangely, the client does not predict waterlogged placements
                    didPlace = true;
                }
            }

            if (!didPlace) {
                // Powder snow, lava, and water all behave like placing normal blocks after checking for waterlogging (replace clicked always false though)
                blockPlace.setReplaceClicked(false);
                blockPlace.set(toPlace);
            }

            if (player.gamemode != GameMode.CREATIVE) {
                player.getInventory().markSlotAsResyncing(blockPlace);
                if (hand == Player.Hand.MAIN) {
                    player.getInventory().inventory.setHeldItem(new ModifiableItemStack(ItemStack.of(Material.BUCKET, 1)));
                } else {
                    player.getInventory().inventory.setPlayerInventoryItem(Inventory.SLOT_OFFHAND, new ModifiableItemStack(ItemStack.of(Material.BUCKET, 1)));
                }
            }
        }
    }

    public static void handleQueuedPlaces(GrimPlayer player, boolean hasLook, float pitch, float yaw, long now) {
        // Handle queue'd block places
        BlockPlaceSnapshot snapshot;
        while ((snapshot = player.placeUseItemPackets.poll()) != null) {
            double lastX = player.x;
            double lastY = player.y;
            double lastZ = player.z;

            player.x = player.packetStateData.lastClaimedPosition.getX();
            player.y = player.packetStateData.lastClaimedPosition.getY();
            player.z = player.packetStateData.lastClaimedPosition.getZ();

            boolean lastSneaking = player.isSneaking;
            player.isSneaking = snapshot.isSneaking();

            if (player.compensatedEntities.getSelf().getRiding() != null) {
                Vector3d posFromVehicle = BoundingBoxSize.getRidingOffsetFromVehicle(player.compensatedEntities.getSelf().getRiding(), player);
                player.x = posFromVehicle.getX();
                player.y = posFromVehicle.getY();
                player.z = posFromVehicle.getZ();
            }

            // Less than 15 milliseconds ago means this is likely (fix all look vectors being a tick behind server sided)
            // Or mojang had the idle packet... for the 1.7/1.8 clients
            // No idle packet on 1.9+
            if ((now - player.lastBlockPlaceUseItem < 15 || player.getClientVersion().isOlderThan(ClientVersion.V_1_9)) && hasLook) {
                player.xRot = yaw;
                player.yRot = pitch;
            }

            player.compensatedWorld.startPredicting();
            handleBlockPlaceOrUseItem(snapshot.getWrapper(), player);
            player.compensatedWorld.stopPredicting(snapshot.getWrapper());

            player.x = lastX;
            player.y = lastY;
            player.z = lastZ;
            player.isSneaking = lastSneaking;
        }
    }

    private static void handleUseItem(GrimPlayer player, ModifiableItemStack placedWithModifiable, Player.Hand hand) {
        // Lilypads are USE_ITEM (THIS CAN DESYNC, WTF MOJANG)
        ItemStack placedWith = placedWithModifiable.getItemStack();
        if (placedWith.material() == Material.LILY_PAD) {
            placeLilypad(player, hand); // Pass a block place because lily pads have a hitbox
            return;
        }

        Block toBucketMat = Materials.transformBucketMaterial(placedWith.material());
        if (toBucketMat != null) {
            placeWaterLavaSnowBucket(player, placedWithModifiable, toBucketMat, hand);
        }

        if (placedWith.material() == Material.BUCKET) {
            placeBucket(player, hand);
        }
    }

    private static void handleBlockPlaceOrUseItem(ClientPacket packet, GrimPlayer player) {
        if (packet instanceof ClientUseItemPacket place) {
            if (player.gamemode == GameMode.SPECTATOR || player.gamemode == GameMode.ADVENTURE) return;

            ModifiableItemStack placedWith = player.getInventory().getHeldItem();
            if (place.hand() == Player.Hand.OFF) {
                placedWith = player.getInventory().getOffHand();
            }

            handleUseItem(player, placedWith, place.hand());
        }

        // Check for interactable first (door, etc)
        if (packet instanceof ClientPlayerBlockPlacementPacket place) {
            ModifiableItemStack placedWith = player.getInventory().getHeldItem();
            ModifiableItemStack offhand = player.getInventory().getOffHand();

            boolean onlyAir = placedWith.isEmpty() && offhand.isEmpty();

            // The offhand is unable to interact with blocks like this... try to stop some desync points before they happen
            if ((!player.isSneaking || onlyAir) && place.hand() == Player.Hand.MAIN) {
                Vector3i blockPosition = new Vector3i(place.blockPosition().blockX(), place.blockPosition().blockY(), place.blockPosition().blockZ());
                BlockPlace blockPlace = new BlockPlace(player, place.hand(), blockPosition, place.blockFace().ordinal(), place.blockFace(), placedWith, getNearestHitResult(player, null, true));

                // Right-clicking a trapdoor/door/etc.
                Block placedAgainst = blockPlace.getPlacedAgainstMaterial();
                if ((player.getClientVersion().isOlderThan(ClientVersion.V_1_8) && (placedAgainst == Block.IRON_TRAPDOOR || placedAgainst == Block.IRON_DOOR))
                        || Materials.isClientSideInteractable(placedAgainst)) {

                    if (!player.compensatedEntities.getSelf().inVehicle()) {
                        player.checkManager.onPostFlyingBlockPlace(blockPlace);
                    }
                    Vector3i location = blockPlace.getPlacedAgainstBlockLocation();
                    player.compensatedWorld.tickOpenable(location.getX(), location.getY(), location.getZ());
                    return;
                }

                // This also has side effects
                // This method is for when the block doesn't always consume the click
                // This causes a ton of desync's but mojang doesn't seem to care...
                if (ConsumesBlockPlace.consumesPlace(player, player.compensatedWorld.getWrappedBlockStateAt(blockPlace.getPlacedAgainstBlockLocation()), blockPlace)) {
                    if (!player.compensatedEntities.getSelf().inVehicle()) {
                        player.checkManager.onPostFlyingBlockPlace(blockPlace);
                    }
                    return;
                }
            }
        }

        if (packet instanceof ClientPlayerBlockPlacementPacket place) {
            Vector3i blockPosition = new Vector3i(place.blockPosition().blockX(), place.blockPosition().blockY(), place.blockPosition().blockZ());
            BlockFace face = place.blockFace();


            if (player.gamemode == GameMode.SPECTATOR || player.gamemode == GameMode.ADVENTURE) return;

            ModifiableItemStack placedWith = player.getInventory().getHeldItem();
            if (place.hand() == Player.Hand.OFF) {
                placedWith = player.getInventory().getOffHand();
            }

            BlockPlace blockPlace = new BlockPlace(player, place.hand(), blockPosition, place.blockFace().ordinal(), face, placedWith, getNearestHitResult(player, null, true));
            // At this point, it is too late to cancel, so we can only flag, and cancel subsequent block places more aggressively
            if (!player.compensatedEntities.getSelf().inVehicle()) {
                player.checkManager.onPostFlyingBlockPlace(blockPlace);
            }

            blockPlace.setInside(place.insideBlock());

            // todo minestom placed material
            if (placedWith.getItemStack().material() != null || placedWith.getType() == Material.FIRE_CHARGE) {
                BlockPlaceResult.getMaterialData(placedWith.getType()).applyBlockPlaceToWorld(player, blockPlace);
            }
        }
    }

    private boolean isMojangStupid(GrimPlayer player, WrapperPlayClientPlayerFlying flying) {
        // Mojang has become less stupid!
        if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21)) return false;

        final Pos location = flying.getLocation();
        final double threshold = player.getMovementThreshold();

        // Don't check duplicate 1.17 packets (Why would you do this mojang?)
        // Don't check rotation since it changes between these packets, with the second being irrelevant.
        //
        // removed a large rant, but I'm keeping this out of context insult below
        // EVEN A BUNCH OF MONKEYS ON A TYPEWRITER COULDNT WRITE WORSE NETCODE THAN MOJANG
        if (!player.packetStateData.lastPacketWasTeleport && flying.hasPositionChanged() && flying.hasRotationChanged() &&
                // Ground status will never change in this stupidity packet
                ((flying.isOnGround() == player.packetStateData.packetPlayerOnGround
                        // Mojang added this stupid mechanic in 1.17
                        && (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17) &&
                        // Due to 0.03, we can't check exact position, only within 0.03
                        player.filterMojangStupidityOnMojangStupidity.distanceSquared(new Vector3d(location)) < threshold * threshold))
                        // If the player was in a vehicle, has position and look, and wasn't a teleport, then it was this stupid packet
                        || player.compensatedEntities.getSelf().inVehicle())) {

            // Mark that we want this packet to be cancelled from reaching the server
            // Additionally, only yaw/pitch matters: https://github.com/GrimAnticheat/Grim/issues/1275#issuecomment-1872444018
            // 1.9+ isn't impacted by this packet as much.
//            if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_9)) {
//                if (GrimAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("cancel-duplicate-packet", true)) {
//                    player.packetStateData.cancelDuplicatePacket = true;
//                }
//            } else {
//                // Override location to force it to use the last real position of the player. Prevents position-related bypasses like nofall.
//                flying.setLocation(new Location(player.filterMojangStupidityOnMojangStupidity.getX(), player.filterMojangStupidityOnMojangStupidity.getY(), player.filterMojangStupidityOnMojangStupidity.getZ(), location.getYaw(), location.getPitch()));
//            }

            // todo minestom
//            flying.setLocation(new Location(player.filterMojangStupidityOnMojangStupidity.getX(), player.filterMojangStupidityOnMojangStupidity.getY(), player.filterMojangStupidityOnMojangStupidity.getZ(), location.yaw(), location.pitch()));

            player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = true;

            if (!GrimAPI.INSTANCE.getConfigManager().isIgnoreDuplicatePacketRotation()) {
                if (player.xRot != location.yaw() || player.yRot != location.pitch()) {
                    player.lastXRot = player.xRot;
                    player.lastYRot = player.yRot;
                }

                // Take the pitch and yaw, just in case we were wrong about this being a stupidity packet
                player.xRot = location.yaw();
                player.yRot = location.pitch();
            }

            player.packetStateData.lastClaimedPosition = new Vector3d(location);
            return true;
        }
        return false;
    }

    public void onPacketReceive(PlayerPacketEvent event) {
        if (event.getPlayer().getPlayerConnection().getConnectionState() != ConnectionState.PLAY) return;
        GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
        if (player == null) return;

        // Determine if teleport BEFORE we call the pre-prediction vehicle
        if (event.getPacket() instanceof ClientVehicleMovePacket move) {
            Vector3d position = new Vector3d(move.position());
            player.packetStateData.lastPacketWasTeleport = player.getSetbackTeleportUtil().checkVehicleTeleportQueue(position.getX(), position.getY(), position.getZ());
        }

        TeleportAcceptData teleportData = null;

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            ac.grim.grimac.utils.WrapperPlayClientPlayerFlying flying = new ac.grim.grimac.utils.WrapperPlayClientPlayerFlying(event);

            Vector3d position = VectorUtils.clampVector(new Vector3d(flying.getLocation()));
            // Teleports must be POS LOOK
            teleportData = flying.hasPositionChanged() && flying.hasRotationChanged() ? player.getSetbackTeleportUtil().checkTeleportQueue(position.getX(), position.getY(), position.getZ()) : new TeleportAcceptData();
            player.packetStateData.lastPacketWasTeleport = teleportData.isTeleport();
            // Teleports can't be stupidity packets
            player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = !player.packetStateData.lastPacketWasTeleport && isMojangStupid(player, flying);
        }


        if (player.compensatedEntities.getSelf().inVehicle() ? event.getPacket() instanceof ClientVehicleMovePacket : WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            // Update knockback and explosions immediately, before anything can setback
            int kbEntityId = player.compensatedEntities.getSelf().inVehicle() ? player.getRidingVehicleId() : player.entityID;

            VelocityData calculatedFirstBreadKb = player.checkManager.getKnockbackHandler().calculateFirstBreadKnockback(kbEntityId, player.lastTransactionReceived.get());
            VelocityData calculatedRequireKb = player.checkManager.getKnockbackHandler().calculateRequiredKB(kbEntityId, player.lastTransactionReceived.get(), false);
            player.firstBreadKB = calculatedFirstBreadKb == null ? player.firstBreadKB : calculatedFirstBreadKb;
            player.likelyKB = calculatedRequireKb == null ? player.likelyKB : calculatedRequireKb;

            VelocityData calculateFirstBreadExplosion = player.checkManager.getExplosionHandler().getFirstBreadAddedExplosion(player.lastTransactionReceived.get());
            VelocityData calculateRequiredExplosion = player.checkManager.getExplosionHandler().getPossibleExplosions(player.lastTransactionReceived.get(), false);
            player.firstBreadExplosion = calculateFirstBreadExplosion == null ? player.firstBreadExplosion : calculateFirstBreadExplosion;
            player.likelyExplosions = calculateRequiredExplosion == null ? player.likelyExplosions : calculateRequiredExplosion;
        }

        player.checkManager.onPrePredictionReceivePacket(event);

        // The player flagged crasher or timer checks, therefore we must protect predictions against these attacks
        if (event.isCancelled() && (WrapperPlayClientPlayerFlying.isFlying(event.getPacket()) || event.getPacket() instanceof ClientVehicleMovePacket)) {
            player.packetStateData.cancelDuplicatePacket = false;
            return;
        }

        if (ac.grim.grimac.utils.WrapperPlayClientPlayerFlying.isFlying(event.getPacket())) {
            ac.grim.grimac.utils.WrapperPlayClientPlayerFlying flying = new ac.grim.grimac.utils.WrapperPlayClientPlayerFlying(event);
            Pos pos = flying.getLocation();
            boolean ignoreRotation = player.packetStateData.lastPacketWasOnePointSeventeenDuplicate && GrimAPI.INSTANCE.getConfigManager().isIgnoreDuplicatePacketRotation();
            handleFlying(player, pos.x(), pos.y(), pos.z(), ignoreRotation ? player.xRot : pos.yaw(), ignoreRotation ? player.yRot : pos.pitch(), flying.hasPositionChanged(), flying.hasRotationChanged(), flying.isOnGround(), teleportData, event);
        }

        if (event.getPacket() instanceof ClientVehicleMovePacket move && player.compensatedEntities.getSelf().inVehicle()) {
            Vector3d position = new Vector3d(move.position());

            player.lastX = player.x;
            player.lastY = player.y;
            player.lastZ = player.z;

            Vector3d clamp = VectorUtils.clampVector(position);
            player.x = clamp.getX();
            player.y = clamp.getY();
            player.z = clamp.getZ();

            player.xRot = move.position().yaw();
            player.yRot = move.position().pitch();

            final VehiclePositionUpdate update = new VehiclePositionUpdate(clamp, position, move.position().yaw(), move.position().pitch(), player.packetStateData.lastPacketWasTeleport);
            player.checkManager.onVehiclePositionUpdate(update);

            player.packetStateData.receivedSteerVehicle = false;
        }

        if (event.getPacket() instanceof ClientPlayerDiggingPacket dig) {
            MinestomWrappedBlockState block = player.compensatedWorld.getWrappedBlockStateAt(dig.blockPosition());

            player.checkManager.getPacketCheck(BadPacketsX.class).handle(event, dig, block.getType());
            player.checkManager.getPacketCheck(BadPacketsZ.class).handle(event, dig);

            if (dig.status() == ClientPlayerDiggingPacket.Status.FINISHED_DIGGING) {
                // Not unbreakable
                if (!block.getType().isAir() && block.getType().registry().hardness() != -1.0D && !event.isCancelled()) {
                    player.compensatedWorld.startPredicting();
                    player.compensatedWorld.updateBlock(dig.blockPosition().blockX(), dig.blockPosition().blockY(), dig.blockPosition().blockZ(), 0);
                    player.compensatedWorld.stopPredicting(dig);
                }
            }

            if (dig.status() == ClientPlayerDiggingPacket.Status.STARTED_DIGGING && !event.isCancelled()) {
                double damage = BlockBreakSpeed.getBlockDamage(player, new Vector3i(dig.blockPosition()));

                //Instant breaking, no damage means it is unbreakable by creative players (with swords)
                if (damage >= 1) {
                    player.compensatedWorld.startPredicting();
                    if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13) && Materials.isWaterSource(player.getClientVersion(), block)) {
                        // Vanilla uses a method to grab water flowing, but as you can't break flowing water
                        // We can simply treat all waterlogged blocks or source blocks as source blocks
                        player.compensatedWorld.updateBlock(new Vector3i(dig.blockPosition()), new MinestomWrappedBlockState(Block.WATER));
                    } else {
                        player.compensatedWorld.updateBlock(dig.blockPosition().blockZ(), dig.blockPosition().blockY(), dig.blockPosition().blockZ(), 0);
                    }
                    player.compensatedWorld.stopPredicting(dig);
                }
            }

            if (!event.isCancelled()) {
                if (dig.status() == ClientPlayerDiggingPacket.Status.STARTED_DIGGING
                        || dig.status() == ClientPlayerDiggingPacket.Status.FINISHED_DIGGING
                        || dig.status() == ClientPlayerDiggingPacket.Status.CANCELLED_DIGGING) {
                    player.compensatedWorld.handleBlockBreakPrediction(dig);
                }
            }
        }

        if (event.getPacket() instanceof ClientPlayerBlockPlacementPacket packet) {
            player.lastBlockPlaceUseItem = System.currentTimeMillis();

            ModifiableItemStack placedWith = player.getInventory().getHeldItem();
            if (packet.hand() == Player.Hand.OFF) {
                placedWith = player.getInventory().getOffHand();
            }

            // This is the use item packet
            // Anti-air place
            BlockPlace blockPlace = new BlockPlace(player, packet.hand(), new Vector3i(packet.blockPosition()), packet.blockFace().ordinal(), packet.blockFace(), placedWith, getNearestHitResult(player, null, true));
            blockPlace.setCursor(new Vector3f(packet.cursorPositionX(), packet.cursorPositionY(), packet.cursorPositionZ()));

            if (player.getClientVersion().isOlderThan(ClientVersion.V_1_11)) {
                // ViaRewind is stupid and divides the byte by 15 to get the float
                // We must undo this to get the correct block place... why?
                if (packet.cursorPositionX() * 15 % 1 == 0 && packet.cursorPositionY() * 15 % 1 == 0 && packet.cursorPositionZ() * 15 % 1 == 0) {
                    // This is impossible to occur without ViaRewind, fix their stupidity
                    int trueByteX = (int) (packet.cursorPositionX() * 15);
                    int trueByteY = (int) (packet.cursorPositionY() * 15);
                    int trueByteZ = (int) (packet.cursorPositionZ() * 15);

                    blockPlace.setCursor(new Vector3f(trueByteX / 16f, trueByteY / 16f, trueByteZ / 16f));
                }
            }

            if (!player.compensatedEntities.getSelf().inVehicle())
                player.checkManager.onBlockPlace(blockPlace);

            if (event.isCancelled() || blockPlace.isCancelled() || player.getSetbackTeleportUtil().shouldBlockMovement()) { // The player tried placing blocks in air/water

                if (!event.isCancelled()) {
                    event.setCancelled(true);
                    player.onPacketCancel();
                }

                Vector3i facePos = new Vector3i(packet.blockPosition().blockX() + packet.blockFace().toDirection().normalX(),
                        packet.blockPosition().blockY() + packet.blockFace().toDirection().normalY(),
                        packet.blockPosition().blockZ() + packet.blockFace().toDirection().normalZ());

                // Ends the client prediction introduced in 1.19+
                if (player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19)) {
                    player.bukkitPlayer.sendPacket(new AcknowledgeBlockChangePacket(packet.sequence()));
                } else { // The client isn't smart enough to revert changes
                    ResyncWorldUtil.resyncPosition(player, new Vector3i(packet.blockPosition()));
                    ResyncWorldUtil.resyncPosition(player, facePos);
                }

                // Stop inventory desync from cancelling place
                if (player.bukkitPlayer != null) {
                    if (packet.hand() == Player.Hand.MAIN) {
                        ItemStack mainHand = player.bukkitPlayer.getInventory().getItemInHand(Player.Hand.MAIN);
                        player.bukkitPlayer.sendPacket(new SetSlotPacket((byte) 0, player.getInventory().stateID, ((short)(36 + player.packetStateData.lastSlotSelected)), mainHand));
                    } else {
                        ItemStack offHand = player.bukkitPlayer.getInventory().getItemInOffHand();
                        player.bukkitPlayer.sendPacket(new SetSlotPacket((byte) 0, player.getInventory().stateID, (short) 45, offHand));
                    }
                }

            } else { // Legit place
                player.placeUseItemPackets.add(new BlockPlaceSnapshot(packet, player.isSneaking));
            }
        }

        if (event.getPacket() instanceof ClientUseItemPacket packet) {
            player.placeUseItemPackets.add(new BlockPlaceSnapshot(packet, player.isSneaking));
            player.lastBlockPlaceUseItem = System.currentTimeMillis();
        }

        // Call the packet checks last as they can modify the contents of the packet
        // Such as the NoFall check setting the player to not be on the ground
        player.checkManager.onPacketReceive(event);

        if (player.packetStateData.cancelDuplicatePacket) {
            event.setCancelled(true);
            player.packetStateData.cancelDuplicatePacket = false;
        }

        // Finally, remove the packet state variables on this packet
        player.packetStateData.lastPacketWasOnePointSeventeenDuplicate = false;
        player.packetStateData.lastPacketWasTeleport = false;
    }

    private static void placeBucket(GrimPlayer player, Player.Hand hand) {
        HitData data = getNearestHitResult(player, null, true);

        if (data != null) {
            BlockPlace blockPlace = new BlockPlace(player, hand, data.getPosition(), data.getClosestDirection().ordinal(), data.getClosestDirection(), ModifiableItemStack.EMPTY, data);
            blockPlace.setReplaceClicked(true); // Replace the block clicked, not the block in the direction

            boolean placed = false;
            Material type = null;

            if (data.getState().getType() == Block.POWDER_SNOW) {
                blockPlace.set(Block.AIR);
                type = Material.POWDER_SNOW_BUCKET;
                placed = true;
            }

            if (data.getState().getType() == Block.LAVA) {
                blockPlace.set(Block.AIR);
                type = Material.LAVA_BUCKET;
                placed = true;
            }

            // We didn't hit fluid source
            if (!placed && !player.compensatedWorld.isWaterSourceBlock(data.getPosition().getX(), data.getPosition().getY(), data.getPosition().getZ()))
                return;

            // We can't replace plants with a water bucket
            if (data.getState().getType() == Block.KELP || data.getState().getType() == Block.SEAGRASS || data.getState().getType() == Block.TALL_SEAGRASS) {
                return;
            }

            if (!placed) {
                type = Material.WATER_BUCKET;
            }

            MinestomWrappedBlockState existing = blockPlace.getExistingBlockData();
            if (existing.getInternalData().containsKey(StateValue.WATERLOGGED)) { // waterloggable
                existing.setWaterlogged(false);
                blockPlace.set(existing);
                placed = true;
            }

            // Therefore, not waterlogged and is a fluid, and is therefore a source block
            if (!placed) {
                blockPlace.set(Block.AIR);
            }

            if (player.gamemode != GameMode.CREATIVE) {
                player.getInventory().markSlotAsResyncing(blockPlace);
                setPlayerItem(player, hand, type);
            }
        }
    }

    public static void setPlayerItem(GrimPlayer player, Player.Hand hand, Material type) {
        // Give the player a water bucket
        if (player.gamemode != GameMode.CREATIVE) {
            if (hand == Player.Hand.MAIN) {
                if (player.getInventory().getHeldItem().getAmount() == 1) {
                    ItemStack itemStack = ItemStack.of(type, 1);
                    player.getInventory().inventory.setHeldItem(new ModifiableItemStack(ItemStack.of(type, 1)));
                } else { // Give the player a water bucket
                    player.getInventory().inventory.add(new ModifiableItemStack(ItemStack.of(type, 1)));
                    // and reduce the held item
                    player.getInventory().getHeldItem().setAmount(player.getInventory().getHeldItem().getAmount() - 1);
                }
            } else {
                if (player.getInventory().getOffHand().getAmount() == 1) {
                    player.getInventory().inventory.setPlayerInventoryItem(Inventory.SLOT_OFFHAND, new ModifiableItemStack(ItemStack.of(type, 1)));
                } else { // Give the player a water bucket
                    player.getInventory().inventory.add(Inventory.SLOT_OFFHAND, new ModifiableItemStack(ItemStack.of(type, 1)));
                    // and reduce the held item
                    player.getInventory().getOffHand().setAmount(player.getInventory().getOffHand().getAmount() - 1);
                }
            }
        }
    }

    private void handleFlying(GrimPlayer player, double x, double y, double z, float yaw, float pitch, boolean hasPosition, boolean hasLook, boolean onGround, TeleportAcceptData teleportData, PlayerPacketEvent event) {
        long now = System.currentTimeMillis();

        if (!hasPosition) {
            // This may need to be secured later, although nothing that is very important relies on this
            // 1.8 ghost clients can't abuse this anyway
            player.uncertaintyHandler.lastPointThree.reset();
        }

        // We can't set the look if this is actually the stupidity packet
        // If the last packet wasn't stupid, then ignore this logic
        // If it was stupid, only change the look if it's different
        // Otherwise, reach and fireworks can false
        if (hasLook && (!player.packetStateData.lastPacketWasOnePointSeventeenDuplicate ||
                player.xRot != yaw || player.yRot != pitch)) {
            player.lastXRot = player.xRot;
            player.lastYRot = player.yRot;
        }

        handleQueuedPlaces(player, hasLook, pitch, yaw, now);

        // We can set the new pos after the places
        if (hasPosition) {
            player.packetStateData.lastClaimedPosition = new Vector3d(x, y, z);
        }

        // This stupid mechanic has been measured with 0.03403409022229198 y velocity... DAMN IT MOJANG, use 0.06 to be safe...
        if (!hasPosition && onGround != player.packetStateData.packetPlayerOnGround && !player.compensatedEntities.getSelf().inVehicle()) {
            player.lastOnGround = onGround;
            player.clientClaimsLastOnGround = onGround;
            player.uncertaintyHandler.onGroundUncertain = true;

            // Ghost block/0.03 abuse
            // Check for blocks within 0.03 of the player's position before allowing ground to be true - if 0.03
            // Cannot use collisions like normal because stepping messes it up :(
            //
            // This may need to be secured better, but limiting the new setback positions seems good enough for now...
            boolean canFeasiblyPointThree = Collisions.slowCouldPointThreeHitGround(player, player.x, player.y, player.z);
            if ((!canFeasiblyPointThree && !player.compensatedWorld.isNearHardEntity(player.boundingBox.copy().expand(4))) || player.clientVelocity.getY() > 0.06) {
                // todo minestom this breaks the server
                player.getSetbackTeleportUtil().executeForceResync();
            }
        }

        if (!player.packetStateData.lastPacketWasTeleport) {
            player.packetStateData.packetPlayerOnGround = onGround;
        }

        if (hasLook) {
            player.xRot = yaw;
            player.yRot = pitch;

            float deltaXRot = player.xRot - player.lastXRot;
            float deltaYRot = player.yRot - player.lastYRot;

            final RotationUpdate update = new RotationUpdate(new HeadRotation(player.lastXRot, player.lastYRot), new HeadRotation(player.xRot, player.yRot), deltaXRot, deltaYRot);
            player.checkManager.onRotationUpdate(update);
        }

        if (hasPosition) {
            Vector3d position = new Vector3d(x, y, z);
            Vector3d clampVector = VectorUtils.clampVector(position);
            final PositionUpdate update = new PositionUpdate(new Vector3d(player.x, player.y, player.z), position, onGround, teleportData.getSetback(), teleportData.getTeleportData(), teleportData.isTeleport());

            // Stupidity doesn't care about 0.03
            if (!player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
                player.filterMojangStupidityOnMojangStupidity = clampVector;
            }

            if (!player.compensatedEntities.getSelf().inVehicle() && !player.packetStateData.lastPacketWasOnePointSeventeenDuplicate) {
                player.lastX = player.x;
                player.lastY = player.y;
                player.lastZ = player.z;

                player.x = clampVector.getX();
                player.y = clampVector.getY();
                player.z = clampVector.getZ();

                player.checkManager.onPositionUpdate(update);
            } else if (update.isTeleport()) { // Mojang doesn't use their own exit vehicle field to leave vehicles, manually call the setback handler
                player.getSetbackTeleportUtil().onPredictionComplete(new PredictionComplete(0, update, true));
            }
        }

        player.packetStateData.didLastLastMovementIncludePosition = player.packetStateData.didLastMovementIncludePosition;
        player.packetStateData.didLastMovementIncludePosition = hasPosition;
    }

    private static void placeLilypad(GrimPlayer player, Player.Hand hand) {
        HitData data = getNearestHitResult(player, null, true);

        if (data != null) {
            // A lilypad cannot replace a fluid
            if (player.compensatedWorld.getFluidLevelAt(data.getPosition().getX(), data.getPosition().getY() + 1, data.getPosition().getZ()) > 0)
                return;

            BlockPlace blockPlace = new BlockPlace(player, hand, data.getPosition(), data.getClosestDirection().ordinal(), data.getClosestDirection(), ModifiableItemStack.EMPTY, data);
            blockPlace.setReplaceClicked(false); // Not possible with use item

            // We checked for a full fluid block below here.
            if (player.compensatedWorld.getWaterFluidLevelAt(data.getPosition().getX(), data.getPosition().getY(), data.getPosition().getZ()) > 0
                    || data.getState().getType() == Block.ICE || data.getState().getType() == Block.FROSTED_ICE) {
                Vector3i pos = data.getPosition();
                pos = pos.add(0, 1, 0);

                blockPlace.set(pos, new MinestomWrappedBlockState(Block.LILY_PAD));

                if (player.gamemode != GameMode.CREATIVE) {
                    player.getInventory().markSlotAsResyncing(blockPlace);
                    if (hand == Player.Hand.MAIN) {
                        player.getInventory().inventory.getHeldItem().setAmount(player.getInventory().inventory.getHeldItem().getAmount() - 1);
                    } else {
                        player.getInventory().getOffHand().setAmount(player.getInventory().getOffHand().getAmount() - 1);
                    }
                }
            }
        }
    }

    private static HitData getNearestHitResult(GrimPlayer player, Block heldItem, boolean sourcesHaveHitbox) {
        Vector3d startingPos = new Vector3d(player.x, player.y + player.getEyeHeight(), player.z);
        MutableVector startingVec = new MutableVector(startingPos.getX(), startingPos.getY(), startingPos.getZ());
        Ray trace = new Ray(player, startingPos.getX(), startingPos.getY(), startingPos.getZ(), player.xRot, player.yRot);
        final double distance = player.compensatedEntities.getSelf().getAttributeValue(Attribute.PLAYER_BLOCK_INTERACTION_RANGE);
        MutableVector endVec = trace.getPointAtDistance(distance);
        Vector3d endPos = new Vector3d(endVec.getX(), endVec.getY(), endVec.getZ());

        return traverseBlocks(player, startingPos, endPos, (block, vector3i) -> {
            CollisionBox data = HitboxData.getBlockHitbox(player, heldItem, player.getClientVersion(), block, vector3i.getX(), vector3i.getY(), vector3i.getZ());
            List<SimpleCollisionBox> boxes = new ArrayList<>();
            data.downCast(boxes);

            double bestHitResult = Double.MAX_VALUE;
            MutableVector bestHitLoc = null;
            BlockFace bestFace = null;

            for (SimpleCollisionBox box : boxes) {
                Pair<MutableVector, BlockFace> intercept = ReachUtils.calculateIntercept(box, trace.getOrigin(), trace.getPointAtDistance(distance));
                if (intercept.getFirst() == null) continue; // No intercept

                MutableVector hitLoc = intercept.getFirst();

                if (hitLoc.distanceSquared(startingVec) < bestHitResult) {
                    bestHitResult = hitLoc.distanceSquared(startingVec);
                    bestHitLoc = hitLoc;
                    bestFace = intercept.getSecond();
                }
            }
            if (bestHitLoc != null) {
                return new HitData(vector3i, bestHitLoc, bestFace, block);
            }

            if (sourcesHaveHitbox &&
                    (player.compensatedWorld.isWaterSourceBlock(vector3i.getX(), vector3i.getY(), vector3i.getZ())
                            || player.compensatedWorld.getLavaFluidLevelAt(vector3i.getX(), vector3i.getY(), vector3i.getZ()) == (8 / 9f))) {
                double waterHeight = player.compensatedWorld.getFluidLevelAt(vector3i.getX(), vector3i.getY(), vector3i.getZ());
                SimpleCollisionBox box = new SimpleCollisionBox(vector3i.getX(), vector3i.getY(), vector3i.getZ(), vector3i.getX() + 1, vector3i.getY() + waterHeight, vector3i.getZ() + 1);

                Pair<MutableVector, BlockFace> intercept = ReachUtils.calculateIntercept(box, trace.getOrigin(), trace.getPointAtDistance(distance));

                if (intercept.getFirst() != null) {
                    return new HitData(vector3i, intercept.getFirst(), intercept.getSecond(), block);
                }
            }

            return null;
        });
    }

    public void onPacketSend(PlayerPacketOutEvent event) {
        if (event.getPlayer().getPlayerConnection().getConnectionState() != ConnectionState.PLAY) return;
        GrimPlayer player = GrimAPI.INSTANCE.getPlayerDataManager().getPlayer(event.getPlayer());
        if (player == null) return;

        player.checkManager.onPacketSend(event);
    }
}

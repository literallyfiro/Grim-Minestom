package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.collisions.datatypes.SimpleCollisionBox;
import ac.grim.grimac.utils.data.packetentity.PacketEntity;
import ac.grim.grimac.utils.data.packetentity.PacketEntityHorse;
import ac.grim.grimac.utils.data.packetentity.PacketEntitySizeable;
import ac.grim.grimac.utils.data.packetentity.PacketEntityTrackXRot;
import ac.grim.grimac.utils.vector.Vector3d;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.animal.AnimalMeta;
import net.minestom.server.entity.metadata.minecart.AbstractMinecartMeta;
import net.minestom.server.entity.metadata.minecart.MinecartMeta;
import net.minestom.server.entity.metadata.other.BoatMeta;

/**
 * Yeah, I know this is a bad class
 * I just can't figure out how to PR it to PacketEvents due to babies, slimes, and other irregularities
 * <p>
 * I could PR a ton of classes in order to accomplish it but then no one would use it
 * (And even if they did they would likely be breaking my license...)
 */
public final class BoundingBoxSize {

    public static float getWidth(GrimPlayer player, PacketEntity packetEntity) {
        // Turtles are the only baby animal that don't follow the * 0.5 rule
        if (packetEntity.getType() == EntityType.TURTLE && packetEntity.isBaby) return 0.36f;
        return getWidthMinusBaby(player, packetEntity) * (packetEntity.isBaby ? 0.5f : 1f);
    }

    private static float getWidthMinusBaby(GrimPlayer player, PacketEntity packetEntity) {
        final EntityType type = packetEntity.getType();
        if (EntityType.AXOLOTL.equals(type)) {
            return 0.75f;
        } else if (EntityType.PANDA.equals(type)) {
            return 1.3f;
        } else if (EntityType.BAT.equals(type) || EntityType.PARROT.equals(type) || EntityType.COD.equals(type) || EntityType.EVOKER_FANGS.equals(type) || EntityType.TROPICAL_FISH.equals(type) || EntityType.FROG.equals(type)) {
            return 0.5f;
        } else if (EntityType.ARMADILLO.equals(type) || EntityType.BEE.equals(type) || EntityType.PUFFERFISH.equals(type) || EntityType.SALMON.equals(type) || EntityType.SNOW_GOLEM.equals(type) || EntityType.CAVE_SPIDER.equals(type)) {
            return 0.7f;
        } else if (EntityType.WITHER_SKELETON.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.7f : 0.72f;
        } else if (EntityType.WITHER_SKULL.equals(type) || EntityType.SHULKER_BULLET.equals(type)) {
            return 0.3125f;
        } else if (EntityType.HOGLIN.equals(type) || EntityType.ZOGLIN.equals(type)) {
            return 1.3964844f;
        } else if (EntityType.SKELETON_HORSE.equals(type) || EntityType.ZOMBIE_HORSE.equals(type) || EntityType.HORSE.equals(type) ||EntityType.DONKEY.equals(type) || EntityType.MULE.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.3964844f : 1.4f;
        } else if (packetEntity.getEntity().getEntityMeta() instanceof BoatMeta) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.375f : 1.5f;
        } else if (EntityType.CHICKEN.equals(type) || EntityType.ENDERMITE.equals(type) || EntityType.SILVERFISH.equals(type) || EntityType.VEX.equals(type) || EntityType.TADPOLE.equals(type)) {
            return 0.4f;
        } else if (EntityType.RABBIT.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.4f : 0.6f;
        } else if (EntityType.STRIDER.equals(type) || EntityType.COW.equals(type) || EntityType.SHEEP.equals(type) || EntityType.MOOSHROOM.equals(type) || EntityType.PIG.equals(type) || EntityType.LLAMA.equals(type) || EntityType.DOLPHIN.equals(type) || EntityType.WITHER.equals(type) || EntityType.TRADER_LLAMA.equals(type) || EntityType.WARDEN.equals(type) || EntityType.GOAT.equals(type)) {
            return 0.9f;
        } else if (EntityType.PHANTOM.equals(type)) {
            if (packetEntity instanceof PacketEntitySizeable) {
                return 0.9f + ((PacketEntitySizeable) packetEntity).size * 0.2f;
            }

            return 1.5f;
        } else if (EntityType.ELDER_GUARDIAN.equals(type)) { // TODO: 2.35 * guardian?
            return 1.9975f;
        } else if (EntityType.END_CRYSTAL.equals(type)) {
            return 2.0f;
        } else if (EntityType.ENDER_DRAGON.equals(type)) {
            return 16.0f;
        } else if (EntityType.FIREBALL.equals(type)) {
            return 1f;
        } else if (EntityType.GHAST.equals(type)) {
            return 4.0f;
        } else if (EntityType.GIANT.equals(type)) {
            return 3.6f;
        } else if (EntityType.GUARDIAN.equals(type)) {
            return 0.85f;
        } else if (EntityType.IRON_GOLEM.equals(type)) {
            return 1.4f;
        } else if (EntityType.MAGMA_CUBE.equals(type)) {
            if (packetEntity instanceof PacketEntitySizeable) {
                float size = ((PacketEntitySizeable) packetEntity).size;
                return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5)
                        ? 0.52f * size : player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)
                        ? 2.04f * (0.255f * size)
                        : 0.51000005f * size;
            }

            return 0.98f;
        } else if (packetEntity.getEntity().getEntityMeta() instanceof AbstractMinecartMeta) {
            return 0.98f;
        } else if (EntityType.PLAYER.equals(type)) {
            return 0.6f;
        } else if (EntityType.POLAR_BEAR.equals(type)) {
            return 1.4f;
        } else if (EntityType.RAVAGER.equals(type)) {
            return 1.95f;
        } else if (EntityType.SHULKER.equals(type)) {
            return 1.0f;
        } else if (EntityType.SLIME.equals(type)) {
            if (packetEntity instanceof PacketEntitySizeable) {
                float size = ((PacketEntitySizeable) packetEntity).size;
                return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5)
                        ? 0.52f * size : player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)
                        ? 2.04f * (0.255f * size) : 0.51000005f * size;
            }

            return 0.3125f;
        } else if (EntityType.SMALL_FIREBALL.equals(type)) {
            return 0.3125f;
        } else if (EntityType.SPIDER.equals(type)) {
            return 1.4f;
        } else if (EntityType.SQUID.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.8f : 0.95f;
        } else if (EntityType.TURTLE.equals(type)) {
            return 1.2f;
        } else if (EntityType.ALLAY.equals(type)) {
            return 0.35f;
        } else if (EntityType.SNIFFER.equals(type)) {
            return 1.9f;
        } else if (EntityType.CAMEL.equals(type)) {
            return 1.7f;
        } else if (EntityType.WIND_CHARGE.equals(type)) {
            return 0.3125F;
        }
        return 0.6f;
    }

    public static Vector3d getRidingOffsetFromVehicle(PacketEntity entity, GrimPlayer player) {
        SimpleCollisionBox box = entity.getPossibleCollisionBoxes();
        double x = (box.maxX + box.minX) / 2.0;
        double y = box.minY;
        double z = (box.maxZ + box.minZ) / 2.0;

        if (entity instanceof PacketEntityTrackXRot) {
            PacketEntityTrackXRot xRotEntity = (PacketEntityTrackXRot) entity;

            // Horses desync here, and we can't do anything about it without interpolating animations.
            // Mojang just has to fix it.  I'm not attempting to fix it.
            // Striders also do the same with animations, causing a desync.
            // At least the only people using buckets are people in boats for villager transportation
            // and people trying to false the anticheat.
            if (entity.getEntity().getEntityMeta() instanceof BoatMeta) {
                float f = 0.0F;
                float f1 = (float) (getPassengerRidingOffset(player, entity) - 0.35f); // hardcoded player offset

                if (!entity.passengers.isEmpty()) {
                    int i = entity.passengers.indexOf(player.compensatedEntities.getSelf());

                    if (i == 0) {
                        f = 0.2F;
                    } else if (i == 1) {
                        f = -0.6F;
                    }
                }

                Vector3d vec3 = (new Vector3d(f, 0.0D, 0.0D));
                vec3 = yRot(-xRotEntity.interpYaw * ((float) Math.PI / 180F) - ((float) Math.PI / 2F), vec3);
                return new Vector3d(x + vec3.x, y + (double) f1, z + vec3.z);
            } else if (entity.getType() == EntityType.LLAMA) {
                float f = player.trigHandler.cos(xRotEntity.interpYaw * ((float) Math.PI / 180F));
                float f1 = player.trigHandler.sin(xRotEntity.interpYaw * ((float) Math.PI / 180F));
                return new Vector3d(x + (double) (0.3F * f1), y + getPassengerRidingOffset(player, entity) - 0.35f, z + (double) (0.3F * f));
            } else if (entity.getType() == EntityType.CHICKEN) {
                float f = player.trigHandler.sin(xRotEntity.interpYaw * ((float) Math.PI / 180F));
                float f1 = player.trigHandler.cos(xRotEntity.interpYaw * ((float) Math.PI / 180F));
                y = y + (getHeight(player, entity) * 0.5f);
                return new Vector3d(x + (double) (0.1F * f), y - 0.35f, z - (double) (0.1F * f1));
            }
        }

        return new Vector3d(x, y + getPassengerRidingOffset(player, entity) - 0.35f, z);
    }

    private static Vector3d yRot(float p_82525_, Vector3d start) {
        float f = (float) Math.cos(p_82525_);
        float f1 = (float) Math.sin(p_82525_);
        double d0 = start.getX() * (double) f + start.getZ() * (double) f1;
        double d1 = start.getY();
        double d2 = start.getZ() * (double) f - start.getX() * (double) f1;
        return new Vector3d(d0, d1, d2);
    }

    public static float getHeight(GrimPlayer player, PacketEntity packetEntity) {
        // Turtles are the only baby animal that don't follow the * 0.5 rule
        if (packetEntity.getType() == EntityType.TURTLE && packetEntity.isBaby) return 0.12f;
        return getHeightMinusBaby(player, packetEntity) * (packetEntity.isBaby ? 0.5f : 1f);
    }

    public static double getMyRidingOffset(PacketEntity packetEntity) {
        final EntityType type = packetEntity.getType();
        if (EntityType.PIGLIN.equals(type) || EntityType.ZOMBIFIED_PIGLIN.equals(type) || EntityType.ZOMBIE.equals(type)) {
            return packetEntity.isBaby ? -0.05 : -0.45;
        } else if (EntityType.SKELETON.equals(type)) {
            return -0.6;
        } else if (EntityType.ENDERMITE.equals(type) || EntityType.SILVERFISH.equals(type)) {
            return 0.1;
        } else if (EntityType.EVOKER.equals(type) || EntityType.ILLUSIONER.equals(type) || EntityType.PILLAGER.equals(type) || EntityType.RAVAGER.equals(type) || EntityType.VINDICATOR.equals(type) || EntityType.WITCH.equals(type)) {
            return -0.45;
        } else if (EntityType.PLAYER.equals(type)) {
            return -0.35;
        }

        if (packetEntity.getEntity().getEntityMeta() instanceof AnimalMeta) {
            return 0.14;
        }

        return 0;
    }

    public static double getPassengerRidingOffset(GrimPlayer player, PacketEntity packetEntity) {
        if (packetEntity instanceof PacketEntityHorse)
            return (getHeight(player, packetEntity) * 0.75) - 0.25;

        final EntityType type = packetEntity.getType();
        if (packetEntity.getEntity().getEntityMeta() instanceof AbstractMinecartMeta) {
            return 0;
        } else if (packetEntity.getEntity().getEntityMeta() instanceof BoatMeta) {
            return -0.1;
        } else if (EntityType.HOGLIN.equals(type) || EntityType.ZOGLIN.equals(type)) {
            return getHeight(player, packetEntity) - (packetEntity.isBaby ? 0.2 : 0.15);
        } else if (EntityType.LLAMA.equals(type)) {
            return getHeight(player, packetEntity) * 0.67;
        } else if (EntityType.PIGLIN.equals(type)) {
            return getHeight(player, packetEntity) * 0.92;
        } else if (EntityType.RAVAGER.equals(type)) {
            return 2.1;
        } else if (EntityType.SKELETON.equals(type)) {
            return (getHeight(player, packetEntity) * 0.75) - 0.1875;
        } else if (EntityType.SPIDER.equals(type)) {
            return getHeight(player, packetEntity) * 0.5;
        } else if (EntityType.STRIDER.equals(type)) {// depends on animation position, good luck getting it exactly, this is the best you can do though
            return getHeight(player, packetEntity) - 0.19;
        }
        return getHeight(player, packetEntity) * 0.75;
    }
    private static float getHeightMinusBaby(GrimPlayer player, PacketEntity packetEntity) {
        final EntityType type = packetEntity.getType();
        if (EntityType.ARMADILLO.equals(type)) {
            return 0.65f;
        } else if (EntityType.AXOLOTL.equals(type)) {
            return 0.42f;
        } else if (EntityType.BEE.equals(type) || EntityType.DOLPHIN.equals(type) || EntityType.ALLAY.equals(type)) {
            return 0.6f;
        } else if (EntityType.EVOKER_FANGS.equals(type) || EntityType.VEX.equals(type)) {
            return 0.8f;
        } else if (EntityType.SQUID.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.8f : 0.95f;
        } else if (EntityType.PARROT.equals(type) || EntityType.BAT.equals(type) || EntityType.PIG.equals(type) || EntityType.SPIDER.equals(type)) {
            return 0.9f;
        } else if (EntityType.WITHER_SKULL.equals(type) || EntityType.SHULKER_BULLET.equals(type)) {
            return 0.3125f;
        } else if (EntityType.BLAZE.equals(type)) {
            return 1.8f;
        } else if (packetEntity.getEntity().getEntityMeta() instanceof BoatMeta) {
            // WHY DOES VIAVERSION OFFSET BOATS? THIS MAKES IT HARD TO SUPPORT, EVEN IF WE INTERPOLATE RIGHT.
            // I gave up and just exempted boats from the reach check and gave up with interpolation for collisions
            return 0.5625f;
        } else if (EntityType.CAT.equals(type)) {
            return 0.7f;
        } else if (EntityType.CAVE_SPIDER.equals(type)) {
            return 0.5f;
        } else if (EntityType.FROG.equals(type)) {
            return 0.55f;
        } else if (EntityType.CHICKEN.equals(type)) {
            return 0.7f;
        } else if (EntityType.HOGLIN.equals(type) || EntityType.ZOGLIN.equals(type)) {
            return 1.4f;
        } else if (EntityType.COW.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.4f : 1.3f;
        } else if (EntityType.STRIDER.equals(type)) {
            return 1.7f;
        } else if (EntityType.CREEPER.equals(type)) {
            return 1.7f;
        } else if (EntityType.DONKEY.equals(type)) {
            return 1.5f;
        } else if (EntityType.ELDER_GUARDIAN.equals(type)) {
            return 1.9975f;
        } else if (EntityType.ENDERMAN.equals(type) || EntityType.WARDEN.equals(type)) {
            return 2.9f;
        } else if (EntityType.ENDERMITE.equals(type) || EntityType.COD.equals(type)) {
            return 0.3f;
        } else if (EntityType.END_CRYSTAL.equals(type)) {
            return 2.0f;
        } else if (EntityType.ENDER_DRAGON.equals(type)) {
            return 8.0f;
        } else if (EntityType.FIREBALL.equals(type)) {
            return 1f;
        } else if (EntityType.FOX.equals(type)) {
            return 0.7f;
        } else if (EntityType.GHAST.equals(type)) {
            return 4.0f;
        } else if (EntityType.GIANT.equals(type)) {
            return 12.0f;
        } else if (EntityType.GUARDIAN.equals(type)) {
            return 0.85f;
        } else if (EntityType.HORSE.equals(type)) {
            return 1.6f;
        } else if (EntityType.IRON_GOLEM.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 2.7f : 2.9f;
        } else if (EntityType.LLAMA.equals(type) || EntityType.TRADER_LLAMA.equals(type)) {
            return 1.87f;
        } else if (EntityType.TROPICAL_FISH.equals(type)) {
            return 0.4f;
        } else if (EntityType.MAGMA_CUBE.equals(type)) {
            if (packetEntity instanceof PacketEntitySizeable) {
                float size = ((PacketEntitySizeable) packetEntity).size;
                return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5)
                        ? 0.52f * size : player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)
                        ? 2.04f * (0.255f * size)
                        : 0.51000005f * size;
            }

            return 0.7f;
        } else if (packetEntity.getEntity().getEntityMeta() instanceof AbstractMinecartMeta) {
            return 0.7f;
        } else if (EntityType.MULE.equals(type)) {
            return 1.6f;
        } else if (EntityType.MOOSHROOM.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.4f : 1.3f;
        } else if (EntityType.OCELOT.equals(type)) {
            return 0.7f;
        } else if (EntityType.PANDA.equals(type)) {
            return 1.25f;
        } else if (EntityType.PHANTOM.equals(type)) {
            if (packetEntity instanceof PacketEntitySizeable) {
                return 0.5f + ((PacketEntitySizeable) packetEntity).size * 0.1f;
            }

            return 1.8f;
        } else if (EntityType.PLAYER.equals(type)) {
            return 1.8f;
        } else if (EntityType.POLAR_BEAR.equals(type)) {
            return 1.4f;
        } else if (EntityType.PUFFERFISH.equals(type)) {
            return 0.7f;
        } else if (EntityType.RABBIT.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.5f : 0.7f;
        } else if (EntityType.RAVAGER.equals(type)) {
            return 2.2f;
        } else if (EntityType.SALMON.equals(type)) {
            return 0.4f;
        } else if (EntityType.SHEEP.equals(type) || EntityType.GOAT.equals(type)) {
            return 1.3f;
        } else if (EntityType.SHULKER.equals(type)) { // Could maybe guess peek size, although seems useless
            return 2.0f;
        } else if (EntityType.SILVERFISH.equals(type)) {
            return 0.3f;
        } else if (EntityType.SKELETON.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 1.99f : 1.95f;
        } else if (EntityType.SKELETON_HORSE.equals(type)) {
            return 1.6f;
        } else if (EntityType.SLIME.equals(type)) {
            if (packetEntity instanceof PacketEntitySizeable) {
                float size = ((PacketEntitySizeable) packetEntity).size;
                return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_20_5)
                        ? 0.52f * size : player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)
                        ? 2.04f * (0.255f * size)
                        : 0.51000005f * size;
            }

            return 0.3125f;
        } else if (EntityType.SMALL_FIREBALL.equals(type)) {
            return 0.3125f;
        } else if (EntityType.SNOW_GOLEM.equals(type)) {
            return 1.9f;
        } else if (EntityType.STRAY.equals(type)) {
            return 1.99f;
        } else if (EntityType.TURTLE.equals(type)) {
            return 0.4f;
        } else if (EntityType.WITHER.equals(type)) {
            return 3.5f;
        } else if (EntityType.WITHER_SKELETON.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 2.4f : 2.535f;
        } else if (EntityType.WOLF.equals(type)) {
            return player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? 0.85f : 0.8f;
        } else if (EntityType.ZOMBIE_HORSE.equals(type)) {
            return 1.6f;
        } else if (EntityType.TADPOLE.equals(type)) {
            return 0.3f;
        } else if (EntityType.SNIFFER.equals(type)) {
            return 1.75f;
        } else if (EntityType.CAMEL.equals(type)) {
            return 2.375f;
        } else if (EntityType.BREEZE.equals(type)) {
            return 1.77F;
        } else if (EntityType.BOGGED.equals(type)) {
            return 1.99F;
        } else if (EntityType.WIND_CHARGE.equals(type)) {
            return 0.3125F;
        }
        return 1.95f;
    }
}

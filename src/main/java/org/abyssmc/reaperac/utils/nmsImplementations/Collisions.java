package org.abyssmc.reaperac.utils.nmsImplementations;

import net.minecraft.server.v1_16_R3.*;
import org.abyssmc.reaperac.GrimPlayer;
import org.abyssmc.reaperac.utils.enums.MoverType;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.stream.Stream;

public class Collisions {
    public static final double maxUpStep = 0.6f;

    // Entity line 686
    public static Vector collide(Vector vector, GrimPlayer grimPlayer) {
        Vec3D vec3 = new Vec3D(vector.getX(), vector.getY(), vector.getZ());

        AxisAlignedBB aABB = grimPlayer.entityPlayer.getBoundingBox();
        VoxelShapeCollision collisionContext = VoxelShapeCollision.a(grimPlayer.entityPlayer);
        VoxelShape voxelShape = grimPlayer.entityPlayer.getWorld().getWorldBorder().c(); // Technically this should be lag compensated...
        Stream<VoxelShape> stream = VoxelShapes.c(voxelShape, VoxelShapes.a(aABB.shrink(1.0E-7)), OperatorBoolean.AND) ? Stream.empty() : Stream.of(voxelShape);
        Stream<VoxelShape> stream2 = grimPlayer.entityPlayer.getWorld().c(grimPlayer.entityPlayer, aABB.b(vec3), entity -> true);
        StreamAccumulator<VoxelShape> rewindableStream = new StreamAccumulator<>(Stream.concat(stream2, stream));

        Vec3D vec32 = vec3.g() == 0.0 ? vec3 : collideBoundingBoxHeuristically(grimPlayer.entityPlayer, vec3, aABB, grimPlayer.entityPlayer.getWorld(), collisionContext, rewindableStream);
        boolean bl2 = vec3.x != vec32.x;
        boolean bl3 = vec3.y != vec32.y;
        boolean bl4 = vec3.z != vec32.z;
        boolean bl = grimPlayer.lastOnGround || bl3 && vec3.y < 0.0;
        if (bl && (bl2 || bl4)) {
            Vec3D vec33;
            Vec3D vec34 = collideBoundingBoxHeuristically(grimPlayer.entityPlayer, new Vec3D(vec3.x, maxUpStep, vec3.z), aABB, grimPlayer.entityPlayer.getWorld(), collisionContext, rewindableStream);
            Vec3D vec35 = collideBoundingBoxHeuristically(grimPlayer.entityPlayer, new Vec3D(0.0, maxUpStep, 0.0), aABB.b(vec3.x, 0.0, vec3.z), grimPlayer.entityPlayer.getWorld(), collisionContext, rewindableStream);
            if (vec35.y < maxUpStep && Entity.c(vec33 = collideBoundingBoxHeuristically(grimPlayer.entityPlayer, new Vec3D(vec3.x, 0.0, vec3.z), AxisAlignedBB.a(vec35), grimPlayer.entityPlayer.getWorld(), collisionContext, rewindableStream).e(vec35)) > Entity.c(vec34)) {
                vec34 = vec33;
            }
            if (Entity.c(vec34) > Entity.c(vec32)) {
                Vec3D allowedMovement = collideBoundingBoxHeuristically(grimPlayer.entityPlayer, new Vec3D(0.0, -vec34.y + vec3.y, 0.0), aABB.c(vec34), grimPlayer.entityPlayer.getWorld(), collisionContext, rewindableStream);
                vec34 = vec34.e(allowedMovement);
                return new Vector(vec34.x, vec34.y, vec34.z);
            }
        }
        return new Vector(vec32.x, vec32.y, vec32.z);
    }

    public static Vec3D collideBoundingBoxHeuristically(@Nullable Entity entity, Vec3D vec3d, AxisAlignedBB axisalignedbb, World world, VoxelShapeCollision voxelshapecollision, StreamAccumulator<VoxelShape> streamaccumulator) {
        boolean flag = vec3d.x == 0.0D;
        boolean flag1 = vec3d.y == 0.0D;
        boolean flag2 = vec3d.z == 0.0D;
        if (flag && flag1 || flag && flag2 || flag1 && flag2) {
            return collideBoundingBox(vec3d, axisalignedbb, world, voxelshapecollision, streamaccumulator);
        } else {
            StreamAccumulator<VoxelShape> streamaccumulator1 = new StreamAccumulator(Stream.concat(streamaccumulator.a(), world.b(entity, axisalignedbb.b(vec3d))));
            return collideBoundingBoxLegacy(vec3d, axisalignedbb, streamaccumulator1);
        }
    }

    public static Vec3D collideBoundingBox(Vec3D vec3d, AxisAlignedBB axisalignedbb, IWorldReader iworldreader, VoxelShapeCollision voxelshapecollision, StreamAccumulator<VoxelShape> streamaccumulator) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;
        if (d1 != 0.0D) {
            d1 = a(EnumDirection.EnumAxis.Y, axisalignedbb, iworldreader, d1, voxelshapecollision, streamaccumulator.a());
            if (d1 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);

        // TODO: VoxelShapes.a needs to be lag compensated
        if (flag && d2 != 0.0D) {
            d2 = a(EnumDirection.EnumAxis.Z, axisalignedbb, iworldreader, d2, voxelshapecollision, streamaccumulator.a());
            if (d2 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D) {
            d0 = a(EnumDirection.EnumAxis.X, axisalignedbb, iworldreader, d0, voxelshapecollision, streamaccumulator.a());
            if (!flag && d0 != 0.0D) {
                axisalignedbb = axisalignedbb.d(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D) {
            d2 = a(EnumDirection.EnumAxis.Z, axisalignedbb, iworldreader, d2, voxelshapecollision, streamaccumulator.a());
        }

        return new Vec3D(d0, d1, d2);
    }

    public static double a(EnumDirection.EnumAxis var0, AxisAlignedBB var1, IWorldReader var2, double var3, VoxelShapeCollision var5, Stream<VoxelShape> var6) {
        return a(var1, var2, var3, var5, EnumAxisCycle.a(var0, EnumDirection.EnumAxis.Z), var6);
    }

    private static double a(AxisAlignedBB var0, IWorldReader var1, double var2, VoxelShapeCollision var4, EnumAxisCycle var5, Stream<VoxelShape> var6) {
        if (!(var0.b() < 1.0E-6D) && !(var0.c() < 1.0E-6D) && !(var0.d() < 1.0E-6D)) {
            if (Math.abs(var2) < 1.0E-7D) {
                return 0.0D;
            } else {
                EnumAxisCycle var7 = var5.a();
                EnumDirection.EnumAxis var8 = var7.a(EnumDirection.EnumAxis.X);
                EnumDirection.EnumAxis var9 = var7.a(EnumDirection.EnumAxis.Y);
                EnumDirection.EnumAxis var10 = var7.a(EnumDirection.EnumAxis.Z);
                BlockPosition.MutableBlockPosition var11 = new BlockPosition.MutableBlockPosition();
                int var12 = MathHelper.floor(var0.a(var8) - 1.0E-7D) - 1;
                int var13 = MathHelper.floor(var0.b(var8) + 1.0E-7D) + 1;
                int var14 = MathHelper.floor(var0.a(var9) - 1.0E-7D) - 1;
                int var15 = MathHelper.floor(var0.b(var9) + 1.0E-7D) + 1;
                double var16 = var0.a(var10) - 1.0E-7D;
                double var18 = var0.b(var10) + 1.0E-7D;
                boolean var20 = var2 > 0.0D;
                int var21 = var20 ? MathHelper.floor(var0.b(var10) - 1.0E-7D) - 1 : MathHelper.floor(var0.a(var10) + 1.0E-7D) + 1;
                int var22 = a(var2, var16, var18);
                int var23 = var20 ? 1 : -1;
                int var24 = var21;

                while (true) {
                    if (var20) {
                        if (var24 > var22) {
                            break;
                        }
                    } else if (var24 < var22) {
                        break;
                    }

                    for (int var25 = var12; var25 <= var13; ++var25) {
                        for (int var26 = var14; var26 <= var15; ++var26) {
                            int var27 = 0;
                            if (var25 == var12 || var25 == var13) {
                                ++var27;
                            }

                            if (var26 == var14 || var26 == var15) {
                                ++var27;
                            }

                            if (var24 == var21 || var24 == var22) {
                                ++var27;
                            }

                            if (var27 < 3) {
                                var11.a(var7, var25, var26, var24);
                                IBlockData var28 = var1.getType(var11);
                                if ((var27 != 1 || var28.d()) && (var27 != 2 || var28.a(Blocks.MOVING_PISTON))) {
                                    var2 = var28.b(var1, var11, var4).a(var10, var0.d(-var11.getX(), -var11.getY(), -var11.getZ()), var2);
                                    if (Math.abs(var2) < 1.0E-7D) {
                                        return 0.0D;
                                    }

                                    var22 = a(var2, var16, var18);
                                }
                            }
                        }
                    }

                    var24 += var23;
                }

                double[] var24array = new double[]{var2};
                var6.forEach((var3) -> var24array[0] = var3.a(var10, var0, var24array[0]));
                return var24array[0];
            }
        } else {
            return var2;
        }
    }

    private static int a(double var0, double var2, double var4) {
        return var0 > 0.0D ? MathHelper.floor(var4 + var0) + 1 : MathHelper.floor(var2 + var0) - 1;
    }

    public static Vec3D collideBoundingBoxLegacy(Vec3D vec3d, AxisAlignedBB axisalignedbb, StreamAccumulator<VoxelShape> streamaccumulator) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;
        if (d1 != 0.0D) {
            d1 = a(EnumDirection.EnumAxis.Y, axisalignedbb, streamaccumulator.a(), d1);
            if (d1 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, d1, 0.0D);
            }
        }

        boolean flag = Math.abs(d0) < Math.abs(d2);
        if (flag && d2 != 0.0D) {
            d2 = a(EnumDirection.EnumAxis.Z, axisalignedbb, streamaccumulator.a(), d2);
            if (d2 != 0.0D) {
                axisalignedbb = axisalignedbb.d(0.0D, 0.0D, d2);
            }
        }

        if (d0 != 0.0D) {
            d0 = a(EnumDirection.EnumAxis.X, axisalignedbb, streamaccumulator.a(), d0);
            if (!flag && d0 != 0.0D) {
                axisalignedbb = axisalignedbb.d(d0, 0.0D, 0.0D);
            }
        }

        if (!flag && d2 != 0.0D) {
            d2 = a(EnumDirection.EnumAxis.Z, axisalignedbb, streamaccumulator.a(), d2);
        }

        return new Vec3D(d0, d1, d2);
    }

    public static double a(EnumDirection.EnumAxis var0, AxisAlignedBB var1, Stream<VoxelShape> var2, double var3) {
        for (Iterator var5 = var2.iterator(); var5.hasNext(); var3 = ((VoxelShape) var5.next()).a(var0, var1, var3)) {
            if (Math.abs(var3) < 1.0E-7D) {
                return 0.0D;
            }
        }

        return var3;
    }

    // MCP mappings PlayerEntity 959
    // Mojang mappings 911
    public static Vector maybeBackOffFromEdge(Vector vec3, MoverType moverType, GrimPlayer grimPlayer) {
        Player bukkitPlayer = grimPlayer.bukkitPlayer;

        if (!bukkitPlayer.isFlying() && (moverType == MoverType.SELF || moverType == MoverType.PLAYER) && bukkitPlayer.isSneaking() && isAboveGround(grimPlayer)) {
            double d = vec3.getX();
            double d2 = vec3.getZ();
            while (d != 0.0 && ((CraftWorld) bukkitPlayer.getWorld()).getHandle().getCubes(((CraftPlayer) bukkitPlayer).getHandle(),
                    ((CraftPlayer) bukkitPlayer).getHandle().getBoundingBox().d(d, -maxUpStep, 0.0))) {
                if (d < 0.05 && d >= -0.05) {
                    d = 0.0;
                    continue;
                }
                if (d > 0.0) {
                    d -= 0.05;
                    continue;
                }
                d += 0.05;
            }
            while (d2 != 0.0 && ((CraftWorld) bukkitPlayer.getWorld()).getHandle().getCubes(((CraftPlayer) bukkitPlayer).getHandle(),
                    ((CraftPlayer) bukkitPlayer).getHandle().getBoundingBox().d(0.0, -maxUpStep, d2))) {
                if (d2 < 0.05 && d2 >= -0.05) {
                    d2 = 0.0;
                    continue;
                }
                if (d2 > 0.0) {
                    d2 -= 0.05;
                    continue;
                }
                d2 += 0.05;
            }
            while (d != 0.0 && d2 != 0.0 && ((CraftWorld) bukkitPlayer.getWorld()).getHandle().getCubes(((CraftPlayer) bukkitPlayer).getHandle(),
                    ((CraftPlayer) bukkitPlayer).getHandle().getBoundingBox().d(d, -maxUpStep, d2))) {
                d = d < 0.05 && d >= -0.05 ? 0.0 : (d > 0.0 ? (d -= 0.05) : (d += 0.05));
                if (d2 < 0.05 && d2 >= -0.05) {
                    d2 = 0.0;
                    continue;
                }
                if (d2 > 0.0) {
                    d2 -= 0.05;
                    continue;
                }
                d2 += 0.05;
            }
            vec3 = new Vector(d, vec3.getY(), d2);
        }
        return vec3;
    }

    private static boolean isAboveGround(GrimPlayer grimPlayer) {
        Player bukkitPlayer = grimPlayer.bukkitPlayer;

        return grimPlayer.lastOnGround || bukkitPlayer.getFallDistance() < Collisions.maxUpStep && !
                ((CraftWorld) bukkitPlayer.getWorld()).getHandle().getCubes(((CraftPlayer) bukkitPlayer).getHandle(), ((CraftPlayer) bukkitPlayer).getHandle().getBoundingBox().d(0.0, bukkitPlayer.getFallDistance() - Collisions.maxUpStep, 0.0));
    }
}

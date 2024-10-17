/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2022 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ac.grim.grimac.utils.vector;

import lombok.Getter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 3D int Vector.
 * This vector can represent coordinates, angles, or anything you want.
 * You can use this to represent an array if you really want.
 * PacketEvents usually uses this for block positions as they don't need any decimals.
 *
 * @author retrooper
 * @since 1.7
 */
@Getter
public class Vector3i {
    /**
     * X (coordinate/angle/whatever you wish)
     */
    public final int x;
    /**
     * Y (coordinate/angle/whatever you wish)
     */
    public final int y;
    /**
     * Z (coordinate/angle/whatever you wish)
     */
    public final int z;

    /**
     * Default constructor setting all coordinates/angles/values to their default values (=0).
     */
    public Vector3i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector3i(Point pos) {
        this.x = (int) pos.x();
        this.y = (int) pos.y();
        this.z = (int) pos.z();
    }

    public Vector3i(long val) {
        int x = (int) (val >> 38);
        int y;
        int z;
        // 1.14 method for this is storing X Z Y
        // 1.17 added support for negative values
        y = (int) (val << 52 >> 52);
        z = (int) (val << 26 >> 38);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructor allowing you to set the values.
     *
     * @param x X
     * @param y Y
     * @param z Z
     */
    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructor allowing you to specify an array.
     * X will be set to the first index of an array(if it exists, otherwise 0).
     * Y will be set to the second index of an array(if it exists, otherwise 0).
     * Z will be set to the third index of an array(if it exists, otherwise 0).
     *
     * @param array Array.
     */
    public Vector3i(int[] array) {
        if (array.length > 0) {
            x = array[0];
        } else {
            x = 0;
            y = 0;
            z = 0;
            return;
        }
        if (array.length > 1) {
            y = array[1];
        } else {
            y = 0;
            z = 0;
            return;
        }
        if (array.length > 2) {
            z = array[2];
        } else {
            z = 0;
        }
    }

    public long getSerializedPosition() {
        long x = getX() & 0x3FFFFFF;
        long y = getY() & 0xFFF;
        long z = getZ() & 0x3FFFFFF;
        return x << 38 | z << 12 | y;
    }

    /**
     * Is the object we are comparing to equal to us?
     * It must be of type Vector3d or Vector3i and all values must be equal to the values in this class.
     *
     * @param obj Compared object.
     * @return Are they equal?
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector3i) {
            Vector3i vec = (Vector3i) obj;
            return x == vec.x && y == vec.y && z == vec.z;
        } else if (obj instanceof Vector3d) {
            Vector3d vec = (Vector3d) obj;
            return x == vec.x && y == vec.y && z == vec.z;
        } else if (obj instanceof Vector3f) {
            Vector3f vec = (Vector3f) obj;
            return x == vec.x && y == vec.y && z == vec.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public Vector3d toVector3d() {
        return new Vector3d(x, y, z);
    }

    public Vector3i add(int x, int y, int z) {
        return new Vector3i(this.x + x, this.y + y, this.z + z);
    }

    public Vector3i add(Vector3i other) {
        return add(other.x, other.y, other.z);
    }

    public Vector3i offset(BlockFace face) {
        final var direction = face.toDirection();
        return add(direction.normalX(), direction.normalY(), direction.normalZ());
    }

    public Vector3i subtract(int x, int y, int z) {
        return new Vector3i(this.x - x, this.y - y, this.z - z);
    }

    public Vector3i subtract(Vector3i other) {
        return subtract(other.x, other.y, other.z);
    }

    public Vector3i multiply(int x, int y, int z) {
        return new Vector3i(this.x * x, this.y * y, this.z * z);
    }

    public Vector3i multiply(Vector3i other) {
        return multiply(other.x, other.y, other.z);
    }

    public Vector3i multiply(int value) {
        return multiply(value, value, value);
    }

    public Vector3i crossProduct(Vector3i other) {
        int newX = this.y * other.z - other.y * this.z;
        int newY = this.z * other.x - other.z * this.x;
        int newZ = this.x * other.y - other.x * this.y;
        return new Vector3i(newX, newY, newZ);
    }

    public int dot(Vector3i other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3i clone() {
        return new Vector3i(x, y, z);
    }

    public Vector3i with(Integer x, Integer y, Integer z) {
        return new Vector3i(x == null ? this.x : x, y == null ? this.y : y, z == null ? this.z : z);
    }

    public Vector3i withX(int x) {
        return new Vector3i(x, this.y, this.z);
    }

    public Vector3i withY(int y) {
        return new Vector3i(this.x, y, this.z);
    }

    public Vector3i withZ(int z) {
        return new Vector3i(this.x, this.y, z);
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }

    public static Vector3i zero() {
        return new Vector3i();
    }

    public @NotNull Point asPos() {
        return new Pos(x, y, z);
    }
}

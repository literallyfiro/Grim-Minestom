//package ac.grim.grimac.utils.vector;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;
//import net.minestom.server.coordinate.Vec;
//
//@Setter
//@Getter
//@AllArgsConstructor
//public class Vector3d {
//
//    private Vec vector;
//
//    public Vector3d(double x, double y, double z) {
//        this.vector = new Vec(x, y, z);
//    }
//
//    public Vector3d() {
//        this.vector = new Vec(0, 0, 0);
//    }
//
//    public Vector3d add(double x, double y, double z) {
//        this.vector = this.vector.add(x, y, z);
//        return this;
//    }
//
//    public Vector3d add(Vec vector) {
//        this.vector = this.vector.add(vector);
//        return this;
//    }
//
//    public Vector3d add(Vector3d vector) {
//        this.vector = this.vector.add(vector.vector);
//        return this;
//    }
//
//    public Vector3d subtract(double x, double y, double z) {
//        this.vector = this.vector.sub(x, y, z);
//        return this;
//    }
//
//    public Vector3d subtract(Vector3d vector) {
//        this.vector = this.vector.sub(vector.vector);
//        return this;
//    }
//
//    public Vector3d multiply(Vector3d other) {
//        this.vector = this.vector.mul(other.vector);
//        return this;
//    }
//
//    public Vector3d multiply(double x, double y, double z) {
//        this.vector = this.vector.mul(x, y, z);
//        return this;
//    }
//
//    public Vector3d multiply(int m) {
//        this.vector = this.vector.mul(m);
//        return this;
//    }
//
//    public Vector3d multiply(double m) {
//        this.vector = this.vector.mul(m);
//        return this;
//    }
//
//    public Vector3d divide(double x, double y, double z) {
//        this.vector = this.vector.div(x, y, z);
//        return this;
//    }
//
//    public Vector3d divide(int m) {
//        this.vector = this.vector.div(m);
//        return this;
//    }
//
//    public Vector3d crossProduct(Vector3d vector) {
//        this.vector = this.vector.cross(vector.vector);
//        return this;
//    }
//
//    public Vector3d normalize() {
//        this.vector = this.vector.normalize();
//        return this;
//    }
//
//    public Vector3d withX(double x) {
//        this.vector = new Vec(x, this.vector.y(), this.vector.z());
//        return this;
//    }
//
//    public Vector3d withY(double y) {
//        this.vector = new Vec(this.vector.x(), y, this.vector.z());
//        return this;
//    }
//
//    public Vector3d withZ(double z) {
//        this.vector = new Vec(this.vector.x(), this.vector.y(), z);
//        return this;
//    }
//
//    public double length() {
//        return this.vector.length();
//    }
//
//    public double distance(Vector3d vector) {
//        return this.vector.distance(vector.vector);
//    }
//
//    public double distanceSquared(Vector3d vector) {
//        return this.vector.distanceSquared(vector.vector);
//    }
//
//    public double dot(Vector3d other) {
//        return this.getX() * other.getX() + this.getY() * other.getY() + this.getZ() * other.getZ();
//    }
//
//    public double getX() {
//        return this.vector.x();
//    }
//
//    public double getY() {
//        return this.vector.y();
//    }
//
//    public double getZ() {
//        return this.vector.z();
//    }
//
//    public Vector3d setX(double x) {
//        this.vector = new Vec(x, this.vector.y(), this.vector.z());
//        return this;
//    }
//
//    public Vector3d setY(double y) {
//        this.vector = new Vec(this.vector.x(), y, this.vector.z());
//        return this;
//    }
//
//    public Vector3d setZ(double z) {
//        this.vector = new Vec(this.vector.x(), this.vector.y(), z);
//        return this;
//    }
//
//    public double lengthSquared() {
//        return this.vector.lengthSquared();
//    }
//
//    public Vector3d clone() {
//        return new Vector3d(this.vector.x(), this.vector.y(), this.vector.z());
//    }
//
//}
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
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.BlockFace;

import java.util.Objects;

/**
 * 3D double Vector.
 * This vector can represent coordinates, angles, or anything you want.
 * You can use this to represent an array if you really want.
 *
 * @author retrooper
 * @since 1.8
 */
@Getter
public class Vector3d {
    /**
     * X (coordinate/angle/whatever you wish)
     */
    public final double x;
    /**
     * Y (coordinate/angle/whatever you wish)
     */
    public final double y;
    /**
     * Z (coordinate/angle/whatever you wish)
     */
    public final double z;

    /**
     * Default constructor setting all coordinates/angles/values to their default values (=0).
     */
    public Vector3d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public Vector3d(Pos pos) {
        this.x = pos.x();
        this.y = pos.y();
        this.z = pos.z();
    }

    /**
     * Constructor allowing you to set the values.
     *
     * @param x X
     * @param y Y
     * @param z Z
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3d(Vec vec) {
        this.x = vec.x();
        this.y = vec.y();
        this.z = vec.z();
    }

    /**
     * Constructor allowing you to specify an array.
     * X will be set to the first index of an array(if it exists, otherwise 0).
     * Y will be set to the second index of an array(if it exists, otherwise 0).
     * Z will be set to the third index of an array(if it exists, otherwise 0).
     *
     * @param array Array.
     */
    public Vector3d(double[] array) {
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

    /**
     * Is the object we are comparing to equal to us?
     * It must be of type Vector3d or Vector3i and all values must be equal to the values in this class.
     *
     * @param obj Compared object.
     * @return Are they equal?
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector3d) {
            Vector3d vec = (Vector3d) obj;
            return x == vec.x && y == vec.y && z == vec.z;
        } else if (obj instanceof Vector3f) {
            Vector3f vec = (Vector3f) obj;
            return x == vec.x && y == vec.y && z == vec.z;
        } else if (obj instanceof Vector3i) {
            Vector3i vec = (Vector3i) obj;
            return x == (double) vec.x && y == (double) vec.y && z == (double) vec.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public Vector3d add(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d add(Vector3d other) {
        return add(other.x, other.y, other.z);
    }

    public Vector3d offset(BlockFace face) {
        return add(face.toDirection().normalX(), face.toDirection().normalY(), face.toDirection().normalZ());
    }

    public Vector3d subtract(double x, double y, double z) {
        return new Vector3d(this.x - x, this.y - y, this.z - z);
    }

    public Vector3d subtract(Vector3d other) {
        return subtract(other.x, other.y, other.z);
    }

    public Vector3d multiply(double x, double y, double z) {
        return new Vector3d(this.x * x, this.y * y, this.z * z);
    }

    public Vector3d multiply(Vector3d other) {
        return multiply(other.x, other.y, other.z);
    }

    public Vector3d multiply(double value) {
        return multiply(value, value, value);
    }

    public Vector3d setX(double x) {
        return new Vector3d(x, y, z);
    }

    public Vector3d setZ(double z) {
        return new Vector3d(x, y, z);
    }

    public Vector3d setY(double y) {
        return new Vector3d(x, y, z);
    }

    public Vector3d crossProduct(Vector3d other) {
        double newX = this.y * other.z - other.y * this.z;
        double newY = this.z * other.x - other.z * this.x;
        double newZ = this.x * other.y - other.x * this.y;
        return new Vector3d(newX, newY, newZ);
    }

    public double dot(Vector3d other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3d with(Double x, Double y, Double z) {
        return new Vector3d(x == null ? this.x : x, y == null ? this.y : y, z == null ? this.z : z);
    }

    public Vector3d withX(double x) {
        return new Vector3d(x, this.y, this.z);
    }

    public Vector3d withY(double y) {
        return new Vector3d(this.x, y, this.z);
    }

    public Vector3d withZ(double z) {
        return new Vector3d(this.x, this.y, z);
    }

    public double distance(Vector3d other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return (x * x) + (y * y) + (z * z);
    }

    public Vector3d normalize() {
        double length = length();
        return new Vector3d(x / length, y / length, z / length);
    }

    public Vector3d clone() {
        return new Vector3d(x, y, z);
    }

    public double distanceSquared(Vector3d other) {
        double distX = (x - other.x) * (x - other.x);
        double distY = (y - other.y) * (y - other.y);
        double distZ = (z - other.z) * (z - other.z);
        return distX + distY + distZ;
    }

    public Vector3i toVector3i() {
        return new Vector3i((int) x, (int) y, (int) z);
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y + ", Z: " + z;
    }

    public static Vector3d zero() {
        return new Vector3d();
    }
}

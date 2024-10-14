package ac.grim.grimac.utils.vector;

import com.google.common.primitives.Doubles;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a mutable vector. Because the components of Vectors are mutable,
 * storing Vectors long term may be dangerous if passing code modifies the
 * Vector later. If you want to keep around a Vector, it may be wise to call
 * <code>clone()</code> in order to get a copy.
 * <p>
 * from bukkit
 */
public class MutableVector implements Cloneable {

    protected double x;
    protected double y;
    protected double z;

    public MutableVector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public MutableVector(Vec vec) {
        this.x = vec.x();
        this.y = vec.y();
        this.z = vec.z();
    }

    /**
     * Construct the vector with provided integer components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public MutableVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct the vector with provided double components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public MutableVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct the vector with provided float components.
     *
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public MutableVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Adds a vector to this one
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public MutableVector add(@NotNull MutableVector vec) {
        x += vec.x;
        y += vec.y;
        z += vec.z;
        return this;
    }

    /**
     * Subtracts a vector from this one.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public MutableVector subtract(@NotNull MutableVector vec) {
        x -= vec.x;
        y -= vec.y;
        z -= vec.z;
        return this;
    }

    /**
     * Multiplies the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public MutableVector multiply(@NotNull MutableVector vec) {
        x *= vec.x;
        y *= vec.y;
        z *= vec.z;
        return this;
    }

    /**
     * Divides the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public MutableVector divide(@NotNull MutableVector vec) {
        x /= vec.x;
        y /= vec.y;
        z /= vec.z;
        return this;
    }

    /**
     * Copies another vector
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public MutableVector copy(@NotNull MutableVector vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
        return this;
    }

    /**
     * Gets the magnitude of the vector, defined as sqrt(x^2+y^2+z^2). The
     * value of this method is not cached and uses a costly square-root
     * function, so do not repeatedly call this method to get the vector's
     * magnitude. NaN will be returned if the inner result of the sqrt()
     * function overflows, which will be caused if the length is too long.
     *
     * @return the magnitude
     */
    public double length() {
        return Math.sqrt(NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z));
    }

    /**
     * Gets the magnitude of the vector squared.
     *
     * @return the magnitude
     */
    public double lengthSquared() {
        return NumberConversions.square(x) + NumberConversions.square(y) + NumberConversions.square(z);
    }

    /**
     * Get the distance between this vector and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the vector's magnitude. NaN will be
     * returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param o The other vector
     * @return the distance
     */
    public double distance(@NotNull MutableVector o) {
        return Math.sqrt(NumberConversions.square(x - o.x) + NumberConversions.square(y - o.y) + NumberConversions.square(z - o.z));
    }

    /**
     * Gets the angle between this vector and another in radians.
     *
     * @param other The other vector
     * @return angle in radians
     */
    public float angle(@NotNull MutableVector other) {
        double dot = Doubles.constrainToRange(dot(other) / (length() * other.length()), -1.0, 1.0);

        return (float) Math.acos(dot);
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     *
     * @param other The other vector
     * @return this same vector (now a midpoint)
     */
    @NotNull
    public MutableVector midpoint(@NotNull MutableVector other) {
        x = (x + other.x) / 2;
        y = (y + other.y) / 2;
        z = (z + other.z) / 2;
        return this;
    }

    /**
     * Gets a new midpoint vector between this vector and another.
     *
     * @param other The other vector
     * @return a new midpoint vector
     */
    @NotNull
    public MutableVector getMidpoint(@NotNull MutableVector other) {
        double x = (this.x + other.x) / 2;
        double y = (this.y + other.y) / 2;
        double z = (this.z + other.z) / 2;
        return new MutableVector(x, y, z);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public MutableVector multiply(int m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public MutableVector multiply(double m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public MutableVector multiply(float m) {
        x *= m;
        y *= m;
        z *= m;
        return this;
    }

    /**
     * Calculates the dot product of this vector with another. The dot product
     * is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
     *
     * @param other The other vector
     * @return dot product
     */
    public double dot(@NotNull MutableVector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Calculates the cross product of this vector with another. The cross
     * product is defined as:
     * <ul>
     * <li>x = y1 * z2 - y2 * z1
     * <li>y = z1 * x2 - z2 * x1
     * <li>z = x1 * y2 - x2 * y1
     * </ul>
     *
     * @param o The other vector
     * @return the same vector
     */
    @NotNull
    public MutableVector crossProduct(@NotNull MutableVector o) {
        double newX = y * o.z - o.y * z;
        double newY = z * o.x - o.z * x;
        double newZ = x * o.y - o.x * y;

        x = newX;
        y = newY;
        z = newZ;
        return this;
    }

    /**
     * Calculates the cross product of this vector with another without mutating
     * the original. The cross product is defined as:
     * <ul>
     * <li>x = y1 * z2 - y2 * z1
     * <li>y = z1 * x2 - z2 * x1
     * <li>z = x1 * y2 - x2 * y1
     * </ul>
     *
     * @param o The other vector
     * @return a new vector
     */
    @NotNull
    public MutableVector getCrossProduct(@NotNull MutableVector o) {
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new MutableVector(x, y, z);
    }

    /**
     * Converts this vector to a unit vector (a vector with length of 1).
     *
     * @return the same vector
     */
    @NotNull
    public MutableVector normalize() {
        double length = length();

        x /= length;
        y /= length;
        z /= length;

        return this;
    }

    /**
     * Get the squared distance between this vector and another.
     *
     * @param o The other vector
     * @return the distance
     */
    public double distanceSquared(@NotNull MutableVector o) {
        return NumberConversions.square(x - o.x) + NumberConversions.square(y - o.y) + NumberConversions.square(z - o.z);
    }

    /**
     * Gets the X component.
     *
     * @return The X component.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y component.
     *
     * @return The Y component.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the Z component.
     *
     * @return The Z component.
     */
    public double getZ() {
        return z;
    }


    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setX(int x) {
        this.x = x;
        return this;
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Set the X component.
     *
     * @param x The new X component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setY(int y) {
        this.y = y;
        return this;
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * Set the Y component.
     *
     * @param y The new Y component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setZ(int z) {
        this.z = z;
        return this;
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Set the Z component.
     *
     * @param z The new Z component.
     * @return This vector.
     */
    @NotNull
    public MutableVector setZ(float z) {
        this.z = z;
        return this;
    }

    @NotNull
    @Override
    public MutableVector clone() {
        try {
            return (MutableVector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }


}

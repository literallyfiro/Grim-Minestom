package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.Pair;
import ac.grim.grimac.utils.vector.MutableVector;

// Copied directly from Hawk
public class Ray implements Cloneable {

    private MutableVector origin;
    private MutableVector direction;

    public Ray(MutableVector origin, MutableVector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Ray(GrimPlayer player, double x, double y, double z, float xRot, float yRot) {
        this.origin = new MutableVector(x, y, z);
        this.direction = calculateDirection(player, xRot, yRot);
    }

    // Account for FastMath by using player's trig handler
    // Copied from hawk which probably copied it from NMS
    public static MutableVector calculateDirection(GrimPlayer player, float xRot, float yRot) {
        MutableVector vector = new MutableVector();
        float rotX = (float) Math.toRadians(xRot);
        float rotY = (float) Math.toRadians(yRot);
        vector.setY(-player.trigHandler.sin(rotY));
        double xz = player.trigHandler.cos(rotY);
        vector.setX(-xz * player.trigHandler.sin(rotX));
        vector.setZ(xz * player.trigHandler.cos(rotX));
        return vector;
    }

    public Ray clone() {
        Ray clone;
        try {
            clone = (Ray) super.clone();
            clone.origin = this.origin.clone();
            clone.direction = this.direction.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toString() {
        return "origin: " + origin + " direction: " + direction;
    }

    public MutableVector getPointAtDistance(double distance) {
        MutableVector dir = new MutableVector(direction.getX(), direction.getY(), direction.getZ());
        MutableVector orig = new MutableVector(origin.getX(), origin.getY(), origin.getZ());
        return orig.add(dir.multiply(distance));
    }

    //https://en.wikipedia.org/wiki/Skew_lines#Nearest_Points
    public Pair<MutableVector, MutableVector> closestPointsBetweenLines(Ray other) {
        MutableVector n1 = direction.clone().crossProduct(other.direction.clone().crossProduct(direction));
        MutableVector n2 = other.direction.clone().crossProduct(direction.clone().crossProduct(other.direction));

        MutableVector c1 = origin.clone().add(direction.clone().multiply(other.origin.clone().subtract(origin).dot(n2) / direction.dot(n2)));
        MutableVector c2 = other.origin.clone().add(other.direction.clone().multiply(origin.clone().subtract(other.origin).dot(n1) / other.direction.dot(n1)));

        return new Pair<>(c1, c2);
    }

    public MutableVector getOrigin() {
        return origin;
    }

    public MutableVector calculateDirection() {
        return direction;
    }
}

package ac.grim.grimac.utils.data;

import ac.grim.grimac.utils.vector.MutableVector;

public class VelocityData {
    public final MutableVector vector;
    public final int entityID;
    public final int transaction;
    public double offset = Integer.MAX_VALUE;
    public boolean isSetback;

    public VelocityData(int entityID, int transaction, boolean isSetback, MutableVector vector) {
        this.entityID = entityID;
        this.vector = vector;
        this.transaction = transaction;
        this.isSetback = isSetback;
    }

    // First bread last tick -> Required this tick = don't require kb twice
    public VelocityData(int entityID, int transaction, MutableVector vector, boolean isSetback, double offset) {
        this.entityID = entityID;
        this.vector = vector;
        this.transaction = transaction;
        this.isSetback = isSetback;
        this.offset = offset;
    }
}

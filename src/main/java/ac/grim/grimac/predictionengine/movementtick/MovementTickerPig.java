package ac.grim.grimac.predictionengine.movementtick;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.data.packetentity.PacketEntityRideable;
import ac.grim.grimac.utils.vector.MutableVector;
import net.minestom.server.entity.attribute.Attribute;

public class MovementTickerPig extends MovementTickerRideable {
    public MovementTickerPig(GrimPlayer player) {
        super(player);
        movementInput = new MutableVector(0, 0, 1);
    }

    @Override
    public float getSteeringSpeed() { // Vanilla multiples by 0.225f
        PacketEntityRideable pig = (PacketEntityRideable) player.compensatedEntities.getSelf().getRiding();
        return (float) pig.getAttributeValue(Attribute.GENERIC_MOVEMENT_SPEED) * 0.225f;
    }
}

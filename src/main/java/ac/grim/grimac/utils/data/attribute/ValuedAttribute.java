package ac.grim.grimac.utils.data.attribute;

import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.ClientVersion;
import ac.grim.grimac.utils.math.GrimMath;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.network.packet.server.play.EntityAttributesPacket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class ValuedAttribute {

    private static final Function<Double, Double> DEFAULT_GET_REWRITE = Function.identity();

    private final Attribute attribute;
    // Attribute limits defined by https://minecraft.wiki/w/Attribute
    // These seem to be clamped on the client, but not the server
    private final double min, max;

    private EntityAttributesPacket.Property lastProperty;
    private final double defaultValue;
    private double value;

    // BiFunction of <Old, New, Output>
    // This allows us to rewrite the value based on client & server version
    private BiFunction<Double, Double, Double> setRewriter;
    private Function<Double, Double> getRewriter;

    private ValuedAttribute(Attribute attribute, double defaultValue, double min, double max) {
        if (defaultValue < min || defaultValue > max) {
            throw new IllegalArgumentException("Default value must be between min and max!");
        }

        this.attribute = attribute;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.min = min;
        this.max = max;
        this.getRewriter = DEFAULT_GET_REWRITE;
    }

    public static ValuedAttribute ranged(Attribute attribute, double defaultValue, double min, double max) {
        return new ValuedAttribute(attribute, defaultValue, min, max);
    }

    public ValuedAttribute withSetRewriter(BiFunction<Double, Double, Double> rewriteFunction) {
        this.setRewriter = rewriteFunction;
        return this;
    }

    /**
     * Creates a rewriter that prevents the value from ever being modified unless the player meets the required version.
     * @param player the player
     * @param requiredVersion the required version for the attribute
     * @return this instance for chaining
     */
    public ValuedAttribute requiredVersion(GrimPlayer player, ClientVersion requiredVersion) {
        withSetRewriter((oldValue, newValue) -> {
            if (player.getClientVersion().isOlderThan(requiredVersion)) {
                return oldValue;
            }
            return newValue;
        });
        return this;
    }

    public ValuedAttribute withGetRewriter(Function<Double, Double> getRewriteFunction) {
        this.getRewriter = getRewriteFunction;
        return this;
    }

    public Attribute attribute() {
        return attribute;
    }

    public void reset() {
        this.value = defaultValue;
        this.lastProperty = null; // Remove the old property with its modifiers so we don't accidentally use it again, messing up the calculation.
    }

    public double get() {
        return getRewriter.apply(this.value);
    }

    public void override(double value) {
        this.value = value;
    }

    @Deprecated // Avoid using this, it only exists for special cases
    public Optional<EntityAttributesPacket.Property> property() {
        return Optional.ofNullable(lastProperty);
    }

    public void recalculate() {
        with(lastProperty);
    }

    public double with(EntityAttributesPacket.Property property) {
        double baseValue = property.value();
        double additionSum = 0;
        double multiplyBaseSum = 0;
        double multiplyTotalProduct = 1.0;

        Collection<AttributeModifier> modifiersC = property.modifiers();
        // todo check if it works modifiers.removeIf(modifier -> modifier.getUUid().equals(SPRINTING_MODIFIER_UUID) || modifier.id().key().equals(Key.key("sprinting")));
        List<AttributeModifier> modifiers = new ArrayList<>(modifiersC);
        modifiers.removeIf(modifier -> modifier.id().value().equals("sprinting"));

        for (AttributeModifier modifier : modifiers) {
            switch (modifier.operation()) {
                case ADD_VALUE:
                    additionSum += modifier.amount();
                    break;
                case MULTIPLY_BASE:
                    multiplyBaseSum += modifier.amount();
                    break;
                case MULTIPLY_TOTAL:
                    multiplyTotalProduct *= (1.0 + modifier.amount());
                    break;
            }
        }

        double newValue = GrimMath.clamp((baseValue + additionSum) * (1 + multiplyBaseSum) * multiplyTotalProduct, min, max);
        if (setRewriter != null) {
            newValue = setRewriter.apply(this.value, newValue);
        }

        if (newValue < min || newValue > max) {
            throw new IllegalArgumentException("New value must be between min and max!");
        }

        this.lastProperty = property;
        return this.value = newValue;
    }
}

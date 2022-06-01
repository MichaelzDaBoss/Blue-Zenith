package cat.module.value.types;

import cat.module.value.Value;
import cat.module.value.ValueConsumer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Predicate;
import java.util.logging.Logger;

public final class FloatValue extends Value<Float> {
    public Float max;
    public Float min;
    public final float increment;

    public FloatValue(String valueName, float value, float min, float max, float increment, boolean visible, Predicate<Float> modifier) {
        super(valueName, value, visible, null, modifier);
        this.max = max;
        this.min = min;

        if(increment == 0) {
            this.increment = 0.01F;
        } else {
            this.increment = increment;
        }
    }

    public FloatValue(String valueName, Float value, Float min, Float max, float increment, boolean visible, ValueConsumer<Float> consumer, Predicate<Float> modifier) {
        super(valueName, value, visible, consumer, modifier);
        this.max = max;
        this.min = min;

        if(increment == 0) {
            this.increment = 0.01F;
        } else {
            this.increment = increment;
        }
    }

    @Override
    public Float get() {
        return this.value;
    }

    @Override
    public void set(Float newValue) {
        //lmao
        if(consumer != null) {
            this.value = consumer.check(this.value, newValue);
        } else this.value = newValue;
    }

    public void setValuePrecise(Float value) {
        set(makePrecise(Math.round(value / increment) * increment, 5));
    }

    public void next() {
        set(Math.min(value + increment, max));
    }

    @Override
    public void previous() {
        set(Math.max(value - increment, min));
    }

    @Override
    public JsonElement getPrimitive() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void fromPrimitive(JsonPrimitive primitive) {
        try {
            set(primitive.getAsFloat());
        } catch (NumberFormatException e) {
            Logger.getLogger("BlueZenith").warning("Failed to parse float value: " + primitive.getAsString());
        }
    }

    public static float makePrecise(float value, int precision) {
        double pow = Math.pow(10, precision);
        long powValue = Math.round(pow * value);
        return (float) (powValue / pow);
    }
}

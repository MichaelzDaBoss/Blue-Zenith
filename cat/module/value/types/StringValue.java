package cat.module.value.types;

import cat.module.value.Value;
import cat.module.value.ValueConsumer;
import cat.util.ClientUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Predicate;

public class StringValue extends Value<String> {
    public StringValue(String valueName, String value, boolean visible, ValueConsumer<String> consumer, Predicate<String> modifier) {
        super(valueName, value, visible, consumer, modifier);
    }

    public StringValue(String valueName, String value, boolean visible, Predicate<String> modifier) {
        super(valueName, value, visible, null, modifier);
    }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String newValue) {
        if(consumer != null) {
            value = consumer.check(this.value, newValue);
        } else value = newValue;
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    @Override
    public JsonElement getPrimitive() {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void fromPrimitive(JsonPrimitive primitive) {
        set(primitive.getAsString());
    }
}

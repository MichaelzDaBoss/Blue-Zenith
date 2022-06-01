package cat.module.value.types;

import cat.module.value.Value;
import cat.module.value.ValueConsumer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public final class ModeValue extends Value<String> {

    private final ArrayList<String> range;
    public ModeValue(String valueName, String defaultValue, boolean visible, ValueConsumer<String> update, Predicate<String> visibility, String... range) {
        super(valueName, defaultValue, visible, update, visibility);
        this.range = new ArrayList<>(Arrays.asList(range));
    }
    public ModeValue(String valueName, String defaultValue, boolean visible, Predicate<String> visibility, String... range) {
        super(valueName, defaultValue, visible, null, visibility);
        this.range = new ArrayList<>(Arrays.asList(range));
    }
    @Override
    public String get() {
        return this.value;
    }

    public boolean is(String other) { return this.value.equals(other); }
    @Override
    public void set(String newValue) {
        if(consumer != null) {
            this.value = consumer.check(this.value, newValue);
        } else this.value = newValue;
    }

    public String find(String value) {
        return range.stream().filter(m -> m.equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    public void next() {
        int index = range.indexOf(this.value);
        if((index + 1) >= range.size()) {
            index = 0;
        } else index += 1;
        this.set(range.get(index));
    }

    public void previous() {
        int index = range.indexOf(this.value);
        if(index - 1 < 0) {
            index = range.size() - 1;
        } else index -= 1;
        this.set(range.get(index));
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

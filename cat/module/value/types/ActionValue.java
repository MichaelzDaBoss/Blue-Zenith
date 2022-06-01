package cat.module.value.types;

import cat.module.value.Value;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.function.Predicate;

public final class ActionValue extends Value<Runnable> {

    public ActionValue(String valueName, Runnable value, Predicate<Runnable> modifier) {
        super(valueName, value, true, null, modifier);
    }

    public ActionValue(String valueName, Runnable value) {
        super(valueName, value, true, null, null);
    }

    @Override
    public Runnable get() {
        return value;
    }

    @Override
    public void next() {
        value.run();
    }

    @Override
    public void previous() {
        value.run();
    }

    @Override
    public JsonElement getPrimitive() {
        return null;
    }

    @Override
    public void fromPrimitive(JsonPrimitive primitive) {

    }

    @Override
    public void set(Runnable newValue) {

    }
}

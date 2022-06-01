package cat.module.value.types;

import cat.module.value.Value;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListValue extends Value<List<String>> {

    public final ArrayList<BooleanValue> choices = new ArrayList<>();

    public ListValue(String name, boolean visible, String... options) {
        super(name, Arrays.asList(options), visible, null, null);
        Arrays.stream(options).forEach(option -> choices.add(new BooleanValue(option, false, true, null)));
    }

    public ListValue(String name, boolean visible, Predicate<List<String>> modifier, String... options) {
        super(name, Arrays.asList(options), visible, null, modifier);
        Arrays.stream(options).forEach(option -> choices.add(new BooleanValue(option, false, true, null)));
    }


    @Override
    public List<String> get() {
        return value;
    }

    @Override
    public void set(List<String> newValue) {
         //unused
    }

    @Override
    public void next() {
        //unused
    }

    public void toggleOptions(String... options) {
        for(String option : options) {
            choices.stream().filter(v -> v.name.equals(option)).findFirst().ifPresent(value -> value.set(!value.get()));
        }
    }

    public boolean getOptionState(String name) {
        BooleanValue value = choices.stream().filter(v -> v.name.equals(name)).findFirst().orElse(null);

        if (value == null) {
            return false;
        } else {
            return value.get();
        }
    }

    @Override
    public void previous() {
        //unused
    }

    @Override
    public JsonElement getPrimitive() {

        return null;
    }

    @Override
    public void fromPrimitive(JsonPrimitive primitive) {

    }

    public void fromObject(JsonObject object) {
        object.entrySet().forEach(entry -> {
            choices.stream().filter(v -> v.name.equals(entry.getKey())).findFirst().ifPresent(value -> value.set(entry.getValue().getAsBoolean()));
        });
    }
}

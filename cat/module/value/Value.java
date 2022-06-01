package cat.module.value;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.sun.istack.internal.Nullable;

import java.util.function.Predicate;

public abstract class Value<T> {
    public final String name;
    protected T value;
    private boolean visible;
    protected final ValueConsumer<T> consumer;
    protected final Predicate<T> modifier;

    public Value(String valueName, T value, boolean visible, @Nullable ValueConsumer<T> consumer, @Nullable Predicate<T> modifier){
        this.name = valueName;
        this.value = value;
        this.visible = visible;
        this.consumer = consumer;
        this.modifier = modifier;
    }

    public abstract T get();
    public abstract void set(T newValue);
    public abstract void next();
    public abstract void previous();

    public abstract JsonElement getPrimitive();
    public abstract void fromPrimitive(JsonPrimitive primitive);

    public boolean isVisible() {
        if(modifier == null) return visible;
        visible = modifier.test(value);
        return visible;
    }
}

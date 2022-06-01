package cat.module.value.types;

import cat.module.value.Value;
import cat.module.value.ValueConsumer;
import cat.util.MathUtil;
import cat.util.font.sigma.FontUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FontValue extends Value<FontRenderer> {
    private int index = 0;
    public FontValue(String valueName, FontRenderer value, boolean visible, ValueConsumer<FontRenderer> consumer) {
        this(valueName, value, visible, consumer, null);
    }
    public FontValue(String valueName, FontRenderer value, boolean visible, ValueConsumer<FontRenderer> consumer, Predicate<FontRenderer> modifier) {
        super(valueName, value, visible, consumer, modifier);
        if(FontUtil.fonts.contains(value)){
            index = FontUtil.fonts.indexOf(value);
        }
    }

    @Override
    public FontRenderer get() {
        return value != null ? value : FontUtil.fontRendererObj;
    }

    @Override
    public void set(FontRenderer newValue) {
        if(consumer != null) {
            this.value = consumer.check(this.value, newValue);
        } else this.value = newValue;
    }

    @Override
    public void next() {
        if(index >= FontUtil.fonts.size()) index = 0;
        set(FontUtil.fonts.get(MathUtil.inRange(index, 0, FontUtil.fonts.size() - 1)));
        index++;
    }

    @Override
    public void previous() {
        if(index < FontUtil.fonts.size()) index = FontUtil.fonts.size();
        set(FontUtil.fonts.get(MathUtil.inRange(index, 0, FontUtil.fonts.size() - 1)));
        index--;
    }
    //TODO: Implement methods
    @Override
    public JsonElement getPrimitive() {
        return new JsonPrimitive(get().getName());
    }

    @Override
    public void fromPrimitive(JsonPrimitive primitive) {
        this.value = FontUtil.fonts.stream().filter(i -> i.getName().equals(primitive.getAsString())).findFirst().orElse(FontUtil.fontRendererObj);
    }
}

package cat.events.impl;

import cat.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
    public final float partialTicks;
    public final ScaledResolution resolution;

    public Render2DEvent(float partialTicks, ScaledResolution resolution) {
        this.partialTicks = partialTicks;
        this.resolution = resolution;
    }
}
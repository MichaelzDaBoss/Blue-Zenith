package cat.events.impl;

import cat.events.Event;

public class Render3DEvent extends Event {
    public float partialTicks = 0;
    public Render3DEvent(float partialTicks){
        this.partialTicks = partialTicks;
    }
}

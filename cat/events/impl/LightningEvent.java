package cat.events.impl;

import cat.events.Event;

public class LightningEvent extends Event {

    public final double x, y, z;

    public LightningEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

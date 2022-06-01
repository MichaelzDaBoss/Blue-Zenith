package cat.events.impl;

import cat.events.Event;

public class MoveEvent extends Event {
    public double x, y, z;
    public boolean safewalk;
    public MoveEvent(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.safewalk = false;
    }
}

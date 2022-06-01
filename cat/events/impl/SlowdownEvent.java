package cat.events.impl;

import cat.events.Event;

public class SlowdownEvent extends Event {

    public float reducer;
    public SlowdownEvent(float reducer) {
        this.reducer = reducer;
    }
}

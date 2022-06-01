package cat.events.impl;

import cat.events.EventType;
import cat.events.MultiTypeEvent;
import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
public class AttackEvent extends MultiTypeEvent {

    public final Entity target;
    public EventType type;

    public AttackEvent(Entity target, EventType type) {
        this.target = target;
        this.type = type;
    }
}

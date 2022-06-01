package cat.events;

import cat.BlueZenith;
import com.google.common.eventbus.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("all")
public final class EventManager {
    private final Map<Class, CopyOnWriteArrayList<Method>> listeners = new HashMap<>();

    /**
     * only use this when shutting down the client or ur fucked
     */
    public void shutdown() {
        listeners.clear();
    }

    public void registerListener(Object listener) {
        for (Method method : listener.getClass().getMethods()) {
            if(method.isAnnotationPresent(Subscribe.class) && method.getParameterTypes().length == 1 && (method.getParameterTypes()[0].getSuperclass() == Event.class || method.getParameterTypes()[0].getSuperclass().getSuperclass() == Event.class)) {
                Class<?> ev = method.getParameterTypes()[0];
                if(!listeners.containsKey(ev)) {
                    CopyOnWriteArrayList<Method> m = new CopyOnWriteArrayList<>();
                    m.add(method);
                    listeners.put(ev, m);
                } else {
                    listeners.get(ev).add(method);
                }
            }
        }
    }

    public void unregisterListener(Object listener) {
        listeners.values().forEach(list -> list.forEach(func -> list.removeIf(method -> method.getDeclaringClass() == listener.getClass())));
    }

    public void call(Event event) {
        if(BlueZenith.useExperimentalEventBus)
            BlueZenith.eventBus.post(event);
        else dispatch(event);
    }
    private void dispatch(Event event) {
        listeners.forEach((targetEvent, methods) -> {
            if(targetEvent == event.getClass()){
                for (Method m : methods) {
                    m.setAccessible(true);
                    try {
                        m.invoke(BlueZenith.moduleManager.getModule(m.getDeclaringClass()), event);
                    } catch (IllegalAccessException | InvocationTargetException e) {

                    }
                }
            }
        });
    }
}
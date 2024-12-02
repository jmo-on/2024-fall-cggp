package mygame.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import mygame.GameEvent;

public class EventBus {
    private static EventBus instance;
    private Map<String, List<EventListener>> listeners = new HashMap<>();
    
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
    
    public void publish(GameEvent event) {
        List<EventListener> eventListeners = listeners.get(event.getType());
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(event);
            }
        }
    }
    
    public void subscribe(String eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }
    
    public void unsubscribe(String eventType, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }
} 
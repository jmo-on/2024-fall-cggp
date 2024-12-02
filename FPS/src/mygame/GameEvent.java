package mygame;

import java.util.HashMap;
import java.util.Map;

public class GameEvent {
    private String type;
    private Map<String, Object> data;
    
    public GameEvent(String type) {
        this.type = type;
        this.data = new HashMap<>();
    }
    
    public void addData(String key, Object value) {
        data.put(key, value);
    }
    
    public Object getData(String key) {
        return data.get(key);
    }
    
    public String getType() {
        return type;
    }
}
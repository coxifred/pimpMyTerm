package pimpmyterm.beans;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSchSessions {

    Map<String, SchSession> schSessionMap = new ConcurrentHashMap<String, SchSession>();


    public Map<String, SchSession> getSchSessionMap() {
        return schSessionMap;
    }

    public void setSchSessionMap(Map<String, SchSession> schSessionMap) {
        this.schSessionMap = schSessionMap;
    }

}

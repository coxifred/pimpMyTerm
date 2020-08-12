package pimpmyterm.beans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionsOutput {

    //instance id, host output
    Map<String, SessionHostOutput> sessionOutputMap = new ConcurrentHashMap<String,SessionHostOutput>();


    public Map<String, SessionHostOutput> getSessionOutputMap() {
        return sessionOutputMap;
    }

    public void setSessionOutputMap(Map<String, SessionHostOutput> sessionOutputMap) {
        this.sessionOutputMap = sessionOutputMap;
    }
}




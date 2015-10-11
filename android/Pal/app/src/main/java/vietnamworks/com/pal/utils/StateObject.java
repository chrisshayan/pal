package vietnamworks.com.pal.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by duynk on 10/9/15.
 */
public class StateObject {
    private boolean hasSetValue = false;
    ArrayList<HashMap<String, Object>> stateValues = new ArrayList<>();

    public void setParam(String key, Object value) {
        HashMap<String, Object> item = stateValues.get(0);
        item.put(key, value);
    }

    public Object getParam(String key) {
        if (stateValues.size() > 0) {
            HashMap<String, Object> item = stateValues.get(0);
            return item.get(key);
        }
        return null;
    }

    public int getIntParam(String key) {
        if (stateValues.size() > 0) {
            HashMap<String, Object> item = stateValues.get(0);
            int val =  (int) item.get(key);
            return val;
        }
        return 0;
    }

    public String getStringParam(String key) {
        if (stateValues.size() > 0) {
            HashMap<String, Object> item = stateValues.get(0);
            return item.get(key).toString();
        }
        return "";
    }

    public void setExtraParam(String key, Object value) {
        HashMap<String, Object> item = stateValues.get(0);
        HashMap<String, Object> extra = (HashMap<String, Object>)item.get("__extra");
        extra.put(key, value);
    }

    public int GetExtraIntParam(String key) {
        HashMap<String, Object> item = stateValues.get(0);
        HashMap<String, Object> extra = (HashMap)item.get("__extra");
        return (int)extra.get(key);
    }

    public String GetExtraStringParam(String key) {
        HashMap<String, Object> item = stateValues.get(0);
        HashMap<String, Object> extra = (HashMap)item.get("__extra");
        return extra.get(key).toString();
    }

    public void pushState(HashMap<String, Object> state_param) {
        if (!state_param.containsKey("__extra")) {
            state_param.put("__extra", new HashMap<String, Object>());
        }
        if (!hasSetValue) {
            setState(0, state_param);
            hasSetValue = true;
        } else {
            stateValues.add(0, state_param);
        }
    }

    public void cloneAndPushState() {
        HashMap<String, Object> item = (HashMap<String, Object>) stateValues.get(0).clone();
        if (hasSetValue) {
            stateValues.add(0, item);
        } else {
            hasSetValue = true;
        }
    }

    public void addState(HashMap<String, Object> state_param) {
        if (!state_param.containsKey("__extra")) {
            state_param.put("__extra", new HashMap<String, Object>());
        }
        if (!hasSetValue) {
            stateValues.set(0, state_param);
            hasSetValue = true;
        } else {
            stateValues.add(state_param);
        }
    }

    public HashMap<String, Object> cloneState() {
        HashMap<String, Object> item = (HashMap<String, Object>) stateValues.get(0).clone();
        return item;
    }

    public HashMap<String, Object> popState() {
        if (stateValues.size() > 1) {
            return stateValues.remove(0);
        } else {
            return stateValues.get(0);
        }
    }

    public int size() {
        return this.stateValues.size();
    }

    public HashMap<String, Object>getState() {
        return this.stateValues.get(0);
    }

    public HashMap<String, Object>getState(int index) {
        return this.stateValues.get(index);
    }

    public int setState(int index, HashMap<String, Object> obj) {
        if (index < stateValues.size()) {
            stateValues.set(index, obj);
            return index;
        } else {
            stateValues.add(obj);
            return stateValues.size()-1;
        }
    }
}

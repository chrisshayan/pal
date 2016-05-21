package vietnamworks.com.pal.entities;

import com.firebase.client.DataSnapshot;

import java.util.HashMap;

import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/1/15.
 */
public class BaseEntity {
    protected String id;
    protected String created_by;
    protected long created_date;
    protected String last_modified_by;
    protected long last_modified_date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public String getLast_modified_by() {
        return last_modified_by;
    }

    public void setLast_modified_by(String last_modified_by) {
        this.last_modified_by = last_modified_by;
    }

    public long getLast_modified_date() {
        return last_modified_date;
    }

    public void setLast_modified_date(long last_modified_date) {
        this.last_modified_date = last_modified_date;
    }

    public void create() {
        created_by = FirebaseService.getUid();
        created_date = Utils.getMillis();
        modify();
    }

    public void modify() {
        last_modified_by = FirebaseService.getUid();
        last_modified_date = Utils.getMillis();
    }

    public void modifyOrCreate() {
        if (created_by == null || created_by.length() == 0) {
            create();
        } else {
            modify();
        }
    }

    public static int safeGetInt(HashMap<String, Object> obj, String key) {
        return safeGetInt(obj, key, Integer.MIN_VALUE);
    }

    public static int safeGetInt(HashMap<String, Object> obj, String key, int _default) {
        if (obj.containsKey(key)) {
            try {
                Object v = obj.get(key);
                if (v instanceof Long) {
                    return ((Long) v).intValue();
                } else if (v instanceof Integer) {
                    return (int)v;
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        return _default;
    }

    public static long safeGetLong(HashMap<String, Object> obj, String key) {
        return safeGetLong(obj, key, Long.MIN_VALUE);
    }

    public static long safeGetLong(HashMap<String, Object> obj, String key, long _default) {
        if (obj.containsKey(key)) {
            try {
                Object v = obj.get(key);
                if (v instanceof Long) {
                    return (Long)v;
                } else if (v instanceof Integer) {
                    return ((Integer) v).longValue();
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        return -_default;
    }

    public static String safeGetString(HashMap<String, Object> obj, String key) {
        return safeGetString(obj, key, null);
    }

    public static String safeGetString(HashMap<String, Object> obj, String key, String _default) {
        if (obj.containsKey(key)) {
            try {
                return obj.get(key).toString();
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        return _default;
    }

    public static boolean safeGetBool(HashMap<String, Object> obj, String key) {
        return safeGetBool(obj, key, false);
    }

    public static boolean safeGetBool(HashMap<String, Object> obj, String key, boolean _default) {
        if (obj.containsKey(key)) {
            try {
                return (boolean)obj.get(key);
            } catch (Exception E) {
                E.printStackTrace();
            }
        }
        return _default;
    }

    public BaseEntity importData(Object data) {
        if (data instanceof DataSnapshot) {
            importData((DataSnapshot)data);
        } else if (data instanceof HashMap) {
            importData((HashMap<String, Object>)data);
        }
        return this;
    }

    public BaseEntity importData(HashMap<String, Object> obj) {
        this.created_date = BaseEntity.safeGetLong(obj, "created_date");
        this.created_by = BaseEntity.safeGetString(obj, "created_by");
        this.last_modified_date = BaseEntity.safeGetLong(obj, "last_modified_date");
        this.last_modified_by = BaseEntity.safeGetString(obj, "last_modified_by");
        return this;
    }

    public BaseEntity importData(DataSnapshot snapshot) {
        if (snapshot != null) {
            importData(snapshot.getValue(HashMap.class));
            setId(snapshot.getKey());
        }
        return this;
    }

    public HashMap exportData() {return null;}

}

package vietnamworks.com.pal.entities;

import java.util.HashMap;

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
        created_by = FirebaseService.authData.getUid();
        created_date = System.currentTimeMillis();
        modify();
    }

    public void modify() {
        last_modified_by = FirebaseService.authData.getUid();
        last_modified_date = System.currentTimeMillis();
    }

    public void modifyOrCreate() {
        if (created_by == null || created_by.length() == 0) {
            create();
        } else {
            modify();
        }
    }

    public static int safeGetInt(HashMap<String, Object> obj, String key) {
        if (obj.containsKey(key)) {
            try {
                int v = (int)obj.get(key);
                return v;
            } catch (Exception E) {
            }
        }
        return -9999;
    }

    public static long safeGetLong(HashMap<String, Object> obj, String key) {
        if (obj.containsKey(key)) {
            try {
                long v = (long)obj.get(key);
                return v;
            } catch (Exception E) {
            }
        }
        return -9999;
    }

    public static String safeGetString(HashMap<String, Object> obj, String key) {
        if (obj.containsKey(key)) {
            try {
                return obj.get(key).toString();
            } catch (Exception E) {
            }
        }
        return null;
    }
}

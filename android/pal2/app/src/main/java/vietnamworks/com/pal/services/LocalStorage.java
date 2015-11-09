package vietnamworks.com.pal.services;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;

/**
 * Created by duynk on 11/9/15.
 */
public class LocalStorage {
    static LocalStorage sInstance = new LocalStorage();
    DB db;

    public static void init(Context ctx) {
        try {
            sInstance.db = DBFactory.open(ctx);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void close() {
        try {
            sInstance.db.close();
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void set(String key, int value) {
        try {
            sInstance.db.putInt(key, value);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void set(String key, String value) {
        try {
            sInstance.db.put(key, value);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void set(String key, boolean b) {
        try {
            sInstance.db.putBoolean(key, b);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return sInstance.db.getInt(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }

    public static String getString(String key, String defaultValue) {
        try {
            return sInstance.db.get(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }

    public static boolean getBool(String key, boolean defaultValue) {
        try {
            return sInstance.db.getBoolean(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }
}

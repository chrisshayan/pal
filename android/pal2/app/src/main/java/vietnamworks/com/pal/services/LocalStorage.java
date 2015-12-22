package vietnamworks.com.pal.services;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;

/**
 * Created by duynk on 11/9/15.
 */
public class LocalStorage {
    static LocalStorage sInstance = new LocalStorage();
    Context ctx;
    DB db;

    public static void init(Context ctx) {
        try {
            sInstance.db = DBFactory.open(ctx);
            sInstance.ctx = ctx;
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

    private static void set(String key, int value) {
        try {
            sInstance.db.putInt(key, value);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private static void set(String key, long value) {
        try {
            sInstance.db.putLong(key, value);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private static void set(String key, String value) {
        try {
            sInstance.db.put(key, value);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    private static void set(String key, boolean b) {
        try {
            sInstance.db.putBoolean(key, b);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }

    public static void set(int key, boolean b) {
        set(sInstance.ctx.getString(key), b);
    }

    public static void set(int key, int value) {
        set(sInstance.ctx.getString(key), value);
    }

    public static void set(int key, long value) {
        set(sInstance.ctx.getString(key), value);
    }

    public static void set(int key, String value) {
        set(sInstance.ctx.getString(key), value);
    }

    private static int getInt(String key, int defaultValue) {
        try {
            return sInstance.db.getInt(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }

    private static long getLong(String key, long defaultValue) {
        try {
            return sInstance.db.getLong(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }

    private static String getString(String key, String defaultValue) {
        try {
            return sInstance.db.get(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }

    private static boolean getBool(String key, boolean defaultValue) {
        try {
            return sInstance.db.getBoolean(key);
        } catch (Exception E) {
            return defaultValue;
        }
    }

    public static int getInt(int key, int defaultValue) {
        return getInt(sInstance.ctx.getString(key), defaultValue);
    }

    public static long getLong(int key, long defaultValue) {
        return getLong(sInstance.ctx.getString(key), defaultValue);
    }

    public static String getString(int key, String defaultValue) {
        return getString(sInstance.ctx.getString(key), defaultValue);
    }

    public static boolean getBool(int key, boolean defaultValue) {
        return getBool(sInstance.ctx.getString(key), defaultValue);
    }
}

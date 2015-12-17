package vietnamworks.com.pal.services;

import com.crittercism.app.Crittercism;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by duynk on 12/17/15.
 */
public class ExceptionReportService {
    public static void report(String message) {
        Exception E = new Exception(message);
        E.printStackTrace();
        Crittercism.logHandledException(E);
    }

    public static void report(String message, HashMap data) {
        String detail = "";
        if (data != null) {
            try {
                JSONObject json = new JSONObject(data);
                detail = json.toString();
            } catch (Exception E) {
                detail = E.toString();
            }
        } else {
            detail = "data is null";
        }
        Exception E = new Exception(message + " : " + detail);
        E.printStackTrace();
        Crittercism.logHandledException(E);
    }

    public static void report(Exception E) {
        E.printStackTrace();
        Crittercism.logHandledException(E);
    }
}

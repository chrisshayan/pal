package vietnamworks.com.pal.services;

import org.json.JSONObject;

/**
 * Created by duynk on 10/1/15.
 */
public interface AsyncCallback {
    void onSuccess(JSONObject obj);
    void onError(int errorcode, String message);
}
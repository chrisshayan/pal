package vietnamworks.com.pal.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by duynk on 9/16/15.
 */
public class BaseService {
    public static final String API_URL = "http://private-c2153-palvnw.apiary-mock.com/";

    public interface OnLoadAsyncCallback {
        void onSuccess(JSONObject obj);
        void onError();
    }

    public static void Get(Context context, String url, final BaseService.OnLoadAsyncCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (callback!=null) {
                            try {
                                JSONObject reader = new JSONObject(response);
                                callback.onSuccess(reader);
                            } catch (Exception E) {
                                callback.onError();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback!=null) {
                            callback.onError();
                        }
                    }
                });
        queue.add(stringRequest);
    }
}

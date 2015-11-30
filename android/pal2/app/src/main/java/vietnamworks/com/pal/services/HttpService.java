package vietnamworks.com.pal.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 11/30/15.
 */
public class HttpService {
    static String API_URL;
    public static void init(Context ctx) {
        API_URL = ctx.getString(R.string.api_url);
    }

    public static void Get(final Context context, String url, final AsyncCallback callback){
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API_URL + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (callback!=null) {
                            try {
                                JSONObject reader = new JSONObject(response);
                                callback.onSuccess(context, reader);
                            } catch (Exception E) {
                                callback.onError(context, 0, E.toString());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback!=null) {
                    callback.onError(context, 0, "");
                }
            }
        });
        queue.add(stringRequest);
    }

    public static void Post(final Context context, String url, HashMap<String, String> params, final AsyncCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest req = new JsonObjectRequest(API_URL + url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(context, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int statusCode = -1;
                        if (error.networkResponse != null) {
                            statusCode = error.networkResponse.statusCode;
                        }
                        callback.onError(context, statusCode, error.getMessage());
                    }
                }
        );
        queue.add(req);
    }
}

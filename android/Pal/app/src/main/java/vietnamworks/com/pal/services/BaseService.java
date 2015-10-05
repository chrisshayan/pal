package vietnamworks.com.pal.services;

import android.content.Context;
import android.util.Log;

import com.alexbbb.uploadservice.UploadRequest;
import com.alexbbb.uploadservice.UploadService;


/**
 * Created by duynk on 9/16/15.
 */
public class BaseService {
    public static final String API_URL = "http://private-c2153-palvnw.apiary-mock.com/";

    /*
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
    */

    public static void PostFile(final Context context, String server, String filepath, String server_file_name) {
        final UploadRequest request = new UploadRequest(context,
                "custom-upload-id",
                server);
        request.addFileToUpload(filepath,
                "parameter-name",
                server_file_name,
                "content-type");

        request.setMaxRetries(2);
        try {
            UploadService.startUpload(request);

        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getLocalizedMessage(), exc);
        }
    }
}

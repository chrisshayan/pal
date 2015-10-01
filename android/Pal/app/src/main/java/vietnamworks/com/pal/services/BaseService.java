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

    /*
     * parameter-name: is the name of the parameter that will contain file's data.
     * Pass "uploaded_file" if you're using the test PHP script
     *
     * custom-file-name.extension: is the file name seen by the server.
     * E.g. value of $_FILES["uploaded_file"]["name"] of the test PHP script
     */
        request.addFileToUpload(filepath,
                "parameter-name",
                server_file_name,
                "content-type");

        /*
        //You can add your own custom headers
        request.addHeader("your-custom-header", "your-custom-value");

        //and parameters
        request.addParameter("parameter-name", "parameter-value");

        //If you want to add a parameter with multiple values, you can do the following:
        request.addParameter("array-parameter-name", "value1");
        request.addParameter("array-parameter-name", "value2");
        request.addParameter("array-parameter-name", "valueN");

        //or
        String[] values = new String[] {"value1", "value2", "valueN"};
        request.addArrayParameter("array-parameter-name", values);

        //or
        List<String> valuesList = new ArrayList<String>();
        valuesList.add("value1");
        valuesList.add("value2");
        valuesList.add("valueN");
        request.addArrayParameter("array-parameter-name", valuesList);

        //configure the notification
        request.setNotificationConfig(android.R.drawable.ic_menu_upload,
                "notification title",
                "upload in progress text",
                "upload completed successfully text"
                "upload error text",
                false);

        // set a custom user agent string for the upload request
        // if you comment the following line, the system default user-agent will be used
        request.setCustomUserAgent("UploadServiceDemo/1.0");

        // set the intent to perform when the user taps on the upload notification.
        // currently tested only with intents that launches an activity
        // if you comment this line, no action will be performed when the user taps on the notification
        request.setNotificationClickIntent(new Intent(context, YourActivity.class));

        */

        // set the maximum number of automatic upload retries on error
        request.setMaxRetries(2);

        try {
            //Start upload service and display the notification
            UploadService.startUpload(request);

        } catch (Exception exc) {
            //You will end up here only if you pass an incomplete UploadRequest
            Log.e("AndroidUploadService", exc.getLocalizedMessage(), exc);
        }
    }
}

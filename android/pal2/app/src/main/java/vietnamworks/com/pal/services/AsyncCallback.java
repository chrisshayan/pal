package vietnamworks.com.pal.services;

import android.content.Context;

/**
 * Created by duynk on 10/1/15.
 */
public interface AsyncCallback {
    void onSuccess(Context ctx, Object obj);
    void onError(Context ctx, int error_code, String message);
}
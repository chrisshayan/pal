package vietnamworks.com.pal.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by duynk on 12/7/15.
 */
public class ParsePushBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {
    @Override
    protected Notification getNotification(Context context, Intent intent) {
        return super.getNotification(context, intent);
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        NotificationManager manager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject data = getDataFromIntent(intent);
        super.onPushReceive(context, intent);
    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}

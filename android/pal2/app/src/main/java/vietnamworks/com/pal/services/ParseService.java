package vietnamworks.com.pal.services;

import android.app.NotificationManager;
import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.Utils;

/**
 * Created by duynk on 10/26/15.
 */
public class ParseService {
    static Context context;
    public static void init(Context ctx) {
        context = ctx;
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.initialize(ctx, Utils.r13(ctx.getString(R.string.parse_app_id)), Utils.r13(ctx.getString(R.string.parse_app_key)));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void registerUser(String user_id, String email) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", user_id);
        installation.put("user_email", email);
        installation.saveInBackground();
    }

    public static void unRegisterUser() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", "");
        installation.put("user_email", "");
        installation.saveInBackground();
    }

    public static void clearAllNotification() {
        NotificationManager nMgr = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (nMgr != null) {
            nMgr.cancelAll();
        }
    }
}

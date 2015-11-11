package vietnamworks.com.pal.services;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;

import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/26/15.
 */
public class ParseService {
    public static void init(Context ctx) {
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.initialize(ctx, ctx.getString(R.string.parse_app_id), ctx.getString(R.string.parse_app_key));
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void RegisterUser(String user_id) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", user_id);
        installation.saveInBackground();
    }
}

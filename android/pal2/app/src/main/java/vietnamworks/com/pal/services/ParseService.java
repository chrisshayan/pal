package vietnamworks.com.pal.services;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseInstallation;

import vietnamworks.com.pal.configurations.ParseSettings;

/**
 * Created by duynk on 10/26/15.
 */
public class ParseService {
    public static void init(Context ctx) {
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Parse.initialize(ctx, ParseSettings.APP_ID, ParseSettings.APP_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void RegisterUser(String user_id) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user_id", user_id);
        installation.saveInBackground();
    }
}

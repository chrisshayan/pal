package vietnamworks.com.pal;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by duynk on 10/5/15.
 */
public class MyApp extends Application {
    public MyApp() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Config.ParseAppId, Config.ParseAppKey);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}

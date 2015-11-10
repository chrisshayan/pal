package vietnamworks.com.pal.services;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;

/**
 * Created by duynk on 11/10/15.
 */
public class GaService {
    private Tracker mTracker;
    private Context ctx;
    static GaService instance = new GaService();
    public static void init(Context context) {
        instance.ctx = context;
    }
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(instance.ctx);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public static void trackScreen(String name) {
        instance.mTracker.setScreenName(name);
        instance.mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void trackScreen(int screen) {
        instance.mTracker.setScreenName(BaseActivity.sInstance.getString(screen));
        instance.mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void trackAction(String action_name) {
        instance.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction(action_name)
                .build());
    }

    public static void trackAction(int action_name) {
        instance.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction(BaseActivity.sInstance.getString(action_name))
                .build());
    }

    public static void trackEvent(String category, String action_name) {
        instance.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action_name)
                .build());
    }

    public static void trackEvent(int category, int action_name) {
        instance.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(BaseActivity.sInstance.getString(category))
                .setAction(BaseActivity.sInstance.getString(action_name))
                .build());
    }

}

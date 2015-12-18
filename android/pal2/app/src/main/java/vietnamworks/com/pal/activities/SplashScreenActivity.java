package vietnamworks.com.pal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import org.json.JSONObject;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.LocalStorage;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        if (LocalStorage.getBool(R.string.ls_first_launch, true)) {
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    openActivity(OnBoardingActivity.class);
                }
            }, 1000);
        } else {
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    if (FirebaseService.checkAuthSync()) {
                        setTimeout(new Runnable() {
                            @Override
                            public void run() {
                                openActivity(TimelineActivity.class);
                            }
                        }, 1000);
                    } else {
                        setTimeout(new Runnable() {
                            @Override
                            public void run() {
                                openActivity(OnBoardingActivity.class);
                            }
                        }, 1000);
                    }
                }
            }, 1000);
        }
    }

    @Override
    protected void onResume() {
        Intent intentNotification = getIntent();
        Bundle extras = intentNotification.getExtras();
        if (extras!=null) {
            String jsonData = extras.getString( "com.parse.Data" );
            if (jsonData != null && !jsonData.isEmpty()) {
                try {
                    JSONObject object = new JSONObject(jsonData);
                    String post_id = object.getString("post_id");
                    if (post_id != null && !post_id.isEmpty()) {
                        TimelineActivity.resumeFromPushWithPostId = post_id;
                    } else {
                        TimelineActivity.resumeFromPushWithPostId = null;
                    }
                }catch (Exception E) {}
            }
        }
        super.onResume();
    }
}

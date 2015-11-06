package vietnamworks.com.pal.activities;

import android.os.Bundle;
import android.view.WindowManager;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.services.FirebaseService;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

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

    @Override
    protected void onResume() {
        super.onResume();
    }
}

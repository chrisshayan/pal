package vietnamworks.com.pal.activities;

import android.os.Bundle;
import android.view.WindowManager;

import vietnamworks.com.pal.R;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        BaseActivity.sInstance.setTimeout(new Runnable() {
            @Override
            public void run() {
                openActivity(OnBoardingActivity.class);
            }
        }, 3000);
    }
}

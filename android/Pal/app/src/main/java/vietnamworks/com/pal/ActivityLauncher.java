package vietnamworks.com.pal;

import android.os.Bundle;

import com.alexbbb.uploadservice.UploadService;

public class ActivityLauncher extends ActivityBase {
    public final static long DELAY_TIME = 3000L;

    public static ActivityLauncher instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openActivity(ActivityTutorial.class);
            }
        }, DELAY_TIME);
        UploadService.NAMESPACE = "vietnamworks.com.pal";
    }
}

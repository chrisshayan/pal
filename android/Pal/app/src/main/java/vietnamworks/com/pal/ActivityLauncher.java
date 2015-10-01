package vietnamworks.com.pal;

import android.content.Intent;
import android.os.Bundle;

public class ActivityLauncher extends ActivityBase {
    public final static long DELAY_TIME = 3000L;

    public static ActivityLauncher instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        instance = this;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ActivityLauncher.instance, ActivityTutorial.class);
                startActivity(intent);
            }
        }, DELAY_TIME);
    }
}

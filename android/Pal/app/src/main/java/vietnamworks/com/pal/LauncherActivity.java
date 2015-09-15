package vietnamworks.com.pal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {
    public final static long DELAY_TIME = 3000L;

    public static LauncherActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        instance = this;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LauncherActivity.instance, TutorialActivity.class);
                startActivity(intent);
            }
        }, DELAY_TIME);
    }
}

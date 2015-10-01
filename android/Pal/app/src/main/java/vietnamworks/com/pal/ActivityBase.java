package vietnamworks.com.pal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/1/15.
 */

public class ActivityBase extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseService.setContext(this);
        ActivityBase.sInstance = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityBase.sInstance = null;
    }

    public void openFragment(android.support.v4.app.Fragment f, int holder_id, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(holder_id, f);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void openActivity(Class<?> cls) {
        Intent intent = new Intent(ActivityBase.sInstance, cls);
        startActivity(intent);
    }

    public void openFragment(android.support.v4.app.Fragment f, int holder_id) {
        openFragment(f, holder_id, false);
    }

    public static ActivityBase sInstance;
}
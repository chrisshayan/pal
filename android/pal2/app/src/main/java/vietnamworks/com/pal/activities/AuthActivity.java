package vietnamworks.com.pal.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.fragments.BaseFragment;

/**
 * Created by duynk on 10/26/15.
 */
public class AuthActivity extends BaseActivity {
    BaseFragment loginFragment, registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);;

        applyFont((TextView) findViewById(R.id.app_title), Bubblegum);

        loginFragment = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        registerFragment = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register);

        registerFragment.getView().setVisibility(View.INVISIBLE);
    }
}

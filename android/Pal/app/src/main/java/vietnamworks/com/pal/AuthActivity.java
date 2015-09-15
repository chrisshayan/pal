package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AuthActivity extends AppCompatActivity {
    SignUpFragment mFragmentSignUp;
    LoginFragment mFragmentLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if (findViewById(R.id.auth_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            SignUpFragment firstFragment = SignUpFragment.create(this);

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.auth_fragment_container, firstFragment).commit();
        }
    }

    public void onLogin(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        if (f instanceof  SignUpFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            LoginFragment next = LoginFragment.create(this);
            transaction.replace(R.id.auth_fragment_container, next);
            //transaction.addToBackStack(null);
            transaction.commit();
        } else {
            ((LoginFragment)f).onLogin();
        }
    }

    public void onSignUp(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        if (f instanceof  LoginFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SignUpFragment next = SignUpFragment.create(this);
            transaction.replace(R.id.auth_fragment_container, next);
            //transaction.addToBackStack(null);
            transaction.commit();
        } else {
            ///// TODO: 9/15/15
        }
    }
}

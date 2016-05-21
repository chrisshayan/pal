package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import vietnamworks.com.pal.fragments.FragmentLogin;
import vietnamworks.com.pal.fragments.FragmentSignUp;
import vietnamworks.com.pal.fragments.FragmentSignUpProcessing;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.utils.Common;

public class AuthActivity extends BaseActivity {
    FragmentSignUp mFragmentSignUp;
    FragmentLogin mFragmentLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FirebaseService.setContext(this);

        if (findViewById(R.id.auth_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            FragmentSignUp firstFragment = FragmentSignUp.create(this);
            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.auth_fragment_container, firstFragment).commit();
        }
    }

    public void onLogin(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        if (f instanceof FragmentSignUp) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentLogin next = FragmentLogin.create(this);
            transaction.replace(R.id.auth_fragment_container, next);
            //transaction.addToBackStack(null);
            transaction.commit();
        } else {
            ((FragmentLogin)f).onLogin();
        }
    }

    public void onSignUp(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        if (f instanceof FragmentLogin) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentSignUp next = FragmentSignUp.create(this);
            transaction.replace(R.id.auth_fragment_container, next);
            //transaction.addToBackStack(null);
            transaction.commit();
        } else if (f instanceof FragmentSignUp) {
            final String email = ((FragmentSignUp)f).getEmail();
            if (email.length() == 0) {
                toast(R.string.login_validation_empty_email);
                return;
            }

            if (!Common.isValidEmail(email)) {
                toast(R.string.login_validation_invalid_email_format);
                return;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            FragmentSignUpProcessing next = FragmentSignUpProcessing.create(this);
            transaction.replace(R.id.auth_fragment_container, next);
            //transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void onRetrySignUp(View v) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        if (f instanceof FragmentSignUpProcessing) {
            ((FragmentSignUpProcessing) f).onSignUp();
        }
    }
}

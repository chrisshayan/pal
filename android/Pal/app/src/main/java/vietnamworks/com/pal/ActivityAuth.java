package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.ui.fragments.FragmentLogin;
import vietnamworks.com.pal.ui.fragments.FragmentSignUp;
import vietnamworks.com.pal.ui.fragments.FragmentSignUpProcessing;
import vietnamworks.com.pal.utils.Common;

public class ActivityAuth extends ActivityBase {
    FragmentSignUp mFragmentSignUp;
    FragmentLogin mFragmentLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FirebaseService.setContext(this);

        if (findViewById(R.id.auth_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of ExampleFragment
            FragmentSignUp firstFragment = FragmentSignUp.create(this);

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
                Toast.makeText(this.getBaseContext(),getString(R.string.login_validation_empty_email),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Common.isValidEmail(email)) {
                Toast.makeText(this.getBaseContext(), getString(R.string.login_validation_invalid_email_format),
                        Toast.LENGTH_SHORT).show();
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

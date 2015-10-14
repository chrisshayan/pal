package vietnamworks.com.pal.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseInstallation;

import vietnamworks.com.pal.AuthActivity;
import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.TaskListActivity;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.utils.Common;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentLogin extends Fragment {

    EditText mTxtEmail;
    EditText mTxtPassword;
    FloatingActionButton mBtnLogin;
    Button mBtnSignup;
    ProgressBar mProgressbar;
    Activity mRefActivity;

    ObjectAnimator loginButtonObjectAnimator;

    public FragmentLogin() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_login, container, false);

        mTxtEmail = ((EditText) rootView.findViewById(R.id.input_login_email));
        mTxtPassword = ((EditText) rootView.findViewById(R.id.input_login_password));
        mBtnLogin = ((FloatingActionButton) rootView.findViewById(R.id.btn_login));
        mBtnSignup = ((Button) rootView.findViewById(R.id.btn_signup));
        mProgressbar = ((ProgressBar) rootView.findViewById(R.id.login_progressBar));
        mProgressbar.setVisibility(View.INVISIBLE);

        loginButtonObjectAnimator = ObjectAnimator.ofFloat(mBtnLogin , "rotation", 0f, 360f);
        loginButtonObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        loginButtonObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);

        BaseActivity.applyFont(rootView);
        return rootView;
    }


    public static FragmentLogin create(Activity act) {
        FragmentLogin fragment = new FragmentLogin();
        fragment.mRefActivity = act;
        return fragment;
    }

    public String getEmail() {
        return this.mTxtEmail.getText().toString();
    }

    public String getPassword() {
        return this.mTxtPassword.getText().toString();
    }

    public void focusEmail() {
        this.mTxtEmail.requestFocus();
    }

    public void focusPassword() {
        this.mTxtPassword.requestFocus();
    }

    public void startProcessing() {
        this.mTxtEmail.setEnabled(false);
        this.mTxtPassword.setEnabled(false);
        this.mBtnLogin.setEnabled(false);
        this.mBtnSignup.setEnabled(false);
        this.mProgressbar.setVisibility(View.VISIBLE);

        loginButtonObjectAnimator.setDuration(1000);
        //loginButtonObjectAnimator.start();
    }

    public void endProcessing() {
        this.mTxtEmail.setEnabled(true);
        this.mTxtPassword.setEnabled(true);
        this.mBtnLogin.setEnabled(true);
        this.mBtnSignup.setEnabled(true);
        this.mProgressbar.setVisibility(View.INVISIBLE);
        this.mTxtPassword.setText("");

        //loginButtonObjectAnimator.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mRefActivity = null;
    }

    public void onLogin() {
        final String email = this.getEmail().trim();
        final String password = this.getPassword();

        if (email.length() == 0) {
            BaseActivity.toast(R.string.login_validation_empty_email);
            this.focusEmail();
            return;
        }

        if (!Common.isValidEmail(email)) {
            BaseActivity.toast(R.string.login_validation_invalid_email_format);
            this.focusEmail();
            return;
        }

        if (password.length() == 0) {
            BaseActivity.toast(R.string.login_validation_empty_password);
            this.focusPassword();
            return;
        }

        this.startProcessing();

        FirebaseService.login(email, password, new AsyncCallback() {
            @Override
            public void onSuccess(Context ctx, Object obj) {
                endProcessing();
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("user_id", FirebaseService.authData.getUid());
                installation.saveInBackground();
                onLoginSuccess();
            }

            @Override
            public void onError(Context ctx, int code, String message) {
                endProcessing();
                //onLoginFail(R.string.message_fail_to_connect_server);
                onLoginFail(code, message);
            }
        });
    }

    public void onLoginFail(int error, String  message) {
        BaseActivity.toast(message);
    }

    public void onLoginSuccess() {
        Intent intent = new Intent(mRefActivity, TaskListActivity.class);
        startActivity(intent);
    }

    public void onSignUp() {
        Intent intent = new Intent(mRefActivity, AuthActivity.class);
        startActivity(intent);
    }

    public void setActivity(Activity act) {
        this.mRefActivity = act;
    }
}

package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends Fragment {

    TextView mTxtEmail;
    TextView mTxtPassword;
    Button mBtnLogin;
    Button mBtnSignup;
    ProgressBar mProgressbar;

    public LoginFragment() {
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

        mTxtEmail = ((TextView) rootView.findViewById(R.id.input_login_email));
        mTxtPassword = ((TextView) rootView.findViewById(R.id.input_login_password));
        mBtnLogin = ((Button) rootView.findViewById(R.id.btn_login));
        mBtnSignup = ((Button) rootView.findViewById(R.id.btn_signup));
        mProgressbar = ((ProgressBar) rootView.findViewById(R.id.progressBar));
        return rootView;
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
    }

    public void endProcessing() {
        this.mTxtEmail.setEnabled(true);
        this.mTxtPassword.setEnabled(true);
        this.mBtnLogin.setEnabled(true);
        this.mBtnSignup.setEnabled(true);
        this.mProgressbar.setVisibility(View.INVISIBLE);
        this.mTxtPassword.setText("");
    }
}

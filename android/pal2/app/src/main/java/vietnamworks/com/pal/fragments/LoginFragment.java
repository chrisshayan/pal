package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.AuthActivity;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.LocalStorage;
import vietnamworks.com.pal.services.ParseService;

/**
 * Created by duynk on 10/26/15.
 */
public class LoginFragment extends BaseFragment {
    EditText txtPassword;
    EditText txtEmail;
    TextView txtError;
    View errorView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_login, container, false);

        BaseActivity.applyFont(rootView);

        txtEmail = (EditText)rootView.findViewById(R.id.email);
        txtPassword = (EditText)rootView.findViewById(R.id.password);

        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == R.id.action_sign_in || actionId == EditorInfo.IME_NULL)) {
                    if (event == null) {
                        login();
                    }
                    return true;
                }
                return false;
            }
        });

        txtError = (TextView) rootView.findViewById(R.id.error);

        errorView = rootView.findViewById(R.id.error_view);
        errorView.setVisibility(View.GONE);

        ((Button) rootView.findViewById(R.id.btn_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return rootView;
    }

    public void login() {
        final String email = getEmail().trim();
        final String password = getPassword().trim();
        if (email.length() == 0) {
            setError(getString(R.string.require_email));
            focusEmail();
        } else if (password.length() == 0) {
            setError(getString(R.string.require_password));
            focusPassword();
        } else if (!Utils.isValidEmail(email)) {
            setError(getString(R.string.invalid_email));
            focusEmail();
        } else {
            setError(null);
            ((AuthActivity)getActivity()).setState(AuthActivity.STATE_PROCESSING);
            FirebaseService.login(email, password, new AsyncCallback() {
                @Override
                public void onSuccess(Context ctx, Object obj) {
                    ParseService.RegisterUser(FirebaseService.authData.getUid());
                    BaseActivity.sInstance.openActivity (TimelineActivity.class);
                    LocalStorage.set(getString(R.string.local_storage_first_launch), false);
                }

                @Override
                public void onError(Context ctx, int code, String message) {
                    HashMap<String, Object> bundle = new HashMap<String, Object>();
                    bundle.put("message", getString(R.string.login_fail));
                    ((AuthActivity) getActivity()).setState(AuthActivity.STATE_REGISTER_ERROR, bundle);
                }
            });
        }
    }

    public void onLayoutChanged() {
        View view = this.getView();
        Activity act = getActivity();
        if (view != null && act != null) {
            Rect r = new Rect();
            act.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            view.animate().y((r.height() - view.getHeight()) >> 1).setDuration(100).start();
        }
    }

    public String getEmail() {
        return txtEmail.getText().toString();
    }

    public String getPassword() {
        return txtPassword.getText().toString();
    }

    public void focusEmail() {
        txtEmail.requestFocus();
    }

    public void focusPassword() {
        txtPassword.requestFocus();
    }

    public void setError(String message) {
        if (message != null && !message.isEmpty()) {
            txtError.setText(message);
            errorView.setVisibility(View.VISIBLE);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    public void resetForm() {
        txtPassword.setText("");
        txtEmail.setText("");
    }

}

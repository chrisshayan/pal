package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;
import vietnamworks.com.pal.services.ParseService;

/**
 * Created by duynk on 10/26/15.
 */
public class LoginFragment extends BaseFragment {
    EditText txtPassword;
    AutoCompleteTextView txtEmail;
    TextView txtError;
    View errorView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_login, container, false);

        BaseActivity.applyFont(rootView);

        txtEmail = (AutoCompleteTextView)rootView.findViewById(R.id.email);

        String emailList = LocalStorage.getString("email_history", "");
        String emailArray[] = emailList.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, emailArray);
        txtEmail.setAdapter(adapter);

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
        errorView.setVisibility(View.INVISIBLE);

        rootView.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        rootView.findViewById(R.id.btn_forget_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forget_password();
            }
        });

        return rootView;
    }

    public void forget_password() {
        GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_do_forget_password);
        final String email = getEmail().trim();
        if (email.length() == 0) {
            setError(getString(R.string.require_email));
            GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_missing_email);
            focusEmail();
        } else {
            setError(null);
            ((AuthActivity)getActivity()).setState(AuthActivity.STATE_PROCESSING);
            FirebaseService.resetPassword(email, new AsyncCallback() {
                @Override
                public void onSuccess(Context ctx, Object obj) {
                    HashMap<String, Object> bundle = new HashMap<String, Object>();
                    bundle.put("message", getString(R.string.reset_password_success));
                    bundle.put("allowShare", false);
                    ((AuthActivity) getActivity()).setState(AuthActivity.STATE_SUCCESS, bundle);
                }

                @Override
                public void onError(Context ctx, int error_code, String message) {
                    HashMap<String, Object> bundle = new HashMap<String, Object>();
                    bundle.put("message", getString(R.string.reset_password_success));
                    ((AuthActivity) getActivity()).setState(AuthActivity.STATE_ERROR, bundle);
                }
            });
        }
    }

    public void login() {
        GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_do_login);
        final String email = getEmail().trim();
        final String password = getPassword().trim();

        if (email.length() == 0) {
            setError(getString(R.string.require_email));
            GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_missing_email);
            focusEmail();
        } else if (password.length() == 0) {
            setError(getString(R.string.require_password));
            GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_missing_password);
            focusPassword();
        } else if (!Utils.isValidEmail(email)) {
            setError(getString(R.string.invalid_email));
            focusEmail();
            GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_invalid_email_format);
        } else {
            setError(null);
            ((AuthActivity)getActivity()).setState(AuthActivity.STATE_PROCESSING);
            FirebaseService.login(email, password, new AsyncCallback() {
                @Override
                public void onSuccess(Context ctx, Object obj) {
                    ParseService.registerUser(FirebaseService.getUid(), email);
                    BaseActivity.sInstance.openActivity(TimelineActivity.class);
                    LocalStorage.set(getString(R.string.local_storage_first_launch), false);
                    GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_login_success);
                    //save email
                    String emailList = LocalStorage.getString("email_history", "");
                    if (!emailList.contains(email)) {
                        emailList = emailList + "," + email;
                        LocalStorage.set("email_history", emailList);
                    }
                }

                @Override
                public void onError(Context ctx, int code, String message) {
                    HashMap<String, Object> bundle = new HashMap<String, Object>();
                    bundle.put("message", getString(R.string.login_fail));
                    ((AuthActivity) getActivity()).setState(AuthActivity.STATE_ERROR, bundle);
                    GaService.trackEvent(R.string.ga_cat_login, R.string.ga_event_login_fail);
                }
            });
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
            errorView.setVisibility(View.INVISIBLE);
        }
    }

    public void resetForm() {
        txtPassword.setText("");
        txtEmail.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        GaService.trackScreen(R.string.ga_screen_login);
    }
}

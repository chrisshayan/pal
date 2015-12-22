package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.AuthActivity;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.HttpService;
import vietnamworks.com.pal.services.LocalStorage;
import vietnamworks.com.pal.services.ParseService;

/**
 * Created by duynk on 10/26/15.
 */
public class RegisterFragment extends BaseFragment {
    EditText txtEmail, txtFullName, txtPassword, txtConfirmPassword;
    View errorView, loadingView;
    TextView errorMessage;
    Button btnRegister, btnCancel, btnDone;
    View registerStep1, registerStep2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_register, container, false);

        BaseActivity.applyFont(rootView);

        txtEmail = (EditText)rootView.findViewById(R.id.email);
        txtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == R.id.action_register || actionId == EditorInfo.IME_NULL)) {
                    if (event == null) {
                        doVerify();
                    }
                    return true;
                }
                return false;
            }
        });

        txtFullName = (EditText)rootView.findViewById(R.id.fullname);

        txtPassword = (EditText)rootView.findViewById(R.id.txt_password);
        txtConfirmPassword = (EditText)rootView.findViewById(R.id.txt_confirm_password);
        txtConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == R.id.action_create_account || actionId == EditorInfo.IME_NULL)) {
                    if (event == null) {
                        doRegister();
                    }
                    return true;
                }
                return false;
            }
        });


        btnCancel = (Button)rootView.findViewById(R.id.btn_cancel);

        btnRegister = (Button)rootView.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doVerify();
            }
        });

        btnDone = (Button)rootView.findViewById(R.id.btn_done);
        btnDone.setVisibility(View.GONE);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });

        errorMessage = (TextView)rootView.findViewById(R.id.error_msg);
        errorView = rootView.findViewById(R.id.errorview);
        loadingView = rootView.findViewById(R.id.loading);

        registerStep1 = rootView.findViewById(R.id.register_step1);
        registerStep2 = rootView.findViewById(R.id.register_step2);
        registerStep1.setVisibility(View.VISIBLE);
        registerStep2.setVisibility(View.GONE);

        stopLoading();
        return rootView;
    }

    public String getEmail() {
        return txtEmail.getText().toString();
    }

    public String getPassword() {
        return txtPassword.getText().toString();
    }

    public String getConfirmPassword() {
        return txtConfirmPassword.getText().toString();
    }

    public String getFullName() {
        return txtFullName.getText().toString();
    }

    public void focusEmail() {
        txtEmail.requestFocus();
    }

    public void focusFullName() {
        txtFullName.requestFocus();
    }

    public void focusPassword() {
        txtPassword.requestFocus();
    }

    public void focusConfirmPassword() {
        txtConfirmPassword.requestFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        openStep1();
        txtEmail.setText("");
        txtFullName.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");

        GaService.trackScreen(R.string.ga_screen_register);
    }

    private void doVerify() {
        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_request_invite);
        final String email = getEmail().trim();
        final String fullname = getFullName().trim();
        if (fullname.length() == 0) {
            showError(R.string.require_full_name);
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_missing_fullname);
            focusFullName();
        } else if (email.length() == 0) {
            showError(R.string.require_email);
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_missing_email);
            focusEmail();
        } else if (!Utils.isValidEmail(email)) {
            showError(R.string.invalid_email);
            focusEmail();
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_invalid_email_format);
        } else if (!FirebaseService.isConnected()) {
            BaseActivity.toast(R.string.no_internet);
        } else {
            startLoading();
            HashMap<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("fullname", fullname);

            HttpService.Post(getContext(), "verify-new-account", data, new AsyncCallback() {
                @Override
                public void onSuccess(Context ctx, Object obj) {
                    JSONObject json = (JSONObject) obj;
                    try {
                        int result = json.getInt("result");
                        if (result == 0) {//domain failed
                            HashMap<String, Object> bundle = new HashMap<String, Object>();
                            bundle.put("message", getString(R.string.register_thank));
                            ((AuthActivity)getActivity()).setState(AuthActivity.STATE_SUCCESS, bundle);
                        } else if (result == 1) { //this account is valid
                            openStep2();
                        } else if (result == 2) { //already existed
                            showError(R.string.register_dup_email);
                        }
                        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_success);
                    } catch (Exception E) {
                        showError(R.string.register_fail);
                    }
                }

                @Override
                public void onError(Context ctx, int error_code, String message) {
                    GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_fail);
                    showError(R.string.register_fail);
                }
            });
        }
    }

    private void doRegister() {
        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_request_invite);
        final String email = getEmail().trim();
        final String fullname = getFullName().trim();
        final String password = getPassword();
        final String confirmPassword = getConfirmPassword();
        if (password.length() == 0) {
            showError(R.string.require_password);
            focusPassword();
        } else if (confirmPassword.length() == 0) {
            showError(R.string.require_confirm_password);
            focusConfirmPassword();
        } else if (!password.equals(confirmPassword)) {
            showError(R.string.password_not_matched);
        } else if (!FirebaseService.isConnected()) {
            BaseActivity.toast(R.string.no_internet);
        } else {
            startLoading();
            HashMap<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("fullname", fullname);
            data.put("password", password);

            HttpService.Post(getContext(), "register", data, new AsyncCallback() {
                @Override
                public void onSuccess(Context ctx, Object obj) {
                    JSONObject json = (JSONObject) obj;
                    try {
                        int result = json.getInt("result");
                        if (result == 0) {//saved domain failed
                            HashMap<String, Object> bundle = new HashMap<String, Object>();
                            bundle.put("message", getString(R.string.register_thank));
                            ((AuthActivity)getActivity()).setState(AuthActivity.STATE_SUCCESS, bundle);
                        } else { //ok
                            FirebaseService.login(getEmail(), getPassword(), new AsyncCallback() {
                                @Override
                                public void onSuccess(Context ctx, Object obj) {
                                    LocalStorage.set(R.string.ls_last_login, Utils.getMillis());
                                    ParseService.registerUser(FirebaseService.getUid(), email);
                                    LocalStorage.set(R.string.ls_first_launch, false);
                                    //save email
                                    String emailList = LocalStorage.getString(R.string.ls_email_history, "");
                                    if (!emailList.contains(email)) {
                                        emailList = emailList + "," + email;
                                        LocalStorage.set(R.string.ls_email_history, emailList);
                                    }

                                    BaseActivity.sInstance.openActivity(TimelineActivity.class);
                                }

                                @Override
                                public void onError(Context ctx, int error_code, String message) {
                                    BaseActivity.toast(R.string.oops);
                                    ((AuthActivity) getActivity()).setState(AuthActivity.STATE_LOGIN);
                                }
                            });
                        }
                        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_success);
                    } catch (Exception E) {
                        showError(R.string.register_fail);
                        openStep1();
                    }
                }

                @Override
                public void onError(Context ctx, int error_code, String message) {
                    GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_fail);
                    if (error_code == 409) { //dup
                        showError(R.string.register_dup_email);
                        openStep1();
                    } else {
                        showError(R.string.register_fail);
                        openStep1();
                    }
                }
            });
        }
    }

    private void showError(int error) {
        showError(getString(error));
        stopLoading();
    }

    private void showError(String error) {
        if (error != null) {
            errorView.setVisibility(View.VISIBLE);
            errorMessage.setText(error);
        } else {
            errorView.setVisibility(View.INVISIBLE);
            errorMessage.setText("");
        }
    }

    private void startLoading() {
        showError(null);
        BaseActivity.sInstance.hideKeyboard();
        txtEmail.setEnabled(false);
        txtFullName.setEnabled(false);
        btnRegister.setEnabled(false);
        btnCancel.setEnabled(false);
        loadingView.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        txtEmail.setEnabled(true);
        txtFullName.setEnabled(true);
        btnRegister.setEnabled(true);
        btnCancel.setEnabled(true);
        loadingView.setVisibility(View.INVISIBLE);
    }

    private void openStep2(){
        stopLoading();
        showError(null);
        btnRegister.setVisibility(View.GONE);
        registerStep1.setVisibility(View.GONE);

        registerStep2.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);

        txtPassword.requestFocus();
    }

    private void openStep1(){
        btnRegister.setVisibility(View.VISIBLE);
        registerStep1.setVisibility(View.VISIBLE);

        registerStep2.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
        txtPassword.setText("");
        txtConfirmPassword.setText("");

        showError(null);
    }
}

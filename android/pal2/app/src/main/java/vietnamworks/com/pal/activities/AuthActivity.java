package vietnamworks.com.pal.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.fragments.AuthProcessingFragment;
import vietnamworks.com.pal.fragments.LoginFragment;
import vietnamworks.com.pal.fragments.RegisterErrorFragment;
import vietnamworks.com.pal.fragments.RegisterFragment;
import vietnamworks.com.pal.fragments.RegisterSuccessFragment;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.HttpService;

/**
 * Created by duynk on 10/26/15.
 */
public class AuthActivity extends BaseActivity {
    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    RegisterSuccessFragment registerSuccessFragment;
    RegisterErrorFragment registerErrorFragment;
    AuthProcessingFragment authProcessingFragment;

    final public static int STATE_LOGIN = 0;
    final public static int STATE_REGISTER = 1;
    final public static int STATE_SUCCESS = 2;
    final public static int STATE_ERROR = 3;
    final public static int STATE_PROCESSING = 4;
    int state = -999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginFragment = (LoginFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        registerFragment = (RegisterFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register);
        registerSuccessFragment = (RegisterSuccessFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register_success);
        registerErrorFragment = (RegisterErrorFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register_error);
        authProcessingFragment = (AuthProcessingFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_auth_processing);
        setState(STATE_LOGIN);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onLayoutChanged(final Rect r, final boolean isKBShown) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                View v =  findViewById(R.id.app_title);
                if (isKBShown) {
                    v.setVisibility(View.GONE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setSetFragmentVisibility() {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                try {
                    loginFragment.getView().setVisibility(state == STATE_LOGIN ? View.VISIBLE : View.GONE);
                    registerFragment.getView().setVisibility(state == STATE_REGISTER ? View.VISIBLE : View.GONE);
                    registerSuccessFragment.getView().setVisibility(state == STATE_SUCCESS ? View.VISIBLE : View.GONE);
                    registerErrorFragment.getView().setVisibility(state == STATE_ERROR ? View.VISIBLE : View.GONE);
                    authProcessingFragment.getView().setVisibility(state == STATE_PROCESSING ? View.VISIBLE : View.GONE);
                } catch (Exception E) {

                }
            }
        });
    }

    private Animator.AnimatorListener stateTransitionAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setSetFragmentVisibility();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public void setState(final int _state) {
        setState(_state, null);
    }

    public void setState(final int _state, final HashMap<String, Object> ext) {
        final int last_state = this.state;
        this.state = _state;
        setTimeout(new Runnable() {
            @Override
            public void run() {
                try {
                    if (last_state == STATE_LOGIN) {
                        loginFragment.getView().animate().alpha(0).setDuration(100).start();
                    } else if (last_state == STATE_REGISTER) {
                        registerFragment.getView().animate().alpha(0).setDuration(100).start();
                    } else if (last_state == STATE_SUCCESS) {
                        registerSuccessFragment.getView().animate().setDuration(100).alpha(0).start();
                    } else if (last_state == STATE_ERROR) {
                        registerErrorFragment.getView().animate().setDuration(100).alpha(0).start();
                    } else if (last_state == STATE_PROCESSING) {
                        authProcessingFragment.getView().animate().setDuration(100).alpha(0).start();
                    } else {
                        setSetFragmentVisibility();
                    }
                    if (_state == STATE_LOGIN) {
                        loginFragment.getView().animate().setDuration(100).alpha(1).setListener(stateTransitionAnimationListener).start();
                    } else if (_state == STATE_REGISTER) {
                        registerFragment.getView().animate().setDuration(100).alpha(1).setListener(stateTransitionAnimationListener).start();
                    } else if (_state == STATE_SUCCESS) {
                        if (ext != null) {
                            if (ext.containsKey("message")) {
                                registerSuccessFragment.setMessage(ext.get("message").toString());
                            } else {
                                registerSuccessFragment.setMessage(R.string.register_thank);
                            }
                            if (ext.containsKey("allowShare")) {
                                registerSuccessFragment.setButtonShareVisible( (boolean)ext.get("allowShare"));
                            } else {
                                registerSuccessFragment.setButtonShareVisible(true);
                            }
                        }
                        registerSuccessFragment.getView().animate().setDuration(100).alpha(1).setListener(stateTransitionAnimationListener).start();
                    } else if (_state == STATE_ERROR) {
                        if (ext != null) {
                            if (ext.containsKey("message")) {
                                registerErrorFragment.setError(ext.get("message").toString());
                            } else {
                                registerErrorFragment.setError(R.string.register_fail);
                            }
                        }
                        registerErrorFragment.getView().animate().setDuration(100).alpha(1).setListener(stateTransitionAnimationListener).start();
                    } else if (_state == STATE_PROCESSING) {
                        hideKeyboard();
                        authProcessingFragment.getView().animate().setDuration(100).alpha(1).setListener(stateTransitionAnimationListener).start();
                    }
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }
        });
    }

    public void onOpenRequestInvite(View v) {
        setState(STATE_REGISTER);
    }

    public void onExecuteRequestInvite(View v) {
        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_request_invite);
        final String email = registerFragment.getEmail().trim();
        final String fullname = registerFragment.getFullName().trim();
        if (fullname.length() == 0) {
            toast(R.string.require_full_name);
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_missing_fullname);
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    registerFragment.focusFullName();
                }
            });
        } else if (email.length() == 0) {
            toast(R.string.require_email);
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_missing_email);
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    registerFragment.focusEmail();
                }
            });
        } else if (!Utils.isValidEmail(email)) {
            toast(R.string.invalid_email);
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    registerFragment.focusEmail();
                }
            });
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_invalid_email_format);
        } else if (!FirebaseService.isConnected()) {
            toast(R.string.no_internet);
        } else {
            setState(STATE_PROCESSING);
            HashMap<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("fullname", fullname);

            HttpService.Post(getBaseContext(), "register", data, new AsyncCallback() {
                @Override
                public void onSuccess(Context ctx, Object obj) {
                    JSONObject json = (JSONObject) obj;
                    try {
                        int result = json.getInt("result");
                        if (result == 0) {//saved
                            HashMap<String, Object> bundle = new HashMap<String, Object>();
                            bundle.put("message", getString(R.string.register_thank));
                            setState(STATE_SUCCESS, bundle);
                            registerFragment.resetForm();
                        } else {
                            HashMap<String, Object> bundle = new HashMap<String, Object>();
                            bundle.put("message", getString(R.string.register_success));
                            setState(STATE_SUCCESS, bundle);
                            registerFragment.resetForm();
                        }
                        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_success);
                    } catch (Exception E) {
                        HashMap<String, Object> bundle = new HashMap<String, Object>();
                        bundle.put("message", getString(R.string.register_fail));
                        setState(STATE_ERROR, bundle);
                        registerFragment.resetForm();
                    }
                }

                @Override
                public void onError(Context ctx, int error_code, String message) {
                    GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_fail);
                    if (error_code == 409) { //dup
                        HashMap<String, Object> bundle = new HashMap<String, Object>();
                        bundle.put("message", getString(R.string.register_dup_email));
                        setState(STATE_ERROR, bundle);
                        registerFragment.resetForm();
                    } else {
                        HashMap<String, Object> bundle = new HashMap<String, Object>();
                        bundle.put("message", getString(R.string.register_fail));
                        setState(STATE_ERROR, bundle);
                        registerFragment.resetForm();
                    }
                }
            });
        }
    }


    public void onOpenLogin(View v) {
        setState(STATE_LOGIN);
        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_back_to_login);
    }

    public  void onCloseRegisterResultDialog(View v) {
        setState(STATE_LOGIN);
        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_close_register_result);
    }

    public void onShare(View v) {
        GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_share);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

}

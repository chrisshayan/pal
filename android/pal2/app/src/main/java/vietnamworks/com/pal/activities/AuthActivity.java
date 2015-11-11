package vietnamworks.com.pal.activities;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.Arrays;
import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.fragments.AuthProcessingFragment;
import vietnamworks.com.pal.fragments.LoginFragment;
import vietnamworks.com.pal.fragments.RegisterErrorFragment;
import vietnamworks.com.pal.fragments.RegisterFragment;
import vietnamworks.com.pal.fragments.RegisterSuccessFragment;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;

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
    final public static int STATE_REGISTER_SUCCESS = 2;
    final public static int STATE_REGISTER_ERROR = 3;
    final public static int STATE_PROCESSING = 4;
    int state = -999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        applyFont(findViewById(R.id.app_title), Bubblegum);

        loginFragment = (LoginFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        registerFragment = (RegisterFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register);
        registerSuccessFragment = (RegisterSuccessFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register_success);
        registerErrorFragment = (RegisterErrorFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register_error);
        authProcessingFragment = (AuthProcessingFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_auth_processing);
        setState(STATE_LOGIN);
    }

    @Override
    public void onLayoutChanged(Rect r, final boolean isSoftKeyShown) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                View v =  findViewById(R.id.app_title);
                float offset_y = v.getY();
                v.animate()
                        .scaleX(isSoftKeyShown ? 0.5f : 1.0f)
                        .scaleY(isSoftKeyShown ? 0.5f : 1.0f)
                        .translationY(isSoftKeyShown ? -offset_y/2 : 0f)
                        .setDuration(100).start();
            }
        });
        loginFragment.onLayoutChanged();
        registerFragment.onLayoutChanged();
    }

    private void setSetFragmentVisibility() {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                try {
                    loginFragment.getView().setVisibility(state == STATE_LOGIN ? View.VISIBLE : View.INVISIBLE);
                    registerFragment.getView().setVisibility(state == STATE_REGISTER ? View.VISIBLE : View.INVISIBLE);
                    registerSuccessFragment.getView().setVisibility(state == STATE_REGISTER_SUCCESS ? View.VISIBLE : View.INVISIBLE);
                    registerErrorFragment.getView().setVisibility(state == STATE_REGISTER_ERROR ? View.VISIBLE : View.INVISIBLE);
                    authProcessingFragment.getView().setVisibility(state == STATE_PROCESSING ? View.VISIBLE : View.INVISIBLE);
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
                    } else if (last_state == STATE_REGISTER_SUCCESS) {
                        registerSuccessFragment.getView().animate().setDuration(100).alpha(0).start();
                    } else if (last_state == STATE_REGISTER_ERROR) {
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
                    } else if (_state == STATE_REGISTER_SUCCESS) {
                        registerSuccessFragment.getView().animate().setDuration(100).alpha(1).setListener(stateTransitionAnimationListener).start();
                    } else if (_state == STATE_REGISTER_ERROR) {
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
        if (email.length() == 0) {
            toast(R.string.require_email);
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_missing_email);
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    registerFragment.focusEmail();
                }
            });
        } else if (fullname.length() == 0) {
            toast(R.string.require_full_name);
            GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_missing_fullname);
            setTimeout(new Runnable() {
                @Override
                public void run() {
                    registerFragment.focusFullName();
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
        } else {
            setState(STATE_PROCESSING);
            FirebaseService.newRef().authWithCustomToken(FirebaseService.getDefaultToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    final String email_hash = Utils.hash(email);
                    FirebaseService.newRef(Arrays.asList("register", email_hash)).runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData currentData) {
                            if (currentData.getValue() == null) {
                                HashMap<String, Object> data = new HashMap<String, Object>();
                                data.put("email", email);
                                data.put("name", fullname);
                                data.put("created_date", Utils.getMillis());
                                data.put("last_modified_date", Utils.getMillis());
                                data.put("hit", 1);
                                currentData.setValue(data);
                            } else {
                                HashMap<String, Object> data = currentData.getValue(HashMap.class);
                                long last_hit = ((Long)data.get("hit")).longValue();
                                data.put("name", fullname);
                                data.put("hit", last_hit + 1);
                                data.put("last_modified_date", Utils.getMillis());
                                currentData.setValue(data);
                            }
                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                setState(STATE_REGISTER_SUCCESS);
                                GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_success);
                            } else {
                                setState(STATE_REGISTER_ERROR);
                                GaService.trackEvent(R.string.ga_cat_register, R.string.ga_event_register_fail);
                            }
                            registerFragment.resetForm();
                        }
                    });
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    setState(STATE_REGISTER_ERROR);
                    registerFragment.resetForm();
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

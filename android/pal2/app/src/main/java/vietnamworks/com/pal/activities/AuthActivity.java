package vietnamworks.com.pal.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.fragments.LoginFragment;
import vietnamworks.com.pal.fragments.RegisterFragment;
import vietnamworks.com.pal.fragments.RegisterSuccessFragment;
import vietnamworks.com.pal.services.GaService;

/**
 * Created by duynk on 10/26/15.
 */
public class AuthActivity extends BaseActivity {
    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    RegisterSuccessFragment registerSuccessFragment;

    final public static int STATE_LOGIN = 0;
    final public static int STATE_REGISTER = 1;
    final public static int STATE_SUCCESS = 2;
    //final public static int STATE_ERROR = 3;
    //final public static int STATE_PROCESSING = 4;
    int state = -999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setState(STATE_LOGIN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private LoginFragment getLoginFragment() {
        if (loginFragment == null) {
            loginFragment = new LoginFragment();
        }
        return loginFragment;
    }

    private RegisterFragment getRegisterFragment() {
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        return registerFragment;
    }

    private RegisterSuccessFragment getRegisterSuccessFragment() {
        if (registerSuccessFragment == null) {
            registerSuccessFragment = new RegisterSuccessFragment();
        }
        return registerSuccessFragment;
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
                    if (_state == STATE_LOGIN) {
                        openFragmentAndClean(getLoginFragment(), R.id.fragment_holder);
                    } else if (_state == STATE_REGISTER) {
                        openFragmentAndClean(getRegisterFragment(), R.id.fragment_holder);
                    } else if (_state == STATE_SUCCESS) {
                        if (ext != null) {
                            openFragment(getRegisterSuccessFragment(), R.id.fragment_holder);
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

    public void doNothing(View v) {}

}

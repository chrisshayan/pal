package vietnamworks.com.pal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.fragments.BaseFragment;

/**
 * Created by duynk on 10/26/15.
 */
public class AuthActivity extends BaseActivity {
    BaseFragment loginFragment, registerFragment, registerSuccessFragment;

    final static int STATE_LOGIN = 0;
    final static int STATE_REGISTER = 1;
    final static int STATE_REGISTER_SUCCESS = 2;
    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);;

        applyFont((TextView) findViewById(R.id.app_title), Bubblegum);

        loginFragment = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_login);
        registerFragment = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register);
        registerSuccessFragment = (BaseFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_register_success);

        setState(STATE_LOGIN);
    }

    public void setState(final int _state) {
        this.state = _state;
        setTimeout(new Runnable() {
            @Override
            public void run() {
                try {
                    loginFragment.getView().setVisibility(state == STATE_LOGIN ? View.VISIBLE : View.INVISIBLE);
                    registerFragment.getView().setVisibility(state == STATE_REGISTER ? View.VISIBLE : View.INVISIBLE);
                    registerSuccessFragment.getView().setVisibility(state == STATE_REGISTER_SUCCESS ? View.VISIBLE : View.INVISIBLE);
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
        setState(STATE_REGISTER_SUCCESS);
    }

    public void onOpenLogin(View v) {
        setState(STATE_LOGIN);
    }

    public  void onCloseRegisterSuccessDialog(View v) {
        setState(STATE_LOGIN);
    }

    public void onShare(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

}

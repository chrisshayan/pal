package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.utils.Common;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSignUpProcessing extends Fragment {
    Activity mRefActivity;
    TextView mLabelMessage;
    ProgressBar mProcessingBar;
    Button mTryAgain;

    public FragmentSignUpProcessing() {
    }

    public static FragmentSignUpProcessing create(Activity act) {
        FragmentSignUpProcessing fragment = new FragmentSignUpProcessing();
        fragment.mRefActivity = act;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_signup_process, container, false);

        mLabelMessage = ((TextView) rootView.findViewById(R.id.signup_message));
        mProcessingBar = ((ProgressBar) rootView.findViewById(R.id.signup_progress_bar));
        mTryAgain = ((Button) rootView.findViewById(R.id.btn_try_again));
        mTryAgain.setVisibility(View.INVISIBLE);

        onSignUp();

        BaseActivity.applyFont(rootView);
        return rootView;
    }

    public void setActivity(Activity act) {
        this.mRefActivity = act;
    }

    private void startProcessing() {
        mLabelMessage.setText(getString(R.string.message_precessing));
        mProcessingBar.setVisibility(View.VISIBLE);
        mTryAgain.setVisibility(View.INVISIBLE);
    }

    private void endProcessing(int id) {
        mLabelMessage.setText(getString(id));
        mProcessingBar.setVisibility(View.INVISIBLE);
    }

    private void onSignUpSuccess() {
        mTryAgain.setVisibility(View.INVISIBLE);
        endProcessing(R.string.signup_queueing);
    }

    private void onSignUpFailed(int id) {
        endProcessing(id);
        mTryAgain.setVisibility(View.VISIBLE);
    }

    public void onSignUp() {
        startProcessing();
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int tmp = Common.randomInt(0, 100);
                if (tmp < 75) {
                    onSignUpFailed(R.string.message_fail_to_connect_server);
                } else {
                    onSignUpSuccess();
                }
            }
        }, 3000L);
    }
}

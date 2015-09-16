package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by duynk on 9/16/15.
 */
public class RecorderFragment extends Fragment {
    MainActivity mRefActivity;

    public final static int BTN_RECORDER_STATE__INIT = 0;
    public final static int BTN_RECORDER_STATE__RECORDING = 1;
    public final static int BTN_RECORDER_STATE__END = 2;
    private int mButtonRecorderState = -1;

    public String mTitle;

    private TextView mLbTitle;
    private TextView mLbMessage;
    ViewGroup mBtnOkGroup;
    ViewGroup mBtnCancelGroup;

    public RecorderFragment() {
    }

    public static RecorderFragment create(MainActivity act, String title) {
        RecorderFragment fragment = new RecorderFragment();
        fragment.mRefActivity = act;
        fragment.mTitle = title;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_recorder, container, false);

        mLbTitle = ((TextView) rootView.findViewById(R.id.lb_recorder_title));
        mLbMessage = ((TextView) rootView.findViewById(R.id.lb_recorder_message));
        mBtnOkGroup = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_btn_ok));
        mBtnCancelGroup = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_btn_cancel));

        mLbTitle.setText(mTitle);

        mRefActivity.getSupportActionBar().hide();


        mButtonRecorderState = -1;
        setRecorderState(BTN_RECORDER_STATE__INIT);

        return rootView;
    }

    private void onStartRecording() {
        this.setRecorderState(BTN_RECORDER_STATE__RECORDING);
    }

    private void onStopRecording() {
        this.setRecorderState(BTN_RECORDER_STATE__END);
    }

    public void onToggleRecorder() {
        if (mButtonRecorderState == BTN_RECORDER_STATE__RECORDING) {
            this.onStopRecording();
        } else {
            this.onStartRecording();
        }
    }

    private void setRecorderState(int state) {
        if (state != mButtonRecorderState) {
            mButtonRecorderState = state;
            switch (state) {
                case BTN_RECORDER_STATE__INIT:
                    mLbMessage.setText(getResources().getStringArray(R.array.recorders_message)[state]);
                    mBtnOkGroup.setVisibility(View.GONE);
                    mBtnCancelGroup.setVisibility(View.VISIBLE);
                    break;
                case BTN_RECORDER_STATE__END:
                    mLbMessage.setText(getResources().getStringArray(R.array.recorders_message)[state]);
                    mBtnOkGroup.setVisibility(View.VISIBLE);
                    mBtnCancelGroup.setVisibility(View.VISIBLE);
                    break;
                case BTN_RECORDER_STATE__RECORDING:
                    mLbMessage.setText(getResources().getStringArray(R.array.recorders_message)[state]);
                    mBtnOkGroup.setVisibility(View.INVISIBLE);
                    mBtnCancelGroup.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}

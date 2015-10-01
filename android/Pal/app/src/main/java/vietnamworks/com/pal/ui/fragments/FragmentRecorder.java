package vietnamworks.com.pal.ui.fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import vietnamworks.com.pal.ActivityMain;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 9/16/15.
 */
public class FragmentRecorder extends Fragment {
    ActivityMain mRefActivity;

    public final static int BTN_RECORDER_STATE__INIT = 0;
    public final static int BTN_RECORDER_STATE__RECORDING = 1;
    public final static int BTN_RECORDER_STATE__END = 2;
    private int mButtonRecorderState = -1;

    private MediaRecorder myAudioRecorder;
    private MediaPlayer   mPlayer = null;
    private String outputFile = null;

    public String mTitle;
    private TextView mLbMessage;
    ViewGroup mBtnOkGroup;
    ViewGroup mBtnCancelGroup;
    ViewGroup mBtnPlayGroup;
    ImageView mBtnPlayIcon;

    public FragmentRecorder() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";;
    }

    public static FragmentRecorder create(ActivityMain act, String title) {
        FragmentRecorder fragment = new FragmentRecorder();
        fragment.mRefActivity = act;
        fragment.mTitle = title;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_recorder, container, false);

        ((TextView) rootView.findViewById(R.id.lb_recorder_title)).setText(mTitle);
        mLbMessage = ((TextView) rootView.findViewById(R.id.lb_recorder_message));
        mBtnOkGroup = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_btn_ok));
        mBtnCancelGroup = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_btn_cancel));
        mBtnPlayGroup = ((ViewGroup) rootView.findViewById(R.id.ui_recorder_btn_play));
        mBtnPlayIcon = ((ImageView) rootView.findViewById(R.id.btn_replay_icon));
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

    public void onReplay() {
        if (mPlayer == null) { //not playing
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                    mBtnPlayIcon.setImageResource(R.drawable.ic_play);
                }
            });
            try {
                mPlayer.setDataSource(outputFile);
                mPlayer.prepare();
                mPlayer.start();
                mBtnPlayIcon.setImageResource(R.drawable.ic_stop);
            } catch (IOException e) {
                e.printStackTrace();
                mPlayer = null;
            }
        } else {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mBtnPlayIcon.setImageResource(R.drawable.ic_play);
        }
    }

    public void stopPlayer() {
        if (mPlayer != null ) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            mBtnPlayIcon.setImageResource(R.drawable.ic_play);
        }
    }

    private void setRecorderState(int state) {
        if (state != mButtonRecorderState) {
            mButtonRecorderState = state;
            switch (state) {
                case BTN_RECORDER_STATE__INIT:
                    mLbMessage.setText(getResources().getStringArray(R.array.recorders_message)[state]);
                    mBtnOkGroup.setVisibility(View.GONE);
                    mBtnPlayGroup.setVisibility(View.GONE);
                    mBtnCancelGroup.setVisibility(View.VISIBLE);
                    break;
                case BTN_RECORDER_STATE__END:
                    mLbMessage.setText(getResources().getStringArray(R.array.recorders_message)[state]);
                    mBtnOkGroup.setVisibility(View.VISIBLE);
                    mBtnCancelGroup.setVisibility(View.VISIBLE);
                    mBtnPlayGroup.setVisibility(View.VISIBLE);

                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;

                    break;
                case BTN_RECORDER_STATE__RECORDING:
                    stopPlayer();
                    mLbMessage.setText(getResources().getStringArray(R.array.recorders_message)[state]);
                    mBtnOkGroup.setVisibility(View.INVISIBLE);
                    mBtnCancelGroup.setVisibility(View.INVISIBLE);
                    mBtnPlayGroup.setVisibility(View.INVISIBLE);

                    myAudioRecorder=new MediaRecorder();
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    myAudioRecorder.setOutputFile(outputFile);
                    try {
                        myAudioRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myAudioRecorder.start();

                    break;
            }
        }
    }
}

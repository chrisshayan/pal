package vietnamworks.com.pal.fragments;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/7/15.
 */
public class FragmentRecording extends FragmentBase {
    public final static int STATE_EMPTY = 0;
    public final static int STATE_RECORDING = 1;
    public final static int STATE_PLAYING = 2;
    public final static int STATE_IDLE = 3;

    private android.os.Handler handler = new android.os.Handler();
    ImageButton btnRecord, btnPlay;
    TextView recordingStatus;

    private MediaRecorder myAudioRecorder;
    private MediaPlayer mPlayer = null;

    Timer recorderTimer;
    int recorderTimerCounter = 0;

    int state = STATE_EMPTY;
    public FragmentRecording() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_recording, container, false);
        btnRecord = (ImageButton)rootView.findViewById(R.id.btn_record);
        btnPlay = (ImageButton)rootView.findViewById(R.id.btn_play);
        recordingStatus = (TextView)rootView.findViewById(R.id.recording_status);
        setState(STATE_EMPTY);

        BaseActivity.applyFont(rootView);
        return rootView;
    }

    public void reset() {
        stopPlayer();
        stopRecording();
        setState(STATE_EMPTY);
    }

    public void setState(int state) {
        this.state = state;
        if (state == STATE_EMPTY) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setImageResource(R.drawable.ic_microphone_red);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.INVISIBLE);
                    recordingStatus.setVisibility(View.GONE);
                }
            });
        } else if (state == STATE_RECORDING) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setImageResource(R.drawable.ic_stop_blue);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.INVISIBLE);
                    recordingStatus.setVisibility(View.VISIBLE);
                }
            });
        } else if (state == STATE_IDLE) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setImageResource(R.drawable.ic_microphone_red);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnPlay.setImageResource(R.drawable.ic_play_blue);
                    btnPlay.setVisibility(View.VISIBLE);
                    recordingStatus.setVisibility(View.GONE);
                }
            });
        } else if (state == STATE_PLAYING) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setImageResource(R.drawable.ic_microphone_red);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnPlay.setImageResource(R.drawable.ic_stop_blue);
                    btnPlay.setVisibility(View.VISIBLE);
                    recordingStatus.setVisibility(View.GONE);
                }
            });
        }
    }

    public void toggleRecording() {
        if (this.state != STATE_RECORDING) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void toggleReplay() {
        if (this.state != STATE_PLAYING) {
            if (startPlayer()) {
                this.setState(STATE_PLAYING);
            } else {
                this.setState(STATE_IDLE);
            }
        } else {
            this.setState(STATE_IDLE);
            stopPlayer();
        }
    }

    private void startRecording() {
        this.stopPlayer();
        this.setState(STATE_RECORDING);
        Common.newSampleRecord();

        if (myAudioRecorder != null) {
            myAudioRecorder.stop();
            myAudioRecorder.reset();
            myAudioRecorder.release();
            myAudioRecorder = null;
        }

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(Common.getSampleRecordPath());
        recorderTimerCounter = 0;

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();

            myAudioRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    if (recorderTimer != null) {
                        recorderTimer.cancel();
                        recorderTimer.purge();
                        recorderTimer = null;
                    }
                    myAudioRecorder = null;
                    setState(STATE_EMPTY);
                }
            });

            if (recorderTimer != null) {
                recorderTimer.cancel();
                recorderTimer.purge();
                recorderTimer = null;
            }

            recorderTimer = new Timer();
            recorderTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int minute = (int) recorderTimerCounter / 60;
                    int sec = recorderTimerCounter % 60;
                    String _m = (int) (minute / 10) + "" + (int) (minute % 10);
                    String _s = (int) (sec / 10) + "" + (int) (sec % 10);
                    final String timer = _m + ":" + _s;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recordingStatus.setText(timer);
                        }});
                    recorderTimerCounter++;
                }
            },0 , 1000);

        }catch (Exception e) {
            myAudioRecorder = null;
            e.printStackTrace();
            this.setState(STATE_EMPTY);
        }
    }

    private void stopRecording() {
        if (recorderTimer != null) {
            recorderTimer.cancel();
            recorderTimer.purge();
            recorderTimer = null;
        }
        try {
            if (myAudioRecorder != null) {
                myAudioRecorder.stop();
                myAudioRecorder.reset();
                myAudioRecorder.release();
                myAudioRecorder = null;
            }
            this.setState(STATE_IDLE);
        }catch (Exception E) {
            E.printStackTrace();
            this.setState(STATE_EMPTY);
        }
    }

    private void stopPlayer() {
        if (mPlayer != null ) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private boolean startPlayer() {
        if (mPlayer == null) { //not playing
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                    setState(STATE_IDLE);
                }
            });
            try {
                mPlayer.setDataSource(Common.getSampleRecordPath());
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp == mPlayer) {
                            mp.start();
                        }
                    }
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                mPlayer = null;
                return false;
            }
        } else {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            return false;
        }
    }
}
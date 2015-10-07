package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import vietnamworks.com.pal.R;

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

        setState(STATE_EMPTY);
        return rootView;
    }

    public void setState(int state) {
        if (state == STATE_EMPTY) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setImageResource(R.drawable.ic_microphone_red);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.INVISIBLE);
                }
            });
        } else if (state == STATE_RECORDING) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    btnRecord.setImageResource(R.drawable.ic_stop_blue);
                    btnRecord.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.INVISIBLE);
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
                }
            });
        }
        this.state = state;
    }

    public void toggleRecording() {
        if (this.state != STATE_RECORDING) {
            this.setState(STATE_RECORDING);
        } else {
            this.setState(STATE_IDLE);
        }
    }

    public void toggleReply() {
        if (this.state != STATE_PLAYING) {
            this.setState(STATE_PLAYING);
        } else {
            this.setState(STATE_IDLE);
        }
    }
}
package vietnamworks.com.pal.fragments;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.custom_views.AudioPlayer;
import vietnamworks.com.pal.services.AudioMixerService;

/**
 * Created by duynk on 10/29/15.
 */
public class ComposerFragment extends BaseFragment {
    FloatingActionButton btnRecorder;
    private MediaRecorder myAudioRecorder;
    private AudioPlayer audioPlayer;
    private boolean hasAudio = false;

    public String GetAudioPath() {
        if (hasAudio) {
            return Utils.getSampleRecordPath();
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_composer, container, false);

        BaseActivity.applyFont(rootView);

        audioPlayer = (AudioPlayer)rootView.findViewById(R.id.player);
        audioPlayer.setVisibility(View.INVISIBLE);
        audioPlayer.setAudioPlayerCallback(new AudioPlayer.AudioPlayerCallback() {
            @Override
            public void onRemoveAudio() {
                audioPlayer.setVisibility(View.INVISIBLE);
                hasAudio = false;
            }
        });

        btnRecorder = (FloatingActionButton)rootView.findViewById(R.id.recorder);
        btnRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioMixerService.stop();

                if (myAudioRecorder != null) { //recording, stop now
                    myAudioRecorder.stop();
                    myAudioRecorder.reset();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    updateUI(false);
                    audioPlayer.setAudioSource(Utils.getSampleRecordPath(), true);
                    audioPlayer.setVisibility(View.VISIBLE);
                    hasAudio = true;

                } else { //start recording

                    Utils.newSampleRecord();

                    audioPlayer.setVisibility(View.INVISIBLE);
                    audioPlayer.setAudioSource(null);

                    myAudioRecorder = new MediaRecorder();
                    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    myAudioRecorder.setOutputFile(Utils.getSampleRecordPath());

                    try {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                        updateUI(true);

                        myAudioRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                            @Override
                            public void onError(MediaRecorder mr, int what, int extra) {
                                myAudioRecorder = null;
                                updateUI(false);
                            }
                        });
                    } catch (Exception e) {
                        myAudioRecorder = null;
                        updateUI(false);
                        e.printStackTrace();
                    }
                }
            }
        });

        return rootView;
    }

    private void updateUI(boolean isplaying) {
        if (isplaying) {
            btnRecorder.setColorNormalResId(R.color.colorFABDanger);
            btnRecorder.setColorPressedResId(R.color.colorFABDanger_Pressed);
            btnRecorder.setIcon(R.drawable.ic_av_stop);
        } else {
            btnRecorder.setColorNormalResId(R.color.colorPrimaryDark);
            btnRecorder.setColorPressedResId(R.color.colorPrimary);
            btnRecorder.setIcon(R.drawable.ic_av_mic);
        }
    }
}
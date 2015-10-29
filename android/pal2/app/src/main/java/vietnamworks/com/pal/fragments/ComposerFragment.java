package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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
    private String postTitle;
    private String topicRef;

    private TextView txtSubject;
    private EditText inputMessage;

    public String getAudioPath() {
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

        txtSubject = (TextView)rootView.findViewById(R.id.subject);
        inputMessage = (EditText)rootView.findViewById(R.id.message);
        setTopic(this.postTitle, this.topicRef);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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

    public ComposerFragment setTopic(String title, String topicRef) {
        this.postTitle = title;
        this.topicRef = topicRef;
        Activity act = getActivity();
        if (act != null) {
            ((BaseActivity)act).setTimeout(new Runnable() {
                @Override
                public void run() {
                    if (postTitle != null) {
                        txtSubject.setText(postTitle);
                    } else {
                        txtSubject.setText(R.string.say_something);
                    }
                }
            });
        }
        return this;
    }

    public String getTopic() {
        if (topicRef == null) {
            return "";
        } else {
            return topicRef;
        }
    }

    public String getSubject() {
        if (postTitle == null) {
            return "";
        } else {
            return postTitle;
        }
    }

    public String getMessage() {
        return inputMessage.getText().toString();
    }
}

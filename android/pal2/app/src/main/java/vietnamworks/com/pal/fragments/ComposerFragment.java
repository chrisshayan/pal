package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.custom_views.AudioPlayer;
import vietnamworks.com.pal.services.AudioMixerService;

/**
 * Created by duynk on 10/29/15.
 */
public class ComposerFragment extends BaseFragment {
    ImageButton btnRecorder;
    private MediaRecorder myAudioRecorder;
    private AudioPlayer audioPlayer;
    private boolean hasAudio = false;
    private String postTitle;
    private String topicRef;

    private TextView txtSubject;
    private EditText inputMessage;
    private TextView txtHint;
    private ImageButton btnHint;

    public String getAudioPath() {
        if (hasAudio) {
            return Utils.getSampleRecordPath();
        } else {
            return null;
        }
    }

    private String tips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_composer, container, false);

        BaseActivity.applyFont(rootView);

        txtHint = (TextView)rootView.findViewById(R.id.recorder_hint);
        txtHint.setVisibility(View.VISIBLE);
        txtHint.setText(getString(R.string.guide_user_recorder_1));

        audioPlayer = (AudioPlayer)rootView.findViewById(R.id.player);
        audioPlayer.setVisibility(View.INVISIBLE);
        audioPlayer.setAudioPlayerCallback(new AudioPlayer.AudioPlayerCallback() {
            @Override
            public void onRemoveAudio() {
                audioPlayer.setVisibility(View.INVISIBLE);
                txtHint.setVisibility(View.VISIBLE);
                hasAudio = false;
            }
        });

        btnRecorder = (ImageButton)rootView.findViewById(R.id.recorder);
        btnRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioMixerService.stop();

                if (myAudioRecorder != null) { //recording, stop now
                    try {
                        myAudioRecorder.stop();
                        myAudioRecorder.reset();
                        myAudioRecorder.release();
                        myAudioRecorder = null;
                    } catch (Exception E) {}
                    updateUI(false);
                    audioPlayer.setAudioSource(Utils.getSampleRecordPath(), true);
                    audioPlayer.setVisibility(View.VISIBLE);
                    txtHint.setVisibility(View.INVISIBLE);
                    hasAudio = true;

                } else { //start recording

                    Utils.newSampleRecord();

                    audioPlayer.setVisibility(View.INVISIBLE);
                    txtHint.setVisibility(View.VISIBLE);

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
        setTopic(this.postTitle, this.topicRef, this.tips);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        inputMessage.requestFocus();
        ((BaseActivity) getActivity()).showKeyboard();

        btnHint = (ImageButton) rootView.findViewById(R.id.btnHint);
        btnHint.setVisibility(tips != null && !tips.isEmpty()?View.VISIBLE:View.GONE);
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = tips;
                new AlertDialog.Builder(BaseActivity.sInstance)
                        .setTitle(BaseActivity.sInstance.getString(R.string.tips))
                        .setMessage(tips)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return rootView;
    }

    public void stopRecoder() {
        if (myAudioRecorder != null) { //recording, stop now
            myAudioRecorder.stop();
            myAudioRecorder.reset();
            myAudioRecorder.release();
            myAudioRecorder = null;
            updateUI(false);
            audioPlayer.setAudioSource(Utils.getSampleRecordPath(), true);
            audioPlayer.setVisibility(View.VISIBLE);
            hasAudio = true;

            txtHint.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUI(boolean isplaying) {
        if (isplaying) {
            btnRecorder.setImageResource(R.drawable.ic_av_stop_circle_outline_danger);
            txtHint.setText(getString(R.string.guide_user_recorder_2));
        } else {
            btnRecorder.setImageResource(R.drawable.ic_av_mic);
            txtHint.setText(getString(R.string.guide_user_recorder_1));
        }
    }

    public ComposerFragment setTopic(String title, String topicRef, String hint) {
        this.postTitle = title;
        this.topicRef = topicRef;
        this.tips = hint;
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
        if (btnHint != null) {
            btnHint.setVisibility(tips != null && !tips.isEmpty()?View.VISIBLE:View.GONE);
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

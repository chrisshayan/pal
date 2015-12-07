package vietnamworks.com.pal.fragments;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.custom_views.AudioPlayer;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.models.Topics;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.AudioMixerService;
import vietnamworks.com.pal.services.CloudinaryService;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;

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
    private TextView txtRecorderTimer;
    private View viewRecorderLayout;

    int tutorStep = 0;
    boolean isInTutorial = false;

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

        viewRecorderLayout = rootView.findViewById(R.id.recorder_layout);
        viewRecorderLayout.setVisibility(View.GONE);

        btnRecorder = (ImageButton)rootView.findViewById(R.id.recorder);
        btnRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.sInstance.askForPermission(new String[]{Manifest.permission.RECORD_AUDIO}, new AsyncCallback() {
                    @Override
                    public void onSuccess(Context ctx, Object obj) {
                        AudioMixerService.stop();

                        if (myAudioRecorder != null) { //recording, stop now
                            try {
                                myAudioRecorder.stop();
                                myAudioRecorder.reset();
                                myAudioRecorder.release();
                                myAudioRecorder = null;
                            } catch (Exception E) {
                            }
                            updateUI(false);
                            audioPlayer.setAudioSource(Utils.getSampleRecordPath(), true);
                            audioPlayer.setVisibility(View.VISIBLE);
                            txtHint.setVisibility(View.INVISIBLE);
                            hasAudio = true;
                        }
                        viewRecorderLayout.setVisibility(View.VISIBLE);
                        GaService.trackAction(R.string.ga_action_do_record);
                        Utils.newSampleRecord();

                        audioPlayer.setVisibility(View.INVISIBLE);
                        txtHint.setVisibility(View.VISIBLE);

                        audioPlayer.setAudioSource(null);

                        myAudioRecorder = new MediaRecorder();
                        try {
                            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                            myAudioRecorder.setOutputFile(Utils.getSampleRecordPath());

                            myAudioRecorder.prepare();
                            myAudioRecorder.start();
                            onStartRecorderTimer();
                            BaseActivity.sInstance.hideKeyboard();
                            updateUI(true);

                            myAudioRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                                @Override
                                public void onError(MediaRecorder mr, int what, int extra) {
                                    myAudioRecorder = null;
                                    updateUI(false);
                                }
                            });
                            GaService.trackEvent(R.string.ga_cat_recorder, R.string.ga_event_recorder_success);
                        } catch (Exception e) {
                            myAudioRecorder = null;
                            updateUI(false);
                            e.printStackTrace();
                            BaseActivity.toast(R.string.fail_to_record);
                            GaService.trackEvent(R.string.ga_cat_recorder, R.string.ga_event_recorder_fail);
                        }
                    }

                    @Override
                    public void onError(Context ctx, int error_code, String message) {
                        myAudioRecorder = null;
                        updateUI(false);
                        BaseActivity.toast(R.string.fail_to_record);
                        GaService.trackEvent(R.string.ga_cat_recorder, R.string.ga_event_recorder_fail);
                    }
                });
            }
        });

        ImageButton btnStopRecorder = (ImageButton)rootView.findViewById(R.id.btn_stop_recorder);
        btnStopRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecorder();
            }
        });


        txtSubject = (TextView)rootView.findViewById(R.id.subject);
        inputMessage = (EditText)rootView.findViewById(R.id.message);
        setTopic(this.postTitle, this.topicRef, this.tips);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        btnHint = (ImageButton) rootView.findViewById(R.id.btnHint);
        btnHint.setVisibility(tips != null && !tips.isEmpty() ? View.VISIBLE : View.GONE);
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

        if (!LocalStorage.getBool(getString(R.string.local_storage_show_composer_guide), false)) {
            isInTutorial = true;
            final View overlay = rootView.findViewById(R.id.overlay);
            final View tutor1 = overlay.findViewById(R.id.tutor_1);
            final View tutor2 = overlay.findViewById(R.id.tutor_2);
            final View tutor3 = overlay.findViewById(R.id.tutor_3);
            overlay.setVisibility(View.GONE);
            overlay.setAlpha(0);
            overlay.animate().alpha(1f).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    overlay.setVisibility(View.VISIBLE);
                    tutor1.setVisibility(View.VISIBLE);
                    tutor2.setVisibility(View.GONE);
                    tutor3.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    overlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LocalStorage.set(getString(R.string.local_storage_show_composer_guide), true);
                            onClickedInGuideComponent(null);
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        } else {
            rootView.findViewById(R.id.overlay).setVisibility(View.GONE);
            inputMessage.requestFocus();
            ((BaseActivity) getActivity()).showKeyboard();
        }

        txtRecorderTimer = (TextView)rootView.findViewById(R.id.recorder_timeleft);

        Button btnDone = (Button)rootView.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Topics.requestRandomTopics();
                stopRecorder();
                if (!FirebaseService.isConnected()) {
                    BaseActivity.toast(R.string.post_audio_no_internet);
                    return;
                }

                String audio = getAudioPath();
                String subject = getSubject();
                String topic = getTopic();
                String message = getMessage().trim();

                if (audio == null && message.length() == 0) {
                    BaseActivity.toast(R.string.empty_message);
                    return;
                }

                if (audio == null) { //text
                    Posts.addText(subject, topic, message);
                    BaseActivity.toast(R.string.create_post_successful);
                } else {
                    final String post_id = Posts.addAudioAsync(subject, topic, message);
                    final String server_file_path = Utils.getAudioServerFileName(FirebaseService.getUid(), post_id);
                    CloudinaryService.upload(audio, server_file_path, new AsyncCallback() {
                        @Override
                        public void onSuccess(Context ctx, Object res) {
                            Map m = (Map) res;
                            BaseActivity.toast(R.string.create_post_successful);
                            Posts.updateAudioLink(post_id, m.get("secure_url").toString());
                        }

                        @Override
                        public void onError(Context ctx, int error_code, String message) {
                            BaseActivity.toast(R.string.create_post_fail_audio);
                            Posts.raiseError(post_id);
                        }
                    });
                }

                FirebaseService.goOnline();
                ((TimelineActivity)getActivity()).resetToMainTimeline();
            }
        });


        ((TimelineActivity) getActivity()).getQuestView().setVisibility(View.GONE);

        return rootView;
    }

    public void stopRecorder() {
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
        viewRecorderLayout.setVisibility(View.GONE);
        onStopRecorderTimer();
    }

    private void updateUI(boolean isplaying) {
        if (isplaying) {
            //btnRecorder.setImageResource(R.drawable.ic_av_stop_circle_outline_danger);
            txtHint.setText("");
        } else {
            //btnRecorder.setImageResource(R.drawable.ic_av_mic);
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
            btnHint.setVisibility(tips != null && !tips.isEmpty() ? View.VISIBLE : View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        GaService.trackScreen(R.string.ga_screen_compose);
    }

    public boolean isInTutorial() {
        return isInTutorial;
    }

    public void onClickedInGuideComponent(View v) {
        final View overlay = getView().findViewById(R.id.overlay);
        final View tutor1 = overlay.findViewById(R.id.tutor_1);
        final View tutor2 = overlay.findViewById(R.id.tutor_2);
        final View tutor3 = overlay.findViewById(R.id.tutor_3);
        tutorStep++;
        if (tutorStep == 1) {
            tutor1.setVisibility(View.GONE);
            tutor2.setVisibility(View.VISIBLE);
        } else if (tutorStep == 2) {
            tutor2.setVisibility(View.GONE);
            tutor3.setVisibility(View.VISIBLE);
        } else {
            tutor3.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            isInTutorial = false;
        }
    }

    //recorder timer
    Timer mTimer = new Timer();
    int timerCounter;
    void onStartRecorderTimer() {
        onStopRecorderTimer();
        timerCounter = 90;
        BaseActivity.timeout(new Runnable() {
            @Override
            public void run() {
                txtRecorderTimer.setText(String.format(getString(R.string.recorder_timer), timerCounter));
            }
        });

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                BaseActivity.sInstance.setTimeout(new Runnable() {
                    @Override
                    public void run() {
                        timerCounter--;
                        if (timerCounter <= 0) {
                            timerCounter = 0;
                            stopRecorder();
                        }
                        txtRecorderTimer.setText(String.format(getString(R.string.recorder_timer), timerCounter));
                    }
                });
            }
        }, 1000, 1000);
    }

    void onStopRecorderTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        stopRecorder();
        super.onDestroy();
    }

}

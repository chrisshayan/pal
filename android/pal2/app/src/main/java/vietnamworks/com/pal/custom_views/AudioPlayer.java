package vietnamworks.com.pal.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.services.AudioMixerService;

/**
 * Created by duynk on 10/29/15.
 */
public class AudioPlayer extends LinearLayout implements AudioMixerService.AudioMixerCallback {
    private String audioSourcePath;
    boolean hasInit = false;
    boolean isPlaying = false;
    boolean isLoading = false;
    long duration = 0;
    long currentPosition = 0;
    Timer mTimer = new Timer();

    ImageButton btnPlay, btnRemove;
    ProgressBar progressBar;
    TextView txtTimer;
    boolean removable;
    private AudioPlayerCallback callback;

    public interface AudioPlayerCallback {
        void onRemoveAudio();
    }

    public AudioPlayer(Context context) {
        super(context);
        initializeViews(context);
    }

    public AudioPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AudioPlayer, 0, 0);
        try {
            removable = ta.getBoolean(R.styleable.AudioPlayer_removable, true);
        } finally {
            ta.recycle();
        }
        initializeViews(context);
    }

    public AudioPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AudioPlayer, 0, 0);
        try {
            removable = ta.getBoolean(R.styleable.AudioPlayer_removable, true);
        } finally {
            ta.recycle();
        }
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cv_audio_player, this);
        onFinishInflate();
    }

    @Override
    protected void onFinishInflate() {
        if (!hasInit) {
            super.onFinishInflate();
            btnPlay = (ImageButton) findViewById(R.id.btn_play);
            btnRemove = (ImageButton) findViewById(R.id.btn_remove);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            txtTimer = (TextView) findViewById(R.id.txt_timer);
            hasInit = true;
            isPlaying = false;

            btnPlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (audioSourcePath != null) {
                        if (isPlaying || isLoading) {
                            AudioMixerService.stop(AudioPlayer.this);
                        } else {
                            AudioMixerService.play(audioSourcePath, AudioPlayer.this);
                        }
                    }
                }
            });

            btnRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        AudioMixerService.stop(AudioPlayer.this);
                        callback.onRemoveAudio();
                    }
                }
            });
            if (!this.removable) {
                btnRemove.setVisibility(GONE);
            }
        }
        BaseActivity.applyFont(this);
    }

    public AudioPlayer setAudioSource(String audioSourcePath) {
        return setAudioSource(audioSourcePath, false);
    }

    public AudioPlayer setAudioSource(String audioSourcePath, boolean prefetch) {
        if (isPlaying) {
            AudioMixerService.stop(this);
        }
        txtTimer.setText("00:00");

        if (prefetch && audioSourcePath != null && audioSourcePath.trim().length() > 0) {
            try {
                final MediaPlayer player = new MediaPlayer();
                player.setDataSource(audioSourcePath);
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp == player) {
                            BaseActivity.sInstance.setTimeout(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (txtTimer != null) {
                                            long dur = player.getDuration();
                                            int sec = (int) dur / 1000;
                                            duration = sec;
                                            int min = sec / 60;
                                            sec = sec % 60;
                                            String _min = (min < 10) ? "0" + min : min + "";
                                            String _sec = (sec < 10) ? "0" + sec : sec + "";
                                            String time = _min + ":" + _sec;
                                            txtTimer.setText(time);
                                        }
                                    } catch (Exception E) {}
                                }
                            });
                        }
                    }
                });
            } catch (Exception E) {
                E.printStackTrace();
            }
        }

        this.audioSourcePath = audioSourcePath;
        return this;
    }

    @Override
    public void onMixerStop() {
        isPlaying = false;
        isLoading = false;

        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        BaseActivity.sInstance.setTimeout(new Runnable() {
            @Override
            public void run() {
                btnPlay.setImageResource(R.drawable.ic_player_play);
                progressBar.setProgress(0);
            }
        });
    }

    @Override
    public void onMixerStart(final long dur) {
        duration = dur/1000;
        isPlaying = true;
        isLoading = false;
        BaseActivity.sInstance.setTimeout(new Runnable() {
            @Override
            public void run() {
                int sec = (int)dur/1000;
                int min = sec /60;
                sec = sec%60;
                String _min = (min<10)?"0"+min:min+"";
                String _sec = (sec<10)?"0"+sec:sec+"";
                String time = _min + ":" + _sec;
                btnPlay.setImageResource(R.drawable.ic_player_stop);
                txtTimer.setText(time);

                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;
                }

                currentPosition = 0;
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        BaseActivity.sInstance.setTimeout(new Runnable() {
                            @Override
                            public void run() {
                                int percent = 0;
                                if (duration > 0) {
                                    percent = (int) Math.min(currentPosition * 100f / duration, 100);
                                }
                                currentPosition++;
                                progressBar.setProgress(percent);
                            }
                        });

                    }

                }, 1000, 1000);
            }
        });
    }

    @Override
    public void onMixerPrepare() {
        isLoading = true;
        isPlaying = false;

        BaseActivity.sInstance.setTimeout(new Runnable() {
            @Override
            public void run() {
                btnPlay.setImageResource(R.drawable.ic_player_prepare);
                txtTimer.setText("__:__");
            }
        });
    }

    public void setAudioPlayerCallback(AudioPlayerCallback callback) {
        this.callback = callback;
    }
}

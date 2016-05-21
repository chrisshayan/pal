package vietnamworks.com.pal.services;

import android.media.MediaPlayer;

/**
 * Created by duynk on 10/29/15.
 */
public class AudioMixerService {
    private MediaPlayer mPlayer = null;
    private AudioMixerCallback currentAudioSubscriber;
    private boolean isPlaying = false;
    private static AudioMixerService sInstance = new AudioMixerService();

    private AudioMixerService() {}

    public interface AudioMixerCallback {
        void onMixerStop();
        void onMixerStart(long dur);
        void onMixerPrepare();
    }


    private void _play(String url, final AudioMixerCallback sender) {
        if (mPlayer == null) { //not playing
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                    sender.onMixerStop();
                }
            });
            try {
                sender.onMixerPrepare();
                mPlayer.setDataSource(url);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp == mPlayer) {
                            mp.start();
                            currentAudioSubscriber = sender;
                            sender.onMixerStart(mp.getDuration());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mPlayer = null;
                sender.onMixerStop();
            }
        } else {
            if (currentAudioSubscriber != null) {
                currentAudioSubscriber.onMixerStop();
                currentAudioSubscriber = null;
            }
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            _play(url, sender);
        }
    }

    private void _stop(AudioMixerCallback sender) {
        if (mPlayer == null) {
            if (sender != null ) {
                sender.onMixerStop();
            }
        } else {
            try {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {

            }
            if (sender != null ) {
                sender.onMixerStop();
            }
            currentAudioSubscriber = null;
        }
    }

    public static void play(String url, AudioMixerCallback callback) {
        sInstance._play(url, callback);
    }

    public static void stop(AudioMixerCallback callback) {
        sInstance._stop(callback);
    }

    public static void stop() {
        sInstance._stop(null);
    }
}

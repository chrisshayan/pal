package vietnamworks.com.pal.components;

/**
 * Created by duynk on 10/15/15.
 */
public interface AudioMixerController {
    void playAudio(String url, AudioMixerSubscriber subscriber);
    void stopAudio(AudioMixerSubscriber subscriber);
}

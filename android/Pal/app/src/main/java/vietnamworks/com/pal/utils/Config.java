package vietnamworks.com.pal.utils;

/**
 * Created by duynk on 10/1/15.
 */
public interface Config {
    public final static String RecorderFileExt = ".3gp";
    public final static String SampleRecorderFilename = "recording" + RecorderFileExt;

    public final static String AudioServerBaseURL = "http://172.18.2.150:4040";
    public final static String AudioUploadURL = AudioServerBaseURL + "/post_audio_android";
}

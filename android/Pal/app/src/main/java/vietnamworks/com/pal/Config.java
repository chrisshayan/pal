package vietnamworks.com.pal;

/**
 * Created by duynk on 10/1/15.
 */
public interface Config {
    public final static String RecorderFileExt = ".mp4";
    public final static String SampleRecorderFilename = "recording" + RecorderFileExt;

    public final static String AudioServerBaseURL = "http://iamprogrammer.work:4040";
    public final static String AudioUploadURL = AudioServerBaseURL + "/post_audio_android";

    public final static String ParseAppId = "WRcgKehX6zd2idIpSUj6GGmwtcMipq7Y0tXzwJ2s";
    public final static String ParseAppKey = "29Xh7sjfYXE2Ju9FlOGgXWk4FeV21T9kWIr7s2gT";
}

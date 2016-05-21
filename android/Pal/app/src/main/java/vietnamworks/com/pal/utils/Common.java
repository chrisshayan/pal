package vietnamworks.com.pal.utils;

import android.os.Build;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.Config;

/**
 * Created by duynk on 9/11/15.
 */
public class Common {
    public static int apiVersion = Build.VERSION.SDK_INT;
    public final static String DEFAULT_DATETIME_FORMAT = "EEE, d MMM yyyy, HH:mm";

    public static boolean isLollipopOrBelow() {
        return apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }

    public static String nowString() {
        return Common.nowString(DEFAULT_DATETIME_FORMAT);
    }


    public static String nowString(String format) {
        java.text.DateFormat df = new SimpleDateFormat(format);
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getDateString(Date date) {
        return getDateString(date, DEFAULT_DATETIME_FORMAT);
    }

    public static String getDateString(long timestamp) {
        Date date = new Date(timestamp);
        return getDateString(date, DEFAULT_DATETIME_FORMAT);
    }

    public static String getDateString(Date date, String format) {
        java.text.DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static int randomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public static String getHomeDir() {
        return BaseActivity.applicationDataPath;
    }

    public static String currentSampleRecordSeed = System.currentTimeMillis() + "";

    public static void newSampleRecord() {
        currentSampleRecordSeed = System.currentTimeMillis() + "";
    }

    public static String getSampleRecordPath() {
        return getHomeDir() + "/" + currentSampleRecordSeed + "_" + Config.SampleRecorderFilename;
    }

    public static String getAudioServerFileName(String user_id, String post_id) {
        return "user_" + user_id + "_" + post_id + "_" + System.currentTimeMillis() + Config.RecorderFileExt;
    }

    public static float lerp(float start, float end, float percent) {
        float dt = Math.abs(start - end);
        float min = Math.max(Math.abs(start) * 0.1f, Math.abs(end) * 0.1f);
        if (dt <= min) {
            start = end;
        }
        return (start + percent * (end - start));
    }

    public static int lerp(int start, int end, float percent) {
        float dt = Math.abs(start - end);
        float min = Math.max(Math.abs(start) * 0.1f, Math.abs(end) * 0.1f);
        if (dt <= min) {
            start = end;
        }
        return (int) (start + percent * (end - start));
    }

    public static double swingSin(float A, float omega, float phi, float t) {
        return A*Math.sin(omega*t + phi);
    }

    public static int sign(float a) {
        return a > 0?1:(a<0?-1:0);
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }
}

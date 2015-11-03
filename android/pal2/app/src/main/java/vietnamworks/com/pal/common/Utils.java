package vietnamworks.com.pal.common;

import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;

import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.configurations.AppConfig;

/**
 * Created by duynk on 10/27/15.
 */
public class Utils {

    public static int apiVersion = Build.VERSION.SDK_INT;
    public final static String DEFAULT_DATETIME_FORMAT = "EEE, d MMM yyyy, HH:mm";

    public static boolean isLollipopOrBelow() {
        return apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }

    public static String nowString() {
        return Utils.nowString(DEFAULT_DATETIME_FORMAT);
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
        return new SimpleDateFormat(format).format(date);
    }

    public static String getDuration(long timestamp) {
        long now = getMillis();
        long minutes = (Math.max(now - timestamp, 0)/1000)/60;

        if (minutes <= 1) { //less than 1 min
            return "just updated";
        } else if (minutes < 60) {
            return minutes + " mins";
        } else if (minutes < 2*60) {
            long min = (minutes%60);
            if (min > 2) {
                return "1 hr " + min +" mins";
            } else {
                return "1 hr " + min +" min";
            }
        } else if (minutes < 24*2*60) {
            return Math.round(minutes / 60f) + " hrs";
        } else if (minutes < 7*24*60) {
            return Math.round(minutes/(24*60f)) + " days";
        } else {
            return getDateString(timestamp);
        }
    }

    public static String getBase64(String input) {
        try {
            byte[] data = input.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            return base64;
        } catch (Exception E) {
            return "";
        }
    }

    public static long getMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
    }

    public static String hash(String input, final String method) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(method);
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            BigInteger bi = new BigInteger(1, messageDigest);
            return String.format("%0" + (messageDigest.length << 1) + "x", bi);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String hash(String input) {
        return md5(input);
    }

    public static String md5(String input) {
        return hash(input, "MD5");
    }

    public static String sha256(String input) {
        return hash(input, "SHA-256");
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public static String getHomeDir() {
        return BaseActivity.applicationDataPath;
    }

    public static String currentSampleRecordSeed = System.currentTimeMillis() + "";

    public static void newSampleRecord() {
        currentSampleRecordSeed = System.currentTimeMillis() + "";
    }

    public static String getSampleRecordPath() {
        return getHomeDir() + "/" + currentSampleRecordSeed + "_" + AppConfig.SampleRecorderFilename;
    }

    public static String getAudioServerFileName(String user_id, String post_id) {
        return "user_" + user_id + "_" + post_id + "_" + System.currentTimeMillis() + AppConfig.RecorderFileExt;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static int randomInt(int min, int max) {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public static int numOfWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length;
    }

    public static String getFirstWords(String s, int n) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(s);
        for(int i = 0; i < n && st.hasMoreTokens(); i++) {
            sb.append(st.nextToken());
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String getFirstWordsExtra(String s, int n) {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(s);
        for(int i = 0; i < n && st.hasMoreTokens(); i++) {
            sb.append(st.nextToken());
            sb.append(" ");
        }
        String re = sb.toString();
        if (re.length() < s.length()) {
            re = re + "...";
        }
        return re;
    }
}

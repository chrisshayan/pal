package vietnamworks.com.pal.common;

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.configurations.Application;

/**
 * Created by duynk on 10/27/15.
 */
public class Utils {
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
        return getHomeDir() + "/" + currentSampleRecordSeed + "_" + Application.SampleRecorderFilename;
    }

    public static String getAudioServerFileName(String user_id, String post_id) {
        return "user_" + user_id + "_" + post_id + "_" + System.currentTimeMillis() + Application.RecorderFileExt;
    }
}

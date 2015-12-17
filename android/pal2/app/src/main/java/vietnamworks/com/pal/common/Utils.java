package vietnamworks.com.pal.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.configurations.AppConfig;
import vietnamworks.com.pal.services.ExceptionReportService;

/**
 * Created by duynk on 10/27/15.
 */
public class Utils {

    public static int apiVersion = Build.VERSION.SDK_INT;
    public final static String DEFAULT_DATETIME_FORMAT = "EEE, d MMM yyyy, HH:mm";

    public static boolean isLollipopOrLater() {
        return apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMarshMallowOrLater() {
        return apiVersion >= Build.VERSION_CODES.M;
    }

    public static boolean isVersionOrLater(int version) {
        return apiVersion >= version;
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
            return String.format(BaseActivity.sInstance.getString(R.string.time_1_min_ago), minutes);
        } else if (minutes < 60) {
            return String.format(BaseActivity.sInstance.getString(R.string.time_n_min_ago), minutes);
        } else if (minutes < 2*60) {
            long min = (minutes%60);
            if (min > 2) {
                return String.format(BaseActivity.sInstance.getString(R.string.time_1_h_n_min_ago), min);
            } else {
                return String.format(BaseActivity.sInstance.getString(R.string.time_1_h_1_min_ago), min);
            }
        } else if (minutes < 24*2*60) {
            return String.format(BaseActivity.sInstance.getString(R.string.time_n_h_ago), Math.round(minutes / 60f));
        } else if (minutes < 7*24*60) {
            return String.format(BaseActivity.sInstance.getString(R.string.time_n_d_ago), Math.round(minutes/(24*60f)));
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

    public static String currentSampleRecordSeed = Utils.getMillis() + "";

    public static void newSampleRecord() {
        currentSampleRecordSeed = Utils.getMillis() + "";
    }

    public static String getSampleRecordPath() {
        return getHomeDir() + "/" + currentSampleRecordSeed + "_" + AppConfig.SampleRecorderFilename;
    }

    public static String getAudioServerFileName(String user_id, String post_id) {
        return "user_" + user_id + "_" + post_id + "_" + Utils.getMillis();// + AppConfig.RecorderFileExt;
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

    public static String counterFormat(long count) {
        if (count < 1000) {
            return count + "";
        } else if (count < 1000000) {
            return (Math.round(count/100)/10f) + "k";
        } else if (count < 1000000000) {
            return (Math.round(count/1000)/100f) + "m";
        } else {
            return (Math.round(count/10000)/1000f) + "b";
        }
    }

    public static Bitmap getFixOrientationBitmap(String path, int maxWidth, int maxHeight) {
        Bitmap bm = null;
        if (path != null && maxWidth > 0 && maxHeight > 0) {
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            bm = BitmapFactory.decodeFile(path, btmapOptions);
            try {
                ExifInterface exif = new ExifInterface(path);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int angle = 0;
                if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
                    angle = 0;
                } else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
                    angle = 180;
                } else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
                    angle = 270;
                }
                Matrix matrix = new Matrix();
                if (rotation != 0f) {
                    matrix.preRotate(angle);
                }
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            } catch (Exception e) {
                ExceptionReportService.report(e);
            }
            bm = resize(bm, maxWidth, maxHeight);
        }
        return bm;
    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (image != null && maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static String r13(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'N' && c <= 'Z') c -= 13;
            sb.append(c);
        }
        return sb.toString();
    }
}

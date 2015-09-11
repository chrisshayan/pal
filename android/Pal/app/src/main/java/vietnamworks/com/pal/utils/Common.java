package vietnamworks.com.pal.utils;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public static String getDateString(Date date, String format) {
        java.text.DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
}

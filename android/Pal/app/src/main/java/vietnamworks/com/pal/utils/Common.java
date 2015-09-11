package vietnamworks.com.pal.utils;

import android.os.Build;

/**
 * Created by duynk on 9/11/15.
 */
public class Common {
    public static int apiVersion = Build.VERSION.SDK_INT;

    public static boolean isLollipopOrBelow() {
        return apiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }
}

package vietnamworks.com.pal.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/1/15.
 */

public class BaseActivity extends AppCompatActivity {
    public static String applicationDataPath = "";
    public static float density;
    private Handler handler = new Handler();

    public static Typeface RobotoL;
    public static Typeface RobotoR;
    public static Typeface RobotoB;
    public static Typeface RobotoBI;
    public static Typeface RobotoI;
    public static Typeface RobotoLI;
    public static Typeface Bubblegum;

    static Toast toast;

    private boolean isKeyboardShown;
    private Rect screenRegion;

    public BaseActivity() {
        super();
        BaseActivity.sInstance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseService.setContext(this);
        if (applicationDataPath.length() == 0) {
            applicationDataPath = this.getApplicationInfo().dataDir;
        }
        density = this.getResources().getDisplayMetrics().density;

        RobotoL = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.ttf");
        RobotoLI = Typeface.createFromAsset(getAssets(),"fonts/Roboto-LightItalic.ttf");
        RobotoR = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
        RobotoB = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Bold.ttf");
        RobotoBI = Typeface.createFromAsset(getAssets(),"fonts/Roboto-BoldItalic.ttf");
        RobotoI = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Italic.ttf");

        Bubblegum = Typeface.createFromAsset(getAssets(),"fonts/Bubblegum.ttf");

        isKeyboardShown = false;
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                boolean isSoftKeyShown = r.height() < getScreenHeight()*0.8;
                isKeyboardShown = isSoftKeyShown;
                screenRegion = r;
                onLayoutChanged(r, isSoftKeyShown);
            }
        });
    }

    public void onLayoutChanged(Rect r, boolean isSoftKeyShown) {
    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseService.setContext(null);
    }

    public static void toast(int messageId) {
        toast(sInstance.getString(messageId));
    }

    public static void toast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(sInstance.getBaseContext(),message, Toast.LENGTH_SHORT);
        if (sInstance.isKeyboardShown) {
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, getScreenHeight() - getStatusBarHeight() - sInstance.screenRegion.height());
        } else {
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
        toast.show();
    }

    public static void applyFont(final View v) {
        applyFont(v, RobotoR);
    }

    public static void applyFont(final View v, String font_des) {
        Typeface t = RobotoR;
        font_des = font_des.toLowerCase();
        boolean isItalic = false;
        boolean isBold = false;
        boolean isRegular = false;
        boolean isLight = false;
        if (font_des.contains("i")) {
            isItalic = true;
        }
        if (font_des.contains("b")) {
            isBold = true;
        }
        if (font_des.contains("r")) {
            isRegular = true;
        }
        if (font_des.contains("l")) {
            isLight = true;
        }

        if (isBold) {
            t = RobotoB;
            if (isItalic) {
                t = RobotoBI;
            }
        } else if (isLight) {
            t = RobotoL;
            if (isItalic) {
                t = RobotoLI;
            }
        } else if (isRegular) {
            t = RobotoR;
        } else if (isItalic) {
            t = RobotoI;
        }
        applyFont(v, t);
    }

    public static void applyFont(final View v, Typeface font) {
        if (v != null) {
            try {
                if (v instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) v;
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        View child = vg.getChildAt(i);
                        applyFont(child, font);
                    }
                } else if (v instanceof TextView) {
                    ((TextView)v).setTypeface(font);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pushFragment(android.support.v4.app.Fragment f, int holder_id, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().add(holder_id, f).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(holder_id, f).commit();
        }
    }

    public void pushFragment(android.support.v4.app.Fragment f, int holder_id) {
        pushFragment(f, holder_id, true);
    }

    public void openFragment(android.support.v4.app.Fragment f, int holder_id, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction().replace(holder_id, f).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(holder_id, f).commit();
        }
    }

    public void openFragment(android.support.v4.app.Fragment f, int holder_id) {
        openFragment(f, holder_id, false);
    }

    public void openActivity(Class<?> cls) {
        Intent intent = new Intent(BaseActivity.sInstance, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void openActivity(Class<?> cls, Bundle b) {
        Intent intent = new Intent(BaseActivity.sInstance, cls);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void openActivity(Class<?> cls, boolean withHistory) {
        Intent intent = new Intent(BaseActivity.sInstance, cls);
        startActivity(intent);
    }

    public void showActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.show();
        }
    }

    public void hideActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    public void setTimeout(final Runnable f, long delay) {
        handler.postDelayed(f, delay);
    }

    public void setTimeout(final Runnable f) {
        handler.post(f);
    }

    public static int[] getScreenSize() {
        Display display = BaseActivity.sInstance.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int s[] = new int[2];
        s[0] = size.x;
        s[1] = size.y;
        return s;
    }

    public static int getScreenHeight() {
        Display display = BaseActivity.sInstance.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getScreenWidth() {
        Display display = BaseActivity.sInstance.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = BaseActivity.sInstance.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = BaseActivity.sInstance.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static BaseActivity sInstance;
}
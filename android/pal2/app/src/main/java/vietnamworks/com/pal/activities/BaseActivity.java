package vietnamworks.com.pal.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.ExceptionReportService;

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

    private boolean lockedBackkey;

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
        if (applicationDataPath.length() == 0) {
            applicationDataPath = this.getApplicationInfo().dataDir;
        }
        density = this.getResources().getDisplayMetrics().density;

        RobotoL = Typeface.createFromAsset(getAssets(),"fonts/RobotoSlab-Light.ttf");
        RobotoLI = Typeface.createFromAsset(getAssets(),"fonts/RobotoSlab-Light.ttf");
        RobotoR = Typeface.createFromAsset(getAssets(),"fonts/RobotoSlab-Regular.ttf");
        RobotoB = Typeface.createFromAsset(getAssets(),"fonts/RobotoSlab-Bold.ttf");
        RobotoBI = Typeface.createFromAsset(getAssets(),"fonts/RobotoSlab-Bold.ttf");
        RobotoI = Typeface.createFromAsset(getAssets(),"fonts/RobotoSlab-Regular.ttf");

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
        setTitleBarColor(R.color.colorPrimaryDark);
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
    }

    public static void toast(int messageId) {
        toast(sInstance.getString(messageId));
    }

    public static void toast(final String message) {
        timeout(new Runnable() {
            @Override
            public void run() {
                int base_padding = 0;
                if (Utils.isLollipopOrLater()) {
                    base_padding = (int) (40 * density);
                }
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }

                toast = Toast.makeText(sInstance.getBaseContext(), message, Toast.LENGTH_SHORT);
                if (sInstance.isKeyboardShown) {
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, getScreenHeight() - getStatusBarHeight() - sInstance.screenRegion.height() + base_padding);
                } else {
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, base_padding);
                }
                toast.show();
            }
        }, 500);

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
        applyFont(v, font, false);
    }

    public static void applyFont(final View v, Typeface font, boolean force) {
        if (v != null) {
            try {
                if (v instanceof ViewGroup) {
                    ViewGroup vg = (ViewGroup) v;
                    for (int i = 0; i < vg.getChildCount(); i++) {
                        View child = vg.getChildAt(i);
                        applyFont(child, font);
                    }
                }
                else if (v instanceof TextView) {
                    TextView t = (TextView)v;
                    if (!force && t.getTypeface() != null) {
                        int style = t.getTypeface().getStyle();
                        if (style == Typeface.BOLD) {
                            ((TextView) v).setTypeface(RobotoB);
                        } else if (style == Typeface.BOLD_ITALIC) {
                            ((TextView) v).setTypeface(RobotoBI);
                        } else if (style == Typeface.ITALIC) {
                            ((TextView) v).setTypeface(RobotoI);
                        } else {
                            ((TextView) v).setTypeface(font);
                        }
                    } else {
                        ((TextView) v).setTypeface(font);
                    }
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

    public void openFragmentAndClean(android.support.v4.app.Fragment f, int holder_id) {
        FragmentManager manager = getSupportFragmentManager();
        try {
            if (manager.getBackStackEntryCount() > 0) {
                FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
                manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }catch (Exception E) {
            ExceptionReportService.report(E);
        }
        manager.beginTransaction().replace(holder_id, f).commit();
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

    public void showActionBar(String title, boolean displayHomeAsUpButton) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.show();
        }
        setTitle(title, displayHomeAsUpButton);
    }

    public void showActionBar(int title, boolean displayHomeAsUpButton) {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.show();
        }
        setTitle(title, displayHomeAsUpButton);
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

    public void setTitle(String title) {
        ActionBar b =  getSupportActionBar();
        if (b != null) {
            b.setTitle(title);
        }
    }

    public class CustomTypefaceSpan extends MetricAffectingSpan
    {
        private final Typeface typeface;

        public CustomTypefaceSpan(final Typeface typeface)
        {
            this.typeface = typeface;
        }

        @Override
        public void updateDrawState(final TextPaint drawState)
        {
            apply(drawState);
        }

        @Override
        public void updateMeasureState(final TextPaint paint)
        {
            apply(paint);
        }

        private void apply(final Paint paint)
        {
            final Typeface oldTypeface = paint.getTypeface();
            final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
            final int fakeStyle = oldStyle & ~typeface.getStyle();

            if ((fakeStyle & Typeface.BOLD) != 0)
            {
                paint.setFakeBoldText(true);
            }

            if ((fakeStyle & Typeface.ITALIC) != 0)
            {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(typeface);
        }
    }

    public void setTitle(int title, boolean displayHomeAsUpButton) {
        setTitle(title);
        displayHomeAsUpButton(displayHomeAsUpButton);
    }

    public void setTitle(String title, boolean displayHomeAsUpButton) {
        setTitle(title);
        displayHomeAsUpButton(displayHomeAsUpButton);
    }

    public void setTitle(int title) {
        ActionBar b =  getSupportActionBar();
        if (b != null) {
            SpannableString s = new SpannableString(getString(title));
            s.setSpan(new CustomTypefaceSpan(RobotoR), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            b.setTitle(s);
        }
    }

    public void displayHomeAsUpButton(boolean v) {
        ActionBar b =  getSupportActionBar();
        if (b != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public static void timeout(Runnable r, long delay) {
        BaseActivity.sInstance.setTimeout(r, delay);
    }

    public static void timeout(Runnable r) {
        BaseActivity.sInstance.setTimeout(r);
    }

    public static void setTitleBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sInstance.getWindow().setStatusBarColor(sInstance.getResources().getColor(color, sInstance.getTheme()));
        }
    }

    public boolean isBackKeyLocked() {
        return lockedBackkey;
    }

    public void lockBackKey(boolean lockedBackkey) {
        this.lockedBackkey = lockedBackkey;
    }

    AsyncCallback permissionCallback;
    public void askForPermission(String[] permission, AsyncCallback callback) {
        List<String> permissions = new ArrayList<String>();
        if (Utils.isMarshMallowOrLater()) {
            for (String p:permission) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(p);
                }
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 3333);
                permissionCallback = callback;
            } else {
                callback.onSuccess(this, null);
            }
        } else {
            callback.onSuccess(this, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case 3333: {
                boolean allAccepted = true;
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        Log.d("Permissions", "Permission Granted: " + permissions[i]);
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                        allAccepted = false;
                    }
                }
                if (permissionCallback != null) {
                    if (allAccepted) {
                        permissionCallback.onSuccess(this, null);
                    } else {
                        permissionCallback.onError(this, -1, null);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public static BaseActivity sInstance;
}
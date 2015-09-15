package vietnamworks.com.pal;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import vietnamworks.com.pal.utils.Common;

public class TutorialActivity extends AppCompatActivity {

    private static TutorialActivity sInstance;
    private static int sNumPages;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Fragment mCurrentFragment;

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        sNumPages = getResources().getStringArray(R.array.tutor).length;

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        sInstance = this;

        sInstance.SetPageIndex(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                sInstance.SetPageIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mToast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    }

    public void SetPageIndex(int index) {
        LinearLayout indicators = (LinearLayout) findViewById(R.id.tutor_indicator_holder);
        indicators.removeAllViewsInLayout();

        Drawable circle;
        Drawable filled_circle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circle = (Drawable) getResources().getDrawable(R.drawable.shape_circle, this.getTheme());
            filled_circle = (Drawable) getResources().getDrawable(R.drawable.shape_filled_circle, this.getTheme());
        } else {
            circle = (Drawable)getResources().getDrawable(R.drawable.shape_circle);
            filled_circle = (Drawable)getResources().getDrawable(R.drawable.shape_filled_circle);
        }

        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            ImageView img = new ImageView(this);
            img.setImageDrawable(index != i?circle:filled_circle);
            indicators.addView(img);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(30, 30);
            lp.setMargins(10, 0, 10, 0);
            img.setLayoutParams(lp);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter( FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < sNumPages - 1) {
                return TutorialFragment.create(position, sNumPages);
            } else {
                return new LoginFragment();
            }
        }

        @Override
        public int getCount() {
            return sNumPages;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (mCurrentFragment != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sInstance = null;
    }

    private void showToastMessage(CharSequence message, int time) {
        mToast.cancel();
        mToast = Toast.makeText(getApplicationContext(), message, time);
        mToast.show();
    }

    private void showToastMessage(int id) {
        String str = getString(id);
        showToastMessage(str, Toast.LENGTH_SHORT);
    }

    private void showToastMessage(int id, int time) {
        String str = getString(id);
        showToastMessage(str, time);
    }

    public void onLogin(View v) {
        final LoginFragment fragment = (LoginFragment)mCurrentFragment;
        final String email = fragment.getEmail().trim();
        final String password = fragment.getPassword();

        if (email.length() == 0) {
            showToastMessage(R.string.login_validation_empty_email);
            fragment.focusEmail();
            return;
        }

        if (!Common.isValidEmail(email)) {
            showToastMessage(R.string.login_validation_invalid_email_format);
            fragment.focusEmail();
            return;
        }

        if (password.length() == 0) {
            showToastMessage(R.string.login_validation_empty_password);
            fragment.focusPassword();
            return;
        }

        fragment.startProcessing();

        //// TODO: 9/15/15 Add login progress here

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.endProcessing();
                if (email.toLowerCase().compareTo("network@email.com") == 0) {
                    onLoginFail(R.string.message_fail_to_connect_server);
                } else if (email.toLowerCase().compareTo("tester01@email.com") == 0 && password.compareTo("1234") == 0) {
                    onLoginSuccess();
                } else {
                    onLoginFail(R.string.login_message_login_fail);
                }
            }
        }, 3000L);

    }

    public void onLoginFail(int error) {
        showToastMessage(error, Toast.LENGTH_LONG);
    }

    public void onLoginSuccess() {
        showToastMessage("OK", Toast.LENGTH_SHORT);
    }
}

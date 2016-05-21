package vietnamworks.com.pal;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.fragments.FragmentLogin;
import vietnamworks.com.pal.fragments.FragmentTutorial;

public class TutorialActivity extends BaseActivity {
    private static int sNumPages;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        FirebaseService.setContext(this);

        sNumPages = getResources().getStringArray(R.array.tutor).length;

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TutorScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        ((TutorialActivity)sInstance).SetPageIndex(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ((TutorialActivity)sInstance).SetPageIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
            lp.setMargins(10, 0, 10, 0);
            img.setLayoutParams(lp);
        }
    }

    private class TutorScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public TutorScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position < sNumPages - 1) {
                return FragmentTutorial.create(position, sNumPages);
            } else {
                return FragmentLogin.create(sInstance);
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
    }

    public void onLogin(View v) {
        if (mCurrentFragment instanceof FragmentLogin) {
            ((FragmentLogin) mCurrentFragment).onLogin();
        }
    }

    public void onSignUp(View v) {
        ((FragmentLogin)mCurrentFragment).onSignUp();
    }
}

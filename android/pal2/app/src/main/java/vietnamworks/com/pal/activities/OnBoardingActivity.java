package vietnamworks.com.pal.activities;

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
import android.widget.TextView;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.fragments.OnBoardingFragment;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;

/**
 * Created by duynk on 10/26/15.
 */
public class OnBoardingActivity extends BaseActivity {
    int nPages;
    int currentPageIndex = 0;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    public OnBoardingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        nPages = getResources().getStringArray(R.array.tutor_body).length;

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TutorScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        SetPageIndex(0);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SetPageIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setTitleBarColor(R.color.colorPrimary);

        if (!(LocalStorage.getBool(R.string.ls_first_launch, true))) {
            onSkip(null);
        }

        GaService.trackScreen(R.string.ga_screen_onboarding);
    }

    public void SetPageIndex(int index) {
        currentPageIndex = index;
        LinearLayout indicators = (LinearLayout) findViewById(R.id.tutor_indicator_holder);
        indicators.removeAllViewsInLayout();

        Drawable circle;
        Drawable filled_circle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circle = getResources().getDrawable(R.drawable.shape_onboarding_indicator, this.getTheme());
            filled_circle = getResources().getDrawable(R.drawable.shape_onboarding_selected_indicator, this.getTheme());
        } else {
            circle = getResources().getDrawable(R.drawable.shape_onboarding_indicator);
            filled_circle = getResources().getDrawable(R.drawable.shape_onboarding_selected_indicator);
        }

        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            ImageView img = new ImageView(this);
            img.setImageDrawable(index != i?circle:filled_circle);
            img.setAlpha(0.75f);
            indicators.addView(img);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(15, 15);
            lp.setMargins(10, 0, 10, 0);
            img.setLayoutParams(lp);
        }

        ((TextView) findViewById(R.id.btn_next)).setText(getString(index >= nPages -1?R.string.start :R.string.next));
    }

    private class TutorScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public TutorScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return OnBoardingFragment.create(position, nPages);
        }

        @Override
        public int getCount() {
            return nPages;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }
    }

    /**
     * button events
     */
    public void onNext(View v) {
        GaService.trackAction(R.string.ga_action_onboarding_next);
        if (currentPageIndex >= nPages - 1) {
            onSkip(v);
        } else {
            mPager.setCurrentItem(++currentPageIndex, true);
        }
    }

    public void onSkip(View v) {
        if (v != null) {
            GaService.trackAction(R.string.ga_action_onboarding_skip);
        }
        openActivity(AuthActivity.class);
    }
}

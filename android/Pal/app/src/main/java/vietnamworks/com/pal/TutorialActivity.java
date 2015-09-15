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
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TutorialActivity extends AppCompatActivity {

    private static TutorialActivity sInstance;
    private static int sNumPages;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

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
            return TutorialFragment.create(position, sNumPages);
        }

        @Override
        public int getCount() {
            return sNumPages;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sInstance = null;
    }
}

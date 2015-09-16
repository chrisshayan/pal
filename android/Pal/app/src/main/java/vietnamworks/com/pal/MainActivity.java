package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by duynk on 9/15/15.
 */
public class MainActivity extends AppCompatActivity {
    private Menu menu;
    private CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter( FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /*
            if (position < sNumPages - 1) {
                return TutorialFragment.create(position, sNumPages);
            } else {
                return LoginFragment.create(sInstance);
            }
            */
            return new TopicFragment();
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            /*
            if (mCurrentFragment != object) {
                mCurrentFragment = ((Fragment) object);
            }
            */
            super.setPrimaryItem(container, position, object);
        }
    }

    public static class TopicFragment extends Fragment {
        public TopicFragment() {}
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout containing a title and body text.
            ViewGroup rootView = (ViewGroup) inflater
                    .inflate(R.layout.fragment_topic, container, false);
            return rootView;
        }
    }
}

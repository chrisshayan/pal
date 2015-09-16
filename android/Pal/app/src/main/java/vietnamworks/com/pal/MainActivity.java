package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import vietnamworks.com.pal.models.AbstractContainer;
import vietnamworks.com.pal.models.AppModel;

/**
 * Created by duynk on 9/15/15.
 */
public class MainActivity extends AppCompatActivity {

    static MainActivity sIntance;

    private Menu menu;
    private CustomViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    TopicLoaderFragment mTopicLoaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        sIntance = this;
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mTopicLoaderFragment = new TopicLoaderFragment();
        this.refreshTopics();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int mDataSize = 1;
        public ScreenSlidePagerAdapter( FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            int count = this.getCount();
            if (position < count - 1) {
                return TopicFragment.create(position);
            } else {
                return mTopicLoaderFragment;
            }
        }

        @Override
        public int getCount() {
            return mDataSize;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            if (position == this.getCount() - 1) {
                AppModel.topics.loadAsync(sIntance, new AbstractContainer.OnLoadAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        refreshTopics();
                    }

                    @Override
                    public void onError() {
                    }
                });
            }
        }

        public void setCount(int value) {
            mDataSize = value + 1;
            this.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sIntance = null;
    }

    public void refreshTopics() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                boolean delay = true;
                if (mPagerAdapter == null) {
                    delay = false;
                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                }

                if (delay) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPager.setAdapter(null);
                            mPagerAdapter.setCount(AppModel.topics.getData().size());
                            mPager.setAdapter(mPagerAdapter);
                        }
                    }, 1000);
                } else {
                    mPager.setAdapter(mPagerAdapter);
                }
            }
        });
    }
}

package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.models.AbstractContainer;
import vietnamworks.com.pal.models.AppModel;

/**
 * Created by duynk on 9/16/15.
 */
public class TalkWithMeFragment extends Fragment {
    MainActivity mRefActivity;
    private CustomViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    TopicLoaderFragment mTopicLoaderFragment;


    public TalkWithMeFragment() {
    }

    public static TalkWithMeFragment create(MainActivity act) {
        TalkWithMeFragment fragment = new TalkWithMeFragment();
        fragment.mRefActivity = act;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragement_talk_with_me, container, false);



        mPager = (CustomViewPager) rootView.findViewById(R.id.pager);
        mTopicLoaderFragment = new TopicLoaderFragment();
        this.refreshTopics();

        mRefActivity.getSupportActionBar().show();
        return rootView;
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
                AppModel.topics.loadAsync(mRefActivity, new AbstractContainer.OnLoadAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        refreshTopics();
                    }

                    @Override
                    public void onError() {
                    }
                });
            } else {
                mRefActivity.mCurrentTopicIndex = position;
            }
        }

        public void setCount(int value) {
            mDataSize = value + 1;
            this.notifyDataSetChanged();
        }
    }

    public void refreshTopics() {
        mRefActivity.runOnUiThread(new Runnable() {
            public void run() {
                boolean delay = true;
                if (mPagerAdapter == null) {
                    delay = false;
                    mPagerAdapter = new ScreenSlidePagerAdapter(mRefActivity.getSupportFragmentManager());
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

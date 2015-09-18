package vietnamworks.com.pal;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.models.AbstractContainer;
import vietnamworks.com.pal.models.AppModel;

/**
 * Created by duynk on 9/16/15.
 */
public class TalkWithMeFragment extends Fragment {
    private CustomViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    public TalkWithMeFragment() {
    }

    public static TalkWithMeFragment create() {
        TalkWithMeFragment fragment = new TalkWithMeFragment();
        return fragment;
    }

    public static boolean isThis(Context context) {
        if (context instanceof MainActivity && !((MainActivity) context).isFinishing()) {
            Fragment fragment = ((MainActivity) context).getActiveFragment();
            if (fragment instanceof TalkWithMeFragment) {
                TalkWithMeFragment f = (TalkWithMeFragment) fragment;
                if (f.isVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadData() {
        AppModel.topics.loadAsync(this.getActivity(), new AbstractContainer.OnLoadAsyncCallback() {
            @Override
            public void onSuccess(Context context) {
                if (TalkWithMeFragment.isThis(context)) {
                    refreshTopics();
                }
            }

            @Override
            public void onError(Context context) {
                if (TalkWithMeFragment.isThis(context)) {
                    if (mPagerAdapter != null) {
                        TopicLoaderFragment loader = mPagerAdapter.getTopicLoaderFragment();
                        if (loader != null) {
                            loader.onLoadingFail();
                        }
                    }
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragement_talk_with_me, container, false);

        mPager = (CustomViewPager) rootView.findViewById(R.id.pager);

        ((MainActivity) this.getActivity()).getSupportActionBar().show();
        ((MainActivity) this.getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mPager.clearOnPageChangeListeners();
        mPager.addOnPageChangeListener(new CustomViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PagerAdapter adapter = mPager.getAdapter();
                if (adapter != null && position == adapter.getCount() - 1) {
                    loadData();
                } else {
                    ((MainActivity) getActivity()).mCurrentTopicIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        ((MainActivity) this.getActivity()).resetMenuItem();
        this.refreshTopics();

        return rootView;
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int mDataSize = 1;
        public ScreenSlidePagerAdapter( FragmentManager fm) {
            super(fm);
        }
        private int mCurrentPosition;
        TopicLoaderFragment loader =  new TopicLoaderFragment();
        @Override
        public Fragment getItem(int position) {
            int count = this.getCount();
            if (position < count - 1) {
                return TopicFragment.create(position);
            } else {
                return loader;
            }
        }

        @Override
        public int getCount() {
            return mDataSize;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentPosition = position;
        }

        public void setCount(int value) {
            mDataSize = value + 1;
            this.notifyDataSetChanged();
        }

        public TopicLoaderFragment getTopicLoaderFragment() {
            /*
            Fragment f = this.getItem(mCurrentPosition);
            if (f instanceof  TopicLoaderFragment) {
                return (TopicLoaderFragment)f;
            }
            return null;
            */
            return loader;
        }
    }

    public void refreshTopics() {
        boolean delay = true;
        if (mPagerAdapter == null) {
            delay = false;
            mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        }

        if (delay) {
            final TopicLoaderFragment loader = mPagerAdapter.getTopicLoaderFragment();
            if (loader != null) {
                loader.onStartLoading();
            }

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Context context = getActivity();
                    if (TalkWithMeFragment.isThis(context)) {
                        mPager.setAdapter(null);
                        mPagerAdapter.setCount(AppModel.topics.getData().size());
                        mPager.setAdapter(mPagerAdapter);
                        if (mPagerAdapter.getCount() <= 1) {
                            loadData();
                        }
                    }
                }
            },100);
        }else {
            mPager.setAdapter(mPagerAdapter);
            loadData();
        }
    }
}

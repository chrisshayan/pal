package vietnamworks.com.pal.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.ActivityMain;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.components.CustomViewPager;
import vietnamworks.com.pal.models.AbstractContainer;
import vietnamworks.com.pal.models.AppModel;

/**
 * Created by duynk on 9/16/15.
 */
public class FragmentTalkWithMe extends Fragment {
    private CustomViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    public FragmentTalkWithMe() {
    }

    public static FragmentTalkWithMe create() {
        FragmentTalkWithMe fragment = new FragmentTalkWithMe();
        return fragment;
    }

    public static boolean isThis(Context context) {
        if (context instanceof ActivityMain && !((ActivityMain) context).isFinishing()) {
            Fragment fragment = ((ActivityMain) context).getActiveFragment();
            if (fragment instanceof FragmentTalkWithMe) {
                FragmentTalkWithMe f = (FragmentTalkWithMe) fragment;
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
                if (FragmentTalkWithMe.isThis(context)) {
                    refreshTopics();
                }
            }

            @Override
            public void onError(Context context) {
                if (FragmentTalkWithMe.isThis(context)) {
                    if (mPagerAdapter != null) {
                        FragmentTopicLoader loader = mPagerAdapter.getTopicLoaderFragment();
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

        ((ActivityMain) this.getActivity()).getSupportActionBar().show();
        ((ActivityMain) this.getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
                    ((ActivityMain) getActivity()).mCurrentTopicIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        ((ActivityMain) this.getActivity()).resetMenuItem();
        this.refreshTopics();

        return rootView;
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int mDataSize = 1;
        public ScreenSlidePagerAdapter( FragmentManager fm) {
            super(fm);
        }
        private int mCurrentPosition;
        FragmentTopicLoader loader =  new FragmentTopicLoader();
        @Override
        public Fragment getItem(int position) {
            int count = this.getCount();
            if (position < count - 1) {
                return FragmentTopic.create(position);
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

        public FragmentTopicLoader getTopicLoaderFragment() {
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
            final FragmentTopicLoader loader = mPagerAdapter.getTopicLoaderFragment();
            if (loader != null) {
                loader.onStartLoading();
            }

            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Context context = getActivity();
                    if (FragmentTalkWithMe.isThis(context)) {
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

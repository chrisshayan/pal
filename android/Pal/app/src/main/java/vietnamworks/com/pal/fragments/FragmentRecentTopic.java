package vietnamworks.com.pal.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vietnamworks.com.pal.ActivityMain;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.components.RecentThreadListAdapter;

/**
 * Created by duynk on 9/17/15.
 */
public class FragmentRecentTopic extends Fragment {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private RecentThreadListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_recent_topics, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recent_topic_list);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mAdapter = new RecentThreadListAdapter(rootView.getContext());
        mRecyclerView.setAdapter(mAdapter);

        RecentThreadListAdapter.OnItemClickListener onItemClickListener = new RecentThreadListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int type, int position) {
                System.out.println("onItemClick " + type + ", " + position);
            }
        };
        mAdapter.setOnItemClickListener(onItemClickListener);

        ((ActivityMain) this.getActivity()).hideListMenuItem();
        ((ActivityMain) this.getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

}

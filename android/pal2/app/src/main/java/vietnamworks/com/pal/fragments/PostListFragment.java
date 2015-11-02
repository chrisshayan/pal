package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.custom_views.TimelineItem;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 11/2/15.
 */
public class PostListFragment extends BaseFragment {
    public final static int FILTER_ALL = 0;
    public final static int FILTER_EVALUATED = 1;
    int filterType = FILTER_ALL;

    private PostItemAdapter mAdapter;
    Firebase dataRef;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterType = getArguments().getInt("mode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_posts, container, false);

        BaseActivity.applyFont(rootView);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new PostItemAdapter();

        return rootView;
    };

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(null);
        Activity _act = this.getActivity();
        if (_act != null) {
            TimelineActivity act = (TimelineActivity) _act;
            dataRef = FirebaseService.newRef("posts");
            int mode = filterType;
            String uid = FirebaseService.authData.getUid();
            if (mode == FILTER_ALL) {
                dataRef.orderByChild("created_by").equalTo(uid).addValueEventListener(dataValueEventListener);
            } else if (mode == FILTER_EVALUATED) {
                String index = Post.buildUserStatusIndex(uid, Post.STATUS_ADVISOR_EVALUATED);
                dataRef.orderByChild("index_user_status").equalTo(index).addValueEventListener(dataValueEventListener);
            }
        }
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (dataRef != null) {
            dataRef.removeEventListener(dataValueEventListener);
        }
    }

    private ValueEventListener dataValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AppModel.posts.getData().clear();
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                String key = postSnapshot.getKey();
                HashMap<String, Object> obj = postSnapshot.getValue(HashMap.class);
                Post p = new Post(obj);
                p.setId(key);
                AppModel.posts.getData().add(0, p);
                //TODO: no need to reload all list like this. Just reload changed item only
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    public static PostListFragment createAllPosts() {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt("mode", FILTER_ALL);
        fragment.setArguments(args);
        return fragment;
    }

    public static PostListFragment createEvaluatedList() {
        PostListFragment fragment = new PostListFragment();
        Bundle args = new Bundle();
        args.putInt("mode", FILTER_EVALUATED);
        fragment.setArguments(args);
        return fragment;
    }

    public int getFilterType() {
        return filterType;
    }


    static class PostItemAdapter extends RecyclerView.Adapter<TimelineItem> {
        @Override
        public int getItemCount() {
            return AppModel.posts.getData().size();
        }

        @Override
        public TimelineItem onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_item, viewGroup, false);
            TimelineItem view = new TimelineItem(v, viewGroup.getContext());
            return view;
        }

        @Override
        public void onBindViewHolder(final TimelineItem view, final int i) {
            Post p = AppModel.posts.getData().get(i);
            if (p != null) {
                view.setItemId(p.getId());
                int icon = R.drawable.ic_queueing;
                if (p.getStatus() == Post.STATUS_ADVISOR_PROCESSING) {
                    icon = R.drawable.ic_evaluating;
                } else if (p.getStatus() == Post.STATUS_ADVISOR_EVALUATED) {
                    icon = R.drawable.ic_evaluated;
                }
                view.setValue( icon, p);
                view.highlight(!p.isHas_read());
            }
        }
    }

}

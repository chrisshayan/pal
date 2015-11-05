package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.custom_views.TimelineItemBaseView;
import vietnamworks.com.pal.custom_views.TimelineItemNullView;
import vietnamworks.com.pal.custom_views.TimelineItemView;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 11/2/15.
 */
public class PostListFragment extends BaseFragment {
    public final static int FILTER_ALL = 0;
    public final static int FILTER_EVALUATED = 1;
    int filterType = FILTER_ALL;


    private PostItemAdapter mAdapter;
    Query dataRef;
    RecyclerView recyclerView;
    View overlay;
    FloatingActionsMenu fab;

    int pageSize = 100;
    int dataSize = pageSize;


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

        overlay = rootView.findViewById(R.id.overlay);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.collapse();
                return;
            }
        });
        fab = (FloatingActionsMenu) rootView.findViewById(R.id.fab);

        fab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                overlay.setVisibility(View.VISIBLE);
                overlay.setAlpha(0);
                overlay.animate().alpha(0.25f).setDuration(200).start();
            }

            @Override
            public void onMenuCollapsed() {
                overlay.setVisibility(View.GONE);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(null);
        Activity _act = this.getActivity();
        if (_act != null) {
            TimelineActivity act = (TimelineActivity) _act;
            int mode = filterType;
            if (mode == FILTER_ALL) {
                dataRef = Posts.getAllPostsQuery().limitToFirst(dataSize);
            } else if (mode == FILTER_EVALUATED) {
                dataRef = Posts.getEvaluatedPostsQuery().limitToFirst(dataSize);
            }
            dataRef.addValueEventListener(dataValueEventListener);
            dataRef.keepSynced(true);
        }
        recyclerView.setAdapter(mAdapter);
        fab.collapseImmediately();
        overlay.setVisibility(View.GONE);
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

    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    class PostItemAdapter extends RecyclerView.Adapter<TimelineItemBaseView> {
        @Override
        public int getItemCount() {
            return AppModel.posts.getData().size() + 1;
        }

        @Override
        public TimelineItemBaseView onCreateViewHolder(ViewGroup viewGroup, int type) {
            int count = getItemCount();
            if (type == 0) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_item, viewGroup, false);
                return new TimelineItemView(v, viewGroup.getContext());
            } else {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_end, viewGroup, false);
                return new TimelineItemNullView(v);
            }

        }

        @Override
        public int getItemViewType(int position) {
            int count = getItemCount();
            return (count > 0 && position < count - 1)?0:1;
        }

        @Override
        public void onBindViewHolder(final TimelineItemBaseView v, final int i) {
            if (v instanceof TimelineItemView) {
                TimelineItemView view = (TimelineItemView)v;
                Post p = AppModel.posts.getData().get(i);
                if (p != null) {
                    view.setItemId(p.getId());
                    int icon = R.drawable.ic_queueing;
                    if (p.getStatus() == Post.STATUS_ADVISOR_PROCESSING) {
                        icon = R.drawable.ic_evaluating;
                    } else if (p.getStatus() == Post.STATUS_ADVISOR_EVALUATED) {
                        icon = R.drawable.ic_evaluated;
                    }
                    view.setValue(icon, p, true);
                    view.highlight(!p.isHas_read());
                    view.setClickEventListener(new TimelineItemView.OnClickEventListener() {
                        @Override
                        public void onClicked(final String itemId) {
                            Posts.markAsRead(itemId);
                            BaseActivity.sInstance.setTimeout(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle b = new Bundle();
                                    b.putString("id", itemId);
                                    BaseActivity.sInstance.openFragment(PostDetailFragment.create(b), R.id.fragment_holder, true);
                                }
                            }, 200);
                        }
                    });
                }
            } else {
                TimelineItemNullView view = (TimelineItemNullView)v;
                long created_date = FirebaseService.getUserProfileLongValue("created_date", 0);
                if (created_date > 0) {
                    String time = Utils.getDuration(created_date);
                    view.setText(String.format(BaseActivity.sInstance.getString(R.string.joined_at), time));
                }

                if (dataSize == getItemCount() - 1) {
                    dataSize += pageSize;
                    int mode = filterType;
                    dataRef.removeEventListener(dataValueEventListener);
                    if (mode == FILTER_ALL) {
                        dataRef = Posts.getAllPostsQuery().limitToFirst(dataSize);
                    } else if (mode == FILTER_EVALUATED) {
                        dataRef = Posts.getEvaluatedPostsQuery().limitToFirst(dataSize);
                    }
                    dataRef.addValueEventListener(dataValueEventListener);
                    dataRef.keepSynced(true);
                }
            }
        }
    }

}

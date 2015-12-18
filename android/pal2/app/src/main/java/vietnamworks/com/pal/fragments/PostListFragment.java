package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.activities.TimelineActivity;
import vietnamworks.com.pal.common.AnimatorEndListener;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.configurations.AppUiConfig;
import vietnamworks.com.pal.custom_views.TimelineItemBaseView;
import vietnamworks.com.pal.custom_views.TimelineItemNullView;
import vietnamworks.com.pal.custom_views.TimelineItemQuest;
import vietnamworks.com.pal.custom_views.TimelineItemView;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.entities.UserProfile;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.services.Callback;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;

/**
 * Created by duynk on 11/2/15.
 */
public class PostListFragment extends BaseFragment {
    public final static int FILTER_ALL = 0;
    public final static int FILTER_EVALUATED = 1;
    int filterType = FILTER_ALL;

    boolean isReady = false;

    private PostItemAdapter mAdapter;
    Query dataRef;
    RecyclerView recyclerView;
    View overlay, mini_quest_view;
    FloatingActionsMenu fab;

    int pageSize = 100;
    int dataSize = pageSize;

    private SwipeRefreshLayout swipeContainer;

    ProgressBar progressBar;

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

        mAdapter = new PostItemAdapter(getContext());

        recyclerView = (RecyclerView) rootView.findViewById(R.id.post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mAdapter.setFirstVisibleItem(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition());
            }
        });

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fab.setVisibility(View.INVISIBLE);
                openOverlay();
                BaseActivity.timeout(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.VISIBLE);
                        swipeContainer.setRefreshing(false);
                        closeOverlay();
                        FirebaseService.goOnline();
                    }
                }, 1000);
            }
        });

        mini_quest_view = ((TimelineActivity)BaseActivity.sInstance).getQuestView();
        mini_quest_view.setVisibility(View.GONE);

        overlay = rootView.findViewById(R.id.overlay);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.collapse();
                return;
            }
        });
        fab = (FloatingActionsMenu) rootView.findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                openOverlay();
            }

            @Override
            public void onMenuCollapsed() {
                closeOverlay();
            }
        });

        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        //show dialog remind user to fulfill profile
        int last_remind_session = LocalStorage.getInt(R.string.ls_total_sessions, 0);
        int current_session = FirebaseService.getUserProfileIntValue("total_sessions", 0);
        if ((last_remind_session == 0 && current_session - last_remind_session >= 5) || (current_session - last_remind_session >= 20)) {
            UserProfile p = UserProfile.getCurrentUserProfile();
            LocalStorage.set(R.string.ls_total_sessions, current_session);
            if (p.getFirstName().isEmpty() || p.getLastName().isEmpty() || p.getAvatar().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.profile_update_encourage_message))
                        .setTitle(R.string.profile_update_encourage_title)
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TimelineActivity) PostListFragment.this.getActivity()).pushFragment(new UpdateProfileFragment(), R.id.fragment_holder);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        return rootView;
    }

    private void openOverlay() {
        overlay.setVisibility(View.VISIBLE);
        overlay.setAlpha(AppUiConfig.BASE_OVERLAY_ALPHA);
    }

    private void closeOverlay() {
        overlay.animate().alpha(0f).setDuration(100).setListener(new AnimatorEndListener(new Callback() {
            @Override
            public void onDone(Context ctx, Object obj) {
                overlay.setVisibility(View.GONE);
            }
        })).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        isReady = false;
        recyclerView.setAdapter(null);
        Activity _act = this.getActivity();
        if (_act != null) {
            TimelineActivity act = (TimelineActivity) _act;
            int mode = filterType;
            if (mode == FILTER_ALL) {
                dataRef = Posts.getAllPostsQuery().limitToFirst(dataSize);
                BaseActivity.sInstance.setTitle(R.string.title_timeline);
                act.highlightAllPostMenuItem(true);
                act.highlightEvaluatedMenuItem(false);

            } else if (mode == FILTER_EVALUATED) {
                dataRef = Posts.getEvaluatedPostsQuery().limitToFirst(dataSize);
                BaseActivity.sInstance.setTitle(R.string.title_evaluated_posts);

                act.highlightAllPostMenuItem(false);
                act.highlightEvaluatedMenuItem(true);
            }
            dataRef.addValueEventListener(dataValueEventListener);
        }
        recyclerView.setAdapter(mAdapter);
        fab.collapseImmediately();
        overlay.setVisibility(View.GONE);
        GaService.trackScreen(R.string.ga_screen_post_list);
    }

    @Override
    public void onPause() {
        super.onDetach();
        if (dataRef != null) {
            dataRef.removeEventListener(dataValueEventListener);
            mini_quest_view.setVisibility(View.GONE);

            Activity _act = this.getActivity();
            if (_act != null) {
                TimelineActivity act = (TimelineActivity) _act;
                act.highlightAllPostMenuItem(false);
                act.highlightEvaluatedMenuItem(false);
            }
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
            BaseActivity.timeout(new Runnable() {
                @Override
                public void run() {
                    fab.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    isReady = true;
                    mAdapter.notifyDataSetChanged();
                }
            }, 200);
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            BaseActivity.timeout(new Runnable() {
                @Override
                public void run() {
                    fab.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    isReady = true;
                    mAdapter.notifyDataSetChanged();
                }
            }, 200);
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
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    class PostItemAdapter extends RecyclerView.Adapter<TimelineItemBaseView> {
        int lastPosition = -1;
        final int TYPE_CHALLENGE = 0;
        final int TYPE_FEED = 1;
        final int TYPE_FOOTER = 2;

        boolean hasQuest = true;
        int count = 0;
        Context context;

        int lastVisibleItem = -1;

        public PostItemAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            hasQuest = isReady && filterType == FILTER_ALL;
            if (hasQuest) {
                for (int i = 0; i < 10 && i < AppModel.posts.getData().size(); i++) {
                    if (AppModel.posts.getData().get(i).getStatus() == Post.STATUS_READY) {
                        hasQuest = false;
                        break;
                    }
                }
                hasQuest = hasQuest && ((TimelineActivity) getActivity()).getCurrentQuest() != null;
            }
            if (hasQuest) {
                count = AppModel.posts.getData().size() + 2;
            } else {
                count = AppModel.posts.getData().size() + 1;
            }
            return isReady?count:0;
        }

        @Override
        public TimelineItemBaseView onCreateViewHolder(ViewGroup viewGroup, int type) {
            if (type == TYPE_FEED) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_item, viewGroup, false);
                return new TimelineItemView(v, viewGroup.getContext());
            } else if (type == TYPE_FOOTER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_end, viewGroup, false);
                return new TimelineItemNullView(v);
            } else {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_random_quest, viewGroup, false);
                return new TimelineItemQuest(v, viewGroup.getContext());
            }
        }

        private boolean isFirstFeedRecord(int position) {
            return hasQuest ?(position == 1):(position == 0);
        }

        private int feedIndex(int pos) {
            return hasQuest ?(pos - 1):(pos);
        }

        @Override
        public int getItemViewType(int position) {
            if (hasQuest) {
                return position == 0 ? TYPE_CHALLENGE : ((count > 0 && position < count - 1) ? TYPE_FEED : TYPE_FOOTER);
            } else {
                return (count > 0 && position < count - 1) ? TYPE_FEED : TYPE_FOOTER;
            }
        }

        @Override
        public void onBindViewHolder(final TimelineItemBaseView v, final int i) {
            int type = getItemViewType(i);
            if (type == TYPE_FEED) {
                final TimelineItemView view = (TimelineItemView)v;
                Post p = AppModel.posts.getData().get(feedIndex(i));
                if (p != null) {
                    view.setItemId(p.getId());
                    int icon = R.drawable.ic_queueing;
                    if (p.getStatus() == Post.STATUS_ADVISOR_PROCESSING) {
                        icon = R.drawable.ic_evaluating;
                    } else if (p.getStatus() == Post.STATUS_ADVISOR_EVALUATED) {
                        icon = R.drawable.ic_evaluated;
                    } else if (p.getStatus() == Post.STATUS_USER_ERROR) {
                        icon = R.drawable.ic_error;
                    } else if (p.getStatus() < Post.STATUS_READY) {
                        icon = R.drawable.timeline_upload_anim_01;
                    }

                    view.highlight(!p.isHas_read() && p.getStatus() > Post.STATUS_READY);
                    view.setClickEventListener(new TimelineItemView.OnClickEventListener() {
                        @Override
                        public void onClick(final String itemId) {
                            if (itemId != null) { //else something was wrong
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
                        }
                    });

                    if (isFirstFeedRecord(i) && p.getStatus() == Post.STATUS_READY && Math.abs(Utils.getMillis() - p.getLast_modified_date()) < 3000) {
                        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                        animation.setDuration(200);
                        v.container.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (Utils.isLollipopOrLater()) {
                                    view.startIconAnim();
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        if (Utils.isLollipopOrLater()) {
                            icon = R.drawable.animation_timeline_uploading;
                        } else {
                            icon = R.drawable.ic_queueing;
                        }
                        view.setValue(icon, p, true);

                    } else {
                        setAnimation(v.container, i);
                    }
                    view.setValue(icon, p, true);
                }
            } else if (type == TYPE_FOOTER) {
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
                }
                setAnimation(v.container, i);
            } else if (type == TYPE_CHALLENGE) {
                //todo load topic
                TimelineItemQuest view = (TimelineItemQuest)v;
                view.setQuestTitle(((TimelineActivity) getActivity()).getCurrentQuest().getTitle());
            }
        }

        private void setFirstVisibleItem(int firstVisibleItem) {
            if (isReady && lastVisibleItem != firstVisibleItem) {
                boolean _showChallengeShortcut = hasQuest && (firstVisibleItem > 0);
                mini_quest_view.setVisibility(_showChallengeShortcut ? View.VISIBLE : View.GONE);
                lastVisibleItem = firstVisibleItem;
            }
        }

        private void setAnimation(View viewToAnimate, int position)
        {
            /*
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_item_appear_anim);
                animation.setDuration(200);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
            */
        }
    }
}

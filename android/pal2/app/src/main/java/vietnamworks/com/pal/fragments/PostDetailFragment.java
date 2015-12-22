package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.common.Utils;
import vietnamworks.com.pal.custom_views.TimelineItemBaseView;
import vietnamworks.com.pal.custom_views.TimelineItemNullView;
import vietnamworks.com.pal.custom_views.TimelineItemView;
import vietnamworks.com.pal.entities.BaseEntity;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.models.Posts;
import vietnamworks.com.pal.models.CurrentUserProfile;
import vietnamworks.com.pal.services.AsyncCallback;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.services.GaService;
import vietnamworks.com.pal.services.LocalStorage;

/**
 * Created by duynk on 11/3/15.
 */
public class PostDetailFragment extends BaseFragment {
    String itemId;

    private PostItemAdapter mAdapter;
    Query dataRef;
    RecyclerView recyclerView;
    Post post;
    ProgressBar progressBar;
    String title = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_post_detail, container, false);

        BaseActivity.applyFont(rootView);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new PostItemAdapter();
        recyclerView.setAdapter(mAdapter);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataRef == null) {
            if (itemId == null) {
                itemId = LocalStorage.getString(R.string.ls_tmp_detail_fragment_id, "-1");
            }
            dataRef = Posts.getPostDetailQuery(itemId);
        }
        dataRef.addValueEventListener(dataValueEventListener);
        GaService.trackScreen(R.string.ga_screen_post_detail);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dataRef != null) {
            dataRef.removeEventListener(dataValueEventListener);
        }
    }

    @Override
    public void onResumeFromBackStack() {
        if (!title.isEmpty()) {
            BaseActivity.sInstance.setTitle(Utils.getFirstWordsExtra(title, 5));
        }
    }


    public static PostDetailFragment create(Bundle b) {
        PostDetailFragment obj = new PostDetailFragment();
        obj.itemId = b.getString("id");
        LocalStorage.set(R.string.ls_tmp_detail_fragment_id, obj.itemId);
        return obj;
    }


    class PostItemAdapter extends RecyclerView.Adapter<TimelineItemBaseView> {
        @Override
        public int getItemCount() {
            if (post != null) {
                return post.getConversationList().size() + 2;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            int count = getItemCount();
            return (count > 1 && position < count - 1)?0:1;
        }


        @Override
        public TimelineItemBaseView onCreateViewHolder(ViewGroup viewGroup, int type) {
            if (type == 0) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_item, viewGroup, false);
                return new TimelineItemView(v, viewGroup.getContext());
            } else {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_timeline_end, viewGroup, false);
                return new TimelineItemNullView(v);
            }
        }

        @Override
        public void onBindViewHolder(final TimelineItemBaseView v, final int i) {
            if (v instanceof TimelineItemView) {
                final TimelineItemView view = (TimelineItemView) v;
                if (i == 0) {
                    String avatar = FirebaseService.getUserProfileStringValue("avatar");
                    if (avatar != null && !avatar.isEmpty()) {
                        view.setValue(avatar, post);
                    } else {
                        view.setValue(R.drawable.ic_action_account_circle_dark, post);
                    }
                } else {
                    HashMap<String, Object> conversation = post.getConversationList().get(i - 1);
                    view.setValue(R.drawable.ic_action_account_circle_dark,
                            getString(R.string.advisor_said),
                            Utils.getDuration((long) conversation.get("created_date")),
                            "",
                            conversation.get("text").toString(),
                            conversation.get("audio").toString(),
                            false
                    );
                    view.setItemId(conversation.get("uid").toString());

                    view.setCTA(getString(R.string.vote_for_advisor), new TimelineItemView.OnClickEventListener() {
                        @Override
                        public void onClick(String itemId) {
                            BaseActivity.sInstance.pushFragment(AdvisorPreviewFragment.create(itemId), R.id.fragment_holder);
                        }
                    });

                    CurrentUserProfile.getUserProfile(conversation.get("uid").toString(), getContext(), new AsyncCallback() {
                        @Override
                        public void onSuccess(Context ctx, Object obj) {
                            DataSnapshot dataSnapshot = (DataSnapshot) obj;
                            HashMap<String, Object> data = dataSnapshot.getValue(HashMap.class);
                            if (data.containsKey("avatar")) {
                                String avatar = data.get("avatar").toString();
                                if (!avatar.isEmpty()) {
                                    view.setIcon(avatar);
                                }
                                String display_name = BaseEntity.safeGetString(data, "display_name");
                                if (display_name != null && !display_name.isEmpty()) {
                                    view.setSubject(display_name + " " + getString(R.string.answer));
                                }
                            }
                        }

                        @Override
                        public void onError(Context ctx, int error_code, String message) {

                        }
                    });
                }
            }
        }
    }


    private ValueEventListener dataValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            post = new Post(dataSnapshot);
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            BaseActivity.sInstance.setTimeout(new Runnable() {
                @Override
                public void run() {
                    title = post.getTitle();
                    if (title == null || title.isEmpty()) {
                        title = getString(R.string.you_said);
                    }
                    BaseActivity.sInstance.setTitle(Utils.getFirstWordsExtra(title, 5));
                }
            });
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };
}

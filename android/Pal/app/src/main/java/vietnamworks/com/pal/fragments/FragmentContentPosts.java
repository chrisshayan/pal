package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.TaskListActivity;
import vietnamworks.com.pal.components.DrawerEventListener;
import vietnamworks.com.pal.components.PostCardAdapter;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/15/15.
 */
public class FragmentContentPosts extends FragmentBase {
    private PostCardAdapter mAdapter;
    Firebase dataRef;

    public static FragmentContentPosts create(Bundle args) {
        FragmentContentPosts fragment = new FragmentContentPosts();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.content_posts, container, false);
        BaseActivity.applyFont(rootView);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) FragmentContentPosts.this.getActivity()).openActivity(TaskListActivity.class);
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new PostCardAdapter();
        recyclerView.setAdapter(mAdapter);

        return rootView;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataRef = FirebaseService.newRef("posts");
        int mode = getArguments().getInt("mode", -1);
        String uid = FirebaseService.authData.getUid();
        if (mode == DrawerEventListener.POST_FILTER_ALL) {
            dataRef.orderByChild("created_by").equalTo(uid).addValueEventListener(dataValueEventListener);
        } else if (mode == DrawerEventListener.POST_FILTER_RECENT_EVALUATED) {
            String index = Post.buildUserStatusIndex(uid, Post.STATUS_ADVISOR_EVALUATED);
            dataRef.orderByChild("index_user_status").equalTo(index).addValueEventListener(dataValueEventListener);
        } else if (mode == DrawerEventListener.POST_FILTER_SPEAKING) {
            String index = Post.buildUserTypeIndex(uid, Topic.TYPE_SPEAKING);
            dataRef.orderByChild("index_user_type").equalTo(index).addValueEventListener(dataValueEventListener);
        } else if (mode == DrawerEventListener.POST_FILTER_WRITING) {
            String index = Post.buildUserTypeIndex(uid, Topic.TYPE_WRITING);
            dataRef.orderByChild("index_user_type").equalTo(index).addValueEventListener(dataValueEventListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (dataRef != null) {
            dataRef.removeEventListener(dataValueEventListener);
        }
    }
}

package vietnamworks.com.pal.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.TaskListActivity;
import vietnamworks.com.pal.components.PostCardAdapter;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.services.FirebaseService;
import vietnamworks.com.pal.utils.Common;

/**
 * Created by duynk on 10/15/15.
 */
public class FragmentPostDetail extends FragmentBase {
    private PostCardAdapter mAdapter;
    Firebase dataRef;

    TextView title;
    TextView score;
    TextView status;
    TextView lastModifiedDate;

    public static FragmentPostDetail create(Bundle args) {
        FragmentPostDetail fragment = new FragmentPostDetail();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_post_detail, container, false);
        BaseActivity.applyFont(rootView);

        title = (TextView) rootView.findViewById(R.id.title);
        score = (TextView) rootView.findViewById(R.id.score);
        status = (TextView) rootView.findViewById(R.id.status);
        lastModifiedDate = (TextView) rootView.findViewById(R.id.last_modified_date);


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) FragmentPostDetail.this.getActivity()).openActivity(TaskListActivity.class);
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.conversation);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        //mAdapter = new PostCardAdapter();
        //recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private ValueEventListener dataValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final Post p = new Post(dataSnapshot);
            ((BaseActivity)getActivity()).setTimeout(new Runnable() {
                @Override
                public void run() {
                    title.setText(p.getTitle());
                    score.setText(p.getScore() == 0?"?":p.getScore() + "");
                    status.setText(p.getStatusString());
                    lastModifiedDate.setText(Common.getDateString(p.getLast_modified_date()));
                }
            });
            //mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String postId = getArguments().getString("id");

        if (postId != null) {
            dataRef = FirebaseService.newRef("posts").child(postId);
            dataRef.addValueEventListener(dataValueEventListener);
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

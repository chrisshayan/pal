package vietnamworks.com.pal.fragments;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
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
import vietnamworks.com.pal.PostsActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.TaskListActivity;
import vietnamworks.com.pal.components.AudioMixerController;
import vietnamworks.com.pal.components.AudioMixerSubscriber;
import vietnamworks.com.pal.components.ConversationAdapter;
import vietnamworks.com.pal.components.ConversationView;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.services.FirebaseService;

/**
 * Created by duynk on 10/15/15.
 */
public class FragmentPostDetail extends FragmentBase implements AudioMixerController {
    private ConversationAdapter mAdapter;
    Firebase dataRef;

    TextView title;
    TextView score;
    TextView status;
    ViewGroup userAnswerHolder;

    private MediaPlayer mPlayer = null;
    private AudioMixerSubscriber currentAudioSubscriber;

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
        mAdapter = new ConversationAdapter(this);
        recyclerView.setAdapter(mAdapter);

        userAnswerHolder = (ViewGroup)rootView.findViewById(R.id.user_answer_holder);

        return rootView;
    }

    private ValueEventListener dataValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final Post p = new Post(dataSnapshot);
            mAdapter.setPost(p);
            ((BaseActivity)getActivity()).setTimeout(new Runnable() {
                @Override
                public void run() {
                    userAnswerHolder.addView(ConversationView.create(
                            FragmentPostDetail.this.getActivity(),
                            FragmentPostDetail.this,
                            "You said:", //TODO: remove hard code text
                            p.getText(),
                            p.getAudio(),
                            p.getCreated_date()));


                    title.setText(p.getTitle());
                    score.setText(p.getScore() == 0?"?":p.getScore() + "");
                    status.setText(p.getStatusString());
                }
            });
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

        ((PostsActivity)this.getActivity()).updateHomeButton();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (dataRef != null) {
            dataRef.removeEventListener(dataValueEventListener);
        }
        if (currentAudioSubscriber != null && mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity _act = this.getActivity();
        if (_act != null) {
            PostsActivity act = (PostsActivity) _act;
            act.fragment_header.setTitle(getResources().getString(R.string.post_page_detail));
        }
    }

    public void playAudio(String url, final AudioMixerSubscriber sender) {
        if (mPlayer == null) { //not playing
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                    sender.onStopAudio();
                }
            });
            try {
                mPlayer.setDataSource(url);
                mPlayer.prepare();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (mp == mPlayer) {
                            mp.start();
                            currentAudioSubscriber = sender;
                            sender.onPlayAudio();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mPlayer = null;
                sender.onStopAudio();
            }
        } else {
            if (currentAudioSubscriber != null) {
                currentAudioSubscriber.onStopAudio();
                currentAudioSubscriber = null;
            }
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            playAudio(url, sender);
        }
    }

    public void stopAudio(final AudioMixerSubscriber sender) {
        if (mPlayer == null) {
            sender.onStopAudio();
        } else {
            try {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {

            }
            sender.onStopAudio();
            currentAudioSubscriber = null;
        }
    }

}

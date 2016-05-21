package vietnamworks.com.pal.fragments;

import android.content.Context;
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

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.activities.BaseActivity;
import vietnamworks.com.pal.custom_views.TopicItemView;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.models.Topics;
import vietnamworks.com.pal.services.GaService;

/**
 * Created by duynk on 11/4/15.
 */
public class TopicsFragment extends BaseFragment {
    private ItemAdapter mAdapter;
    Query dataRef;
    RecyclerView recyclerView;

    int pageSize = 100;
    int dataSize = pageSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_topics, container, false);

        BaseActivity.applyFont(rootView);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.topic_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mAdapter = new ItemAdapter(getContext());
        dataRef = Topics.getAllTopicsQuery().limitToFirst(pageSize);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(null);
        dataRef.addValueEventListener(dataValueEventListener);
        recyclerView.setAdapter(mAdapter);
        GaService.trackScreen(R.string.ga_screen_topics);
    }

    @Override
    public void onPause() {
        super.onDetach();
        if (dataRef != null) {
            dataRef.removeEventListener(dataValueEventListener);
        }
    }

    private ValueEventListener dataValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AppModel.topics.getData().clear();
            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                AppModel.topics.getData().add(0, (Topic)new Topic().importData(snapshot));
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    class ItemAdapter extends RecyclerView.Adapter<TopicItemView> {
        private Context context;
        private int lastPosition = -1;

        public ItemAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return AppModel.topics.getData().size();
        }

        @Override
        public TopicItemView onCreateViewHolder(ViewGroup viewGroup, int type) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_topic, viewGroup, false);
            return new TopicItemView(v);
        }

        @Override
        public void onBindViewHolder(final TopicItemView view, final int i) {
            Topic p = AppModel.topics.getData().get(i);
            if (p != null) {
                view.setData(p.getId(), p.getLevel(), p.getTitle(), p.getHint(), p.getViews(), p.getSubmits());
                view.setClickEventListener(new TopicItemView.OnClickEventListener() {
                    @Override
                    public void onClicked(final String itemId, final String topic, final int level, final String hint) {
                        BaseActivity.timeout(new Runnable() {
                            @Override
                            public void run() {
                                ComposerFragment f = new ComposerFragment();
                                f.setTopic(topic, itemId, hint);
                                BaseActivity.sInstance.pushFragment(f, R.id.fragment_holder);
                                Topics.addView(itemId);
                            }
                        }, 500);
                    }
                });
                setAnimation(view.container, i);
            }
        }

        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            /*
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.list_item_appear_anim);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
            */
        }
    }
}

package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import vietnamworks.com.pal.components.DrawerEventListener;
import vietnamworks.com.pal.components.PostCardAdapter;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.fragments.FragmentHeader;
import vietnamworks.com.pal.models.AppModel;
import vietnamworks.com.pal.services.FirebaseService;

public class PostsActivity extends BaseActivity {
    public FragmentHeader fragment_header;
    private RecyclerView mRecyclerView;
    private PostCardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Firebase dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        mRecyclerView = (RecyclerView) findViewById(R.id.post_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new MyAdapter(myDataset);
        //mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostsActivity.this.openActivity(TaskListActivity.class);
            }
        });

        fragment_header = ((FragmentHeader)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar));

        Firebase.setAndroidContext(this);

        Bundle b = getIntent().getExtras();
        dataRef = FirebaseService.newRef("posts");

        if (b != null) {
            int mode = b.getInt("mode", -1);
            String uid = FirebaseService.authData.getUid();
            if (mode == DrawerEventListener.POST_FILTER_ALL) {
                fragment_header.setTitle("All posts");
                dataRef.orderByChild("created_by").equalTo(uid).addValueEventListener(dataValueEventListener);
            } else if (mode == DrawerEventListener.POST_FILTER_RECENT_EVALUATED) {
                fragment_header.setTitle("Recent evaluated posts");
                String index = Post.buildUserStatusIndex(uid, Post.STATUS_ADVISOR_EVALUATED);
                dataRef.orderByChild("index_user_status").equalTo(index).addValueEventListener(dataValueEventListener);
            } else if (mode == DrawerEventListener.POST_FILTER_SPEAKING) {
                fragment_header.setTitle("Speaking posts");
                String index = Post.buildUserTypeIndex(uid, Topic.TYPE_SPEAKING);
                dataRef.orderByChild("index_user_type").equalTo(index).addValueEventListener(dataValueEventListener);
            } else if (mode == DrawerEventListener.POST_FILTER_WRITING) {
                fragment_header.setTitle("Writing posts");
                String index = Post.buildUserTypeIndex(uid, Topic.TYPE_WRITING);
                dataRef.orderByChild("index_user_type").equalTo(index).addValueEventListener(dataValueEventListener);
            }
        }

        mAdapter = new PostCardAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private ValueEventListener dataValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            AppModel.posts.getData().clear();
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                String key = postSnapshot.getKey();
                Post p = postSnapshot.getValue(Post.class);
                p.setId(key);
                AppModel.posts.getData().add(p);
                //TODO: no need to reload all list like this. Just reload changed item only
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    @Override
    protected  void onDestroy() {
        super.onDestroy();
        dataRef.removeEventListener(dataValueEventListener);
    }

    public void onOpenDrawer(View v) {
        //drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new DrawerEventListener(drawer));
        drawer.openDrawer(navigationView);
    }
}

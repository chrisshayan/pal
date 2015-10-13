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

import java.util.Arrays;

import vietnamworks.com.pal.components.DrawerEventListener;
import vietnamworks.com.pal.components.PostCardAdapter;
import vietnamworks.com.pal.entities.Post;
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
        if (b != null) {
            String mode = b.getString("mode");
            if (mode != null) {
                if (mode.equalsIgnoreCase("recent")) {
                    fragment_header.setTitle("Recent posts");
                    dataRef = FirebaseService.newRef(Arrays.asList("ref_user_posts", FirebaseService.authData.getUid()));
                    dataRef.addValueEventListener(dataValueEventListener);
                } else if (mode.equalsIgnoreCase("evaluated")) {
                    fragment_header.setTitle("Evaluated posts");
                    dataRef = FirebaseService.newRef(Arrays.asList("ref_user_posts", FirebaseService.authData.getUid()));
                    dataRef.orderByChild("status").startAt(Post.STATUS_ADVISOR_EVALUATED).addValueEventListener(dataValueEventListener);
                }
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
                Post p = new Post();
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

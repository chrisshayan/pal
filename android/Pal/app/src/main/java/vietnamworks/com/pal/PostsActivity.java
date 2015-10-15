package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import vietnamworks.com.pal.components.ConversationView;
import vietnamworks.com.pal.components.DrawerEventListener;
import vietnamworks.com.pal.components.PostListDrawerEventListener;
import vietnamworks.com.pal.entities.Post;
import vietnamworks.com.pal.entities.Topic;
import vietnamworks.com.pal.fragments.FragmentPostList;
import vietnamworks.com.pal.fragments.FragmentHeader;
import vietnamworks.com.pal.services.FirebaseService;

public class PostsActivity extends BaseActivity {
    public FragmentHeader fragment_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        fragment_header = ((FragmentHeader)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar));
        Bundle b = getIntent().getExtras();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, FragmentPostList.create(b)).commit();
        }

        if (b != null) {
            int mode = b.getInt("mode", -1);
            String uid = FirebaseService.authData.getUid();
            if (mode == DrawerEventListener.POST_FILTER_ALL) {
                fragment_header.setTitle("All posts");
            } else if (mode == DrawerEventListener.POST_FILTER_RECENT_EVALUATED) {
                fragment_header.setTitle("Recent evaluated posts");
                String index = Post.buildUserStatusIndex(uid, Post.STATUS_ADVISOR_EVALUATED);
            } else if (mode == DrawerEventListener.POST_FILTER_SPEAKING) {
                fragment_header.setTitle("Speaking posts");
                String index = Post.buildUserTypeIndex(uid, Topic.TYPE_SPEAKING);
            } else if (mode == DrawerEventListener.POST_FILTER_WRITING) {
                fragment_header.setTitle("Writing posts");
                String index = Post.buildUserTypeIndex(uid, Topic.TYPE_WRITING);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new PostListDrawerEventListener(drawer));
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
    }

    public void onOpenDrawer(View v) {
        //drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer.openDrawer(navigationView);
    }

    public void onToggleAudio(View v) {
        View p = (View) v.getParent();
        while (p != null) {
            if (p instanceof  ConversationView) {
                ((ConversationView)p).onToggleAudio(p);
                return;
            }
            p = (View) p.getParent();
        }
    }
}

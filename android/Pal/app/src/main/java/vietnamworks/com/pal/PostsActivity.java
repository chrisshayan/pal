package vietnamworks.com.pal;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import vietnamworks.com.pal.components.ConversationView;
import vietnamworks.com.pal.components.PostListDrawerEventListener;
import vietnamworks.com.pal.fragments.FragmentPostDetail;
import vietnamworks.com.pal.fragments.FragmentPostHeader;
import vietnamworks.com.pal.fragments.FragmentPostList;

public class PostsActivity extends BaseActivity {
    public FragmentPostHeader fragment_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        fragment_header = ((FragmentPostHeader)getSupportFragmentManager().findFragmentById(R.id.fragment_toolbar));
        Bundle b = getIntent().getExtras();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, FragmentPostList.create(b)).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new PostListDrawerEventListener(drawer));
    }

    @Override
    protected  void onDestroy() {
        super.onDestroy();
    }

    public void onClickHomeButton(View v) {
        Fragment f = this.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof FragmentPostDetail) {
            this.getSupportFragmentManager().popBackStackImmediate();
        } else if (f instanceof  FragmentPostList) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            drawer.openDrawer(navigationView);
        }

    }

    public void updateHomeButton() {
        fragment_header.updateHomeButton();
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

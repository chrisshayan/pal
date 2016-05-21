package vietnamworks.com.pal.components;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.R;
import vietnamworks.com.pal.fragments.FragmentPostList;

/**
 * Created by duynk on 10/15/15.
 */
public class PostListDrawerEventListener extends DrawerEventListener {
    public final static int POST_FILTER_ALL = 0;
    public final static int POST_FILTER_RECENT_EVALUATED = 1;
    public final static int POST_FILTER_WRITING = 2;
    public final static int POST_FILTER_SPEAKING = 3;

    public PostListDrawerEventListener(DrawerLayout drawer) {
        super(drawer);
    }
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int id = item.getItemId();
                if (id == R.id.all_posts) {
                    Bundle b = new Bundle();
                    b.putInt("mode", POST_FILTER_ALL);
                    BaseActivity.sInstance.openFragment(FragmentPostList.create(b), R.id.fragment_container);
                } else if (id == R.id.recent_evaluated_posts)

                {
                    Bundle b = new Bundle();
                    b.putInt("mode", POST_FILTER_RECENT_EVALUATED);
                    BaseActivity.sInstance.openFragment(FragmentPostList.create(b), R.id.fragment_container);
                } else if (id == R.id.writing_posts)

                {
                    Bundle b = new Bundle();
                    b.putInt("mode", POST_FILTER_WRITING);
                    BaseActivity.sInstance.openFragment(FragmentPostList.create(b), R.id.fragment_container);
                } else if (id == R.id.speaking_posts)

                {
                    Bundle b = new Bundle();
                    b.putInt("mode", POST_FILTER_SPEAKING);
                    BaseActivity.sInstance.openFragment(FragmentPostList.create(b), R.id.fragment_container);
                } else if (id == R.id.nav_change_password)

                {

                } else if (id == R.id.nav_logout)

                {

                }
            }
        }, 500);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

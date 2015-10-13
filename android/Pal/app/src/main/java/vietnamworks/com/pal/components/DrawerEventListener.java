package vietnamworks.com.pal.components;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import vietnamworks.com.pal.BaseActivity;
import vietnamworks.com.pal.PostsActivity;
import vietnamworks.com.pal.R;

/**
 * Created by duynk on 10/13/15.
 */
public class DrawerEventListener implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    public DrawerEventListener(DrawerLayout drawer) {
        this.drawer = drawer;
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_waiting) {
            Bundle b = new Bundle();
            b.putString("mode", "recent");
            BaseActivity.sInstance.openActivity(PostsActivity.class, b);
        } else if (id == R.id.nav_evaluated) {
            Bundle b = new Bundle();
            b.putString("mode", "evaluated");
            BaseActivity.sInstance.openActivity(PostsActivity.class, b);
        } else if (id == R.id.nav_change_password) {

        } else if (id == R.id.nav_logout) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

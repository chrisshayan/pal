package vietnamworks.com.pal.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;

import vietnamworks.com.pal.R;
import vietnamworks.com.pal.custom_views.UserProfileNavView;
import vietnamworks.com.pal.fragments.ComposerFragment;
import vietnamworks.com.pal.fragments.TimelineFragment;
import vietnamworks.com.pal.services.FirebaseService;

public class TimelineActivity extends BaseActivity {

    UserProfileNavView navHeaderView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pushFragment(new TimelineFragment(), R.id.fragment_holder);

        //drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //drawer -- header
        navHeaderView = UserProfileNavView.create(this, 0, 0, 0);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.addHeaderView(navHeaderView);

        setNumberOfUnreadPostUI(200);
        setNumberOfUnreadEvaluatedPostUI(0);

        //listen to user profile changed then update drawer
        FirebaseService.SetUserProfileListener(new FirebaseService.UserProfileListener() {
            @Override
            public void onChanged(HashMap<String, Object> data) {
                if (navHeaderView != null) {
                    navHeaderView.updateStat(
                            FirebaseService.GetUserProfileIntValue("total_posts", 0),
                            FirebaseService.GetUserProfileFloatValue("avg_points", 0),
                            FirebaseService.GetUserProfileIntValue("total_following", 0)
                    );
                    navHeaderView.updateProfile(
                            FirebaseService.GetUserProfileStringValue("display_name"),
                            FirebaseService.GetUserProfileStringValue("level_name"),
                            FirebaseService.GetUserProfileStringValue("avatar")
                    );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseService.SetUserProfileListener(null);
    }


    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void setNumberOfUnreadPostUI(final int val) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                String txt = val + "";
                if ((val) >= 100) {
                    txt = "99+";
                }
                TextView view = ((TextView) findViewById(R.id.num_of_posts));
                view.setText(txt);
                view.setVisibility(val > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void setNumberOfUnreadEvaluatedPostUI(final int val) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                String txt = val + "";
                if ((val) >= 100) {
                    txt = "99+";
                }
                TextView view = ((TextView) findViewById(R.id.num_of_evaluated_post));
                view.setText(txt);
                view.setVisibility(val > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public void onOpenAllPosts(View v) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                closeDrawer();
            }
        }, 500);
    }

    public void onOpenRecentEvaluatedPost(View v) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                closeDrawer();
            }
        }, 500);
    }

    public void onLogout(View v) {
        setTimeout(new Runnable() {
            @Override
            public void run() {
                FirebaseService.logout();
                openActivity(AuthActivity.class);
                closeDrawer();
            }
        }, 500);

    }

    public void onOpenSaySomethingComposer(View v) {
        ((FloatingActionsMenu)findViewById(R.id.multiple_actions)).collapse();
        setTimeout(new Runnable() {
            @Override
            public void run() {
                pushFragment(new ComposerFragment(), R.id.fragment_holder);
            }
        }, 500);
    }

    public void onOpenChallengeList(View v) {
        //TODO: open challenge list
    }
}

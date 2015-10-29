package vietnamworks.com.pal.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_image_dehaze);

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

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int deep = getSupportFragmentManager().getBackStackEntryCount();
                getSupportActionBar().setHomeAsUpIndicator(deep == 0 ? R.drawable.ic_image_dehaze : R.drawable.ic_hardware_keyboard_backspace);

                updateToolbar();
            }
        });

        openFragment(new TimelineFragment(), R.id.fragment_holder);
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
        updateToolbar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            return true;
        } else if (id == android.R.id.home) {
            int deep = getSupportFragmentManager().getBackStackEntryCount();
            if (deep == 0) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                drawer.openDrawer(navigationView);
            } else {
                getSupportFragmentManager().popBackStackImmediate();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseService.SetUserProfileListener(null);
    }

    private void updateToolbar() {
        Menu menu = toolbar.getMenu();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            int id = item.getItemId();
            if (id == R.id.action_send) {
                item.setVisible(f != null && f instanceof ComposerFragment);
            }
        }

        if (f instanceof ComposerFragment) {
            getSupportActionBar().setTitle(R.string.title_composer);
        } else if (f instanceof  TimelineFragment) {
            getSupportActionBar().setTitle(R.string.title_timeline);
        }
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
